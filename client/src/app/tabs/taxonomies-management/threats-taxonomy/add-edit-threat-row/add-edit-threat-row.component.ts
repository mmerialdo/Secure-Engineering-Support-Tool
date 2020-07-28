/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="add-edit-threat-row.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {
  ImpactValueEnum,
  SecondaryAssetCategoryEnum, ThreatClassEnum,
  ThreatTaxonomy,
  VulnerabilityTaxonomy
} from "../../taxonomiesManagement.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {BehaviorSubject, combineLatest, Subject} from "rxjs";
import {map, takeUntil} from "rxjs/operators";
import {ThreatsTaxonomyService} from "../threats-taxonomy.service";
import {VulnerabilitiesTaxonomyService} from "../../vulnerabilities-taxonomy/vulnerabilities-taxonomy.service";

@Component({
  selector: 'app-add-edit-threat-row',
  templateUrl: './add-edit-threat-row.component.html',
  styleUrls: ['./add-edit-threat-row.component.css'],
  providers: [VulnerabilitiesTaxonomyService]
})
export class AddEditThreatRowComponent implements OnInit, OnDestroy {

  @Input() taxonomy: {operation: string, value:ThreatTaxonomy};
  @Input() type: string;
  @Output() closePopUp: EventEmitter<string> = new EventEmitter<string>();
  form: FormGroup;
  showPopup = true;
  vulnerabilities$;
  filteredVulnerabilities$;
  labelPopUp;
  filteredEvents = [];
  filteredActors = [];
  filteredProcesses= [];
  filteredAccesses = [];
  filteredTimes = [];
  filteredPlaces = [];
  threats = [];
  message = '';

  impacts = Object.keys(ImpactValueEnum).map( key => ImpactValueEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  threatClasses = Object.keys(ThreatClassEnum).map( key => ThreatClassEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  secondaryCategories = Object.keys(SecondaryAssetCategoryEnum).map( key => SecondaryAssetCategoryEnum[key]).map((value) => ({
    value
  }));

  private componentDestroyed: Subject<Component> = new Subject<Component>();
  filter$: BehaviorSubject<string[]> = new BehaviorSubject([]);

  constructor(
    private fb: FormBuilder,
    private threatsService: ThreatsTaxonomyService,
    private vulnerabilitiesService: VulnerabilitiesTaxonomyService) { }

  ngOnInit() {

    this.labelPopUp = this.taxonomy && this.taxonomy.operation === 'Edit' ?
      `Edit ${this.taxonomy.value[0].catalogueId}` : 'Create new threat taxonomy';

    this.vulnerabilitiesService.fetch$().subscribe();
    this.vulnerabilities$ = this.vulnerabilitiesService.vulnerabilities;

     this.threatsService.fetch$().subscribe( (result:any) =>{

       this.threats = result.threats;
     });

    this.filter$.next(
      this.taxonomy.operation === 'Edit' ?
        this.taxonomy.value[0].affectedAssetsCategories : []
    );

    this.filteredVulnerabilities$ = this.createFilterVulnerabilities(
      this.filter$,
      this.vulnerabilities$
    ) .pipe(
      map( (response) => {
        return response.map((vulnerability) => {
          return ({value: vulnerability.catalogueId})
        })
      })
    );

    this.form = this.createForm();

    this.form.get('affectedAssetsCategories').valueChanges.pipe(
      takeUntil(this.componentDestroyed)
    ).subscribe((value) => {
      if (value.length > 0) {
        this.form.get('associatedVulnerabilities').enable();
      }else {
        this.form.get('associatedVulnerabilities').disable();
      }
    });

  if(this.taxonomy.operation === 'Edit') {
    this.createCatalogueIdandDescription();
  }

  }

  ngOnDestroy() {
    this.componentDestroyed.next();
    this.componentDestroyed.complete();
  }

  private createForm() {

    return this.fb.group({
      name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].name : '', Validators.required],
      catalogueId: [{ value: '', disabled: true}, Validators.required],
      description: [{value: '', disabled: true }, Validators.required],
      threatClass: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].threatClass : '', Validators.required],
      affectedAssetsCategories: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].affectedAssetsCategories.map(value => ({value}))
        : [], [Validators.required, Validators.minLength(1)]],
      associatedVulnerabilities: [{value: this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].associatedVulnerabilities.map(value => ({value}))
          : [], disabled: this.taxonomy.operation !== 'Edit' }, [Validators.required, Validators.minLength(1)]],
      score: this.fb.group({
        likelihood: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].score.likelihood : '', Validators.required],
      }),
      event: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].event.name : '', Validators.required],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].event.description : '', Validators.required],
      }),
      actor: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].actor.name : '', null],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].actor.description : '', null],
      }),
      process: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].process.name : '', null],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].process.description : '', null],
      }),
      place: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].place.name : '', null],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].place.description : '', null],
      }),
      access: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].access.name : '', null],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].access.description : '', null],
      }),
      time: this.fb.group({
        name: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].time.name : '', null],
        description: [this.taxonomy.operation === 'Edit' ? this.taxonomy.value[0].time.description : '', null],
      }),
    });
  }

  onClose() {
    this.closePopUp.emit(this.message);
  }

  onSave() {

    if (this.taxonomy.operation === 'Create') {
      this.threatsService.addThreat(
        {
          ...this.form.getRawValue(),
          affectedAssetsCategories: this.form.value.affectedAssetsCategories.map(category => category.value),
          associatedVulnerabilities: this.form.value.associatedVulnerabilities.map(threat => threat.value),
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
    } else {
      this.threatsService.editThreat(
        {
          ...this.form.getRawValue(),
          affectedAssetsCategories: this.form.value.affectedAssetsCategories.map(category => category.value),
          associatedVulnerabilities: this.form.value.associatedVulnerabilities.map(threat => threat.value),
          identifier: this.taxonomy.value[0].identifier,
          author: JSON.parse(atob(sessionStorage.getItem('authnToken'))).username,
          catalogue: 'CUSTOM'
        }
      ).subscribe(result => {
        this.message = 'EDITED';
        this.showPopup = false;
      }, error => {
        this.message = 'FAILED';
        this.showPopup = false;
      });
    }
  }

  filterThreats() {

    this.filter$.next(this.form.get('affectedAssetsCategories').value.map(category => category.value));
    this.form.get('associatedVulnerabilities').setValue([]);
  }

  createFilterVulnerabilities(filter$, vulnerabilities$) {

    return combineLatest(
      vulnerabilities$,
      filter$, (vulnerabilities: VulnerabilityTaxonomy[], filter: string[]) => {
        if (filter.length === 0) return vulnerabilities;
        return vulnerabilities.filter(threat => {
          return threat.affectedAssetsCategories.some(category => filter.includes(category));
        })
      });
  }

  filterEvents(value) {

    this.filteredEvents = this.threats.reduce((acc, threat) => {
      if(!acc.some(val => val.name === threat.event.name)) {
        return [...acc, threat.event]
      }else {
        return acc;
      }
    }, [])
      .filter(event => event.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(event => event.name);

  }

  filterActors(value) {

    this.filteredActors = this.threats.reduce((acc, threat) => {
      if(!acc.some(val => val.name === threat.actor.name && val.name !== '')) {
        return [...acc, threat.actor]
      } else {
        return acc;
      }
    }, [])
      .filter(actor => actor.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(actor => actor.name);

  }

  filterProcesses(value) {

    this.filteredProcesses = this.threats.reduce((acc, threat) => {
      if(!acc.some(val => val.name === threat.process.name && val.name !== '')) {
        return [...acc, threat.process]
      } else {
        return acc;
      }
    }, [])
      .filter(process => process.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(process => process.name);

  }

  filterAccesses(value) {

    this.filteredAccesses = this.threats.reduce((acc, threat) => {
      if(!acc.some(val => val.name === threat.access.name && val.name !== '')) {
        return [...acc, threat.access]
      } else {
        return acc;
      }
    }, [])
      .filter(access => access.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(access => access.name);

  }

  filterTimes(value) {

    this.filteredTimes = this.threats.reduce((acc, threat) => {

        if(!acc.some(val => val.name === threat.time.name && val.name !== '')) {
          return [...acc, threat.time]
        }else {
          return acc;
        }
      }, [])
      .filter(time=> time.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(time=> time.name);

  }

  filterPlaces(value) {

    this.filteredPlaces = this.threats.reduce((acc, threat) => {

      if(!acc.some(val => val.name === threat.place.name && val.name !== '')) {
        return [...acc, threat.place]
      }else {
        return acc;
      }
    }, [])
      .filter(place=> place.name.toLowerCase().startsWith(value.query.toLowerCase()))
      .map(place=> place.name);

  }

  selectEventDescription(value) {

    const description = this.threats
      .filter(threat => threat.event.name === value && threat.event.name !== '')
      .map(threat => threat.event.description)[0];
    this.form.get('event').get('description').setValue( description);
    this.createCatalogueIdandDescription();
    this.form.get('event').get('description').disable();
  }

  selectEvent(value) {

    const currentEvent = this.threats
      .filter(threat => threat.event.name.toLowerCase() === value.target.value.toLowerCase() && threat.event.name !== '')
      .map(threat => threat.event);

    if( currentEvent.length > 0) {

      this.form.get('event').get('name').setValue(currentEvent[0].name);
      this.form.get('event').get('description').setValue( currentEvent[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('event').get('description').disable();

    } else {
      this.form.get('event').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('event').get('description').enable();
    }

  }

  selectActorDescription(value) {
    this.form.get('actor').get('description').setValue( this.threats
      .filter(threat => threat.actor.name === value && threat.actor.name !== '')
      .map(threat => threat.actor.description)[0]);
    this.createCatalogueIdandDescription();
    this.form.get('actor').get('description').disable();
  }

  selectActor(value) {

    const currentActor = this.threats
      .filter(threat => threat.actor.name.toLowerCase() === value.target.value.toLowerCase() && threat.actor.name !== '')
      .map(threat => threat.actor);

    if( currentActor.length > 0) {

      this.form.get('actor').get('name').setValue(currentActor[0].name);
      this.form.get('actor').get('description').setValue( currentActor[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('actor').get('description').disable();

    } else {
      this.form.get('actor').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('actor').get('description').enable();
    }
  }

  selectProcessDescription(value) {

    this.form.get('process').get('description').setValue( this.threats
      .filter(threat => threat.process.name === value && threat.process.name !== '')
      .map(threat => threat.process.description)[0]);
    this.createCatalogueIdandDescription();
    this.form.get('process').get('description').disable();

  }

  selectProcess(value) {

    const currentProcess = this.threats
      .filter(threat => threat.process.name.toLowerCase() === value.target.value.toLowerCase() && threat.process.name !== '')
      .map(threat => threat.process);

    if( currentProcess.length > 0) {

      this.form.get('process').get('name').setValue(currentProcess[0].name);
      this.form.get('process').get('description').setValue( currentProcess[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('process').get('description').disable();

    } else {
      this.form.get('process').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('process').get('description').enable();
    }
  }

  selectAccessDescription(value) {

    this.form.get('access').get('description').setValue( this.threats
      .filter(threat => threat.access.name === value && threat.access.name !== '')
      .map(threat => threat.access.description)[0]);
    this.createCatalogueIdandDescription();
    this.form.get('access').get('description').disable();

  }

  selectAccess(value) {

    const currentAccess = this.threats
      .filter(threat => threat.access.name.toLowerCase() === value.target.value.toLowerCase() && threat.access.name !== '')
      .map(threat => threat.access);

    if( currentAccess.length > 0) {

      this.form.get('access').get('name').setValue(currentAccess[0].name);
      this.form.get('access').get('description').setValue( currentAccess[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('access').get('description').disable();

    } else {
      this.form.get('access').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('access').get('description').enable();
    }
  }

  selectPlaceDescription(value) {

    this.form.get('place').get('description').setValue( this.threats
      .filter(threat => threat.place.name === value && threat.place.name !== '')
      .map(threat => threat.place.description)[0]);
    this.createCatalogueIdandDescription();
    this.form.get('place').get('description').disable();

  }

  selectPlace(value) {

    const currentPlace = this.threats
      .filter(threat => threat.place.name.toLowerCase() === value.target.value.toLowerCase() && threat.place.name !== '')
      .map(threat => threat.place);

    if( currentPlace.length > 0) {

      this.form.get('place').get('name').setValue(currentPlace[0].name);
      this.form.get('place').get('description').setValue( currentPlace[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('place').get('description').disable();

    } else {
      this.form.get('place').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('place').get('description').enable();
    }
  }

  selectTimeDescription(value) {

    this.form.get('time').get('description').setValue( this.threats
      .filter(threat => threat.time.name === value && threat.time.name !== '')
      .map(threat => threat.time.description)[0]);
    this.createCatalogueIdandDescription();
    this.form.get('time').get('description').disable();
  }

  selectTime(value) {

    const currentTime = this.threats
      .filter(threat => threat.time.name.toLowerCase() === value.target.value.toLowerCase() && threat.time.name !== '')
      .map(threat => threat.time);

    if( currentTime.length > 0) {

      this.form.get('time').get('name').setValue(currentTime[0].name);
      this.form.get('time').get('description').setValue( currentTime[0].description);
      this.createCatalogueIdandDescription();
      this.form.get('time').get('description').disable();

    } else {
      this.form.get('time').get('description').setValue( '');
      this.createCatalogueIdandDescription();
      this.form.get('time').get('description').enable();
    }
  }


  public createCatalogueIdandDescription( ) {

    const event = this.form.get('event').value.name !== '' ? this.form.get('event').value.name + '-': '';
    const place = this.form.get('place').value.name !== '' ? this.form.get('place').value.name  + '-': '-';
    const time = this.form.get('time').value.name !== '' ? this.form.get('time').value.name  + '-': '-';
    const access = this.form.get('access').value.name !== '' ? this.form.get('access').value.name + '-' : '-';
    const process = this.form.get('process').value.name !== '' ? this.form.get('process').value.name  + '-' : '-';
    const actor = this.form.get('actor').value.name !== '' ? this.form.get('actor').value.name : '';

    const eventDescription = this.form.getRawValue().event.description !== '' ? this.form.getRawValue().event.description: '';
    const placeDescription = this.form.getRawValue().place.description !== '' ? ',' + this.form.getRawValue().place.description: '-';
    const timeDescription = this.form.getRawValue().time.description !== '' ? ',' + this.form.getRawValue().time.description: '-';
    const accessDescription = this.form.getRawValue().access.description !== '' ? ',' + this.form.getRawValue().access.description: '-';
    const processDescription = this.form.getRawValue().process.description !== '' ? ',' + this.form.getRawValue().process.description : '-';
    const actorDescription = this.form.getRawValue().actor.description !== '' ? ',by ' + this.form.getRawValue().actor.description : '';

    const catalogueId = event+place+time+access+process+actor === '----' ? '' : event+place+time+access+process+actor;
    const description = eventDescription + actorDescription + accessDescription + placeDescription + timeDescription + processDescription === '----'
      ? '' : eventDescription + actorDescription + accessDescription + placeDescription + timeDescription + processDescription;


    this.form.get('catalogueId').setValue(catalogueId);
    this.form.get('description').setValue(description);
  }

}
