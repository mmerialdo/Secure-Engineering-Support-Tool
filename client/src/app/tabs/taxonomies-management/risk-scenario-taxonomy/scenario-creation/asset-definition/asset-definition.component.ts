/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="asset-definition.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {
  ImpactTypeEnum,
  PrimaryAssetCategoryEnum, primarySecondaryCategoryMap,
  SecondaryAssetCategoryEnum, ThreatTaxonomy
} from "../../../taxonomiesManagement.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
  selector: 'app-asset-definition',
  templateUrl: './asset-definition.component.html'
})
export class AssetDefinitionComponent implements OnInit,OnDestroy {

  @Input() assetData: {assetType: {value: string}, supportingAsset: {value: string}, aice: string };
  @Output() assetDetails : EventEmitter<{check: boolean, value: {assetType: string, supportingAsset: string, aice: string }}> = new EventEmitter<{check: boolean, value: {assetType: string, supportingAsset: string, aice: string}}>();

  primaryCategories = Object.keys(PrimaryAssetCategoryEnum).map( key => PrimaryAssetCategoryEnum[key]).map((value) => ({
    value
  }));

  secondaryCategories = Object.keys(SecondaryAssetCategoryEnum).map( key => SecondaryAssetCategoryEnum[key]).map((value) => ({
    value
  }));

  impactTypes = Object.keys(ImpactTypeEnum).map( key => ImpactTypeEnum[key]);

  assetCategoriesMap = primarySecondaryCategoryMap;

  form: FormGroup;

  private componentDestroyed: Subject<Component> = new Subject<Component>();


  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.form = this.createForm();
    // it is needed for enable/disable the buttons in the parent view
    this.assetDetails.emit({check: this.form.valid, value: this.form.value});

    this.form.valueChanges.pipe(
      takeUntil(this.componentDestroyed)
    ).subscribe((value) => {
      this.assetDetails.emit({check: this.form.valid, value: this.form.value});
    });

  }

  ngOnDestroy() {
    this.componentDestroyed.next();
    this.componentDestroyed.complete();
  }

  updateSecondaryCategories() {
    if (this.form.get('assetType').value === null) {
      this.secondaryCategories = Object.keys(SecondaryAssetCategoryEnum).map( key => SecondaryAssetCategoryEnum[key]).map((value) => ({
        value
      }));
      this.form.get('supportingAsset').disable();
    } else {
      this.secondaryCategories = primarySecondaryCategoryMap[this.form.get('assetType').value.value].map(value => ({value}));
      this.form.get('supportingAsset').enable();
    }
    this.form.get('supportingAsset').setValue(null);
  }

  private createForm() {

    return this.fb.group({
      assetType: [this.assetData !== undefined ? this.assetData.assetType : null, Validators.required],
      // supportingAsset: [{value: this.assetData !== undefined  ? this.assetData.supportingAsset : null, disabled: this.assetData === undefined  }, Validators.required],
      supportingAsset: [this.assetData !== undefined  ? this.assetData.supportingAsset : null, Validators.required],
      aice: [this.assetData !== undefined  ? this.assetData.aice : '',Validators.required]
    })
  }

}
