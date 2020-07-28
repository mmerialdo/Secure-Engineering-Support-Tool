/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="changeMenuService.ts"
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
export class ChangeMenuService {


  private subMenu = new Subject<boolean>();
  dataProjMenuOn$ = this.subMenu.asObservable();

  private subMenuProc = new Subject<boolean>();
  dataProcMenuOn$ = this.subMenuProc.asObservable();

  changeProjStatusOn(data: boolean) {

    this.subMenu.next(data);
  }

  changeProcStatusOn(data: boolean) {

    this.subMenuProc.next(data);
  }


}
