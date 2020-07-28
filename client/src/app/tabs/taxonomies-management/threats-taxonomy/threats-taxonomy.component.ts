/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="threat-taxonomy.component.ts"
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
import {ImpactValueEnum, ThreatTaxonomy} from "../taxonomiesManagement.model";
import {ConfirmationService, MessageService} from "primeng/api";
import {ThreatsTaxonomyService} from "./threats-taxonomy.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-threats-taxonomy',
  templateUrl: './threats-taxonomy.component.html',
  styleUrls: ['./threats-taxonomy.component.css'],
  providers: [MessageService]
})
export class ThreatsTaxonomyComponent implements OnInit {

  selectedTaxonomies = [];
  showPopup = false;
  currentOperation = 'Create';
  @Input() isAdmin;

  impacts = Object.keys(ImpactValueEnum).map( key => ImpactValueEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  threats$: Observable<ThreatTaxonomy[]>;

  constructor(
    private confirmationService: ConfirmationService,
    private threatsService: ThreatsTaxonomyService,
    private messageService: MessageService) {

  }

  ngOnInit() {

    this.threatsService.fetch$('MEHARI').subscribe();
    this.threats$ = this.threatsService.threats;
  }

  removeTaxonomy() {

    this.confirmationService.confirm({
      message: this.selectedTaxonomies.length === 1 ? 'Are you sure that you want to remove this threat?' : 'Are you sure that you want to remove these threats?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      key: 'account',
      accept: () => {
        this.threatsService.removeThreats(
          this.selectedTaxonomies.map(taxonomy => taxonomy.identifier)
        ).subscribe();

        this.selectedTaxonomies = [];
      }
    });

  }

  showDialog(value: string) {

    this.currentOperation = value;
    this.showPopup = true;
  }

  onClose(value) {
    this.showPopup = false;

    if (value === 'CREATED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Threat Taxonomy Created!'});
    }

    if (value === 'EDITED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Threat Taxonomy Edited!'});
    }

    if (value === 'FAILED') {
      this.messageService.add({severity:'error', summary: 'Error Message', detail:'Operation Failed'});
    }


    this.selectedTaxonomies = [];
  }

}
