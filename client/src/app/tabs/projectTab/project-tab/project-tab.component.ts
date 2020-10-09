/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="project-tab.component.ts"
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
import {MenuItem, ConfirmationService} from 'primeng/primeng';
import {Router} from '@angular/router';
import {ChangeMenuService} from '../changeMenuService';
import {ChangeTabService} from '../../../changetabservice';
import {DataService} from '../../../dataservice';
import {Message} from 'primeng/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {PermissionType} from '../../../permission-type.class';
import {Permission} from '../../../permission.class';


@Component({
  selector: 'app-project-tab',
  templateUrl: './project-tab.component.html',
  styleUrls: ['./project-tab.component.scss'],
  providers: [ConfirmationService, ChangeMenuService]
})
export class ProjectTabComponent implements OnInit, OnDestroy {


  public projectitems: MenuItem[];
  public projectslist: any;
  public projectsListCols: any;
  public profileListCols: any;
  private profilesList: any;

  private selectedProject: any;

  public projectView = null;
  private actualID;

  private actualIDProfile;

  private permission;

  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService, private confirmationService: ConfirmationService, private router: Router,
              private changeMenuService: ChangeMenuService, private changeTabService: ChangeTabService) {

    this.projectsListCols = [
      {field: 'name', header: 'Project Name'},
      {field: 'projectManager.surname', header: 'Project Manager'},
      {field: 'systemProject.name', header: 'System Project'},
      {field: 'riskMethodology', header: 'Methodology'},
      {field: 'template.name', header: 'Template'},
      {field: 'creationTime', header: 'Creation Time'},
      {field: 'updateTime', header: 'Update Time'}
    ];
    this.profileListCols = [
      {field: 'name', header: 'Profile Name'},
      {field: 'organization', header: 'Organization'},
      {field: 'phase', header: 'Phase'},
      {field: 'riskMethodology', header: 'Methodology'}
    ];
    this.projectitems = [{
      label: 'Project',
      items: [
        {
          label: 'Create Project', icon: 'fa fa-fw fa-plus', command: () => {
            this.router.navigate(['/tool/createproject']);
          }
        },
        {
          label: 'View Projects', icon: 'fa fa-fw fa-plus', disabled: true, command: (event) => {
            this.blocked = true;
            this.getProjects();
            this.projectView = 'viewProject';
            (<MenuItem> this.projectitems[0].items[1]).disabled = true;
            (<MenuItem> this.projectitems[1].items[1]).disabled = false;
            (<MenuItem> this.projectitems[1].items[2]).disabled = true;
            (<MenuItem> this.projectitems[1].items[3]).disabled = true;
          }
        },
        {
          label: 'Load Project', icon: 'fa fa-fw fa-spinner', disabled: true, command: (event) => {

            this.changeTabService.changeTab(true);

          }
        },
        {
          label: 'Edit Project', icon: 'fa fa-fw fa-spinner', disabled: true, command: () => {
            this.router.navigate(['/tool/editproject']);
          }
        },
        {
          label: 'Delete Project', icon: 'fa fa-fw fa-minus', disabled: true, command: (event) => {

            this.confirmationService.confirm({
              message: 'Do you want to delete this project?',
              header: 'Delete Confirmation',
              icon: 'fa fa-trash',
              accept: () => {
                this.blocked = true;
                this.deleteProjectByID();

                this.projectslist = this.projectslist.filter(project => project.identifier !== this.actualID);
              }
            });
          }
        }
      ]
    },
      {
        label: 'Profile',
        items: [
          {
            label: 'Create Profile', icon: 'fa fa-fw fa-plus', command: () => {
              this.router.navigate(['/tool/createprofile']);
            }
          },
          {
            label: 'View Profiles', icon: 'fa fa-fw fa-plus', command: (event) => {

              this.getProfiles();
              (<MenuItem> this.projectitems[0].items[1]).disabled = false;
              (<MenuItem> this.projectitems[0].items[2]).disabled = true;
              (<MenuItem> this.projectitems[0].items[3]).disabled = true;
              (<MenuItem> this.projectitems[0].items[4]).disabled = true;

              this.projectView = 'viewProfile';

              (<MenuItem> this.projectitems[1].items[1]).disabled = true;
            }
          },
          {
            label: 'Edit Profile', icon: 'fa fa-fw fa-plus', disabled: true, command: () => {
              this.router.navigate(['/tool/editprofile']);
            }
          },
          {
            label: 'Delete Profile', icon: 'fa fa-fw fa-plus', disabled: true, command: (event) => {

              this.confirmationService.confirm({
                message: 'Are you sure you want to delete this profile?',
                header: 'Delete Confirmation',
                icon: 'fa fa-trash',
                accept: () => {
                  this.blocked = true;
                  this.deleteProfileByID();
                  this.profilesList = this.profilesList.filter(profile => profile.identifier !== this.actualIDProfile);
                }
              });
            }
          }
        ]
      }
    ];
  }

  ngOnInit() {

    this.blocked = true;
    sessionStorage.removeItem('idProject');
    this.subscriptions.push(
      this.dataService.refreshPermissionList().subscribe(tokenPermission => {

        this.setPermissions();
        this.getProjects();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  setPermissions() {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    (<MenuItem> this.projectitems[0].items[0]).disabled = (this.permission.create.findIndex(element => element === PermissionType.AssessmentProject) < 0);
    (<MenuItem> this.projectitems[1].items[0]).disabled = (this.permission.create.findIndex(element => element === PermissionType.AssessmentProfile) < 0);
    (<MenuItem> this.projectitems[1].items[1]).disabled = (this.permission.create.findIndex(element => element === PermissionType.AssessmentProfile) < 0);
  }

  getProjects(): void {

    this.subscriptions.push(
      this.dataService.getProjects().subscribe(response => {
          this.blocked = false;
          this.projectslist = response;

          this.projectView = 'viewProject';
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  getProfiles(): void {

    this.subscriptions.push(
      this.dataService.getProfiles().subscribe(response => {
          this.profilesList = response;
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  findID(s: string) {

    for (const i in this.projectslist) {

      if (this.projectslist[i].name === s) {

        // set id del project
        // this.dataService.setIDProjecy(this.projectslist[i].identifier)
        this.actualID = this.projectslist[i].identifier;
        this.dataService.setProjectId(this.projectslist[i].identifier)
      }
    }
  }

  activeProjectMenu(event) {

    this.findID(event.data.name);

    const indexProject = (this.permission.update.indexOf(event.data.identifier) < 0);
    const loadProject = (this.permission.read.indexOf(event.data.identifier) < 0);

    (<MenuItem> this.projectitems[0].items[3]).disabled = indexProject;
    (<MenuItem> this.projectitems[0].items[4]).disabled = indexProject;

    (<MenuItem> this.projectitems[0].items[2]).disabled = loadProject;
  }

  disactiveProjectMenu() {

    (<MenuItem> this.projectitems[0].items[1]).disabled = true;
    (<MenuItem> this.projectitems[0].items[2]).disabled = true;
    (<MenuItem> this.projectitems[0].items[3]).disabled = true;
    (<MenuItem> this.projectitems[0].items[4]).disabled = true;
  }

  activeProfileMenu(event) {
    this.findIdProfile(event.data.name);

    const profileIndex = (this.permission.create.findIndex(element => element === PermissionType.AssessmentProfile) < 0);

    (<MenuItem> this.projectitems[1].items[2]).disabled = profileIndex;
    (<MenuItem> this.projectitems[1].items[3]).disabled = profileIndex;

    (<MenuItem> this.projectitems[1].items[1]).disabled = false;
  }

  disactiveProfileMenu() {

    (<MenuItem> this.projectitems[1].items[1]).disabled = true;
    (<MenuItem> this.projectitems[1].items[2]).disabled = true;
    (<MenuItem> this.projectitems[1].items[3]).disabled = true;

  }

  findIdProfile(s: String) {

    for (const i in this.profilesList) {

      if (this.profilesList[i].name === s) {


        sessionStorage.setItem('idProfile', this.profilesList[i].identifier);
        this.actualIDProfile = this.profilesList[i].identifier;

      }
    }
  }


  // REST

  deleteProjectByID() {

    this.subscriptions.push(
      this.dataService.deleteProject(this.actualID).subscribe(result => {

          this.blocked = true;
          this.disactiveProjectMenu();
          this.getProjects();
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  showFailed(s: string) {
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

  deleteProfileByID() {

    this.subscriptions.push(
      this.dataService.deleteProfile(this.actualIDProfile).subscribe(result => {
          this.blocked = false;
          this.disactiveProfileMenu();
          this.getProfiles();

        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
