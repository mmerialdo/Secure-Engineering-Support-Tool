/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="edit-activity.component.ts"
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
import {fetchActivityForEdit, selectEditActivity} from '../../../../../../shared/store/reducers/assets.reducer';
import {Observable} from 'rxjs';
import {
  editActivity,
  editActivityClose,
  editAssetClose,
  storeActivity,
  storeAsset
} from '../../../../../../shared/store/actions/assets.actions';
import {ServerAssetHelper} from '../../../../server-asset.helper';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-edit-activity',
  templateUrl: './edit-activity.component.html',
  styleUrls: ['./edit-activity.component.css']
})
export class EditActivityComponent implements OnInit {
  public displayEditActivity$: Observable<boolean>;
  public displayEditActivity = false;
  editActivityForm: FormGroup;
  public name: string;
  public duplicateName = false;
  public description: string;
  public goal: string;
  public ownerListActivity = [];
  public isNew = true;
  public activityToEdit: any;
  @Input()
  public serverAsset: any;
  constructor(private formBuilder: FormBuilder, private store: Store<any>) {
    this.editActivityForm = this.formBuilder.group({
      'nameEA': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEA': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEA': ['', null],
      'goalEA': ['', [Validators.maxLength(100)]]

    });
    this.ownerListActivity.push({'name': undefined, 'role': undefined, 'surname': undefined});

    if (sessionStorage.getItem('sysproject') && JSON.parse(sessionStorage.getItem('sysproject')).participants) {
      const ownersList = JSON.parse(sessionStorage.getItem('sysproject')).participants;
      for (const i in ownersList) {
        this.ownerListActivity.push({
          'name': ownersList[i].name,
          'role': ownersList[i].role,
          'surname': ownersList[i].surname
        });
      }
    }
  }
  ngOnInit() {
    this.displayEditActivity$ = this.store.pipe(select(selectEditActivity));
    this.displayEditActivity$.subscribe(display => {
      this.editActivityForm.reset();
      this.displayEditActivity = display;
    });
    this.store.pipe(select(fetchActivityForEdit)).subscribe(activity => {
      if (activity) {
        this.isNew = false;
        this.activityToEdit = activity;
        this.patchActivityToForm(activity);
      }
    });

  }

  checkName(event) {
    //if (this.isNew && this.serverAsset.nodes.some(i => i.name != null && i.name === event)) {

    if(this.isNew){
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
    else{
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event && i.identifier != this.activityToEdit.identifier)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
  }

  closeEditActivityForm() {
    this.editActivityForm.reset();
    this.duplicateName = false;
    this.displayEditActivity = false;

  }


  cancel(): void {
    this.store.dispatch(editActivityClose());
    this.editActivityForm.reset();
    this.isNew = true;
    this.displayEditActivity = false;
  }

  saveOrEditActivity() {
    if (this.isNew) {
      const activity = ServerAssetHelper.createActivityNode(
        this.editActivityForm.value.nameEA,
        this.editActivityForm.value.descriptionEA,
        this.editActivityForm.value.ownerEA,
        this.editActivityForm.value.goalEA,
      );
      this.store.dispatch(storeActivity(activity));
    }
    else {
      const activityToEdit = ServerAssetHelper.findNodeByIdentifier(this.activityToEdit.identifier, this.serverAsset);
      activityToEdit.name = this.editActivityForm.value.nameEA;
      activityToEdit.description = this.editActivityForm.value.descriptionEA;
      activityToEdit.systemParticipantOwnerId = this.editActivityForm.value.ownerEA;
      activityToEdit.goal = this.editActivityForm.value.goalEA;
      this.store.dispatch(editActivity(activityToEdit));
    }
    this.cancel();
  }

  private patchActivityToForm(activity: any): void {
    if (activity) {
      this.editActivityForm.setValue({
        nameEA: activity.name,
        descriptionEA: activity.description,
        ownerEA: activity.systemParticipantOwnerId,
        goalEA: activity.goal
      });
    }
  }
}
