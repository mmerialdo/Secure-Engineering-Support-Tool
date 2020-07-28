/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="scenario-creation.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RiskScenario, ThreatTaxonomy, VulnerabilityTaxonomy} from "../../taxonomiesManagement.model";
import {RiskScenarioTaxonomiesService} from "../risk-scenario-taxonomies.service";

@Component({
  selector: 'app-scenario-creation',
  templateUrl: './scenario-creation.component.html'
})
export class ScenarioCreationComponent implements OnInit {

  @Input() selectedScenarios: RiskScenario[];
  @Output() closePopup: EventEmitter<string> = new EventEmitter<string>();
  showPopup = true;
  counter = 0;
  items: any[];
  nextLabel = 'Next';
  backLabel = 'Cancel'
  showButton = false;
  assetData: {assetType: {value: string}, supportingAsset: {value: string}, aice: string } ;
  vulnerabilityData: VulnerabilityTaxonomy;
  threatData: ThreatTaxonomy;
  safeguardData: {confining: string, prevention: string, dissuasion: string, palliative: string};
  labelPopUp;
  message = '';

  constructor(
    private riskScenarioService: RiskScenarioTaxonomiesService
  ) {
  }

  ngOnInit() {

    this.items = [
      {label: 'Asset Definition'},
      {label: 'Vulnerability Definition'},
      {label: 'Threat Definition'},
      {label: 'Safeguards Definition'}
    ];

    this.labelPopUp = this.selectedScenarios.length === 1 ?
      'Edit Scenario' : 'Create new scenario taxonomy';

    if (this.selectedScenarios.length === 1) {
      this.assetData = {
        assetType: {value: this.selectedScenarios[0].assetType},
        supportingAsset: {value: this.selectedScenarios[0].supportingAsset},
        aice: this.selectedScenarios[0].aice
      };
      this.vulnerabilityData ={
        name: this.selectedScenarios[0].vulnerabilityCode,
        catalogueId: this.selectedScenarios[0].vulnerabilityCode,
        affectedAssetsCategories: [],
        associatedThreats: [],
        phase: '',
        description: '',
        score: {}
      };
      this.threatData = {
        affectedAssetsCategories: [],
        associatedVulnerabilities: [],
        catalogueId:  this.createThreatName(),
        event: {
          name:''
        },
        catalogue: '',
        description: '',
        name: '',
        phase: '',
        threatClass: '',
        score: {
          likelihood: '',
          score: ''
        }
      };
      this.safeguardData = {
        confining: this.selectedScenarios[0].confining,
        dissuasion: this.selectedScenarios[0].dissuasion,
        prevention: this.selectedScenarios[0].prevention,
        palliative: this.selectedScenarios[0].palliative
      }
    }


  }

  onClose() {
    this.closePopup.emit(this.message);
  }

  onNext() {


    if( this.counter === 3) {

      const newScenario = {
        ...{
          ...this.assetData,
          assetType: this.assetData.assetType.value,
          supportingAsset: this.assetData.supportingAsset.value
        },
        ...{vulnerabilityCode: this.vulnerabilityData.catalogueId},
        ...{
          eventType: this.threatData.event.name.split(".")[0] + '.' + this.threatData.event.name.split(".")[1],
          eventSubType: this.threatData.event.name.split(".")[2],
          actor: this.threatData.actor.name,
          place: this.threatData.place.name,
          time: this.threatData.time.name,
          access: this.threatData.access.name,
          process: this.threatData.process.name,
        },
        ...{...this.safeguardData}
      };

      if (this.selectedScenarios.length ===0 ) {
        this.riskScenarioService.addRiskScenario(newScenario).subscribe(result => {
          this.message = 'CREATED';
          this.showPopup = false;
        }, error => {
          this.message = 'FAILED';
          this.showPopup = false;
        });
      } else {
        this.riskScenarioService.editRiskScenario({...newScenario,
          identifier: this.selectedScenarios[0].identifier
          }
        ).subscribe(result => {
          this.message = 'EDITED';
          this.showPopup = false;
        }, error => {
          this.message = 'FAILED';
          this.showPopup = false;
        });
      }
    } else if (this.counter === 2) {
      this.nextLabel = 'Save';
      this.counter++;
    }else {
      this.backLabel = 'Back';
      this.counter++;
    }
  }

  onBack() {
    if (this.counter === 0) {
      this.showPopup = false;
    } else if (this.counter === 1) {
      this.counter--;
      this.backLabel = 'Cancel';
    } else {
      this.nextLabel = 'Next'
      this.counter--;
    }
  }

  takeAssetData(value: {check: boolean, value:{assetType: {value: string}, supportingAsset: {value: string}, aice: string }}) {
    this.showButton = value.check;
    this.assetData = value.value;
  }

  takeVulnerabilityData(value) {
    this.showButton = value;
    this.vulnerabilityData = value;
  }

  takeThreatData(value) {

    this.showButton = value;
    this.threatData = value;
  }

  takeSafeguardData(value: {check:boolean, value:any}) {
    this.showButton = value.check;
    this.safeguardData = value.value;
  }

  private createThreatName(): string {

    const event = this.selectedScenarios[0].eventType +'.' + this.selectedScenarios[0].eventSubType + '-';
    const place = this.selectedScenarios[0].place !== null && this.selectedScenarios[0].place !== "" ? this.selectedScenarios[0].place  + '-': '-';
    const time = this.selectedScenarios[0].time !== null && this.selectedScenarios[0].time !== "" ? this.selectedScenarios[0].time  + '-': '-';
    const access = this.selectedScenarios[0].access!== null && this.selectedScenarios[0].access !== "" ? this.selectedScenarios[0].access + '-' : '-';
    const process = this.selectedScenarios[0].process !== null && this.selectedScenarios[0].process !== "" ? this.selectedScenarios[0].process  + '-' : '-';
    const actor = this.selectedScenarios[0].actor !== null && this.selectedScenarios[0].actor !== "" ? this.selectedScenarios[0].actor : '';
    return event+place+time+access+process+actor;
  }

}
