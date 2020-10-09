/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="project.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataService} from '../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit, OnDestroy {

  public users: any;
  public parti: any;

  private project: any;

  public nameProject;
  public profile;
  public template;
  public descrProject;
  public nameSys;
  public scope;
  public mandate;
  public descrSyst;

  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService) {
  }

  ngOnInit() {
    const a = {'filterMap': {'PROJECT': sessionStorage.getItem('idProject')}};
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.refreshPermissionList().subscribe(
        response => {
          this.getProject(a);
        }, err => {
          this.blocked = false;
          throw err;
        }));
  }


  getProject(s) {

    this.subscriptions.push(
      this.dataService.loadProject(s).subscribe(response => {
        this.blocked = false;
        this.project = response;

        this.nameProject = this.project.name;
        this.profile = this.project.profile.name;
        this.template = this.project.template.name;
        this.descrProject = this.project.description;
        this.users = this.project.users;
        this.nameSys = this.project.systemProject.name;
        this.scope = this.project.systemProject.scope;
        this.mandate = this.project.systemProject.mandate;
        this.descrSyst = this.project.systemProject.description;
        this.parti = this.project.systemProject.participants;

        sessionStorage.setItem('sysprojectId', this.project.systemProject.identifier);
        sessionStorage.setItem('idProfileProj', this.project.profile.identifier);
        sessionStorage.setItem('sysproject', JSON.stringify(this.project.systemProject));
        sessionStorage.setItem('methodologyProj', this.project.riskMethodology);
      }, err => {
        this.blocked = false;
        throw err;
      }));
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
