/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="message.service.spec.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {TestBed, inject} from '@angular/core/testing';
import {MessageService} from './message.service';
import Spy = jasmine.Spy;
import {Message} from 'primeng/components/common/api';

describe('MessageService', () => {
  let infoMessageSpy: Spy;
  let errorMessageSpy: Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MessageService
      ]
    });
   // infoMessageSpy = spyOn(testMsg, 'open').and.callFake(() => {
   // });
   // errorMessageSpy = spyOn(snackBar, 'openFromComponent').and.callFake(() => {
   // });
  });

  it('should be created', inject([MessageService], (service: MessageService) => {
    expect(service).toBeTruthy();
  }));

  it('should show info message with proper config', inject([MessageService], (service: MessageService) => {
    const testMsg = 'test';

    service.showInfoMessage(testMsg);
    expect(infoMessageSpy).toHaveBeenCalledWith(testMsg, service.defaultDismissAction, {
      duration: service.defaultDuration,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });

    service.showInfoMessage(testMsg, 'OK', 2000);
    expect(infoMessageSpy).toHaveBeenCalledWith(testMsg, 'OK', {
      duration: 2000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }));

  it('should show error message with proper config', inject([MessageService], (service: MessageService) => {
    const testMsg = 'test';
    const errorsList = ['error1', 'error2'];

    service.showErrorMessage(testMsg);
    expect(errorMessageSpy).toHaveBeenCalledWith(testMsg, {
      panelClass: 'snackbar-error',
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      data: {
        message: testMsg,
        errorsList: undefined,
        action: service.defaultErrorDismissAction
      }
    });

    service.showErrorMessage(testMsg, errorsList, 2000);
    expect(errorMessageSpy).toHaveBeenCalledWith(testMsg, {
      panelClass: 'snackbar-error',
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      data: {
        message: testMsg,
        errorsList: errorsList,
        action: 'OK'
      }
    });
  }));
});
