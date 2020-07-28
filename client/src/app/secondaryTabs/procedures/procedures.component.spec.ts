/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="procedure.component.spec.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProceduresComponent } from './procedures.component';

describe('ProceduresComponent', () => {
  let component: ProceduresComponent;
  let fixture: ComponentFixture<ProceduresComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProceduresComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProceduresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
