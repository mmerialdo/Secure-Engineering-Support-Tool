/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="organizations-tab-view.component.ts"
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
import {select, Store} from '@ngrx/store';
import {AbstractTabViewComponent} from '../abstract-tab-view.component';
import {
  editOrganizationOpen,
  newOrganizationOpen,
  refreshTablesStart,
  storeServerAsset
} from '../../../../../shared/store/actions/assets.actions';

import {fetchOrganization, selectRefresh} from '../../../../../shared/store/reducers/assets.reducer';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-organizations-tab-view',
  templateUrl: './organizations-tab-view.component.html',
  styleUrls: ['./organizations-tab-view.component.css']
})
export class OrganizationsTabViewComponent extends AbstractTabViewComponent implements OnInit, OnChanges, OnDestroy {

  @Input()
  public serverAsset: any;
  @Input() tabsStatus: boolean[];
  selected: any[];
  public newOrganization: any;

  rowsNumber = '500px';

  constructor(public store: Store<any>) {
    super('organizations', 'Organizations', store);
  }

  ngOnInit() {
    this.store.pipe(select(selectRefresh)).subscribe(() => {
      this.resetTable();
      const organizations = ServerAssetHelper.retrieveOrganizations(this.serverAsset);

      this.setSimpleRows(organizations);
    });
    this.store.pipe(select(fetchOrganization)).subscribe(newOrganization => {
      if (newOrganization) {
        const predict = r => r.id === newOrganization.identifier;
        if (this.dataRows.some(predict)) {
          const model = this.dataRows.find(predict);
          this.model = model;
          this.model.organizations = newOrganization.name;
          this.model.id = newOrganization.identifier;
          this.dataRows[this.dataRows.indexOf(model)] = this.model;
          this.store.dispatch(storeServerAsset(this.serverAsset));
          this.store.dispatch(refreshTablesStart());
        } else {

          this.newOrganization = newOrganization;
          this.model.organizations = this.newOrganization.name;
          this.model.id = this.newOrganization.identifier;
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

      if (openTabs.counter > 1 || openTabs.counter === 1 && openTabs.index !== 0) {
        this.rowsNumber = '200px';
      } else {
        this.rowsNumber = '500px';
      }
    }
  }

  showDialogToAdd() {
    this.resetModel();
    this.store.dispatch(newOrganizationOpen());
  }

  showDialogToEdit(data: any): any {
    const organizationToEdit = ServerAssetHelper.findNodeByIdentifier(data.id, this.serverAsset);
    this.store.dispatch(editOrganizationOpen(organizationToEdit));
  }

  resetModel(): void {
    this.model = this.convertToObject(this.cols);
    this.model.organizations = '';
  }

  save() {
    const dataRows = [...this.dataRows];
    dataRows.push(this.model);
    this.saveOrganizationToModel();
    this.dataRows = dataRows;
    this.store.dispatch(refreshTablesStart());
  }

  private saveOrganizationToModel() {
    const node = this.newOrganization;

    this.serverAsset.nodes.push(node);
    this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  editName(event) {
    if (typeof (event.field) === 'string') {
      const updatedNodes = this.serverAsset.nodes;
      const found = updatedNodes.find(n => n.identifier === event.data.id);

      //We check if the name is already existing...
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event.data.organizations && i.identifier != found.identifier)) {
        event.data.organizations = found.name;

        return;
      }

      found.name = event.data.organizations;

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
    const node = ServerAssetHelper.findNodeByName(edited.organizations, this.serverAsset);
  }

  deleteOrganizations(): void {
    if (this.selected.length > 0) {
      this.selected.forEach(selected => {
        this.dataRows = this.dataRows.filter(dr => dr !== selected);
        const organizationToDelete = ServerAssetHelper.findNodeByIdentifier(selected.id, this.serverAsset);

        this.updateOrganizationChildrenAndEdges(organizationToDelete);

        const deleted = ServerAssetHelper.removeNodeById(organizationToDelete.identifier, this.serverAsset);
      });
    } else {
      return;
    }

    this.store.dispatch(storeServerAsset(this.serverAsset));
    this.store.dispatch(refreshTablesStart());
    this.selected = [];
    this.validate(this.serverAsset);
  }

  updateOrganizationChildrenAndEdges(organizationToDelete: any) {
    //We collect all children edges
    const edgesToDelete = organizationToDelete.children;

    //For each of the children edges, we delete it from the edges collection and from the parents array in all the children of the Organization
    edgesToDelete.forEach(edge => {
      ServerAssetHelper.removeEdgeById(edge, this.serverAsset);

      ServerAssetHelper.removeEdgeFromChildren(edge, this.serverAsset);
    });
  }

  ngOnDestroy(): void {
    this.clear();
  }
}
