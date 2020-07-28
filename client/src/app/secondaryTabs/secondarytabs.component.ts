/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="secondarytabs.component.ts"
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
import {MenuItem} from 'primeng/primeng';
import {TabStatusService} from '../tabStatusService';
import {NavigationEnd, NavigationStart, Router} from '@angular/router';
import {filter, pairwise} from 'rxjs/operators';
import {LockService} from '../shared/service/lock-service';
import {Subscription} from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-secondarytabs',
  templateUrl: './secondarytabs.component.html',
  styleUrls: ['./secondarytabs.component.scss'],
  providers: [TabStatusService]
})
export class SecondarytabsComponent implements OnInit, OnDestroy {

  public tabsItems: MenuItem[];
  private status = true;
  activeItem: MenuItem;

  currentSecondaryTab;

  selectedTab = 0;
  lockedBy;
  viewIdentifier;
  private subscriptions: Subscription[] = [];

  constructor(private tabStatusService: TabStatusService,
              private lockService: LockService,
              private router: Router) {

    this.tabsItems = [
      {
        label: 'Project', icon: 'fa fa-fw fa-bar-chart', command: () => {
          this.selectedTab = 0;
          this.router.navigate(['/projectabs/project']);
        }
      },
      {
        label: 'Procedures', icon: 'fa fa-fw fa-calendar', command: () => {
          this.selectedTab = 1;
          this.router.navigate(['/projectabs/procedures']);
        }
      },
      {
        label: 'Assets', icon: 'fa fa-fw fa-book', disabled: true, command: () => {
          this.selectedTab = 2;
          this.router.navigate(['/projectabs/assets']);
        }
      },
      {
        label: 'Vulnerabilities', icon: 'fa fa-fw fa-support', disabled: true, command: () => {
          this.selectedTab = 3;
          this.router.navigate(['/projectabs/vulnerabilities']);
        }
      },
      {
        label: 'Threats', icon: 'fa fa-fw fa-twitter', disabled: true, command: () => {
          this.selectedTab = 4;
          this.router.navigate(['/projectabs/threats']);
        }
      },
      {
        label: 'Security Audit', icon: 'fa fa-fw fa-book', disabled: true, command: () => {
          this.selectedTab = 5;
          this.router.navigate(['/projectabs/audit']);
        }
      },
      {
        label: 'Scenarios', icon: 'fa fa-fw fa-support', disabled: true, command: () => {
          this.selectedTab = 6;
          this.router.navigate(['/projectabs/scenario']);
        }
      },
      {
        label: 'Risk Treatment', icon: 'fa fa-fw fa-twitter', disabled: true, command: () => {
          this.selectedTab = 7;
          this.router.navigate(['/projectabs/treatment']);
        }
      }
    ];


    // test
    this.currentSecondaryTab = this.tabsItems[0];
    this.subscriptions.push(
      this.lockService.lockedBy.subscribe((user) => {
        this.lockedBy = user;
      }));

    this.subscriptions.push(
      this.lockService.viewIdentifier.subscribe((identifier) => {
        this.viewIdentifier = identifier;
      }));

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      pairwise()).subscribe((event: any) => {
    });
  }

  lockView() {
    this.lockService.addLockForced(this.viewIdentifier);
  }

  ngOnInit() {

    this.manageCurrentTab();

    const currentStatusInit = sessionStorage.getItem('ableTabs').toLowerCase() !== 'false' ? true : false;

    this.tabsItems[2].disabled = currentStatusInit;
    this.tabsItems[3].disabled = currentStatusInit;
    this.tabsItems[4].disabled = currentStatusInit;
    this.tabsItems[5].disabled = currentStatusInit;
    this.tabsItems[6].disabled = currentStatusInit;
    this.tabsItems[7].disabled = currentStatusInit;


    this.tabStatusService.dataStatus$.subscribe(
      data => {
        this.status = data;

        const currentStatus = sessionStorage.getItem('ableTabs').toLowerCase() !== 'false' ? true : false;


        this.tabsItems[2].disabled = currentStatus;
        this.tabsItems[3].disabled = currentStatus;
        this.tabsItems[4].disabled = currentStatus;
        this.tabsItems[5].disabled = currentStatus;
        this.tabsItems[6].disabled = currentStatus;
        this.tabsItems[7].disabled = currentStatus;


      });

  }

  isPM() {
    const authz = JSON.parse(sessionStorage.getItem('authzToken'));
    if (authz && authz.update
      .some(id => sessionStorage.getItem('idProject') === id)) {
      return true;
    }
    return false;
  }

  private manageCurrentTab() {

    const currentUrl = this.router.url;
    if (currentUrl === '/projectabs/project') {
      this.selectedTab = 0;

      this.currentSecondaryTab = this.tabsItems[0];

    }
    if (currentUrl === '/projectabs/procedures') {
      this.selectedTab = 1;

      this.currentSecondaryTab = this.tabsItems[1];

    }
    if (currentUrl === '/projectabs/assets') {
      this.selectedTab = 2;

      this.currentSecondaryTab = this.tabsItems[2];

    }
    if (currentUrl === '/projectabs/vulnerabilities') {

      this.selectedTab = 3;

      this.currentSecondaryTab = this.tabsItems[3];

    }
    if (currentUrl === '/projectabs/threats') {
      this.selectedTab = 4;

      this.currentSecondaryTab = this.tabsItems[4];

    }
    if (currentUrl === '/projectabs//projectabs/audit') {
      this.selectedTab = 5;

      this.currentSecondaryTab = this.tabsItems[5];

    }
    if (currentUrl === '/projectabs/scenario') {
      this.selectedTab = 6;

      this.currentSecondaryTab = this.tabsItems[6];

    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
