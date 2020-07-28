/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="edit-process.component.ts"
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
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {select, Store} from '@ngrx/store';
import {editProcess, editProcessClose, storeProcess} from '../../../../../../shared/store/actions/assets.actions';
import {fetchProcessForEdit, selectEditProcess} from '../../../../../../shared/store/reducers/assets.reducer';
import {ServerAssetHelper} from '../../../../server-asset.helper';
import {take} from 'rxjs/operators';

@Component({
  selector: 'app-edit-process',
  templateUrl: './edit-process.component.html',
  styleUrls: ['./edit-process.component.css']
})
export class EditProcessComponent implements OnInit {

  @Input()
  public serverAsset: any;
  public displayEditProcess$: Observable<boolean>;
  public displayEditProcess = false;
  public name: string;
  businessEditElementForm: FormGroup;
  public duplicateName = false;
  public description: string;
  public goal: string;
  public ownerListProcess: any[] = [];
  public isNew = true;
  public processToEdit: any;

  constructor(private formBuilder: FormBuilder, private store: Store<any>) {
    this.businessEditElementForm = this.formBuilder.group({
      'nameEP': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEP': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEP': ['', null],
      'goalEP': ['', [Validators.maxLength(100)]],
      'typeEP': ['', Validators.required]
    });

    if (sessionStorage.getItem('sysproject') && JSON.parse(sessionStorage.getItem('sysproject')).participants){
      const ownersList = JSON.parse(sessionStorage.getItem('sysproject')).participants;
      this.ownerListProcess.push({'name': undefined, 'role': undefined, 'surname': undefined});
      for (const i in ownersList) {
        this.ownerListProcess.push({
          'name': ownersList[i].name,
          'role': ownersList[i].role,
          'surname': ownersList[i].surname
        });
      }
    }
  }

  ngOnInit() {
    this.displayEditProcess$ = this.store.pipe(select(selectEditProcess));
    this.displayEditProcess$.subscribe(display => {
      this.businessEditElementForm.reset();
      this.displayEditProcess = display;
    });
    this.store.pipe(select(fetchProcessForEdit)).subscribe(pro => {
      if (pro) {
        this.isNew = false;
        this.processToEdit = pro;
        this.patchProcessToForm(pro);
      }
    });
  }

  cancel(): void {
    this.store.dispatch(editProcessClose());
    this.businessEditElementForm.reset();
    this.isNew = true;
    this.displayEditProcess = false;
  }

  closeEditProForm(): void {
    this.businessEditElementForm.reset();
    this.duplicateName = false;
    this.displayEditProcess = false;
  }

  saveOrEditProcess() {
    if (this.isNew) {
      const businessProcessNode = ServerAssetHelper.createBusinessProcessNode(
        this.businessEditElementForm.value.nameEP,
        this.businessEditElementForm.value.descriptionEP,
        this.businessEditElementForm.value.goalEP,
        this.businessEditElementForm.value.ownerEP,
        this.businessEditElementForm.value.typeEP,
      );
      this.store.dispatch(storeProcess(businessProcessNode));
    }
    else {
      const processToEdit = ServerAssetHelper.findNodeByIdentifier(this.processToEdit.identifier, this.serverAsset);

      processToEdit.name = this.businessEditElementForm.value.nameEP;
      processToEdit.description = this.businessEditElementForm.value.descriptionEP;
      processToEdit.systemParticipantOwnerId = this.businessEditElementForm.value.ownerEP;
      processToEdit.goal = this.businessEditElementForm.value.goalEP;
      //processToEdit.type = this.businessEditElementForm.value.typeEP;
      processToEdit.businessType = this.businessEditElementForm.value.typeEP;
      this.store.dispatch(editProcess(processToEdit));
    }
    this.cancel();
  }

  private patchProcessToForm(process: any): void {
    if (process) {
      this.businessEditElementForm.setValue({
        nameEP: process.name,
        descriptionEP: process.description,
        ownerEP: process.systemParticipantOwnerId,
        goalEP: process.goal,
        typeEP: 'Business' //TODO change in the datamodel
        //typeEP: process.businessType
      });
    }
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
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event && i.identifier != this.processToEdit.identifier)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
  }
}
