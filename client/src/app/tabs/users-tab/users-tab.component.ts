/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="users-tab.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Directive, Component, OnInit, ViewEncapsulation, OnDestroy} from '@angular/core';
import {Users} from '../../users';
import {MenuItem, ConfirmationService} from 'primeng/primeng';
import {DataService} from '../../dataservice';
import {FormBuilder, Validators} from '@angular/forms';
import {ValidationService} from '../../validationservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {PermissionType} from '../../permission-type.class';
import {Permission} from '../../permission.class';
import {MessageService} from 'primeng/api';


@Component({
  selector: 'app-users-tab',
  templateUrl: './users-tab.component.html',
  styleUrls: ['./users-tab.component.scss'],
  providers: [ConfirmationService]
})

export class UsersTabComponent implements OnInit, OnDestroy {

  edituserForm: any;
  public usersListCols: any;
  public case = 'view1';
  private users: any;
  private selectedId: string;
  selectedUser: Users;
  private selectedPass: string;
  listRoles: any;
  public items: MenuItem[];
  private permission;
  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  openRegister = false;

  public displayEditPassword = false;
  public selectedUsername;

  private subscriptions: Subscription[] = [];

  constructor(private confirmationService: ConfirmationService, private dataService: DataService,
              private formBuilder: FormBuilder, private messageService: MessageService) {

    this.usersListCols = [
      {field: 'username', header: 'Username'},
      {field: 'name', header: 'Name'},
      {field: 'surname', header: 'Surname'},
      {field: 'email', header: 'Email'},
      {field: 'profile', header: 'Profile'}
    ];

    this.edituserForm = this.formBuilder.group({
      'firstname': ['', [Validators.required, Validators.maxLength(20)]],
      'surname': ['', [Validators.required, Validators.maxLength(20)]],
      'email': ['', [Validators.required, ValidationService.emailValidator, Validators.maxLength(30)]],
      'username': ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), Validators.maxLength(20)]],
      'profile': ['', [Validators.required]]
    });

    this.items = [
      {
        label: 'Add User', icon: 'fa fa-fw fa-user-plus', command: (event) => {
          this.openRegistration();
        }
      },
      {
        label: 'Delete User', icon: 'fa fa-fw fa-user-times ', disabled: true, command: (event) => {

          this.confirmationService.confirm({
            message: 'Do you want to delete this user?',
            header: 'Delete Confirmation',
            icon: 'fa fa-trash',
            accept: () => {
              this.findSelectedId();
              this.removeUser(this.selectedId);
            }
          });
        }
      },
      {
        label: 'Configure User', icon: 'fa fa-fw fa-pencil-square-o ', disabled: true, command: (event) => {
          this.findSelectedId();
          this.case = 'view2';
          this.items[1].disabled = true;
          this.items[2].disabled = true;
          // this.items[3].disabled=true;
          this.copyEditData();
        }
      }
      // {label: 'Experience', icon: 'fa-graduation-cap', disabled:true},
    ];
  }

  ngOnInit() {
    this.setPermission();
  }

  setPermission() {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.items[0].disabled = (this.permission.create.findIndex(element => element === PermissionType.User) < 0);

    this.getUsers();
  }

  getUsers(): void {

    this.subscriptions.push(
      this.dataService.getUsers().subscribe(response => {
          this.users = response;
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  removeUser(s: string) {
    this.subscriptions.push(
      this.dataService.deleteUser(s).subscribe(response => {
          this.getUsers();
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  onRowSelect(event) {
    const userIndex = this.permission.create.indexOf('User');

    if (userIndex !== -1) {
      this.items[2].disabled = false;
      this.items[1].disabled = false;

      // this.items[3].disabled=false;

    } else {
      this.items[1].disabled = true;
      this.items[2].disabled = true;
      // this.items[3].disabled=true;
    }

    if (event.data.username === JSON.parse(atob(sessionStorage.getItem('authnToken'))).username) {
      this.items[1].disabled = true;
    }

    this.selectedUser = {
      'ID': event.data.identifier,
      'Username': event.data.username,
      'Name': event.data.name,
      'Surname': event.data.surname,
      'Email': event.data.email,
      'Profile': event.data.profile
    };
  }

  onRowUnselect(event) {
    // this.msgs = [];
    // this.msgs.push({severity: 'info', summary: 'Car Unselected', detail: event.data.vin + ' - ' + event.data.brand});
    this.items[1].disabled = true;
    this.items[2].disabled = true;
    // this.items[3].disabled=true;
  }

  expandUserRow(event) {
    this.listRoles = [];
    for (const roleItem of event.data.roles) {
      const newRoles = [];
      newRoles.push(roleItem.role);
      this.listRoles.push({'id': roleItem.projectName, 'rol': newRoles});
    }
  }

  openRegistration() {

    // to disable the menu items
    this.items[0].disabled = true;
    this.items[1].disabled = true;
    this.items[2].disabled = true;
    // this.items[3].disabled=true;

    this.openRegister = true;
  }

  closeRegistration() {

    this.items[0].disabled = false;

    this.openRegister = false;
    this.getUsers();
  }

  sendFeed(change: true) {
    this.closeRegistration();
  }

  // to pass in Edit User mode
  changeCase() {

    if (this.case === 'view1') {
      this.case = 'view2';
    } else {
      this.case = 'view1';
    }
    this.items[1].disabled = false;
    this.items[2].disabled = false;
    // this.items[3].disabled=false;
  }

  findSelectedId() {
    for (const item in this.users) {
      if (this.selectedUser.Email === this.users[item].email) {
        this.selectedId = this.users[item].identifier;
      }
    }
  }

  findSelectedPass() {

    for (const item in this.users) {
      if (this.selectedUser.Email === this.users[item].email) {
        this.selectedPass = this.users[item].password;
      }
    }
  }

  sendEditUser() {

    let arrayform: any;
    let jsonform: any;

    arrayform = {
      'identifier': this.selectedId,
      'username': this.edituserForm.value.username,
      'name': this.edituserForm.value.firstname,
      'surname': this.edituserForm.value.surname,
      'email': this.edituserForm.value.email,
      'password': this.edituserForm.value.password,
      'profile': this.edituserForm.value.profile
    };
    jsonform = JSON.stringify(arrayform);

    this.subscriptions.push(
      this.dataService.updateUser(jsonform).subscribe(response => {

          this.changeCase();
          this.getUsers();
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  showSuccess(s: string) {
    this.blockedMessage = true;
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Successfully', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 6000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  copyEditData() {

    this.findSelectedPass();

    this.edituserForm.controls['firstname'].setValue(this.selectedUser.Name);
    this.edituserForm.controls['surname'].setValue(this.selectedUser.Surname);
    this.edituserForm.controls['username'].setValue(this.selectedUser.Username);
    this.edituserForm.controls['profile'].setValue(this.selectedUser.Profile);

    this.edituserForm.controls['email'].setValue(this.selectedUser.Email);
    this.edituserForm.controls['password'].setValue(this.selectedPass);
  }

  displayPassword() {
    this.displayEditPassword = true;
  }

  onClose(value) {
    this.displayEditPassword = false;
    if (value === 'SUCCESS') {
      this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Password updated!'});
    }

    if (value === 'FAILED') {
      this.messageService.add({severity: 'error', summary: 'Error Message', detail: 'Password update failed!'});
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
