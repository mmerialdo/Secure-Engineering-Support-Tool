/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="login.component.ts"
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
import {ChangeTabService} from '../changetabservice';
import {Confirmation, Message} from 'primeng/components/common/api';

// import {Headers} from "@angular/http";
import {DataService} from '../dataservice';
import {Router} from '@angular/router';

import {FormBuilder, Validators} from '@angular/forms';
import {ValidationService} from '../validationservice';
import {Subscription} from 'rxjs/internal/Subscription';
import {ConfirmationService} from 'primeng/api';
import {ConfigService} from '../configservice';
import {LoginTrackerService} from '../shared/service/login-tracker-service';
import {take} from 'rxjs/operators';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
  // providers:

})
export class LoginComponent implements OnInit, OnDestroy {

  public username;
  public password;
  resetForm: any;

  public displayEditPassword = false;
  private kindTypeConfirmReset = 'password';
  private kindTypePassReset = 'password';

  private glyConfReset = true;
  private glyPassReset = true;

  public showError = false;
  private errorMessage;

  public blockedLogin = false;

  // to show messages
  msgsAsset: Message[] = [];
  private timerSubscription: Subscription;

  private subscriptions: Subscription[] = [];
  private ipServer;
  private timeout;
  private timeoutWarning;

  constructor(private changeTabService: ChangeTabService,
              private router: Router,
              private dataService: DataService,
              private formBuilder: FormBuilder,
              private confirmationService: ConfirmationService,
              private configService: ConfigService,
              private loginTrackerService: LoginTrackerService) {

    this.ipServer = this.configService.getConfiguration().ipServer;
    this.resetForm = this.formBuilder.group({

      'username': ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator,
        ValidationService.usernameCheckValidator, Validators.maxLength(20)]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]]
    });
    this.subscriptions.push(
      this.dataService.getConfiguration().subscribe((response: string[]) => {
        this.timeout = response[0];
        this.timeoutWarning = response[1];
      }));
  }


  ngOnInit() {

    // to disable tabs
    sessionStorage.setItem('ableTabs', 'true');
  }


  login() {
    this.blockedLogin = true;

    const tokenLogin = new LoginToken(this.username, this.password);
    const tokenLoginJson = JSON.stringify(tokenLogin);

    const encryptedToSend = btoa(tokenLoginJson);
    const loginTime = Date.now();

    this.subscriptions.push(
      this.dataService.login(encryptedToSend).subscribe(
        response => {
          const tokenLogged = response as any;
          const token = btoa(JSON.stringify(tokenLogged.token));
          this.dataService.setLoggedUserId(tokenLogged.userid, this.username);
          this.dataService.setAuthnToken(token);
          this.subscriptions.push(
            this.dataService.refreshPermissionList().subscribe(
              response => {
                this.blockedLogin = false;
                // graphical side
                sessionStorage.setItem('showLogout', 'true');
                this.changeTabService.changeLogout(true);
                this.router.navigate(['/tool/home']);
              },
              err => {
                this.blockedLogin = false;
                throw err;
              }));

          if (this.timeout) {
            this.startSessionTimer(Date.now() - loginTime, this.timeout, this.timeoutWarning);
          } else {
            this.subscriptions.push(
              this.dataService.getConfiguration().subscribe((response: string[]) => {
                this.timeout = response[0];
                this.timeoutWarning = response[1];
                this.startSessionTimer(Date.now() - loginTime, this.timeout, this.timeoutWarning);
              }));
          }
        },
        err => {
          this.blockedLogin = false;
          throw err;
        }));
  }

  timerStopped() {
    return !this.timerSubscription || this.timerSubscription.closed;
  }

  startSessionTimer(millisAlreadyPassed, timeoutValue, timeoutWarningValue) {

    const timeoutMills = millisAlreadyPassed ? timeoutValue - millisAlreadyPassed : timeoutValue;
    this.timerSubscription = this.loginTrackerService.startSessionTimer(+timeoutMills, +timeoutWarningValue)
      .subscribe((i) => {
        if (i === 0) {
          if (sessionStorage.getItem('authnToken') && this.router.url !== '/login') {
            this.openSessionTimeoutWarningDialog(timeoutValue);
          }
        } else {
          this.resetUser();
        }
      });
  }

  resetUser() {

    this.confirmationService.confirm({
      message: 'Session EXPIRED. Please login again.',
      header: 'Session message',
      icon: 'ui-icon-blank',
      accept: () => {
        this.router.navigate(['/login']);
        this.stopSessionTimer();
      }
    });
  }

  stopSessionTimer() {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  private openSessionTimeoutWarningDialog(timeout): ConfirmationService {
    return this.confirmationService.confirm({
      message: 'Your session will expire soon. Do you wish to continue?',
      header: 'Session expiration',
      icon: 'ui-icon-blank',
      accept: () => {
        this.dataService.ping().pipe(take(1)).subscribe();
        this.loginTrackerService.restartSessionTimer();
      }
    });
  }

  closeForm() {
    this.displayEditPassword = false;
    this.resetForm = this.formBuilder.group({

      'username': ['', [Validators.required, Validators.minLength(4), Validators.maxLength(20)]],
      'password': ['', [Validators.required, Validators.minLength(6), ValidationService.passwordValidator, ValidationService.usernameCheckValidator, Validators.maxLength(20)]],
      'confirm': ['', [Validators.required, ValidationService.passwordMatchValidator, Validators.maxLength(20)]]
    });
  }

  changePassword() {

    const arrayform = {
      'username': this.resetForm.value.username,
      'password': this.resetForm.value.password
    };

    const jsonform = JSON.stringify(arrayform);
    this.subscriptions.push(
      this.dataService.changeUserPassword(jsonform).subscribe(response => {
          this.closeForm();
        },
        error => {
          throw error;
        },
        () => console.info('OK')));
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

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}

export class LoginToken {

  constructor(
    public username: string,
    public password: string
  ) {
  }

}
