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

import {Component, OnInit, OnDestroy, Output, EventEmitter, Input} from '@angular/core';
import {ConfirmationService} from 'primeng/primeng';
import {FormBuilder, Validators} from '@angular/forms';
import {Subscription} from 'rxjs/internal/Subscription';
import {ValidationService} from '../../../validationservice';
import {DataService} from '../../../dataservice';
import {MessageService} from "primeng/api";


@Component({
  selector: 'user-password',
  templateUrl: './user-password.component.html',
  styleUrls: ['./user-password.component.scss'],
  providers: [ConfirmationService]
})

export class UserPasswordComponent implements OnInit, OnDestroy {

  @Output() closePopup: EventEmitter<string> = new EventEmitter<string>();
  @Input() displayEditPassword;
  @Input() username;

  passwordForm: any;
  message = '';

  public kindTypeConfirmReset = 'password';
  public kindTypePassReset = 'password';
  public glyConfReset = true;
  public glyPassReset = true;

  private subscriptions: Subscription[] = [];

  constructor(private confirmationService: ConfirmationService, private dataService: DataService,
              private formBuilder: FormBuilder, private messageService: MessageService) {
  }

  ngOnInit() {
    this.passwordForm = this.formBuilder.group({
      'username': [{value: this.username, disabled: true}, [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator, Validators.maxLength(20)]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]]
    });
  }

  changePassword() {
    const arrayform = {
      'username': this.passwordForm.get('username').value,
      'password': this.passwordForm.get('password').value
    };

    const jsonform = JSON.stringify(arrayform);

    this.subscriptions.push(
      this.dataService.changeUserPassword(jsonform).subscribe(response => {

          this.messageService.add({severity: 'success', summary: 'Success Message', detail: 'Password updated!'});
          this.closeForm();
        },
        err => {
          throw err;
        }));
  }

  changeConfirmReset() {

    if (this.kindTypeConfirmReset === 'password') {
      this.kindTypeConfirmReset = 'text';
      this.glyConfReset = false;
    } else {
      this.kindTypeConfirmReset = 'password';
      this.glyConfReset = true;
    }
  }

  changePassReset() {

    if (this.kindTypePassReset === 'password') {
      this.kindTypePassReset = 'text';
      this.glyPassReset = false;
    } else {
      this.kindTypePassReset = 'password';
      this.glyPassReset = true;
    }
  }

  closeForm() {

    this.passwordForm = this.formBuilder.group({
      'username': [{value: '', disabled: true}, [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator, Validators.maxLength(20)]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]]
    });

    this.onClose();
  }

  onClose() {
    this.closePopup.emit(this.message);
  }


  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
