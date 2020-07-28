/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="tabStatusService.ts"
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
export class TabStatusService {


  private sub = new Subject<boolean>();
  dataStatus$ = this.sub.asObservable();

  private subTab = new Subject<boolean>();
  tabStatus$ = this.subTab.asObservable();

  changeStatus(data: boolean) {


    // to able tabs in the secondary tabs
    // sessionStorage.setItem("ableTabs","false");
    sessionStorage.setItem('ableTabs', data.toString());

    this.sub.next(data);


  }

  changeTabStatus(data: boolean) {

    this.subTab.next(data);
  }

}

