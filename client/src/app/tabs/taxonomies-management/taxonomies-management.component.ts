/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="taxonomies-management.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {TaxonomiesManagementTypeEnum} from './taxonomiesManagement.model';
import {ConfirmationService} from 'primeng/api';
import {VulnerabilitiesTaxonomyService} from './vulnerabilities-taxonomy/vulnerabilities-taxonomy.service';
import {DataService} from '../../dataservice';
import {Subscription} from 'rxjs/internal/Subscription';
import {MessageService} from 'primeng/components/common/messageservice';
import {Permission} from "../../permission.class";
import {PermissionType} from "../../permission-type.class";
import {TaxonomiesService} from "./taxonomies.service";
import {ThreatsTaxonomyService} from "./threats-taxonomy/threats-taxonomy.service";
import {RiskScenarioTaxonomiesService} from "./risk-scenario-taxonomy/risk-scenario-taxonomies.service";
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-taxonomies-management',
  templateUrl: './taxonomies-management.component.html',
  styleUrls: ['./taxonomies-management.component.scss'],
  providers: [TaxonomiesService,
    VulnerabilitiesTaxonomyService,
    ThreatsTaxonomyService,
    RiskScenarioTaxonomiesService]
})
export class TaxonomiesManagementComponent implements OnInit, OnDestroy {


  taxonomiesSelections = Object.keys(TaxonomiesManagementTypeEnum).map(key => TaxonomiesManagementTypeEnum[key]);

  selectedTaxonomy = 'Vulnerabilities';
  blocked = false;
  blockedMessage = false;
  msgImportExport = '';
  admin = false;

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService,
              private taxonomiesService: TaxonomiesService,
              private messageService: MessageService) {
  }

  ngOnInit() {

    const permissions = new Permission();
    permissions.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.admin = permissions.create.indexOf(PermissionType.Taxonomy) > -1;

  }

  getCreateBtnLabel(value: string): string {

    return `${value} ${this.selectedTaxonomy.toLowerCase()} taxonomy`;
  }

  export() {
    this.blocked = true;
    switch (this.selectedTaxonomy) {
      case TaxonomiesManagementTypeEnum.VULNERABILITIES: {
        this.subscriptions.push(
          this.dataService.exportVulnerabilityReference().subscribe(response => {
              this.blocked = false;
              const fileName = "VulnerabilityExport.json";
              const blob = new Blob([response.body], {type: 'application/json'});
              FileSaver.saveAs(blob, fileName);
            },
            err => {
              this.blocked = false;
              throw err;
            }));
        break;
      }
      case TaxonomiesManagementTypeEnum.THREATS: {
        this.subscriptions.push(
          this.dataService.exportThreatReference().subscribe(response => {
              this.blocked = false;
              const fileName = "ThreatExport.json";
              const blob = new Blob([response.body], {type: 'application/json'});
              FileSaver.saveAs(blob, fileName);
            },
            err => {
              this.blocked = false;
              throw err;
            }));
        break;
      }
      case TaxonomiesManagementTypeEnum.RISKSCENARIOS: {
        this.subscriptions.push(
          this.dataService.exportRiskScenarioReference().subscribe(response => {
              this.blocked = false;
              const fileName = "RiskScenarioExport.json";
              const blob = new Blob([response.body], {type: 'application/json'});
              FileSaver.saveAs(blob, fileName);
            },
            err => {
              this.blocked = false;
              throw err;
            }));
        break;
      }
      default : {
        this.blocked = false;
        break;
      }
    }
  }

  importTaxonomyFile(event) {
    this.blocked = true;
    switch (this.selectedTaxonomy) {
      case TaxonomiesManagementTypeEnum.VULNERABILITIES: {
        this.subscriptions.push(
          this.taxonomiesService.importVulnerabilitiesTaxonomiesByFile(event.target.files)
            .subscribe( data => {
              this.blocked = false;
              },
              error => {
                this.blocked = false;
                throw error;
              }
            )
        );
        break;
      }
      case TaxonomiesManagementTypeEnum.THREATS: {
        this.subscriptions.push(
          this.taxonomiesService.importThreatsTaxonomiesByFile(event.target.files)
            .subscribe( data => {
                this.blocked = false;
              },
              error => {
                this.blocked = false;
                throw error;
              }
            )
        );
        break;
      }
      case TaxonomiesManagementTypeEnum.RISKSCENARIOS: {
        this.subscriptions.push(
          this.taxonomiesService.importScenariosTaxonomiesByFile(event.target.files)
            .subscribe( data => {
                this.blocked = false;
              },
              error => {
                this.blocked = false;
                throw error;
              }
            )
        );
        break;
      }
      default : {
        this.blocked = false;
        break;
      }
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  updateGasf() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateGasfRepository().subscribe(response => {
          this.blocked = false;
          this.messageService.add({severity: 'success', summary: 'Import GASF Taxonomy', detail: 'Successfully imported.', life: 10000});
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }
}
