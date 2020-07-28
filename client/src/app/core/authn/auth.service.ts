/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="auth.service.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import { Injectable } from '@angular/core';

@Injectable()
export class AuthService {
  constructor() {}

  public isAuthenticated(): boolean {
    if ((sessionStorage.getItem('loggedUserId') === null) || (sessionStorage.getItem('loggedUserId') === '')) {
      return false;
    }
    return true;
  }}
