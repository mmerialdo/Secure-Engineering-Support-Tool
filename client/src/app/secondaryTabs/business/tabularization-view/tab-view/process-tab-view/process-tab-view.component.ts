/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="process-tab-view.component.ts"
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
import {AbstractTabViewComponent} from '../abstract-tab-view.component';
import {select, Store} from '@ngrx/store';
import {ServerAssetHelper} from '../../../server-asset.helper';
import {
  editProcessOpen,
  newProcessOpen,
  refreshTablesStart,
  storeServerAsset
} from '../../../../../shared/store/actions/assets.actions';
import {fetchProcess, selectRefresh} from '../../../../../shared/store/reducers/assets.reducer';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-process-tab-view',
  templateUrl: './process-tab-view.component.html',
  styleUrls: ['./process-tab-view.component.css']
})
export class ProcessTabViewComponent extends AbstractTabViewComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  public organization: any;
  @Input()
  public serverAsset: any;
  @Input() tabsStatus: boolean[];

  rowsNumber = '500px';
  selected: any[];
  public newProcess: any;

  constructor(public store: Store<any>) {
    super('processes', 'Processes', store);
  }

  ngOnInit() {
    this.store.pipe(select(selectRefresh)).subscribe(() => {
      this.resetTable();
      const businessProcessesMap = ServerAssetHelper.retrieveBusinessProcessesMap(this.serverAsset);
      const processes: any[] = [];
      for (const key of businessProcessesMap.keys()) {
        processes.push(key);
      }
      this.setColumns(this.serverAsset.nodes.filter(e => e.nodeType === 'Organization'));
      this.setRows(processes, businessProcessesMap);

    });
    this.store.pipe(select(fetchProcess)).subscribe(newPr => {
      if (newPr) {
        const predict = r => r.id === newPr.identifier;
        if (this.dataRows.some(predict)) {
          const model = this.dataRows.find(predict);
          this.model = model;
          this.model.processes = newPr.name;
          this.model.id = newPr.identifier;
          this.dataRows[this.dataRows.indexOf(model)] = this.model;
        this.store.dispatch(storeServerAsset(this.serverAsset));
          this.store.dispatch(refreshTablesStart());
        }
        else {
          this.newProcess = newPr;
          this.model.processes = this.newProcess.name;
          this.model.id = this.newProcess.identifier;
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

      if (openTabs.counter > 1 || openTabs.counter === 1 && openTabs.index !== 1) {
        this.rowsNumber = '200px';
      } else {
        this.rowsNumber = '500px';
      }
    }
  }

  showDialogToAdd() {
    this.resetModel();
    this.store.dispatch(newProcessOpen());
  }

  showDialogToEdit(data: any): any {
    const processToEdit = ServerAssetHelper.findNodeByIdentifier(data.id, this.serverAsset);
    this.store.dispatch(editProcessOpen(processToEdit));
  }

  resetModel(): void {
    this.model = this.convertToObject(this.cols);
    this.model.processes = '';
  }

  save() {
    const dataRows = [...this.dataRows];
    dataRows.push(this.model);
    this.saveProcessToModel();
    this.dataRows = dataRows;
    this.store.dispatch(refreshTablesStart());
  }

  private saveProcessToModel() {
    const node = this.newProcess;

    this.serverAsset.nodes.push(node);
  this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  editName(event) {
    if (typeof(event.field) === 'string') {
      const updatedNodes = this.serverAsset.nodes;
      const found = updatedNodes.find(n => n.identifier === event.data.id);

      //We check if the name is already existing...
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event.data.processes && i.identifier != found.identifier)) {
        event.data.processes = found.name;

        return;
      }

      found.name = event.data.processes;

    this.store.dispatch(storeServerAsset(this.serverAsset));
      this.store.dispatch(refreshTablesStart());
    }
  }

  edit(event: any, rowIndex: any, column: any, changedValue: any) {
    this.disableCheckBox();
    this.editModel(rowIndex, column, changedValue);
    // this.validate();
  }

  editModel (i: number, column: any, changedValue: any): void {
    const edited = this.dataRows[i];
    const node = ServerAssetHelper.findNodeByName(edited.processes, this.serverAsset);
    if (changedValue) {
      this.addProcessRelationWithOrganization(node, column);
    } else {
      this.removeProcessRelationWithOrganization(node, column);
    }
  }

  addProcessRelationWithOrganization(node: any, column: any): void {
    const organization = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    const updatedEdges = this.serverAsset.edges;
    const updatedNodes = this.serverAsset.nodes;
    const edge = ServerAssetHelper.createEdgeBetweenChildAndParent(node, organization);
    updatedEdges.push(edge);
    organization.children.push(edge.identifier);
    node.parents.push(edge.identifier);

  this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  removeProcessRelationWithOrganization(node: any, column: any): void {
    const organization = this.serverAsset.nodes.filter(n => n.identifier === column.field)[0];
    let edge = this.serverAsset.edges.filter(e => e.source === organization.identifier && e.target === node.identifier)[0];

    if(edge === null){
      edge = this.serverAsset.edges.filter(e => e.target === organization.identifier && e.source === node.identifier)[0];
    }

    ServerAssetHelper.removeEdgeById(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromParents(edge.identifier, this.serverAsset);
    ServerAssetHelper.removeEdgeFromChildren(edge.identifier, this.serverAsset);

  this.store.dispatch(storeServerAsset(this.serverAsset));
    this.validate(this.serverAsset);
  }

  deleteProcesses(): void {
    if (this.selected.length > 0) {
      this.selected.forEach(selected => {
        this.dataRows = this.dataRows.filter( dr => dr !== selected);
        const processToDelete = ServerAssetHelper.findNodeByIdentifier(selected.id, this.serverAsset);

        Object.keys(selected).filter(k => k !== 'processes').filter(k => k !== 'id').forEach(rowKey => {
          if (selected[rowKey]) {
            this.updateProcessParentsAndEdges(processToDelete);
            this.updateProcessChildrenAndEdges(processToDelete);
          }
        });
        const deleted = ServerAssetHelper.removeNodeById(processToDelete.identifier, this.serverAsset);
      });
    }
    else{
      return;
    }

  this.store.dispatch(storeServerAsset(this.serverAsset));
    this.store.dispatch(refreshTablesStart());
    this.selected = [];
    this.validate(this.serverAsset);
  }

  updateProcessParentsAndEdges(processToDelete: any) {
    //We collect all parents edges
    const edgesToDelete = processToDelete.parents;

    //For each of the parents edges, we delete it from the edges collection and from the children array in all the parents of the Business Process
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromParents(edge, this.serverAsset);
    });
  }

  updateProcessChildrenAndEdges(processToDelete: any) {
    //We collect all children edges
    const edgesToDelete = processToDelete.children;

    //For each of the children edges, we delete it from the edges collection and from the parents array in all the children of the Business Process
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromChildren(edge, this.serverAsset);
    });
  }

  ngOnDestroy(): void {
    this.clear();
  }
}
