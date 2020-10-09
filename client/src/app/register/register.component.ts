/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="register.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnInit, EventEmitter, Output, ViewEncapsulation, OnDestroy, Input} from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ValidationService } from '../validationservice';
import { DataService } from '../dataservice';
import { Message } from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {MessageService} from 'primeng/api';
import {User} from '../shared/model/user.class';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {

  @Input()
  private users: User[];

  userForm: any;
  usernamecomp: string;

  public kindTypeConfirm = 'password';
  public kindTypePass = 'password';

  public glyConf = true;
  public glyPass = true;

  private userCreation: any;

  public blocked = false;

  private subscriptions: Subscription[] = [];

  @Output() sendFeed = new EventEmitter<boolean>();

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private messageService: MessageService) {


    this.userForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      'surname': ['', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      'email': ['', [Validators.required, ValidationService.emailValidator, Validators.maxLength(30)]],
      'username': ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20), ValidationService.passwordUsernameCheckValidator]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator, ValidationService.usernameCheckValidator, Validators.maxLength(20), ValidationService.passwordMatchValidatorForPassword]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]],
      'profile': ['', [Validators.required]],
    });


    this.userForm.valueChanges.subscribe(value => {
      this.usernamecomp = value.username;
    });
  }

  saveUser() {
    if (this.userForm.dirty && this.userForm.valid) {
      alert(`Name: ${this.userForm.value.name} Email: ${this.userForm.value.email}`);
    }
  }

  ngOnInit() {
    console.log(this.users);
  }


  sendFeedback(change: true) {

    this.sendFeed.emit(change);
    this.clearForm();
  }

  createUser() {

    const alreadyExistingUser = this.users.filter(x => x.email == this.userForm.value.email ||
      x.username == this.userForm.value.username);

    if (!alreadyExistingUser || alreadyExistingUser.length <= 0) {
      this.blocked = true;

      this.subscriptions.push(
        this.dataService.insertUser(JSON.stringify(this.userForm.value)).subscribe((response: any) => {
            this.userCreation = response.response;

            this.blocked = false;
            this.sendFeedback(true);

            if (this.userForm.valid) {
              this.userForm.reset();
            }
          },
          err => {
            this.blocked = false;
            throw err;
          }));
    } else {

      this.messageService.add({severity: 'error', summary: 'Warning', detail: 'Already existing user!'});
    }
  }

  clearMessage() {
    this.blocked = false;
  }

  clearForm() {

    this.userForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      'surname': ['', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      'email': ['', [Validators.required, ValidationService.emailValidator, Validators.maxLength(30)]],
      'username': ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator, ValidationService.usernameCheckValidator, Validators.maxLength(20)]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]],
      'profile': ['', [Validators.required]],
    });
  }

  changeConfirm() {

    if (this.kindTypeConfirm === 'password') {
      this.kindTypeConfirm = 'text';
      this.glyConf = false;
    } else {
      this.kindTypeConfirm = 'password';
      this.glyConf = true;
    }
  }

  changePass() {

    if (this.kindTypePass === 'password') {
      this.kindTypePass = 'text';
      this.glyPass = false;
    } else {
      this.kindTypePass = 'password';
      this.glyPass = true;
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
