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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataService} from '../../dataservice';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {selectView} from '../../shared/store/reducers/assets.reducer';
import {storeServerAsset, switchToTabView} from '../../shared/store/actions/assets.actions';
import {Permission} from '../../permission.class';
import {PermissionType} from '../../permission-type.class';
import {take} from 'rxjs/operators';

@Component({
  templateUrl: './business.component.html',
})
export class BusinessComponent implements OnInit, OnDestroy {

  public tabView$: Observable<boolean>;

  private permission: Permission;
  public enableSaveButton = true;

  constructor(private dataService: DataService, private store: Store<any>) {
  }

  ngOnInit(): void {

    this.tabView$ = this.store.pipe(select(selectView));

    this.setPermission();
  }

  setPermission() {
    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.AssetModel) >= 0);
  }

  ngOnDestroy(): void {
    this.store.dispatch(switchToTabView());
  }
}
