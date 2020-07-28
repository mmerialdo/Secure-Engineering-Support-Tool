/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="changetabservice.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import { Subject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class ChangeTabService {


  private subTab = new Subject<boolean>();
  dataTabOn$ = this.subTab.asObservable();


  private subTabShowLogout = new Subject<boolean>();
  dataTabOnsubTabShowLogout$ = this.subTabShowLogout.asObservable();

  changeTab(data: boolean) {

    console.log('change tab');
    console.log(data);
    this.subTab.next(data);
  }

  changeLogout(data: boolean) {


    this.subTabShowLogout.next(data);

  }




}
