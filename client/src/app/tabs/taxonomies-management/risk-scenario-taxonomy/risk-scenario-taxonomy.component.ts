/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="risk-scenario-taxonom.component.ts"
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
import {RiskScenarioTaxonomiesService} from "./risk-scenario-taxonomies.service";
import {ConfirmationService, MessageService} from "primeng/api";
import {tap} from "rxjs/operators";
import {ImpactTypeEnum} from "../taxonomiesManagement.model";

@Component({
  selector: 'app-risk-scenario-taxonomy',
  templateUrl: './risk-scenario-taxonomy.component.html',
  styleUrls: ['./risk-scenario-taxonomy.component.scss'],
  providers: [MessageService]
})
export class RiskScenarioTaxonomyComponent implements OnInit {

  // taxonomies: any[];
  selectedTaxonomy = [];
  showPopup = false;
  scenarios$;

  blocked = false;
  blockedMessage = false;
  msgRiskScenario = '';
  @Input() isAdmin;

  impacts = Object.keys(ImpactTypeEnum).map( key => ImpactTypeEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  constructor(
    private confirmationService: ConfirmationService,
    private riskScenarioService: RiskScenarioTaxonomiesService,
    private messageService: MessageService
  ) {
  }

  ngOnInit() {
    this.blocked = true;
    this.riskScenarioService.fetch$('MEHARI').subscribe(result => {
      this.blocked = false;
    });
    this.scenarios$ = this.riskScenarioService.scenarios;
  }

  showDialog(value: string) {

    if (value === 'Create') {
      this.selectedTaxonomy = [];
    }
    this.showPopup = true;
  }

  removeTaxonomy() {
    this.confirmationService.confirm({
      message: this.selectedTaxonomy.length === 1 ? 'Are you sure that you want to remove this scenario?' : 'Are you sure that you want to remove these scenarios?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      key: 'account',
      accept: () => {
        this.riskScenarioService.removeRiskScenarios(
          this.selectedTaxonomy.map(taxonomy => taxonomy.identifier)
        ).subscribe();
        this.selectedTaxonomy = [];
      }
    });

  }

  onClose(value) {
    this.showPopup = false;
    if (value === 'CREATED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Scenario Taxonomy Created!'});
    }

    if (value === 'EDITED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Scenario Taxonomy Edited!'});
    }

    if (value === 'FAILED') {
      this.messageService.add({severity:'error', summary: 'Error Message', detail:'Operation Failed'});
    }

    this.selectedTaxonomy = [];
  }

  onClick() {
    if (this.selectedTaxonomy.length === 1) {

    }
  }

  private createThreatName(eventType: string, eventSubType: string, placeV: string,
                           timeV: string, accessV: string, processV: string, actorV: string): string {

    const event = eventType +'.' +eventSubType + '-';
    const place = placeV !== null && placeV !== "" ? placeV  + '-': '-';
    const time = timeV !== null && timeV !== "" ? timeV  + '-': '-';
    const access = accessV!== null && accessV !== "" ? accessV + '-' : '-';
    const process = processV !== null && processV !== "" ? processV  + '-' : '-';
    const actor = actorV !== null && actorV !== "" ? actorV : '';
    return event+place+time+access+process+actor;
  }

}
