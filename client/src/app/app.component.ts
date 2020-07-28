/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="app.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, EventEmitter, OnDestroy, Output} from '@angular/core';
import {Location} from '@angular/common';
import {ChangeTabService} from './changetabservice';
import {Router, NavigationEnd} from '@angular/router';
import {DataService} from './dataservice';
import {NotificationService} from './notificationservice';
import {FormBuilder, Validators} from '@angular/forms';
import {ValidationService} from './validationservice';
import {filter} from 'rxjs/operators';
import {ConfirmationService, MessageService} from 'primeng/api';
import {Message} from 'primeng/api';
import {Subscription} from 'rxjs';
import {UserPasswordComponent} from './tabs/users-tab/user-password/user-password.component';
import {RiskScenarioTaxonomiesService} from './tabs/taxonomies-management/risk-scenario-taxonomy/risk-scenario-taxonomies.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [ChangeTabService, NotificationService, ConfirmationService, UserPasswordComponent]

})
export class AppComponent implements OnDestroy {
  private currentUrl = '/login';
  title = 'Secure Engineering Support Tool';

  public backArrow = false;

  public showLogout;

  public displayEditPassword = false;
  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  private username;
  profile = '';

  private subscriptions: Subscription[] = [];

  constructor(private changeTabService: ChangeTabService, private router: Router, private dataService: DataService,
              private messageService: MessageService) {

    console.log('-------------------APP-------------');
    router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe((e: NavigationEnd) => {
        this.currentUrl = e.url;
        if ((this.currentUrl === '/login') || (this.currentUrl === '/')) {
          this.showLogout = false;
          sessionStorage.setItem('showLogout', 'false');
        }

        if (this.currentUrl !== undefined && this.currentUrl.indexOf('projectabs') === 1) {
          this.backArrow = true;
        } else {
          this.backArrow = false;
        }
      });

    if ((sessionStorage.getItem('showLogout') === null) || (sessionStorage.getItem('showLogout') === 'false')) {
      this.showLogout = false;
    } else {

      this.username = JSON.parse(atob(sessionStorage.getItem('authnToken'))).username;
      const permission = JSON.parse(sessionStorage.getItem('authzToken'));
      this.profile = permission.profile;
      this.showLogout = true;
    }

    this.subscriptions.push(
      this.changeTabService.dataTabOn$.subscribe(
        data => {
          this.router.navigate(['/projectabs/project']);
        }
      ));


    this.subscriptions.push(
      this.changeTabService.dataTabOnsubTabShowLogout$.subscribe(
        data => {
          if (sessionStorage.getItem('showLogout') === 'true') {

            this.username = JSON.parse(atob(sessionStorage.getItem('authnToken'))).username;
            const permission = JSON.parse(sessionStorage.getItem('authzToken'));
            this.profile = permission.profile;
            this.showLogout = true;
            sessionStorage.setItem('showLogout', 'true');
          } else {
            this.showLogout = false;
            sessionStorage.setItem('showLogout', 'false');
          }
        }
      ));

    console.log('-------------------APP after-------------');
  }

  backProjectsList() {

    // to disable tabs in the secondary tabs
    sessionStorage.setItem('ableTabs', 'true');
    this.router.navigate(['/tool/projects']);
    this.backArrow = false;
  }

  displayPassword() {
    this.displayEditPassword = true;
    console.log('displayEditPassword ' + this.displayEditPassword);
  }

  onClose(value) {
    this.displayEditPassword = false;
    console.log('onClose ' + value);
    if (value === 'SUCCESS') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Password updated!'});
    }

    if (value === 'FAILED') {
      this.messageService.add({severity: 'error', summary: 'Error Message', detail: 'Password update failed!'});
    }
    console.log('onClose ' + this.displayEditPassword);
  }

  logout() {

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.logout().subscribe(response => {
        this.blocked = false;
        this.showLogout = false;

        this.router.navigate(['/login']).then(()=>{
          this.sessionClear();
        });
      }, err => {
        this.blocked = false;
        this.router.navigate(['/login']).then(()=>{
          this.sessionClear();
        });
      }));
  }

  sessionClear() {
    sessionStorage.clear();

    sessionStorage.setItem('ableTabs', 'true');
    sessionStorage.setItem('showLogout', 'false');
    this.dataService.setAuthnToken(null);
  }

  showFailed(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'error', summary: 'Error', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 6000);
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


export interface Message {
  severity?: string;
  summary?: string;
  detail?: string;
}
