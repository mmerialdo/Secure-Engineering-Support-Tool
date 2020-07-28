/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="errors.service.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {ErrorHandler, Injectable, Injector, NgZone} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';

import {MessageService} from 'primeng/components/common/messageservice';

@Injectable()
export class ErrorsService implements ErrorHandler {
  errors = {
    server: {
      errors: []
    }
  };

  errorsCache = [];

  constructor(private injector: Injector,
              private ngZone: NgZone,
              private messageService: MessageService) {
  }

  handleError(error: any) {
    let errorMessage;
    // server-side error
    if (error instanceof HttpErrorResponse) {
      const router = this.injector.get(Router);
      // Server or connection error happened
      if (error.status === 401 || error.status === 403) {
        // if we are already on login page and response is from backend login url
        if (router.url === '/login') {
          errorMessage = 'Login failed. ' + (error.status === 401 ? 'You have entered an invalid username or password.' :
            'You don\'t have permissions to access application.');
          this.displayError({message: errorMessage});
        } else {
          router.navigate(['login']);
        }
        return;
      } else if (error.status === 0) {
        errorMessage = 'Please check server connection';
        if (router.url === '/login') {
          errorMessage = errorMessage.concat(' or SSL certificate approval on https://host:9090.');
        }
        this.displayError({message: errorMessage});
        return;
      }
    }
    if (error.status === 500) {
      if (error.error.indexOf('COMMAND_EXCEPTION') !== -1) {
        errorMessage = 'Server error!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('SESSION_EXCEPTION') !== -1) {
        errorMessage = 'Session expired!';
        this.displayError({message: errorMessage});
        const router = this.injector.get(Router);
        router.navigate(['/login']);
      } else if (error.error.indexOf('AUTHN_AUTHZ_EXCEPTION') !== -1) {
        errorMessage = 'Authorization / authentication exception!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('WRONG_USERNAME_PASSWORD') !== -1) {
        errorMessage = 'Wrong username/password!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('PASSWORD_EXPIRED') !== -1) {
        errorMessage = 'Password expired!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('AUTHZ_EXCEPTION') !== -1) {
        errorMessage = 'Authorization exception!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('USERNAME_NULL') !== -1) {
        errorMessage = 'Username empty!';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('HISTORY_PASSWORD_FOUND') !== -1) {
        errorMessage = 'Same Password in history. Please update it';
        this.displayError({message: errorMessage});
      } else if (error.error.indexOf('PASSWORD_INVALID') !== -1) {
        errorMessage = 'Invalid Password';
        this.displayError({message: errorMessage});
      } else {
        errorMessage = 'Unexpected exception!';
        this.displayError({message: errorMessage});
      }
    } else {
      errorMessage = 'Generic error.';
     // this.displayError({message: 'Generic error.'});
    }
    //else {
    // client-side error
    // this.processClientError(error);
    // }
  }

  /* processClientError(error: any) {
     let errorStr = '';
     if (error instanceof Error) {
       errorStr += error.name + '\n' + error.message + '\n';

       // optional field
       if (error.hasOwnProperty('stack')) {
         errorStr += error.stack;
       }
     } else {
       errorStr = error.toString();
     }

     if (!this.errorsCache.some(errorCached => errorCached === errorStr)) {
       // this.dataAccessService.clientError(errorStr).subscribe();
       this.errorsCache.push(errorStr);
     }
   } */

  displayError(error: { message: string; errorsList?: string[] }) {
    this.ngZone.run(() => {
      this.messageService.add({severity:'error', summary: 'Error', detail: error.message, life: 10000});
    });
  }

  clear() {
    this.messageService.clear();
  }
}
