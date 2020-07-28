/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="auth-guard.service.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Injectable} from '@angular/core';
import {AuthService} from './core/authn/auth.service';
import {CanActivate, Router} from '@angular/router';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(public auth: AuthService, public router: Router) {
  }  canActivate(): boolean {
  if (!this.auth.isAuthenticated()) {
    this.router.navigate(['login']);
    return false;
  }
  return true;
}}
