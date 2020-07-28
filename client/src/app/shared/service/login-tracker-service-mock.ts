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

import {of} from 'rxjs';

export class LoginTrackerServiceMock {

  public static LOGGED_IN = true;
  public static TIMEOUT_MILLIS = '123';

  restartSessionTimer() {
  }

  startSessionTimer() {
    return of({});
  }

  getTimeoutMills() {
    return LoginTrackerServiceMock.TIMEOUT_MILLIS;
  }
}
