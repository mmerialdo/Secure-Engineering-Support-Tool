/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="abstract-tab-view.component.ts"
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
  editAssetOpen,
  newAssetOpen, refreshTablesStart, storeServerAsset
} from '../../../../../shared/store/actions/assets.actions';
import {select, Store} from '@ngrx/store';
import {fetchAsset, selectRefresh} from '../../../../../shared/store/reducers/assets.reducer';
import {take} from 'rxjs/operators';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-assets-tab-view',
  templateUrl: './assets-tab-view.component.html',
  styleUrls: ['./assets-tab-view.component.css']
})
export class AssetsTabViewComponent extends AbstractTabViewComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  public organization: any;
  @Input()
  public serverAsset: any;
  @Input() tabsStatus: boolean[];

  rowsNumber = '500px';

  public displayDialog: boolean;
  public newAsset: any;
  public assetToEdit;
  selected: any[];

  constructor(public store: Store<any>, public messageService: MessageService) {
    super('assets', 'Assets', store, messageService);
  }

  ngOnInit() {
    this.store.pipe(select(selectRefresh)).subscribe(refresh => {
      this.resetTable();
      const assetsMap = ServerAssetHelper.retrieveAssetsMap(this.serverAsset);
      const allActivities = ServerAssetHelper.getAllBusinessActivities(this.serverAsset);
      const assets: any[] = [];
      for (const key of assetsMap.keys()) {
        assets.push(key);
      }

      this.setColumns(allActivities);
      this.setRows(assets, assetsMap);
    });
    this.store.pipe(select(fetchAsset)).subscribe(newAsset => {
      if (newAsset) {
        const predict = r => r.id === newAsset.identifier;
        if (this.dataRows.some(predict)) {
          const model = this.dataRows.find(predict);
          this.model = model;
          this.model.assets = newAsset.name;
          this.model.id = newAsset.identifier;
          this.dataRows[this.dataRows.indexOf(model)] = this.model;
          this.store.dispatch(storeServerAsset(this.serverAsset));
          this.store.dispatch(refreshTablesStart());
        } else {
          this.newAsset = newAsset;
          this.model.assets = this.newAsset.name;
          this.model.id = this.newAsset.identifier;
          this.save();
        }
        this.validate(this.serverAsset);
      }
    });

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

      if (openTabs.counter > 1 || openTabs.counter === 1 && openTabs.index !== 3) {
        this.rowsNumber = '200px';
      } else {
        this.rowsNumber = '500px';
      }
    }
  }

  save() {
    const dataRows = [...this.dataRows];
    dataRows.push(this.model);
    this.saveAssetToModel();
    this.dataRows = dataRows;
    this.displayDialog = false;
    this.store.dispatch(refreshTablesStart());
  }

  showDialogToAdd() {
    this.resetModel();
    this.store.dispatch(newAssetOpen());
  }

  resetModel(): void {
    this.model = this.convertToObject(this.cols);
    this.model.assets = '';
  }

  showDialogToEdit(data: any): any {
    this.assetToEdit = ServerAssetHelper.findNodeByName(data.assets, this.serverAsset);
    this.store.dispatch(editAssetOpen(this.assetToEdit));
  }

  editName(event) {
    if (typeof (event.field) === 'string') {
      const updatedNodes = this.serverAsset.nodes;
      const found = updatedNodes.find(n => n.identifier === event.data.id);

      //We check if the name is already existing...
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event.data.assets && i.identifier != found.identifier)) {
        event.data.assets = found.name;

        return;
      }

      found.name = event.data.assets;

      this.store.dispatch(storeServerAsset(this.serverAsset));
      this.store.dispatch(refreshTablesStart());
    }
  }

  edit(event: any, rowIndex: any, column: any, changedValue: any) {
    this.disableCheckBox();
    this.editModel(rowIndex, column, changedValue);
  }

  editModel(i: number, column: any, changedValue: any): void {
    const edited = this.dataRows[i];
    const node = ServerAssetHelper.findNodeByName(edited.assets, this.serverAsset);
    if (changedValue) {
      this.addAssetRelationWithActivity(node, column);
    } else {
      this.removeAssetRelationWithActivity(node, column);
    }
  }

  removeAssetRelationWithActivity(node: any, column: any): void {
    const businessActivity = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    let edge = this.serverAsset.edges.filter(e => e.source === businessActivity.identifier && e.target === node.identifier);

    if (!edge || edge === null) {
      edge = this.serverAsset.edges.filter(e => e.target === businessActivity.identifier && e.source === node.identifier);
    }

    if (edge) {
      edge.forEach(edgeElement => {
        this.removeEdgeAssetRelationWithActivity(node, edgeElement);
      });
    }
  }

  removeEdgeAssetRelationWithActivity(node: any, edge: any): void {

    ServerAssetHelper.removeEdgeById(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromParents(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromChildren(edge.identifier, this.serverAsset);

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

    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  //TODO add severity of the edges - il coglione sicuro se ne e' scordato
  addAssetRelationWithActivity(node: any, column: any): void {
    const businessActivity = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    const edge = ServerAssetHelper.createEdgeBetweenChildAndParent(node, businessActivity);
    this.serverAsset.edges.push(edge);
    businessActivity.children.push(edge.identifier);
    node.parents.push(edge.identifier);
    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  saveAssetToModel(): void {
    const node = this.newAsset;

    this.serverAsset.nodes.push(node);
    this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  deleteAssets(): void {
    if (this.selected.length > 0) {
      this.selected.forEach(selected => {
        this.dataRows = this.dataRows.filter(dr => dr !== selected);
        const assetToDelete = ServerAssetHelper.findNodeByIdentifier(selected.id, this.serverAsset);

        this.updateAssetParentsAndEdges(assetToDelete);
        this.updateAssetChildrenAndEdges(assetToDelete);
        const deleted = ServerAssetHelper.removeNodeById(assetToDelete.identifier, this.serverAsset);
      });
    } else {
      return;
    }

    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.selected = [];
    this.validate(this.serverAsset);
  }

  updateAssetParentsAndEdges(assetToDelete: any) {
    //We collect all parents edges
    const edgesToDelete = assetToDelete.parents;

    //For each of the parents edges, we delete it from the edges collection and from the children array in all the parents of the Asset
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromParents(edge, this.serverAsset);
    });
  }

  updateAssetChildrenAndEdges(assetToDelete: any) {
    //We collect all children edges
    const edgesToDelete = assetToDelete.children;

    //For each of the children edges, we delete it from the edges collection and from the parents array in all the children of the Asset
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromChildren(edge, this.serverAsset);
    });
  }

  ngOnDestroy(): void {
    this.clear();
  }

  assetColor(value): string {

    const asset = ServerAssetHelper.findNodeByIdentifier(value.id, this.serverAsset);

    const maxImpact = asset.securityImpacts.reduce((acc, curr) => {

      return ServerAssetHelper.maxImpactValue(curr.impact, acc);
    }, null);

    return this.impactColor(maxImpact);
  }

  private impactColor(value): string {

    if (value === null) {
      return '#ffffff00';
    }

    if (value === 'CRITICAL') {
      return '#ff0000';
    }

    if (value === 'HIGH') {
      return '#ffa500';
    }

    if (value === 'MEDIUM') {
      return '#ffdb00';
    }
    return '#00ff00';
  }
}
