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

  public lockedBy: BehaviorSubject<string> = new BehaviorSubject<string>('');
  public viewIdentifier: BehaviorSubject<string> = new BehaviorSubject<string>('');

  private intervalId;
  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService) {
    this.startTimer();
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
  }

  private addLockSendRequest(identifier: string) {
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

  private changeData(identifier: any, lockedBy: any) {
    this.viewIdentifier.next(identifier);
    this.lockedBy.next(lockedBy);
  }

  startTimer() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
    this.intervalId = interval(10000);
    this.subscriptions.push(
      this.intervalId.subscribe((val) => {
        const identifier = this.viewIdentifier.getValue();
        this.checkLocker(identifier);
      }));
  }

  private checkLocker(identifier: String) {
    if (identifier && identifier !== '') {
      const filterLock = {
        'filterMap': {
          'IDENTIFIER': identifier
        }
      };
      this.dataService.getlock(filterLock).pipe(take(1)).subscribe(responseLockedBy => {
          if (this.lockedBy.getValue() !== responseLockedBy) {
            this.changeData(identifier, responseLockedBy);
          }
        }
      );
    }
  }

  removeLock(identifier: string) {

    const actualLockedBy = this.lockedBy.getValue();
    this.changeData('', '');
    if (sessionStorage.getItem('loggedUsername') === actualLockedBy) {
      const filterUnlock = {
        'filterMap': {
          'IDENTIFIER': identifier,
          'PROJECT': sessionStorage.getItem('idProject'),
          'USER': sessionStorage.getItem('loggedUserId')
        }
      };
      this.dataService.unlock(filterUnlock).pipe(take(1)).subscribe();
    }
  }

  stopTimer() {
    clearInterval(this.intervalId);
  }

  ngOnDestroy() {
    this.stopTimer();
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
