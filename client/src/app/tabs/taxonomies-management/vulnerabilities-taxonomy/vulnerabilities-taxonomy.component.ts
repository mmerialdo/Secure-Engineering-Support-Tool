/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="vulnerability-taxonomy.component.ts"
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
import {ImpactTypeEnum, VulnerabilityTaxonomy} from "../taxonomiesManagement.model";
import {ConfirmationService, MessageService} from "primeng/api";
import {DataService} from "../../../dataservice";
import {VulnerabilitiesTaxonomyService} from "./vulnerabilities-taxonomy.service";
import {Observable} from "rxjs";
import {ThreatsTaxonomyService} from "../threats-taxonomy/threats-taxonomy.service";
import {Permission} from "../../../permission.class";
import {PermissionType} from "../../../permission-type.class";

@Component({
  selector: 'app-vulnerabilities-taxonomy',
  templateUrl: './vulnerabilities-taxonomy.component.html',
  styleUrls: ['./vulnerabilities-taxonomy.component.scss'],
  providers: [
              MessageService]
})
export class VulnerabilitiesTaxonomyComponent implements OnInit {

  selectedTaxonomy = [];
  showPopup = false;
  currentOperation = 'Create';
  @Input() isAdmin: boolean;

  impacts = Object.keys(ImpactTypeEnum).map( key => ImpactTypeEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  vulnerabilities$: Observable<VulnerabilityTaxonomy[]>;

  constructor(
    private confirmationService: ConfirmationService,
    private vulnerabilitiesService: VulnerabilitiesTaxonomyService,
    private messageService: MessageService) {
  }

  ngOnInit() {

    this.vulnerabilitiesService.fetch$().subscribe();
    this.vulnerabilities$ = this.vulnerabilitiesService.vulnerabilities;
  }

  removeTaxonomy() {

    this.confirmationService.confirm({
      message: this.selectedTaxonomy.length === 1 ? 'Are you sure that you want to remove this vulnerability?' : 'Are you sure that you want to remove these vulnerabilities?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      key: 'account',
      accept: () => {

        this.vulnerabilitiesService.removeVulnerability(
          this.selectedTaxonomy.map(taxonomy => taxonomy.identifier)
        ).subscribe();

        this.selectedTaxonomy = [];
      }
    });
  }

  showDialog(value) {
    this.currentOperation = value;
    this.showPopup= true;
  }

  onClose(value: string) {

    this.showPopup = false;

    if (value === 'CREATED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Vulnerability Taxonomy Created!'});
    }

    if (value === 'EDITED') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Vulnerability Taxonomy Edited!'});
    }

    if (value === 'FAILED') {
      this.messageService.add({severity:'error', summary: 'Error Message', detail:'Operation Failed'});
    }

    this.selectedTaxonomy = [];
  }
}
