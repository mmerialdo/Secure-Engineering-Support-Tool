/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="malfunction-tab-view.component.ts"
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
import {MessageService} from 'primeng/api';
import {ServerAssetHelper} from '../../../server-asset.helper';
import {AbstractTabViewComponent} from '../abstract-tab-view.component';
import {
  editMalfunctionOpen,
  newMalfunctionOpen,
  readMalfunction, refreshTablesStart, refreshTablesStop, storeServerAsset
} from '../../../../../shared/store/actions/assets.actions';
import {select, Store} from '@ngrx/store';
import {fetchMalfunction, fetchMalfunctionForEdit, selectRefresh} from '../../../../../shared/store/reducers/assets.reducer';
import {Observable, Subscription} from 'rxjs';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-malfunctions-tab-view',
  templateUrl: './malfunctions-tab-view.component.html',
  styleUrls: ['./malfunctions-tab-view.component.css']
})
export class MalfunctionsTabViewComponent extends AbstractTabViewComponent implements OnInit, OnChanges, OnDestroy {
  @Input()
  public organization: any;
  @Input()
  public serverAsset: any;
  @Input() tabsStatus: boolean[];


  private sub: Subscription;
  private newMalfunction: any;
  private rows: any[];
  selected: any[];
  displayDialog: boolean;
  rowsNumber = '500px';
  newMalfun: boolean;
  modelKeys: string[];
  addedMalfunctions: Map<string, any> = new Map<string, any>();

  private subscriptions: Subscription[] = [];

  constructor(public store: Store<any>, public messageService: MessageService) {
    super('malfunctions', 'Malfunction', store, messageService);
  }

  ngOnInit() {
    this.subscriptions.push(
    this.store.pipe(select(selectRefresh)).subscribe(refresh => {
      this.resetTable();
      const malfunctionsMap = ServerAssetHelper.retrieveMalfunctionsMap(this.serverAsset);
      const activitiesForOrganisation = ServerAssetHelper.getAllBusinessActivities(this.serverAsset);
      const malfunctionsForOrganization: any[] = [];
      for (const key of malfunctionsMap.keys()) {
        malfunctionsForOrganization.push(key);
      }
      this.setColumns(activitiesForOrganisation);
      this.setRows(malfunctionsForOrganization, malfunctionsMap);

      this.store.dispatch(refreshTablesStop());
    }));

    this.subscriptions.push(
    this.store.pipe(select(fetchMalfunction)).subscribe(newMal => {
      if (newMal) {
        const predict = r => r.id === newMal.identifier;
        if (this.dataRows.some(predict)) {
          const model = this.dataRows.find(predict);
          this.model = model;
          this.model.malfunctions = newMal.name;
          this.model.id = newMal.identifier;
          this.dataRows[this.dataRows.indexOf(model)] = this.model;
         this.store.dispatch(storeServerAsset(this.serverAsset));
          this.store.dispatch(refreshTablesStart());
        } else {
          this.newMalfunction = newMal;
          this.model.malfunctions = this.newMalfunction.name;
          this.model.id = this.newMalfunction.identifier;
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

      if (openTabs.counter > 1 || openTabs.counter === 1 && openTabs.index !== 4) {
        this.rowsNumber = '200px';
      } else {
        this.rowsNumber = '500px';
      }
    }
  }

  save() {
    const dataRows = [...this.dataRows];
    dataRows.push(this.model);
    this.saveMalfunctionToModel();
    this.dataRows = dataRows;
    this.displayDialog = false;
    this.store.dispatch(refreshTablesStart());
  }

  showDialogToAdd() {
    this.resetModel();
    this.store.dispatch(newMalfunctionOpen());
  }

  editName(event) {
    if (typeof (event.field) === 'string') {
      const updatedNodes = this.serverAsset.nodes;
      const found = updatedNodes.find(n => n.identifier === event.data.id);

      //We check if the name is already existing...
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event.data.malfunctions && i.identifier != found.identifier)) {
        event.data.malfunctions = found.name;

        return;
      }

      found.name = event.data.malfunctions;

     this.store.dispatch(storeServerAsset(this.serverAsset));
      this.store.dispatch(refreshTablesStart());
    }
  }

  edit(event: any, rowIndex: any, column: any, changedValue: any) {
    this.disableCheckBox();
    this.editModel(rowIndex, column, changedValue);
  }

  resetModel(): void {
    this.model = this.convertToObject(this.cols);
    this.model.malfunctions = '';
  }

  editModel(i: number, column: any, changedValue: any): void {
    const edited = this.dataRows[i];
    const node = ServerAssetHelper.findNodeByName(edited.malfunctions, this.serverAsset);

    if (changedValue) {
      this.addMalfunctionRelationWithActivity(node, column);
    } else {
      this.removeMalfunctionRelationWithActivity(node, column);
    }
  }

  removeMalfunctionRelationWithActivity(node: any, column: any): void {
    const businessActivity = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    let edge = this.serverAsset.edges.filter(e => e.source === businessActivity.identifier && e.target === node.identifier);

    if (!edge || edge === null) {
      edge = this.serverAsset.edges.filter(e => e.target === businessActivity.identifier && e.source === node.identifier);
    }

    if(edge) {
      edge.forEach(edgeElement => {
        this.removeEdgeMalfunctionRelationWithActivity(node, edgeElement);
      })
    }
  }

  removeEdgeMalfunctionRelationWithActivity(node: any, edge: any): void {

    ServerAssetHelper.removeEdgeById(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromParents(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromChildren(edge.identifier, this.serverAsset);

    const allAssets: any[] = ServerAssetHelper.findAssetsWithMalfunction(node, this.serverAsset);
    allAssets.forEach(asset => {
      const activities = [];
      const children = new Set<any>();
      const malfunctions = [];

      asset.parents.forEach(parent => {
        activities.push(ServerAssetHelper.traverseFromChildrenToParent(asset, parent, this.serverAsset));
      });

      activities.forEach(a => {
        a.children.forEach(activityId => children.add(
          ServerAssetHelper.traverseFromParentToChildren(a, activityId, this.serverAsset)));
      });


      [...children].filter(c => c.nodeType === 'Malfunction').forEach(child => {
        malfunctions.push(child.identifier);
      });

      //Here we check if, despite having removed the link between Malfunction and Activity, each Asset linked to the Malfunction still have a possible link
      //or if the link must be removed
      let malfunctionFound = false;
      for (let malId of malfunctions) {
        if (malId === node.identifier) {
          malfunctionFound = true;
          break;
        }
      }

      if (malfunctionFound === false) {
        ServerAssetHelper.removeMalfunctionFromAsset(node, asset);
        ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
        ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);
      }

    });

   this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  addMalfunctionRelationWithActivity(node: any, column: any): void {
    const businessActivity = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];

    const edge = ServerAssetHelper.createEdgeBetweenChildAndParent(node, businessActivity);
    this.serverAsset.edges.push(edge);
    businessActivity.children.push(edge.identifier);
    node.parents.push(edge.identifier);

   this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  saveMalfunctionToModel(): void {
    const node = this.newMalfunction;

    this.serverAsset.nodes.push(node);
   this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  ngOnDestroy(): void {
    this.clear();
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  deleteMalfunctions(): void {
    if (this.selected.length > 0) {
      this.selected.forEach(selectedMal => {
        this.dataRows = this.dataRows.filter(dr => dr !== selectedMal);
        const malfunctionToDelete = ServerAssetHelper.findNodeByIdentifier(selectedMal.id, this.serverAsset);

        Object.keys(selectedMal).filter(k => k !== 'malfunctions').filter(k => k !== 'id').forEach(rowKey => {
          if (selectedMal[rowKey]) {
            this.updateMalfunctionParentsAndEdges(malfunctionToDelete);
            this.updateMalfunctionChildrenAndEdges(malfunctionToDelete);
            this.updateMalfunctionRelatedAssets(malfunctionToDelete);
          }
        });
        const deletedMalfunction = ServerAssetHelper.removeNodeById(malfunctionToDelete.identifier, this.serverAsset);
      });
    }

   this.store.dispatch(storeServerAsset(this.serverAsset));
    this.selected = [];
    this.validate(this.serverAsset);
  }

  updateMalfunctionParentsAndEdges(malfunctionToDelete: any) {
    //We collect all parents edges
    const edgesToDelete = malfunctionToDelete.parents;

    //For each of the parents edges, we delete it from the edges collection and from the children array in all the parents of the Business Process
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromParents(edge, this.serverAsset);
    });
  }

  updateMalfunctionChildrenAndEdges(malfunctionToDelete: any) {
    //We collect all children edges
    const edgesToDelete = malfunctionToDelete.children;

    //For each of the children edges, we delete it from the edges collection and from the parents array in all the children of the Business Process
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromChildren(edge, this.serverAsset);
    });
  }

  updateMalfunctionRelatedAssets(malfunctionToDelete: any) {
    //we need to remove the malfunctionId from all the assets with a link

    const allAssets: any[] = ServerAssetHelper.findAssetsWithMalfunction(malfunctionToDelete, this.serverAsset);

    allAssets.forEach(asset => {
      ServerAssetHelper.removeMalfunctionFromAsset(malfunctionToDelete, asset);
      ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
      ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);
    });

  }

  showDialogToEdit(data: any): any {
    const malfunction = ServerAssetHelper.findNodeByIdentifier(data.id, this.serverAsset);
    this.store.dispatch(editMalfunctionOpen(malfunction));
  }
}
