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

import { Component, OnInit, EventEmitter, Output, ViewEncapsulation } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ValidationService } from '../validationservice';
import { DataService } from '../dataservice';
import { Message } from 'primeng/components/common/api';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  userForm: any;
  usernamecomp: string;

  public kindTypeConfirm = 'password';
  public kindTypePass = 'password';

  public glyConf = true;
  public glyPass = true;

  private userCreation: any;

  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  @Output() sendFeed = new EventEmitter<boolean>();

  constructor(private formBuilder: FormBuilder, private dataService: DataService) {


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
  }


  sendFeedback(change: true) {

    this.sendFeed.emit(change);
    this.clearForm();
  }

  createUser() {

    this.blocked = true;
    this.blockedMessage = true;

    this.dataService.insertUser(JSON.stringify(this.userForm.value)).subscribe(
      response => {
        this.userCreation = response;

        this.blocked = false;
        this.blockedMessage = false;
        this.sendFeedback(true);

        if (this.userForm.valid) {
          this.userForm.reset();
        }
      },
      err => {
        this.blocked = false;
        this.blockedMessage = false;
        throw err;
      });
  }

  showFailed(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({ severity: 'error', summary: 'Error', detail: s });

    setTimeout(() => { this.clearMessage(); }, 8000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
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

}
