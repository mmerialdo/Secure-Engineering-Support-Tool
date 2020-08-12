/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="edit-malfunction.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {fetchMalfunctionForEdit, selectEditMalfunction} from '../../../../../../shared/store/reducers/assets.reducer';
import {editMalfunction, editMalfunctionClose, storeMalfunction} from '../../../../../../shared/store/actions/assets.actions';
import {ServerAssetHelper} from '../../../../server-asset.helper';
import {take} from 'rxjs/operators';


@Component({
  selector: 'app-edit-malfunction',
  templateUrl: './edit-malfunction.component.html',
  styleUrls: ['./edit-malfunction.component.css'],
})
export class EditMalfunctionComponent implements OnInit {

  public malfunctionForm: FormGroup;
  public displayEditMalfunction: boolean;
  public displayEditMalfunction$: Observable<boolean>;
  public isNew = true;
  public malfunctionToEdit: any;
  @Input()
  public serverAsset: any;
  public name: string;
  public duplicateName: boolean;
  public malfunctionConfidentiality: boolean;
  public malfunctionAvailability: boolean;
  public malfunctionIntegrity: boolean;
  public malfunctionEfficency: boolean;
  public functionalConsequence;
  public functionalDescription;
  public functionalType;
  public technicalConsequence;
  public technicalDescription;
  public low;
  public medium;
  public high;
  public critical;
  public showMalfunctionTypeInfo = false;
  public showSeriousnessInfo = false;

  constructor(private formBuilder: FormBuilder,
              private store: Store<any>) {
    this.malfunctionForm = this.formBuilder.group({
      'nameEM': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionET': ['', [Validators.maxLength(500)]],
      'descriptionEF': ['', [Validators.maxLength(500)]],
      'consE': ['', [Validators.maxLength(500)]],
      'consEF': ['', [Validators.maxLength(500)]],
      'lowE': ['', [Validators.maxLength(500)]],
      'typeEF': ['', Validators.required],
      'mediumE': ['', [Validators.maxLength(500)]],
      'highE': ['', [Validators.maxLength(500)]],
      'criticalE': ['', [Validators.maxLength(500)]],
      'malfunctionConfidentiality': [Validators.required],
      'malfunctionAvailability': [Validators.required],
      'malfunctionIntegrity': [Validators.required],
      'malfunctionEfficency': [Validators.required]
    });
  }

  ngOnInit() {
    this.displayEditMalfunction$ = this.store.pipe(select(selectEditMalfunction));
    this.displayEditMalfunction$.subscribe(display => {
      this.malfunctionForm.reset();
      this.displayEditMalfunction = display;
    });
    this.store.pipe(select(fetchMalfunctionForEdit)).subscribe(mal => {
      if(mal){
        this.isNew = false;
        this.malfunctionToEdit = mal;
        this.patchMalfunctionToForm(mal);
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
      if (this.serverAsset.nodes.some(i => i.name != null && i.name === event && i.identifier != this.malfunctionToEdit.identifier)) {
        this.duplicateName = true;
      }
      else {
        this.duplicateName = false;
      }
    }
  }

  patchMalfunctionToForm(malfunction: any): void {
    if (malfunction) {
      const low = malfunction.scales.find(scale => scale.seriousness === 'LOW');
      const medium = malfunction.scales.find(scale => scale.seriousness === 'MEDIUM');
      const high = malfunction.scales.find(scale => scale.seriousness === 'HIGH');
      const critical = malfunction.scales.find(scale => scale.seriousness === 'CRITICAL');
      this.malfunctionConfidentiality = malfunction.technicalTypes.some(type => type === 'Confidentiality_Loss');
      this.malfunctionAvailability = malfunction.technicalTypes.some(type => type === 'Availability_Loss');
      this.malfunctionIntegrity = malfunction.technicalTypes.some(type => type === 'Integrity_Loss');
      this.malfunctionEfficency = malfunction.technicalTypes.some(type => type === 'Efficiency_Loss');

      this.malfunctionForm.setValue({
        nameEM: malfunction.name,
        descriptionET: malfunction.technicalDescription,
        descriptionEF: malfunction.functionalDescription,
        consE: malfunction.technicalConsequence,
        consEF: malfunction.functionalConsequence,
        typeEF: malfunction.functionalType,
        lowE: low ? low.description : '',
        mediumE: medium ? medium.description : '',
        highE: high ? high.description : '',
        criticalE: critical ? critical.description : '',
        malfunctionConfidentiality: this.malfunctionConfidentiality,
        malfunctionAvailability: this.malfunctionAvailability,
        malfunctionIntegrity: this.malfunctionIntegrity,
        malfunctionEfficency: this.malfunctionEfficency
      });
    }
  }

  cancel(): void {
    this.store.dispatch(editMalfunctionClose());
    this.reset();
    this.isNew = true;
  }

  infoMalfunctionType() {
    this.showMalfunctionTypeInfo = true;
  }

  infoSeriousness() {
    this.showSeriousnessInfo = true;

  }

  closeEditMalForm() {
    this.reset();
  }

  saveOrEditMalfunction(): void {
    debugger;
    const technicalTypes: string[] = [];
    if (this.malfunctionAvailability) {
      technicalTypes.push('Availability_Loss');
    }
    if (this.malfunctionConfidentiality) {
      technicalTypes.push('Confidentiality_Loss');
    }
    if (this.malfunctionEfficency) {
      technicalTypes.push('Efficiency_Loss');
    }
    if (this.malfunctionIntegrity) {
      technicalTypes.push('Integrity_Loss');
    }
    const scales: any[] = [];
    if ((this.malfunctionForm.value.criticalE != null) && (this.malfunctionForm.value.criticalE !== '')) {
      scales.push({'seriousness': 'CRITICAL', 'description': this.malfunctionForm.value.criticalE});
    }
    if ((this.malfunctionForm.value.highE != null) && (this.malfunctionForm.value.highE !== '')) {
      scales.push({'seriousness': 'HIGH', 'description': this.malfunctionForm.value.highE});
    }
    if ((this.malfunctionForm.value.mediumE != null) && (this.malfunctionForm.value.mediumE !== '')) {
      scales.push({'seriousness': 'MEDIUM', 'description': this.malfunctionForm.value.mediumE});
    }
    if ((this.malfunctionForm.value.lowE != null) && (this.malfunctionForm.value.lowE !== '')) {
      scales.push({'seriousness': 'LOW', 'description': this.malfunctionForm.value.lowE});
    }

    if (this.isNew) {
      const malfunction = ServerAssetHelper.createMalfunctionNode(
        this.malfunctionForm.value.nameEM,
        this.malfunctionForm.value.descriptionET,
        this.malfunctionForm.value.descriptionEF,
        this.malfunctionForm.value.consE,
        this.malfunctionForm.value.consEF,
        technicalTypes,
        this.malfunctionForm.value.typeEF,
        scales
      );
      this.store.dispatch(storeMalfunction(malfunction));
    }
    else {
      const malfunction = ServerAssetHelper.findNodeByIdentifier(this.malfunctionToEdit.identifier, this.serverAsset);
      malfunction.name = this.malfunctionForm.value.nameEM;
      malfunction.technicalDescription = this.malfunctionForm.value.descriptionET;
      malfunction.functionalDescription = this.malfunctionForm.value.descriptionEF;
      malfunction.technicalConsequence = this.malfunctionForm.value.consE;
      malfunction.functionalConsequence = this.malfunctionForm.value.consEF;
      malfunction.technicalTypes = technicalTypes;
      malfunction.technicalType = this.malfunctionForm.value.typeEF;
      malfunction.scales = scales;
      this.store.dispatch(editMalfunction(malfunction));

      //If the Malfunction changed, we need to update all related Assets and Edges
      const allAssets: any[] = ServerAssetHelper.findAssetsWithMalfunction(malfunction, this.serverAsset);

      allAssets.forEach(asset => {
        ServerAssetHelper.associateImpactToAsset(asset, this.serverAsset);
        ServerAssetHelper.associateImpactToAssetEdges(asset, this.serverAsset);
      });

      this.store.dispatch(storeMalfunction(malfunction));
    }
    this.cancel();
    // this.displayEditMalfunction = false;
  }

  reset(): void {
    this.malfunctionForm.reset();
    this.malfunctionAvailability = false;
    this.malfunctionConfidentiality = false;
    this.malfunctionEfficency = false;
    this.malfunctionIntegrity = false;
    this.duplicateName = false;
    this.displayEditMalfunction = false;
  }
}

