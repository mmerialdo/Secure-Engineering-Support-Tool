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
import {take} from 'rxjs/operators';
import {BehaviorSubject} from 'rxjs/internal/BehaviorSubject';
import {DataService} from '../../dataservice';
import {interval} from 'rxjs/internal/observable/interval';
import {Subscription} from 'rxjs/internal/Subscription';

@Injectable()
export class LockService {

  constructor(private dataService: DataService) {
  }

  public lockedBy: BehaviorSubject<string> = new BehaviorSubject<string>('');
  public viewIdentifier: BehaviorSubject<string> = new BehaviorSubject<string>('');
  checkTimerSubscriber: Subscription;
  intervalId;

    changeData(identifier: any, lockedBy: any) {
    this.viewIdentifier.next(identifier);
    this.lockedBy.next(lockedBy);
  }

  addLockForced(identifier: string) {
    this.addLockSendRequest(identifier);
  }

  addLock(identifier: string, lockedBy: string) {

    if (!lockedBy) {
      this.addLockSendRequest(identifier);
    } else {
      this.changeData(identifier, lockedBy);
    }
    this.startTimer(identifier);
  }

  addLockSendRequest(identifier: string) {
    const filterLock = {
      'filterMap': {
        'IDENTIFIER': identifier,
        'PROJECT': sessionStorage.getItem('idProject'),
        'USER': sessionStorage.getItem('loggedUserId')
      }
    };
    this.dataService.lock(filterLock).pipe(take(1)).subscribe(response => {
      if (response) {
        this.changeData(identifier, sessionStorage.getItem('loggedUsername'));
      }
    });
  }

  removeLock(identifier: string) {

    if (sessionStorage.getItem('loggedUsername') === this.lockedBy.getValue()) {
      const filterUnlock = {
        'filterMap': {
          'IDENTIFIER': identifier,
          'PROJECT': sessionStorage.getItem('idProject'),
          'USER': sessionStorage.getItem('loggedUserId')
        }
      };
      this.dataService.unlock(filterUnlock).pipe(take(1)).subscribe();
    }

    this.changeData(identifier, '');
    this.stopTimer();
  }

  startTimer(identifier: string) {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
    this.intervalId = interval(30000);
    this.checkTimerSubscriber = interval(30000).subscribe((val) => {
      const filterLock = {
        'filterMap': {
          'IDENTIFIER': identifier
        }
      };
      this.dataService.getlock(filterLock).pipe(take(1)).subscribe(responseLockedBy => {
          if (this.lockedBy !== responseLockedBy) {
            this.changeData(identifier, responseLockedBy);
          }
        }
      );
    });
  }

  stopTimer() {
    if (this.checkTimerSubscriber) {
      clearInterval(this.intervalId);
      this.checkTimerSubscriber.unsubscribe();
    }
  }
}
