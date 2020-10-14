/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="activities-tab-view.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */
import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {ServerAssetHelper} from '../../../server-asset.helper';
import {AbstractTabViewComponent} from '../abstract-tab-view.component';
import {
  editActivityOpen,
  newActivityOpen,
  refreshTablesStart,
  storeServerAsset
} from '../../../../../shared/store/actions/assets.actions';
import {select, Store} from '@ngrx/store';
import {fetchActivity, selectRefresh} from '../../../../../shared/store/reducers/assets.reducer';
import {take} from 'rxjs/operators';
import {MessageService} from 'primeng/api';
import {Subscription} from "rxjs/internal/Subscription";

@Component({
  selector: 'app-activities-tab-view',
  templateUrl: './activities-tab-view.component.html',
  styleUrls: ['./activities-tab-view.component.css']
})
export class ActivitiesTabViewComponent extends AbstractTabViewComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  public organization: any;
  @Input()
  public serverAsset: any;
  @Input() tabsStatus: boolean[];

  rowsNumber = '500px';
  selected: any[];
  public newActivity: any;

  private subscriptions: Subscription[] = [];

  constructor(public store: Store<any>, public messageService: MessageService) {
    super('activities', 'Activities', store, messageService);
  }

  ngOnInit() {

    this.subscriptions.push(
      this.store.pipe(select(selectRefresh)).subscribe(() => {
        this.resetTable();
        const activitiesMap = ServerAssetHelper.retrieveActivitiesMap(this.serverAsset);
        const allBusinessProcesses = ServerAssetHelper.getAllBusinessProcesses(this.serverAsset);
        const activities: any[] = [];
        for (const key of activitiesMap.keys()) {
          activities.push(key);
        }
        this.setColumns(allBusinessProcesses);
        this.setRows(activities, activitiesMap);
      }));
    this.subscriptions.push(
      this.store.pipe(select(fetchActivity)).subscribe(newActivity => {
        if (newActivity) {
          const predict = r => r.id === newActivity.identifier;
          if (this.dataRows.some(predict)) {
            const model = this.dataRows.find(predict);
            this.model = model;
            this.model.activities = newActivity.name;
            this.model.id = newActivity.identifier;
            this.dataRows[this.dataRows.indexOf(model)] = this.model;
            this.store.dispatch(storeServerAsset(this.serverAsset));
            this.store.dispatch(refreshTablesStart());
          } else {
            this.newActivity = newActivity;
            this.model.activities = this.newActivity.name;
            this.model.id = this.newActivity.identifier;
            this.save();
          }
          this.validate(this.serverAsset);
        }
      }));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tabsStatus) {
      const openTabs = changes.tabsStatus.currentValue.reduce((acc, val, ind) => {
        if (val === true) {

          acc.counter++;
          acc.index = ind;
        }
        return acc;
      }, {counter: 0, index: -1});

      if (openTabs.counter > 1 || openTabs.counter === 1 && openTabs.index !== 2) {
        this.rowsNumber = '200px';
      } else {
        this.rowsNumber = '500px';
      }
    }
  }

  showDialogToAdd() {
    this.resetModel();
    this.store.dispatch(newActivityOpen());
  }

  showDialogToEdit(data: any): any {
    const activityToEdit = ServerAssetHelper.findNodeByName(data.activities, this.serverAsset);
    this.store.dispatch(editActivityOpen(activityToEdit));
  }

  resetModel(): void {
    this.model = this.convertToObject(this.cols);
    this.model.activities = '';
  }

  save() {
    const dataRows = [...this.dataRows];
    dataRows.push(this.model);
    this.saveActivityToModel();
    this.dataRows = dataRows;
    this.store.dispatch(refreshTablesStart());
  }

  private saveActivityToModel() {
    const node = this.newActivity;
    this.serverAsset.nodes.push(node);
    this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  editName(event) {
    if (typeof (event.field) === 'string') {
      const updatedNodes = this.serverAsset.nodes;
      const found = updatedNodes.find(n => n.identifier === event.data.id);

      //We check if the name is already existing...
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event.data.activities && i.identifier != found.identifier)) {
        event.data.activities = found.name;
        return;
      }

      found.name = event.data.activities;
      this.store.dispatch(storeServerAsset(this.serverAsset));
      this.store.dispatch(refreshTablesStart());
    }
  }


  edit(event: any, rowIndex: any, column: any, changedValue: any) {
    this.disableCheckBox();
    this.editModel(rowIndex, column, changedValue);
    // this.validate();
  }

  editModel(i: number, column: any, changedValue: any): void {
    const edited = this.dataRows[i];
    const node = ServerAssetHelper.findNodeByName(edited.activities, this.serverAsset);
    if (changedValue) {
      this.addActivityRelationWithBusinessProcess(node, column);
    } else {
      this.removeActivityRelationWithBusinessProcess(node, column);
    }
  }

  addActivityRelationWithBusinessProcess(node: any, column: any): void {
    const businessProcess = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    const edge = ServerAssetHelper.createEdgeBetweenChildAndParent(node, businessProcess);
    this.serverAsset.edges.push(edge);
    businessProcess.children.push(edge.identifier);
    node.parents.push(edge.identifier);


    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  removeActivityRelationWithBusinessProcess(node: any, column: any): void {
    const businessProcess = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    let edge = this.serverAsset.edges.filter(e => e.source === businessProcess.identifier && e.target === node.identifier);

    if (!edge || edge === null) {
      edge = this.serverAsset.edges.filter(e => e.target === businessProcess.identifier && e.source === node.identifier);
    }

    if (edge) {
      edge.forEach(edgeElement => {
        this.removeEdgeAssetRelationWithBusinessProcess(node, edgeElement);
      });
    }
  }

  removeEdgeAssetRelationWithBusinessProcess(node: any, edge: any): void {

    ServerAssetHelper.removeEdgeById(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromParents(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromChildren(edge.identifier, this.serverAsset);

    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  deleteActivities(): void {
    if (this.selected.length > 0) {
      this.selected.forEach(selected => {
        this.dataRows = this.dataRows.filter(dr => dr !== selected);

        const activityToDelete = ServerAssetHelper.findNodeByIdentifier(selected.id, this.serverAsset);

        this.updateActivityParentsAndEdges(activityToDelete);
        let assetsChildren = ServerAssetHelper.getActivityRelatedAssets(activityToDelete, this.serverAsset);
        this.updateActivityChildrenAndEdges(activityToDelete);
        this.updateAssetsImpactsAndMalfunctions(assetsChildren);
        const deleted = ServerAssetHelper.removeNodeById(activityToDelete.identifier, this.serverAsset);
      });
    } else {
      return;
    }

    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.store.dispatch(refreshTablesStart());
    this.selected = [];
    this.validate(this.serverAsset);
  }

  updateActivityParentsAndEdges(activityToDelete: any) {
    //We collect all parents edges
    const edgesToDelete = activityToDelete.parents;

    //For each of the parents edges, we delete it from the edges collection and from the children array in all the parents of the Business Activity
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromParents(edge, this.serverAsset);
    });
  }

  updateActivityChildrenAndEdges(activityToDelete: any) {
    //We collect all children edges
    const edgesToDelete = activityToDelete.children;

    //For each of the children edges, we delete it from the edges collection and from the parents array in all the children of the Business Activity
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromChildren(edge, this.serverAsset);
    });
  }


  ngOnDestroy(): void {
    this.clear();
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  //When we remove an Activity, we need to check that its Assets are not losing Impact vaues (since the Activity may be linked to some Malfunction
  //associated to the Asset)
  private updateAssetsImpactsAndMalfunctions(assetsChildren: any[]) {
    for (let node of assetsChildren) {
      const activities = [];
      const children = new Set<any>();
      const malfunctions = [];

      node.parents.forEach(parent => {
        activities.push(ServerAssetHelper.traverseFromChildrenToParent(node, parent, this.serverAsset));
      });

      activities.forEach(a => {
        a.children.forEach(activityId => children.add(
          ServerAssetHelper.traverseFromParentToChildren(a, activityId, this.serverAsset)));
      });

      [...children].filter(c => c.nodeType === 'Malfunction').forEach(child => {
        malfunctions.push(child.identifier);
      });

      //Here we check if, despite having removed the link between Asset and Activity, the Asset still maintains a link with all its Malfunctions or something must be removed
      //or if the link must be removed
      const missingMalfunctions = [];
      for (const malfunctionId in node.malfunctionsIds) {

        let missingMalfunction = true;
        for (let malId of malfunctions) {
          if (malId === node.malfunctionsIds[malfunctionId]) {
            missingMalfunction = false;
            break;
          }
        }
        if (missingMalfunction) {
          missingMalfunctions.push(node.malfunctionsIds[malfunctionId]);
        }
      }

      for (let malId of missingMalfunctions) {
        const malfunction = ServerAssetHelper.findNodeByIdentifier(malId, this.serverAsset);
        ServerAssetHelper.removeMalfunctionFromAsset(malfunction, node);
        ServerAssetHelper.associateImpactToAsset(node, this.serverAsset);
        ServerAssetHelper.associateImpactToAssetEdges(node, this.serverAsset);
      }
    }
  }
}
