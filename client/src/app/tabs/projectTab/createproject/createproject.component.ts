/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="createproject.component.ts"
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
import {FormBuilder, Validators} from '@angular/forms';
import {Users} from '../../../users';
import {SelectItem} from 'primeng/primeng';
import {DataService} from '../../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';


@Component({
  selector: 'app-createproject',
  templateUrl: './createproject.component.html',
  styleUrls: ['./createproject.component.scss'],

})
export class CreateprojectComponent implements OnInit {

  public projectMenu: MenuItem[];

  public projectStep = 'form';

  projectForm: any;

  private actualForm: any;
  private actualSysProj: any;

  private profilesList: any;

  private templateList = [];

  private selectedProfile;

  private selectedTemplate;

  private alreadyCreate = false;

  private projectID = ' ';


  private users: any;

  sysusers = [];

  // usermanagement

  private usersM: SelectItem[];
  private selectedList: SelectItem[];

  // to activate div to see an user
  public selectedUser: string = null;


  // to activate div to edit an user
  public chosenUser: string = null;


  public selectedPartecipant: string = null;

  public displayRole = false;
  public displayRoleEdit = false;


  public riskAnalyst = 'RiskAnalyst';
  public reviewer = 'Reviewer';

  public selectedRole = '';

  private roles = [];
  private usersList: any;

  private systemPartecipantsList = [];

  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];


  // Add it to show the info
  public showProfileInfo = false;
  public showTemplateInfo = false;
  public showMandateInfo = false;
  public showScopeInfo = false;

  // -----------------------------------

  // System Project
  private systemprojectForm: any;
  private systempartecipansForm: any;
  private display = false;

  private savedSystemProj = {};

  private subscriptions: Subscription[] = [];

  // -----------------------------------


  constructor(private formBuilder: FormBuilder, private dataService: DataService, private router: Router) {


    this.projectMenu = [

      {
        label: 'Save Project', icon: 'fa fa-fw fa-floppy-o ', command: (event) => {

          this.sendCompleteProjectJson();


        }
      },
      {label: 'Cancel', icon: 'fa fa-fw fa-times ', routerLink: ['/tool/projects']}
    ];

    this.projectForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.maxLength(50)]],
      'profile': ['', Validators.required],
      'template': ['', [Validators.required]],
      'description': ['', [Validators.required, Validators.maxLength(800)]]


    });

    this.systemprojectForm = this.formBuilder.group({
      'names': ['', [Validators.required, Validators.maxLength(50)]],
      'mandate': ['', [Validators.required, Validators.maxLength(50)]],
      'scope': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptions': ['', [Validators.required, Validators.maxLength(800)]]

    });

    this.systempartecipansForm = this.formBuilder.group({
      'partecipansname': ['', [Validators.required, Validators.maxLength(20)]],
      'surname': ['', [Validators.required, Validators.maxLength(20)]],
      'role': ['', [Validators.required, Validators.maxLength(40)]]

    });


    this.usersM = [];
    this.selectedList = [];


    this.subscriptions.push(
      this.projectForm.valueChanges.subscribe(value => {

        if ((this.projectForm.valid)) {

          this.projectMenu[0].disabled = false;
        } else {

          this.projectMenu[0].disabled = true;

        }


      }));


  }

  ngOnInit() {
    this.getProfiles();


  }

  userManStep() {

    this.actualForm = this.projectForm.value;

    this.getUsers();


  }

  systProjStep() {


    this.actualForm = this.projectForm.value;

    this.projectStep = 'systproj';

  }

  formStepSyst() {

    this.actualSysProj = this.systemprojectForm.value;

    this.projectStep = 'form';


  }

  createSystemProject() {

    this.savedSystemProj = {
      'name': this.systemprojectForm.value.names,
      'mandate': this.systemprojectForm.value.mandate,
      'scope': this.systemprojectForm.value.scope,
      'description': this.systemprojectForm.value.descriptions,
      'participants': this.systemPartecipantsList
    };
    this.projectStep = 'form';


  }


  addUser(s: string) {

    for (const i in this.usersM) {
      if (this.usersM[i].value === s) {

        this.usersM.splice(parseInt(i), 1);
      }
    }

    for (const i in this.usersList) {
      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === s) {
        const id = this.usersList[i].identifier;

        const b = [{'role': this.selectedRole}];
        this.roles.push({'identifier': id, 'roles': b});
      }
    }
    this.selectedList.push({label: s, value: s});
    this.selectedUser = null;
    this.displayRole = false;
    this.selectedRole = '';
  }

  removeUser(s: string) {

    for (const i in this.usersList) {
      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === s) {
        const id = this.usersList[i].identifier;

        for (const j in this.roles) {
          if (this.roles[i].identifier === id) {
            this.roles.splice(parseInt(j), 1);
          }
        }
      }
    }
    for (const i in this.selectedList) {
      if ((this.selectedList[i].label === s) && (this.selectedList[i].value === s)) {
        this.selectedList.splice(parseInt(i), 1);
      }
    }

    this.usersM.push({label: s, value: s});
  }


  editUser() {

    this.chosenUser = null;
    this.selectedRole = '';
    this.displayRoleEdit = false;
  }


  editRoles(user) {
    this.displayRoleEdit = true;
    let id;

    for (const i in this.usersList) {

      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === user) {
        id = this.usersList[i].identifier;
      }
    }

    for (const j in this.roles) {
      if (this.roles[j].identifier === id) {
        this.selectedRole = this.roles[j].roles[0].role;
      }
    }
  }

  // to disactive the view user div
  deselectFirstList() {

    this.selectedUser = null;

  }

  // to disactive the edit div
  deselectSecondList(s) {

    this.chosenUser = null;
    const button = document.getElementById('setB').setAttribute('disabled', 'disabled');
  }


  showDialog() {
    this.display = true;
  }

  backForm() {

    this.projectStep = 'form';
  }

  addSystemPartecipant() {

    this.systemPartecipantsList.push({
      'name': this.systempartecipansForm.value.partecipansname,
      'surname': this.systempartecipansForm.value.surname,
      'role': this.systempartecipansForm.value.role
    });

    this.sysusers.push({
      'label': this.systempartecipansForm.value.partecipansname + ' ' + this.systempartecipansForm.value.surname + ' ' + this.systempartecipansForm.value.role,
      'value': this.systempartecipansForm.value.partecipansname + ' ' + this.systempartecipansForm.value.surname + ' ' + this.systempartecipansForm.value.role
    });


    this.systempartecipansForm.reset();
    this.display = false;

  }

  editProject() {

  }


  sendCompleteProjectJson() {


    let systemArray = {};


    this.actualForm = this.projectForm.value;


    const methodology = this.getMethodology(this.actualForm.profile);

    const template = {'identifier': this.actualForm.template};

    const profile = {'identifier': this.actualForm.profile};

    const projMan = {'identifier': sessionStorage.getItem('loggedUserId')};
    if (!this.alreadyCreate) {


      systemArray = {
        'name': this.actualForm.name,
        'profile': profile,
        'template': template,
        'description': this.actualForm.description,
        'riskMethodology': methodology,
        'status': 'OnGoing',
        'systemProject': this.savedSystemProj,
        'users': this.roles,
        'projectManager': projMan
      };

      this.blocked = true;
      this.createProject(JSON.stringify(systemArray));
      this.alreadyCreate = true;

    } else {

      if (this.projectID.length > 1) {

        systemArray = {
          'identifier': this.projectID,
          'name': this.actualForm.name,
          'profile': profile,
          'template': template,
          'description': this.actualForm.description,
          'riskMethodology': methodology,
          'status': 'OnGoing',
          'systemProject': this.savedSystemProj,
          'users': this.roles,
          'projectManager': projMan
        };
      } else {

        let projectsList: any;

        this.subscriptions.push(
          this.dataService.getProjects().subscribe(response => {
            projectsList = response;
            this.getIDandSend(projectsList, profile, template, methodology, projMan);
          }));
      }
    }
  }

  getIDandSend(projectsList: any, profile: any, template: any, methodology: string, projMan: any) {

    for (const i in projectsList) {

      if (projectsList[i].name === this.actualForm.name) {


        this.projectID = projectsList[i].identifier;

      }


    }
    const systemArray = {
      'identifier': this.projectID,
      'name': this.actualForm.name,
      'profile': profile,
      'template': template,
      'description': this.actualForm.description,
      'riskMethodology': methodology,
      'status': 'OnGoing',
      'systemProject': this.savedSystemProj,
      'users': this.roles,
      'projectManager': projMan
    };

    this.dataService.updateProject(JSON.stringify(systemArray));

  }


  createProject(s: Object) {

    this.subscriptions.push(
      this.dataService.insertProject(s).subscribe(response => {

        this.blocked = false;

        this.router.navigate(['/tool/projects']);

      }, err => {
        this.blocked = false;
        throw err;
      }));

  }

  showSuccess(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Import complete', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
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
    this.router.navigate(['/tool/projects']);
  }

  getMethodology(id: string) {

    for (const i in this.profilesList) {

      if (this.profilesList[i].identifier === id) {

        return this.profilesList[i].riskMethodology;

      }

    }


  }


  backRoles() {

    this.selectedRole = '';
    this.displayRoleEdit = false;
    this.displayRole = false;
  }

  // show roles

  showRoles() {

    this.displayRole = true;
  }

  getProfiles(): void {

    this.subscriptions.push(
      this.dataService.getProfiles().subscribe(response => {
        this.profilesList = response;
        // this.insertComboProf("profile")

        // console.log("Profiles")
        // console.log(this.profilesList)
      }));

  }

  getTemplateByProfile(): void {


    // let a={"filterMap":{"PROFILE":this.selectedProfile}}

    const a = {
      'filterMap': {
        'PROFILE': this.selectedProfile,
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    // console.log(this.selectedProfile)

    this.subscriptions.push(
      this.dataService.loadTemplateByProfile(JSON.stringify(a)).subscribe(response => {
        // this.templateList = response;
        // this.insertComboTemp("template")

        this.converterTemplate(response);
      }));

  }

  converterTemplate(array) {

    this.templateList = [];

    for (const i in array) {

      const a = JSON.parse(JSON.stringify(array[i]));
      const b = JSON.parse(a);
      this.templateList.push(b);


    }


  }

  getUsers(): void {

    this.subscriptions.push(
      this.dataService.getUsers().subscribe(response => {
        this.usersList = response;

        this.firstList();

        this.actualForm = this.projectForm.value;

        this.projectStep = 'userman';


      }));

  }

  firstList() {

    const loggedUser = JSON.parse(atob(sessionStorage.getItem('authnToken'))).username;

    // to avoid the logged User
    if ((this.usersM.length === 0) && (this.selectedList.length === 0)) {

      for (const i in this.usersList) {
        if (this.usersList[i].username != loggedUser) {
          // value: this.usersList[i].name + " " + this.usersList[i].surname
          this.usersM.push({
            label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,

            value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
          });
        }
      }
    } else if ((this.usersM.length != 0) && (this.selectedList.length === 0)) {

      for (const i in this.usersList) {
        let a = 0;
        for (const j in this.usersM) {
          if (this.usersM[j].label === this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email) {

            a = a + 1;
          }
        }

        if (a == 0) {

          if (this.usersList[i].username != loggedUser) {

            this.usersM.push({
              label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,
              value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
            });
          }
        }
      }
    } else {

      for (const i in this.usersList) {

        let a = 0;
        let b = 0;

        for (const j in this.selectedList) {
          i;
          if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === this.selectedList[j].value) {

            b = b + 1;

          }

        }
        if (b === 0) {


          for (const j in this.usersM) {

            if (this.usersM[j].label === this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email) {

              a = a + 1;
            }

          }

          if (a == 0) {

            // to avoid the logged User
            if (this.usersList[i].username != loggedUser) {

              this.usersM.push({
                label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,
                value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
              });

            }
          }


        }


      }


    }


  }

  // it loads corrensponding Template after a Profile selection

  checkedTemplate() {

    if (this.selectedProfile != undefined) {

      this.getTemplateByProfile();
    }

  }

  onChangeEdit(u: string, s: string, isChecked: boolean) {

    let id;
    let a = 0;

    for (const i in this.usersList) {

      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === u) {
        // if(this.usersList[i].name+" "+this.usersList[i].surname===u){
        id = this.usersList[i].identifier;


      }


    }


    if (isChecked) {


      for (const j in this.roles) {

        if (this.roles[j].identifier === id) {

          a++;
          this.roles[j].roles.push({'role': s});


        }


      }
      if (a == 0) {

        const b = [{'role': s}];
        this.roles.push({'identifier': id, 'roles': b});

        a = 0;

      }


    }

    if (!isChecked) {

      for (const j in this.roles) {

        if (this.roles[j].identifier === id) {


          for (const x in this.roles[j].roles) {


            if (this.roles[j].roles[x].role === s) {

              if (this.roles[j].roles.length === 1) {

                const d = parseInt(j);

                this.roles.splice(d, 1);

              } else {

                const e = parseInt(x);

                this.roles[j].roles.splice(e, 1);

              }


            }

          }


        }


      }


    }

    // console.log("complete")
    //  console.log(this.roles)

  }

  onChangeCheck(u: string, s: string, isChecked: boolean) {


    //  console.log("stamp")
    //  console.log(u)

    let id;
    let a = 0;

    for (const i in this.usersList) {

      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === u) {
        // if(this.usersList[i].name+" "+this.usersList[i].surname===u){
        id = this.usersList[i].identifier;


      }


    }


    if (isChecked) {


      for (const j in this.roles) {

        if (this.roles[j].identifier === id) {

          a++;
          this.roles[j].roles.push({'role': s});

        }

      }
      if (a == 0) {

        const b = [{'role': s}];
        this.roles.push({'identifier': id, 'roles': b});

        a = 0;

      }

    }

    if (!isChecked) {

      for (const j in this.roles) {

        if (this.roles[j].identifier === id) {


          for (const x in this.roles[j].roles) {


            if (this.roles[j].roles[x].role === s) {

              if (this.roles[j].roles.length === 1) {

                const d = parseInt(j);

                this.roles.splice(d, 1);

              } else {

                const e = parseInt(x);

                this.roles[j].roles.splice(e, 1);

              }


            }

          }

        }

      }

    }

    // console.log("complete")
    // console.log(this.roles)

  }


  removePartecipant(selectedPartecipant) {


    for (const i in this.sysusers) {

      if (this.sysusers[i].label === selectedPartecipant) {


        this.sysusers.splice(parseInt(i), 1);

      }

    }

    for (const j in this.systemPartecipantsList) {

      const nospace = selectedPartecipant.split(' ');
      if ((this.systemPartecipantsList[j].name === nospace[0]) && (this.systemPartecipantsList[j].surname === nospace[1]) && (this.systemPartecipantsList[j].role === nospace[2])) {


        this.systemPartecipantsList.splice(parseInt(j), 1);

      }

    }

    this.selectedPartecipant = null;

    // console.log(this.systemPartecipantsList)

  }

  infoProf() {

    this.showProfileInfo = true;
  }

  infoTemplate() {

    this.showTemplateInfo = true;
  }

  infoMandate() {

    this.showMandateInfo = true;
  }

  infoScope() {

    this.showScopeInfo = true;
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
