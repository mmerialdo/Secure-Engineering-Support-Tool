/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="data-access.service.spec.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
import {inject, TestBed} from '@angular/core/testing';
import {DataAccessService} from './data-access.service';
import {HttpClientModule} from '@angular/common/http';

describe('DataAccessService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        DataAccessService
      ]
    });
  });

  it('should be created', inject([DataAccessService], (service: DataAccessService) => {
    expect(service).toBeTruthy();
  }));
});
