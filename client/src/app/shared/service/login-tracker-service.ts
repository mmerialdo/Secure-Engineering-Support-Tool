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

import {Injectable} from '@angular/core';
import {Subject, timer} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';

@Injectable()
export class LoginTrackerService {

  /**
   * session timer Subject, used to reset timer on http call
   * @type {Subject<any>}
   */
  private timerReset = new Subject();

  constructor() {
  }

  startSessionTimer(timeoutValue: number, timeoutWarning: number) {
    console.log('start session timer');
    const warningTime = timeoutValue - timeoutWarning;
    const timerObservable = this.timerReset.pipe(
      startWith(0),
      switchMap(() => timer(warningTime, timeoutWarning))
    );
    return timerObservable;
  }

  restartSessionTimer() {
    console.log('restart timer');
    console.log(this.timerReset);
    if (this.timerReset) {
      this.timerReset.next(void 0);
    }
  }
}
