/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="add-edit-row.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input,OnDestroy, OnInit, Output} from '@angular/core';
import {
  ImpactValueVulEnum,
  SecondaryAssetCategoryEnum, ThreatTaxonomy,
  VulnerabilityTaxonomy
} from "../../taxonomiesManagement.model";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {map, takeUntil, tap} from "rxjs/operators";
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {VulnerabilitiesTaxonomyService} from "../vulnerabilities-taxonomy.service";
import {ThreatsTaxonomyService} from "../../threats-taxonomy/threats-taxonomy.service";

const impactValidator = (control: AbstractControl): {[key: string]: boolean} => {
  const confidentiality = control.get('confidentiality').value;
  const integrity = control.get('integrity').value;
  const availability = control.get('availability').value;
  const efficiency = control.get('efficiency').value;
  if (confidentiality || integrity || availability || efficiency) {
    return null;
  }
  return { noImpact: true };
};


@Component({
  selector: 'app-add-edit-row',
  templateUrl: './add-edit-row.component.html',
  styleUrls: ['./add-edit-row.component.css'],
  providers: [ThreatsTaxonomyService]
})
export class AddEditRowComponent implements OnInit, OnDestroy{

  @Input() taxonomy: {operation: string, value:VulnerabilityTaxonomy};
  @Input() type: string;
  @Output() closePopUp: EventEmitter<string> = new EventEmitter<string>();
  form: FormGroup;
  showPopup = true;
  threats$;
  filteredThreats$;
  message = '';

  impacts = Object.keys(ImpactValueVulEnum).map( key => ImpactValueVulEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  secondaryCategories = Object.keys(SecondaryAssetCategoryEnum).map( key => SecondaryAssetCategoryEnum[key]).map((value) => ({
    value
  }));

  labelPopUp;

  private componentDestroyed: Subject<Component> = new Subject<Component>();

  filter$: BehaviorSubject<string[]> = new BehaviorSubject([]);

  constructor(
    private fb: FormBuilder,
    private vulnerabilityService: VulnerabilitiesTaxonomyService,
    private threatsService: ThreatsTaxonomyService
    ) {
  }

  ngOnInit() {

    this.labelPopUp = this.taxonomy && this.taxonomy.operation === 'Edit' ?
      `Edit ${this.taxonomy.value[0].name}` : 'Create new vulnerability taxonomy';

    this.threatsService.fetch$().subscribe();
    this.threats$ = this.threatsService.threats;

    this.filter$.next(
      this.taxonomy.operation === 'Edit' ?
        this.taxonomy.value[0].affectedAssetsCategories : []
    );

    this.filteredThreats$ = this.createFilterThreats(
      this.filter$,
      this.threats$
    ) .pipe(
      map( (response) => {
        return response.map((threat) => {
          return ({value: threat.catalogueId})
        })
      })
    );

    this.form = this.createForm();

    this.form.get('affectedAssetsCategories').valueChanges.pipe(
      takeUntil(this.componentDestroyed)
    ).subscribe((value) => {
      if (value.length > 0) {
        this.form.get('associatedThreats').enable();
      }else {
        this.form.get('associatedThreats').disable();
      }
    });

  }

  ngOnDestroy(): void {
    this.componentDestroyed.next();
    this.componentDestroyed.complete();
  }

  private createForm() {
    return this.fb.group({
      name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].name : '', Validators.required],
      catalogueId: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].catalogueId : '', Validators.required],
      description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].description :'', Validators.required],
      affectedAssetsCategories: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].affectedAssetsCategories.map(value => ({value}))
        : [], [Validators.required, Validators.minLength(1)]],
      associatedThreats: [{value: this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].associatedThreats.map(value => ({value}))
         : [], disabled: this.taxonomy.operation !== 'Edit'}, [Validators.required, Validators.minLength(1)]],
      score: this.fb.group({
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].score.consequences[0].description : '', Validators.required],
        securityImpacts: this.fb.group({
          confidentiality: [this.taxonomy.operation === 'Edit' ?
            this.taxonomy.value[0].score.consequences[0].securityImpacts.filter(securityImp => securityImp.scope === 'Confidentiality').map(impact => impact.impact)[0]  : ''],
          integrity: [this.taxonomy.operation === 'Edit' ?
            this.taxonomy.value[0].score.consequences[0].securityImpacts.filter(securityImp => securityImp.scope === 'Integrity').map(impact => impact.impact)[0] : ''],
          availability: [this.taxonomy.operation === 'Edit' ?
            this.taxonomy.value[0].score.consequences[0].securityImpacts.filter(securityImp => securityImp.scope === 'Availability').map(impact => impact.impact)[0] : ''],
          efficiency: [this.taxonomy.operation === 'Edit' ?
            this.taxonomy.value[0].score.consequences[0].securityImpacts.filter(securityImp => securityImp.scope === 'Efficiency').map(impact => impact.impact)[0] : ''],

        }, {validators: impactValidator})
      })
    });
  }

  onClose() {
    this.closePopUp.emit(this.message);
  }

  onSave() {
    if (this.taxonomy.operation === 'Create') {
      this.vulnerabilityService.addVulnerability(
        {
          ...this.form.value,
          affectedAssetsCategories: this.form.value.affectedAssetsCategories.map(category => category.value),
          associatedThreats: this.form.value.associatedThreats.map(threat => threat.value),
          score: this.calculateSecurityImpacts(this.form.value.score),
          author: JSON.parse(atob(sessionStorage.getItem('authnToken'))).username,
          catalogue: 'CUSTOM'
        }
      ).subscribe(result => {
        this.message = 'CREATED';
        this.showPopup = false;
      }, error => {
        this.message = 'FAILED';
        this.showPopup = false;
      });
    }else {
      this.vulnerabilityService.editVulnerability({
          ...this.form.value,
          affectedAssetsCategories: this.form.value.affectedAssetsCategories.map(category => category.value),
          associatedThreats: this.form.value.associatedThreats.map(threat => threat.value),
          score: this.calculateSecurityImpacts(this.form.value.score),
          identifier: this.taxonomy.value[0].identifier,
          author: JSON.parse(atob(sessionStorage.getItem('authnToken'))).username,
          catalogue: 'CUSTOM'
        }
      ).subscribe( result =>{
        this.message = 'EDITED';
        this.showPopup = false;
      }, error => {
        this.message = 'FAILED';
        this.showPopup = false;
      })
    }

  }

  private calculateSecurityImpacts(value: any) {

    let securityArray = [];
    if(value.securityImpacts.confidentiality !== undefined && value.securityImpacts.confidentiality !== null && value.securityImpacts.confidentiality !== '') {

      securityArray.push( {impact: value.securityImpacts.confidentiality, scope: 'Confidentiality', technicalImpacts: []})
    }
    if(value.securityImpacts.integrity !== undefined && value.securityImpacts.integrity !== null && value.securityImpacts.integrity !== '') {
      securityArray.push( {impact: value.securityImpacts.integrity, scope: 'Integrity', technicalImpacts: []})
    }
    if(value.securityImpacts.availability !== undefined && value.securityImpacts.availability !== null && value.securityImpacts.availability !== '') {
      securityArray.push( {impact: value.securityImpacts.availability, scope: 'Availability', technicalImpacts: []})
    }
    if(value.securityImpacts.efficiency !== undefined && value.securityImpacts.efficiency !== null && value.securityImpacts.efficiency !== '') {
      securityArray.push( {impact: value.securityImpacts.efficiency, scope: 'Efficiency', technicalImpacts: []})
    }

    value.securityImpacts = securityArray;
    return {consequences: [value]};

  }

  filterThreats() {

    this.filter$.next(this.form.get('affectedAssetsCategories').value.map(category => category.value));
    this.form.get('associatedThreats').setValue([]);
  }

  createFilterThreats(filter$, threats$) {

    return combineLatest(
      threats$,
      filter$, (threats: ThreatTaxonomy[], filter: string[]) => {
        if (filter.length === 0) return threats;
        return threats.filter(threat => {
          return threat.affectedAssetsCategories.some(category => filter.includes(category));
        })
      });
  }

}
