/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="business.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, OnInit} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Permission} from '../../permission.class';
import {PermissionType} from '../../permission-type.class';

@Component({
  templateUrl: './business.component.html',
})
export class BusinessComponent implements OnInit {

  public tabView$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  private permission: Permission;
  public enableSaveButton = true;

  constructor() {
  }

  ngOnInit(): void {

    this.setPermission();
  }

  gotoTabView(isTab: boolean) {

    this.tabView$.next(isTab);
  }

  setPermission() {
    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.AssetModel) >= 0);
  }
}
