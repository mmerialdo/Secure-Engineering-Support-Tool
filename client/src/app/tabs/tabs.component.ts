/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="tabs.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from '@angular/router';
import {MenuItem} from 'primeng/primeng';
import {DataService} from '../dataservice';
import {PermissionType} from '../permission-type.class';
import {Permission} from '../permission.class';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.css']
})


export class TabsComponent implements OnInit {

  // settable variable to disable the tabs

  disabled = false;

  public mainTabsItems: MenuItem[];

  activeItem: MenuItem;

  currentTab;


  constructor(private router: Router, private dataService: DataService) {

    this.mainTabsItems = [
      {
        label: 'Home', icon: 'fa fa-fw fa-home', command: () => {
          this.router.navigate(['/tool/home']);
        }
      },
      {
        label: 'Projects', icon: 'fa fa-fw fa-cubes', command: () => {
          this.router.navigate(['/tool/projects']);
        }
      },
      {
        label: 'Users', icon: 'fa fa-fw fa-user', command: () => {
          this.router.navigate(['/tool/users']);
        }
      },
      // {
      //   label: 'Settings', icon: 'fa fa-fw fa-wrench', command: () => {
      //     this.router.navigate(['/tool/settings']);
      //   }
      // },
      {
        label: 'Taxonomies Management', icon: 'fa fa-fw fa-wrench', command: () => {
          this.router.navigate(['/tool/taxonomiesManagement']);
        }
      }
    ];

    this.currentTab = this.mainTabsItems[0];


    // to highlight the correct tab in every situation
    this.router.events.subscribe((url: any) => {

      if (url.url === '/tool/home') {
        this.currentTab = this.mainTabsItems[0];

      }

      if (url.url === '/tool/users') {

        this.currentTab = this.mainTabsItems[2];

      }
      if (url.url === '/tool/projects') {
        this.currentTab = this.mainTabsItems[1];

      }
      // if (url.url === '/tool/settings') {
      //   this.currentTab = this.mainTabsItems[3];
      //
      // }
      if (url.url === '/tool/taxonomiesManagement') {
        this.currentTab = this.mainTabsItems[3];

      }


    });

  }

  ngOnInit() {

    this.setPermissions();

  }

  setPermissions() {

    const permission = new Permission();
    permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));

    if (permission) {
      const indexViewProject = permission.view.indexOf(PermissionType.AssessmentProject);
      const indexViewUser = permission.view.indexOf(PermissionType.User);
      const indexViewTaxonomy = permission.view.indexOf(PermissionType.Taxonomy);
      this.setTabs(indexViewProject, 1);
      this.setTabs(indexViewUser, 2);
      this.setTabs(indexViewTaxonomy, 3);
    } else {
      this.mainTabsItems[1].disabled = true;
      this.mainTabsItems[2].disabled = true;

    }
  }

  private setTabs(viewIndex: number, tabIndex: number): void {
    if (viewIndex === -1) {
      this.mainTabsItems[tabIndex].disabled = true;
    } else {
      this.mainTabsItems[tabIndex].disabled = false;
    }
  }
}
