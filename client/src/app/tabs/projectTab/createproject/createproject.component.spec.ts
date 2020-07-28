/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="createproject.component.spec.ts"
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

import { CreateprojectComponent } from './createproject.component';

describe('CreateprojectComponent', () => {
  let component: CreateprojectComponent;
  let fixture: ComponentFixture<CreateprojectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateprojectComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateprojectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
