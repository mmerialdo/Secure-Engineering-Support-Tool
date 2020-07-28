/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="settings.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {DataService} from '../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit, OnDestroy {
  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService) {
  }

  ngOnInit() {
  }

  updateVulnerability() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateVulnerabilityRepository().subscribe(response => {
          this.blocked = false;
          this.showSuccess('Vulnerability Taxonomy imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  updateThreat() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateThreatRepository().subscribe(response => {
          this.blocked = false;
          this.showSuccess('Threat Taxonomy imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  updateScenario() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateScenarioRepository().subscribe(response => {
          this.blocked = false;
          this.showSuccess('Scenario Taxonomy imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  updateGasf() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateGasfRepository().subscribe(response => {
          this.blocked = false;
          this.showSuccess('Security Requirements Taxonomy imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  updateAudit() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateAuditRepository().subscribe(response => {
          this.blocked = false;
          this.showSuccess('Audit Taxonomy imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  showSuccess(s: string) {
    this.blocked = true;
    this.blockedMessage = true;
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Import complete', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showFailed(s: string) {
    this.blocked = true;
    this.blockedMessage = true;
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'error', summary: 'Error', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
