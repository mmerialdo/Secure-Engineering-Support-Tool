/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="activities-tab-view.component.spects"
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

import { ActivitiesTabViewComponent } from './activities-tab-view.component';

describe('ActivitiesTabViewComponent', () => {
  let component: ActivitiesTabViewComponent;
  let fixture: ComponentFixture<ActivitiesTabViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActivitiesTabViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivitiesTabViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
