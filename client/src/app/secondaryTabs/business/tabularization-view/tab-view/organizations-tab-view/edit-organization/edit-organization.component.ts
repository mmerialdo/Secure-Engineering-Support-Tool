/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="edit-organization.component.ts"
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
import {select, Store} from '@ngrx/store';
import {fetchOrganizationForEdit, selectEditOrganization} from '../../../../../../shared/store/reducers/assets.reducer';
import {Observable} from 'rxjs';
import {
  editOrganizationClose,
  editOrganizationOpen,
  storeOrganization,
  editOrganization
} from '../../../../../../shared/store/actions/assets.actions';
import {ServerAssetHelper} from '../../../../server-asset.helper';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-edit-organization',
  templateUrl: './edit-organization.component.html',
  styleUrls: ['./edit-organization.component.css']
})
export class EditOrganizationComponent implements OnInit {
  public displayEditOrganization$: Observable<boolean>;
  public displayEditOrganization = false;
  editOrganizationForm: FormGroup;
  public name: string;
  public duplicateName = false;
  public description: string;
  public goal: string;
  public ownerListOrganization = [];
  public isNew = true;
  public organizationToEdit: any;
  @Input()
  public serverAsset: any;
  constructor(private formBuilder: FormBuilder, private store: Store<any>) {
    this.editOrganizationForm = this.formBuilder.group({
      'nameEA': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEA': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEA': ['', null],
      'goalEA': ['', [Validators.maxLength(100)]]

    });
    this.ownerListOrganization.push({'name': undefined, 'role': undefined, 'surname': undefined});

    if (sessionStorage.getItem('sysproject') && JSON.parse(sessionStorage.getItem('sysproject')).participants) {
      const ownersList = JSON.parse(sessionStorage.getItem('sysproject')).participants;
      for (const i in ownersList) {
        this.ownerListOrganization.push({
          'name': ownersList[i].name,
          'role': ownersList[i].role,
          'surname': ownersList[i].surname
        });
      }
    }
  }
  ngOnInit() {
    this.displayEditOrganization$ = this.store.pipe(select(selectEditOrganization));
    this.displayEditOrganization$.subscribe(display => {
      this.editOrganizationForm.reset();
      this.displayEditOrganization = display;
    });
    this.store.pipe(select(fetchOrganizationForEdit)).subscribe(organization => {
       if (organization) {
        this.isNew = false;
        this.organizationToEdit = organization;
        this.patchOrganizationToForm(organization);
      }
    });

  }

  checkName(event) {

    if(this.isNew){
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
    else{
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event && i.identifier != this.organizationToEdit.identifier)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
  }

  closeEditOrganizationForm() {
    this.editOrganizationForm.reset();
    this.duplicateName = false;
    this.displayEditOrganization = false;

  }


  cancel(): void {
    this.store.dispatch(editOrganizationClose());
    this.editOrganizationForm.reset();
    this.isNew = true;
    this.displayEditOrganization = false;
  }

  saveOrEditOrganization() {
    if (this.isNew) {
      const activity = ServerAssetHelper.createOrganizationNode(
        this.editOrganizationForm.value.nameEA,
        this.editOrganizationForm.value.descriptionEA,
        this.editOrganizationForm.value.ownerEA,
        this.editOrganizationForm.value.goalEA,
      );
      this.store.dispatch(storeOrganization(activity));
    }
    else {
      const organizationToEdit = ServerAssetHelper.findNodeByIdentifier(this.organizationToEdit.identifier, this.serverAsset);
      organizationToEdit.name = this.editOrganizationForm.value.nameEA;
      organizationToEdit.description = this.editOrganizationForm.value.descriptionEA;
      organizationToEdit.systemParticipantOwnerId = this.editOrganizationForm.value.ownerEA;
      organizationToEdit.goal = this.editOrganizationForm.value.goalEA;
      this.store.dispatch(editOrganization(organizationToEdit));
    }
    this.cancel();
  }

  private patchOrganizationToForm(organization: any): void {
    if (organization) {
      this.editOrganizationForm.setValue({
        nameEA: organization.name,
        descriptionEA: organization.description,
        ownerEA: organization.systemParticipantOwnerId,
        goalEA: organization.goal
      });
    }
  }
}
