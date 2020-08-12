/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="edit-asset.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SelectItem} from 'primeng/primeng';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {
  fetchAssetForEdit,
  selectEditAsset
} from '../../../../../../shared/store/reducers/assets.reducer';
import {
  editAsset,
  editAssetClose,
  storeAsset, storeServerAsset
} from '../../../../../../shared/store/actions/assets.actions';
import {ServerAssetHelper} from '../../../../server-asset.helper';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-edit-asset',
  templateUrl: './edit-asset.component.html',
  styleUrls: ['./edit-asset.component.css']
})
export class EditAssetComponent implements OnInit {

  public displayEditAsset$: Observable<boolean>;
  public displayEditAsset = false;
  editAssetForm: FormGroup;
  name: string;
  public ownerListAsset = [];
  public duplicateName = false;

  public assetDescription;
  public associatedMalf: SelectItem[];
  public selectedMalfunction;
  public originalSelectedMalfunction;

  public selectedCategoryData = '';
  public selectedCategoryCompli = '';
  public selectedCategoryService = '';

  public primaryAssetConf = [];
  public primaryAssetInt = [];
  public primaryAssetAva = [];
  public selectedSecondary = '';

  public assetConfidentiality;
  public assetIntegrity;
  public assetEfficiency;
  public assetAvailability;

  public showAssetPrimaryDataTypeInfo = false;
  public showAssetPrimaryServicesTypeInfo = false;
  public showAssetPrimaryManagementTypeInfo = false;
  public showAssetSecondaryTypeInfo = false;
  public ownersList;

  public isNew = true;
  public assetToEdit: any;
  @Input()
  public serverAsset: any;

  public primaryAssetCategory = [];
  public owner: any;
  public secondaryAssetCategory;
  public showMalfunction;

  constructor(private formBuilder: FormBuilder, private store: Store<any>) {
    this.editAssetForm = this.formBuilder.group({
      'assetName': ['', [Validators.required, Validators.maxLength(100)]],
      'desAsset': ['', [Validators.required, Validators.maxLength(800)]],
      'ownAsset': ['', null]
    });
    this.ownerListAsset.push({'name': undefined, 'role': undefined, 'surname': undefined});

    if (sessionStorage.getItem('sysproject') && JSON.parse(sessionStorage.getItem('sysproject')).participants) {
      this.ownersList = JSON.parse(sessionStorage.getItem('sysproject')).participants;
      for (const i in this.ownersList) {
        this.ownerListAsset.push({'name': this.ownersList[i].name, 'role': this.ownersList[i].role, 'surname': this.ownersList[i].surname});
      }
    }
    this.primaryAssetConf = [];
    this.primaryAssetConf.push('');
    this.primaryAssetConf.push('Data_DataFile_Database');
    this.primaryAssetConf.push('Data_Shared_Office_File');
    this.primaryAssetConf.push('Data_Personal_Office_File');
    this.primaryAssetConf.push('Data_Physical_File');
    this.primaryAssetConf.push('Data_Exchanged_Message');
    this.primaryAssetConf.push('Data_Digital_Mail');
    this.primaryAssetConf.push('Data_Physical_Mail');
    this.primaryAssetConf.push('Data_Physical_Archive');
    this.primaryAssetConf.push('Data_IT_Archive');
    this.primaryAssetConf.push('Data_Published_Data');

    this.primaryAssetInt = [];
    this.primaryAssetInt.push('');
    this.primaryAssetInt.push('Service_Extended_Network_Service');
    this.primaryAssetInt.push('Service_Local_Network_Service');
    this.primaryAssetInt.push('Service_Application_Service');
    this.primaryAssetInt.push('Service_Shared_Service');
    this.primaryAssetInt.push('Service_User_Hardware');
    this.primaryAssetInt.push('Service_Common_Service');
    this.primaryAssetInt.push('Service_User_Workspace');
    this.primaryAssetInt.push('Service_Telecommunication_Service');
    this.primaryAssetInt.push('Service_Web_editing_Service');

    this.primaryAssetAva = [];
    this.primaryAssetAva.push('');
    this.primaryAssetAva.push('Compliance_Policy_Personal_Information_Protection');
    this.primaryAssetAva.push('Compliance_Policy_Financial_Communication');
    this.primaryAssetAva.push('Compliance_Policy_Digital_Accounting_Control');
    this.primaryAssetAva.push('Compliance_Policy_Intellectual_Property');
    this.primaryAssetAva.push('Compliance_Policy_Protection_Of_Information_Systems');
    this.primaryAssetAva.push('Compliance_Policy_People_And_Environment_Safety');
    //////
    this.associatedMalf = [];
  }

  ngOnInit() {
    this.displayEditAsset$ = this.store.pipe(select(selectEditAsset));
    this.displayEditAsset$.subscribe(display => {
      this.resetForm();
      this.displayEditAsset = display;
    });
    this.store.pipe(select(fetchAssetForEdit)).subscribe(asset => {
      if (asset) {
        this.isNew = false;
        this.assetToEdit = asset;
        this.patchAssetToForm(asset);
        this.fetchMalfunctions(asset);
      }
    });
  }

  saveOrEditAsset() {
    this.primaryAssetCategory = [];
    if (this.selectedCategoryData !== '') {
      this.primaryAssetCategory.push(this.selectedCategoryData);
      this.selectedCategoryData = '';
    } else if (this.selectedCategoryService !== '') {
      this.primaryAssetCategory.push(this.selectedCategoryService);
      this.selectedCategoryService = '';
    } else if (this.selectedCategoryCompli !== '') {
      this.primaryAssetCategory.push(this.selectedCategoryCompli);
      this.selectedCategoryCompli = '';
    }
    this.name = this.editAssetForm.value.assetName;
    this.owner = this.editAssetForm.value.ownAsset;

    this.secondaryAssetCategory = this.selectedSecondary;
    this.showMalfunction = [];

    if (this.isNew) {
      const asset = ServerAssetHelper.createAssetNode(
        this.editAssetForm.value.assetName,
        this.editAssetForm.value.desAsset,
        this.editAssetForm.value.ownAsset
      );
      asset.category = this.selectedSecondary;
      asset.primaryCategories = [...this.primaryAssetCategory];
      this.store.dispatch(storeAsset(asset));
    } else {
      const assetToEdit = ServerAssetHelper.findNodeByIdentifier(this.assetToEdit.identifier, this.serverAsset);
      assetToEdit.name = this.editAssetForm.value.assetName;
      assetToEdit.description = this.editAssetForm.value.desAsset;
      assetToEdit.systemParticipantOwnerId = this.editAssetForm.value.ownAsset;
      assetToEdit.category = this.selectedSecondary;
      assetToEdit.primaryCategories = [...this.primaryAssetCategory];
      assetToEdit.malfunctionsIds = JSON.parse(JSON.stringify(this.selectedMalfunction));
      this.originalSelectedMalfunction = this.selectedMalfunction;
      this.store.dispatch(editAsset(assetToEdit));
    }
    this.cancel();
  }

  checkName(event) {
    if (this.isNew) {
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event)) {
        this.duplicateName = true;
      } else {
        this.duplicateName = false;
      }
    } else {
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event && i.identifier != this.assetToEdit.identifier)) {
        this.duplicateName = true;
      } else {
        this.duplicateName = false;
      }
    }
  }

  infoDataAsset() {
    this.showAssetPrimaryDataTypeInfo = true;
  }

  infoServicesAsset() {
    this.showAssetPrimaryServicesTypeInfo = true;
  }

  infoManagementAsset() {
    this.showAssetPrimaryManagementTypeInfo = true;
  }

  infoSecondaryAsset() {
    this.showAssetSecondaryTypeInfo = true;
  }

  closeEditAssetForm() {
    //Since the change in the selection of Malfunctions triggers an automatic save of the model, if we Cancel or Close the form we don't want to save
    //the changes in Malfunctions. Hence we restore the original state
    if (!this.isNew) {
      const asset = ServerAssetHelper.findNodeByIdentifier(this.assetToEdit.identifier, this.serverAsset);
      asset.malfunctionsIds = JSON.parse(JSON.stringify(this.originalSelectedMalfunction));
      ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
      ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);
      this.store.dispatch(storeServerAsset(this.serverAsset));
    }

    this.resetForm();
    this.duplicateName = false;
    this.displayEditAsset = false;
  }

  cancel(): void {
    //Since the change in the selection of Malfunctions triggers an automatic save of the model, if we Cancel or Close the form we don't want to save
    //the changes in Malfunctions. Hence we restore the original state
    if (!this.isNew) {
      const asset = ServerAssetHelper.findNodeByIdentifier(this.assetToEdit.identifier, this.serverAsset);
      asset.malfunctionsIds = JSON.parse(JSON.stringify(this.originalSelectedMalfunction));
      ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
      ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);
      this.store.dispatch(storeServerAsset(this.serverAsset));
    }

    this.store.dispatch(editAssetClose());
    this.resetForm();
    this.isNew = true;
    this.displayEditAsset = false;
    this.associatedMalf = [];
  }

  fetchMalfunctions(asset: any): void {
    const activities = [];
    const children = new Set<any>();
    asset.parents.forEach(parent => {
      activities.push(ServerAssetHelper.traverseFromChildrenToParent(asset, parent, this.serverAsset));
    });

    if (activities && activities.length > 0) {
      activities.forEach(a => {
        a.children.forEach(activityId => children.add(
          ServerAssetHelper.traverseFromParentToChildren(a, activityId, this.serverAsset)));
      });

      [...children].filter(c => (c.nodeType === 'Malfunction' && c.scales.length > 0)).forEach(child => {
        this.associatedMalf.push({label: child.name, value: child.identifier});
      });
    }
  }

  private patchAssetToForm(asset: any): void {
    if (asset) {
      this.selectedMalfunction = JSON.parse(JSON.stringify(asset.malfunctionsIds));
      this.originalSelectedMalfunction = JSON.parse(JSON.stringify(asset.malfunctionsIds));
      this.selectedSecondary = asset.category;
      this.primaryAssetCategory = asset.primaryCategories;
      if (this.primaryAssetCategory.length > 0) {
        if (this.primaryAssetCategory[0].indexOf('Data') !== -1) {
          this.selectedCategoryData = this.primaryAssetCategory[0];
        } else if (this.primaryAssetCategory[0].indexOf('Service') !== -1) {
          this.selectedCategoryService = this.primaryAssetCategory[0];
        } else if (this.primaryAssetCategory[0].indexOf('Compliance') !== -1) {
          this.selectedCategoryCompli = this.primaryAssetCategory[0];
        }
      }
      this.showSecurityImpacts(asset);
      this.editAssetForm.setValue({
        desAsset: asset.description,
        assetName: asset.name,
        ownAsset: asset.systemParticipantOwnerId
      });
    }
  }

  private showSecurityImpacts(asset: any) {
    this.assetAvailability = null;
    this.assetConfidentiality = null;
    this.assetIntegrity = null;
    this.assetEfficiency = null;

    for (const securityImpact in asset.securityImpacts) {
      if (asset.securityImpacts[securityImpact].scope === 'Availability') {
        this.assetAvailability = asset.securityImpacts[securityImpact].impact;
      }
      if (asset.securityImpacts[securityImpact].scope === 'Integrity') {
        this.assetIntegrity = asset.securityImpacts[securityImpact].impact;
      }
      if (asset.securityImpacts[securityImpact].scope === 'Confidentiality') {
        this.assetConfidentiality = asset.securityImpacts[securityImpact].impact;
      }
      if (asset.securityImpacts[securityImpact].scope === 'Efficiency') {
        this.assetEfficiency = asset.securityImpacts[securityImpact].impact;
      }
    }
  }

  editMalfunction(event) {
    const asset = ServerAssetHelper.findNodeByIdentifier(this.assetToEdit.identifier, this.serverAsset);

    asset.malfunctionsIds = JSON.parse(JSON.stringify(this.selectedMalfunction));

    ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
    ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);

    this.showSecurityImpacts(asset);

    this.store.dispatch(storeServerAsset(this.serverAsset));
  }

  private resetForm() {
    this.editAssetForm.reset();
    this.selectedCategoryCompli = '';
    this.selectedCategoryData = '';
    this.selectedCategoryService = '';
    this.selectedSecondary = '';
    this.assetConfidentiality = null;
    this.assetIntegrity = null;
    this.assetAvailability = null;
    this.assetEfficiency = null;
  }
}
