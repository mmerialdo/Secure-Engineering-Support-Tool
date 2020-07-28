/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="errors.service.spec.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {RouterTestingModule} from '@angular/router/testing';
import {inject, TestBed} from '@angular/core/testing';
import {ErrorsService} from './errors.service';
import {Injector, NgZone} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {Router} from '@angular/router';
import {of} from 'rxjs';
import Spy = jasmine.Spy;
import {MessageService} from '../shared/message.service';
import {DataAccessService} from '../data-access.service';
import {MessageServiceMock} from '../shared/message.service.mock';

class MockDataAccessService {

  public postGeneric() {
    return of([]);
  }
}

class MockAccessControlService {

  public isAnonymous = false;

  public isAnonymousOrUninitialized() {
    return this.isAnonymous;
  }
}

class CustomError {
  public name = 'CustomErrorName';

  public toString() {
    return this.name;
  }
}

describe('ErrorsService', () => {
  let errorsService: ErrorsService;
  let router: Router;
  let zone: NgZone;
  let messageService: MessageService;
  let dataService: MockDataAccessService;
  let accessControlService: MockAccessControlService;
  let dataServiceSpy: Spy;
  let navigateSpy: Spy;
  let messageServiceSpy: Spy;

  const unablePerformMessage = 'Unable to perform operation.';
  const unablePerformWithMultipleErrorsMessage = 'Unable to perform operation with multiple errors.';
  const msg = 'message';
  const field = 'field';
  const msg1 = 'message1';
  const field1 = 'field1';
  const stack = 'stack';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        NoopAnimationsModule
      ],
      providers: [
        ErrorsService,
        Injector,
        {
          provide: MessageService,
          useClass: MessageServiceMock
        },
        {
          provide: DataAccessService,
          useClass: MockDataAccessService
        }
      ]
    });

    router = TestBed.get(Router);
    navigateSpy = spyOn(router, 'navigate').and.callFake(() => {
    });

    messageService = TestBed.get(MessageService);
    messageServiceSpy = spyOn(messageService, 'showErrorMessage');

    zone = TestBed.get(NgZone);
    spyOn(zone, 'run').and.callFake((fn: Function) => fn());

    errorsService = TestBed.get(ErrorsService);
    dataService = TestBed.get(DataAccessService);
    dataServiceSpy = spyOn(dataService, 'postGeneric').and.callThrough();
  });

  it('should be created', inject([ErrorsService], (service: ErrorsService) => {
    expect(service).toBeTruthy();
  }));

  it('should log error to console', () => {
    spyOn(console, 'error').and.callThrough();
    errorsService.handleError({});

    expect(console.error).toHaveBeenCalled();

  });

  it('should handle non-field error', () => {

    let errorResponse = new HttpErrorResponse({status: 400});
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual([{message: unablePerformMessage}]);
    expect(messageServiceSpy).toHaveBeenCalled();

    errorResponse = new HttpErrorResponse({error: {}, status: 400});
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual([{message: unablePerformMessage}]);
    expect(messageServiceSpy).toHaveBeenCalled();

    errorResponse = new HttpErrorResponse({error: {errors: {}}, status: 400});
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual([{message: unablePerformMessage}]);
    expect(messageServiceSpy).toHaveBeenCalled();

  });

  it('should handle field error', () => {

    let errorResponse = new HttpErrorResponse(
      {error: {errors: [{field: field, code: 'code', message: msg}]}, status: 400}
    );

    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual([{message: field + ' ' + msg, errorsList: null}]);
    expect(messageServiceSpy).toHaveBeenCalled();

    errorResponse = new HttpErrorResponse(
      {error: {errors: [{field: field, code: 'AssertTrue', message: msg}]}, status: 400}
    );
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual([{message: msg, errorsList: null}]);
    expect(messageServiceSpy).toHaveBeenCalled();

  });

  it('should correctly proceed multiple errors', () => {
    let errorResponse = new HttpErrorResponse({
      error: {
        errors: [
          {field: field, code: 'code', message: msg},
          {field: field1, code: 'code', message: msg1}
        ]
      },
      status: 400
    });
    let expected = [{
      message: unablePerformWithMultipleErrorsMessage,
      errorsList: [field + ' ' + msg, field1 + ' ' + msg1]
    }];
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual(expected);
    expect(messageServiceSpy).toHaveBeenCalled();

    errorResponse = new HttpErrorResponse({
      error: {
        errors: [
          {field: field, code: 'code', message: msg, errorsList: [msg1]}
        ]
      },
      status: 400
    });
    expected = [{
      message: field + ' ' + msg,
      errorsList: [msg1]
    }];
    errorsService.handleError(errorResponse);
    expect(errorsService.errors.server.errors).toEqual(expected);
    expect(messageServiceSpy).toHaveBeenCalled();
  });

  it('should redirect to login page on access denied', () => {

    let errorResponse = new HttpErrorResponse({status: 401});
    errorsService.handleError(errorResponse);
    expect(navigateSpy).toHaveBeenCalledWith(['login']);

    errorResponse = new HttpErrorResponse({status: 403});
    errorsService.handleError(errorResponse);
    expect(navigateSpy).toHaveBeenCalledWith(['login']);

  });

  it('should not send error to backend if anonymous', () => {
    accessControlService.isAnonymous = true;
    errorsService.handleError({});

    expect(dataServiceSpy).not.toHaveBeenCalled();

  });

  it('should send errors to backend if not anonymous', () => {

    const error = new Error(msg);
    error.name = msg;
    errorsService.handleError(error);
    expect(dataServiceSpy).toHaveBeenCalled();

    dataServiceSpy.calls.reset();

    error.stack = stack;
    errorsService.handleError(error);
    expect(dataServiceSpy).toHaveBeenCalled();

    const errorStr = errorsService.errorsCache.pop();
    expect(errorStr.indexOf(stack) >= 0);

  });

  it('should not send same error twice', () => {

    const error = new Error(msg);
    error.name = msg;
    errorsService.handleError(error);
    expect(dataServiceSpy).toHaveBeenCalled();

    dataServiceSpy.calls.reset();

    errorsService.handleError(error);
    expect(dataServiceSpy).not.toHaveBeenCalled();

  });

  it('should handle non-standard errors', () => {

    const error = new CustomError();

    errorsService.handleError(error);
    expect(errorsService.errorsCache[0] === error.name);
    expect(dataServiceSpy).toHaveBeenCalled();
  });


});
