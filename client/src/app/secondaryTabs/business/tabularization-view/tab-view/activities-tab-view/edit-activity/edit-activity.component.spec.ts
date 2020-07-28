/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="edit-activity.component.spec.ts"
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

import { EditActivityComponent } from './edit-activity.component';

describe('EditActivityComponent', () => {
  let component: EditActivityComponent;
  let fixture: ComponentFixture<EditActivityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditActivityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
