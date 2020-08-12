/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="editproject.component.ts"
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
import {Router} from '@angular/router';
import {MenuItem} from 'primeng/primeng';
import {FormBuilder, Validators} from '@angular/forms';
import {SelectItem} from 'primeng/primeng';
import {DataService} from '../../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-editproject',
  templateUrl: './editproject.component.html',
  styleUrls: ['./editproject.component.scss'],
})
export class EditprojectComponent implements OnInit, OnDestroy {

  public projectMenu: MenuItem[];

  public projectStep = 'form';

  projectForm: any;

  private actualForm: any;
  private actualSysProj: any;

  private profilesList = [];

  private templateList = [];

  private selectedProfile;

  private selectedTemplate;

  private alreadyCreate = false;

  private projectID = ' ';


  private users: any;


  private project: any;

  sysusers = [];

  // usermanagement

  private usersM: SelectItem[];
  private selectedList: SelectItem[];

  // to activate div to see an user
  public selectedUser: string = null;


  // to activate div to edit an user
  public chosenUser: string = null;


  private selectedPartecipant: string = null;

  public displayRole = false;
  public displayRoleEdit = false;

  public selectedRole = '';

  private roles = [];
  private usersList: any;

  private systemPartecipantsList = [];

  // -----------------------------------

  // System Project
  private systemprojectForm: any;
  private systempartecipansForm: any;
  private display = false;

  private savedSystemProj = {};

  // -----------------------------------

  public showMandateInfo = false;
  public showScopeInfo = false;

  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  private filenames: any;

  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private router: Router) {

    this.projectMenu = [

      {
        label: 'Save Project', icon: 'fa fa-fw fa-floppy-o ', command: (event) => {

          // this.sendCompleteProjectJson();
          this.editProject();


        }
      },
      {label: 'Cancel', icon: 'fa-times ', routerLink: ['/tool/projects']}
    ];

    this.projectForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.maxLength(50)]],
      'profile': ['', Validators.required],
      'template': ['', [Validators.required]],
      'description': ['', [Validators.required, Validators.maxLength(800)]]


    });

    this.systemprojectForm = this.formBuilder.group({
      'names': ['', [Validators.required, Validators.maxLength(50)]],
      'mandate': ['', [Validators.required, Validators.maxLength(250)]],
      'scope': ['', [Validators.required, Validators.maxLength(400)]],
      'descriptions': ['', [Validators.required, Validators.maxLength(800)]]

    });

    this.systempartecipansForm = this.formBuilder.group({
      'partecipansname': ['', [Validators.required, Validators.maxLength(20)]],
      'surname': ['', [Validators.required, Validators.maxLength(20)]],
      'role': ['', [Validators.required, Validators.maxLength(40)]]

    });


    this.usersM = [];
    this.selectedList = [];


  }

  ngOnInit() {

    const a = {'filterMap': {'PROJECT': sessionStorage.getItem('idProject')}};
    this.getProject(JSON.stringify(a));
  }

  userManStep() {

    this.actualForm = this.projectForm.value;

    this.getUsers();


  }

  systProjStep() {

    this.actualForm = this.projectForm.value;

    this.projectStep = 'systproj';

    this.filenameUploaded();

  }

  formStepSyst() {

    this.actualSysProj = this.systemprojectForm.value;

    this.projectStep = 'form';


  }

  createSystemProject() {

    this.savedSystemProj = {
      'identifier': this.project.systemProject.identifier,
      'name': this.systemprojectForm.value.names,
      'mandate': this.systemprojectForm.value.mandate,
      'scope': this.systemprojectForm.value.scope,
      'description': this.systemprojectForm.value.descriptions,
      'participants': this.systemPartecipantsList
    };
    this.projectStep = 'form';


  }


  addUser(selectedUser: string) {

    let id;
    for (const i in this.usersM) {
      if (this.usersM[i].value === selectedUser) {
        this.usersM.splice(Number(i), 1);
      }
    }
    this.selectedList.push({label: selectedUser, value: selectedUser});

    let selectUserComplete;
    for (const i in this.usersList) {
      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === selectedUser) {
        selectUserComplete = this.usersList[i];
      }
    }
    if (selectUserComplete) {
      const roleElement = {'projectIdentifier': this.projectID, 'role': this.selectedRole};
      selectUserComplete.roles = [roleElement];
      this.roles.push(selectUserComplete);
    }

    this.selectedUser = null;
    this.selectedRole = '';
    this.displayRole = false;
  }

  removeUser(s: string) {

    for (const i in this.selectedList) {

      if ((this.selectedList[i].label === s) && (this.selectedList[i].value === s)) {

        this.selectedList.splice(Number(i), 1);
      }
    }

    const email = s.split(' ');

    for (const j in this.roles) {
      if (this.roles[j].email === email[email.length - 1]) {
        this.roles.splice(Number(j), 1);
      }
    }


    this.usersM.push({label: s, value: s});
  }


  editUser() {

    let id = this.chosenUser;
    let a = 0;

    for (const i in this.usersList) {
      if (this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email === this.chosenUser) {
        id = this.usersList[i].identifier;
      }
    }

    for (const j in this.roles) {
      if (this.roles[j].identifier === id && this.roles[j].roles) {
        const index = this.roles[j].roles.findIndex(role => (role.projectIdentifier === this.projectID));
        const roleElement = this.roles[j].roles[index];
        roleElement.role = this.selectedRole;

        this.roles[j].roles[index] = roleElement;
      }
    }
    this.chosenUser = null;
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
    const foundUser = this.roles.find(userFromRoleList => userFromRoleList.identifier === id);
    if (foundUser && foundUser.roles[0] && foundUser.roles[0].role) {
      this.selectedRole = foundUser.roles[0].role;
    }
  }


  // to disactive the view user div
  deselectFirstList() {

    this.selectedUser = null;

    // This to prevent removal of Project Manager
    if (this.chosenUser === (this.project.projectManager.name + ' ' + this.project.projectManager.surname + ' ' + this.project.projectManager.email)) {
      this.chosenUser = null;

    }

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

    // this.projectForm.controls['name'].setValue(this.actualForm.name);
    // this.projectForm.controls['template'].setValue(this.selectedUser.template);
    // this.projectForm.controls['description'].setValue(this.selectedUser.description);
    // this.projectForm.controls['profile'].setValue(this.selectedUser.profile);
    // this.projectForm.controls['profile'].setValue()

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

    const projMan = {'identifier': '8e56ce50-7ea2-11e7-96f1-0200c0a80208'};

    const systemArray = {
      'identifier': this.projectID,
      'name': this.projectForm.value.name,
      'profile': this.project.profile,
      'template': this.project.template,
      'description': this.projectForm.value.description,
      'riskMethodology': this.project.riskMethodology,
      'status': 'OnGoing',
      'systemProject': this.savedSystemProj,
      'users': this.roles,
      'projectManager': this.project.projectManager
    };

    this.blocked = true;
    this.editproject(systemArray);

  }


  editproject(systemArray) {

    this.subscriptions.push(
      this.dataService.updateProject(JSON.stringify(systemArray)).subscribe(response => {

        this.blocked = false;
        this.router.navigate(['/tool/projects']);
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  showSuccessAndNavigate(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Import complete', detail: s});

    setTimeout(() => {
      this.clearMessageAndNavigate();
    }, 4000);
  }

  showFailedAndNavigate(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'error', summary: 'Error', detail: s});

    setTimeout(() => {
      this.clearMessageAndNavigate();
    }, 4000);
  }

  showSuccess(s: string) {
    this.blockedMessage = true;
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
    }, 4000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  clearMessageAndNavigate() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
    this.router.navigate(['/tool/projects']);
  }


  createProject(s: Object) {

    this.subscriptions.push(
      this.dataService.insertProject(s).subscribe(response => {
        this.router.navigate(['/tool/projects']);
      }));
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


  getTemplateByProfile(): void {

    // let a={"filterMap":{"PROFILE":this.selectedProfile}}
    const a = {'filterMap': {'PROFILE': this.selectedProfile,
        'PROJECT': sessionStorage.getItem('idProject')}};
    this.subscriptions.push(
      this.dataService.loadTemplateByProfile(JSON.stringify(a)).subscribe(response => {
        // this.templateList = response;
        // this.insertComboTemp("template")

        this.converterTemplate(response);
      }));
  }

  converterTemplate(array) {

    for (const i in array) {
      const a = JSON.parse(JSON.stringify(array[i]));
      const b = JSON.parse(a);
      this.templateList.push(b);
    }
  }


  getProject(s) {

    this.subscriptions.push(
      this.dataService.loadProject(s).subscribe(response => {
        this.project = response;

        this.projectID = this.project.identifier;
        this.projectForm.controls['name'].setValue(this.project.name);
        this.projectForm.controls['profile'].setValue(this.project.profile.identifier);
        this.projectForm.controls['template'].setValue(this.project.template.identifier);
        this.projectForm.controls['description'].setValue(this.project.description);

        this.profilesList.push({'name': this.project.profile.name, 'identifier': this.project.profile.identifier});
        this.templateList.push({'name': this.project.template.name, 'identifier': this.project.template.identifier});

        this.systemprojectForm.controls['names'].setValue(this.project.systemProject.name);
        this.systemprojectForm.controls['mandate'].setValue(this.project.systemProject.mandate);
        this.systemprojectForm.controls['descriptions'].setValue(this.project.systemProject.description);
        this.systemprojectForm.controls['scope'].setValue(this.project.systemProject.scope);

        for (const i in this.project.systemProject.participants) {

          this.systemPartecipantsList.push({
            'name': this.project.systemProject.participants[i].name,
            'surname': this.project.systemProject.participants[i].surname,
            'role': this.project.systemProject.participants[i].role
          });

          this.sysusers.push({
            'label': this.project.systemProject.participants[i].name + ' ' + this.project.systemProject.participants[i].surname + ' ' + this.project.systemProject.participants[i].role,
            'value': this.project.systemProject.participants[i].name + ' ' + this.project.systemProject.participants[i].surname + ' ' + this.project.systemProject.participants[i].role
          });


        }

        for (const j in this.project.users) {

          this.selectedList.push({
            label: this.project.users[j].name + ' ' + this.project.users[j].surname + ' ' + this.project.users[j].email,
            value: this.project.users[j].name + ' ' + this.project.users[j].surname + ' ' + this.project.users[j].email
          });
        }


        this.roles = this.project.users;

        this.createSystemProject();

        // }

      }));

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

    if ((this.usersM.length === 0) && (this.selectedList.length === 0)) {

      for (const i in this.usersList) {

        // value: this.usersList[i].name + " " + this.usersList[i].surname
        this.usersM.push({
          label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,

          value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
        });

      }
    } else if ((this.usersM.length !== 0) && (this.selectedList.length === 0)) {

      for (const i in this.usersList) {
        let a = 0;


        for (const j in this.usersM) {

          if (this.usersM[j].label === this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email) {

            a = a + 1;
          }

        }

        if (a == 0) {
          this.usersM.push({
            label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,
            value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
          });
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
            this.usersM.push({
              label: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email,
              value: this.usersList[i].name + ' ' + this.usersList[i].surname + ' ' + this.usersList[i].email
            });
          }


        }


      }


    }


  }

  // it loads corrensponding Template after a Profile selection

  checkedTemplate() {

    if (this.selectedProfile !== undefined) {

      this.getTemplateByProfile();
    }

  }

  onChangeRoleCheck(u: string, s: string, isChecked: boolean) {

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
                const d = Number(j);
                this.roles.splice(d, 1);
              } else {
                const e = Number(x);
                this.roles[j].roles.splice(e, 1);
              }
            }
          }
        }
      }
    }
  }


  removePartecipant(selectedPartecipant) {

    for (const i in this.sysusers) {

      if (this.sysusers[i].label === selectedPartecipant) {

        this.sysusers.splice(Number(i), 1);
      }
    }

    for (const j in this.systemPartecipantsList) {

      const nospace = selectedPartecipant.split(' ');
      if ((this.systemPartecipantsList[j].name === nospace[0]) && (this.systemPartecipantsList[j].surname === nospace[1]) && (this.systemPartecipantsList[j].role === nospace[2])) {

        this.systemPartecipantsList.splice(Number(j), 1);
      }
    }
    this.selectedPartecipant = null;
  }

  onBasicUpload(event) {
  }


  myUploader(event) {

    this.dataService.setSystemProject(this.project.systemProject.identifier);
    this.realUpload(event);
  }

  realUpload(event) {
    this.blocked = true;

    this.subscriptions.push(
      this.dataService.uploadRequirement(event.target.files).subscribe(response => {
          this.blocked = false;
          this.showSuccess('Requirements imported!');
        },
        err => {
          this.blocked = false;
          throw err;
        }));

    this.filenameUploaded();
  }

  filenameUploaded() {

    const a = {'filterMap': {'SYS_PROJECT': this.project.systemProject.identifier,
        'PROJECT': sessionStorage.getItem('idProject')}};
    this.subscriptions.push(
      this.dataService.listUploadedFilename(JSON.stringify(a)).subscribe(response => {

          this.filenames = response;
        }, err => {
        }
      ));
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
