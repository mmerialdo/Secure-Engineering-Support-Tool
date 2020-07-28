/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="page.class.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/


export class Page<T> {
  public totalItems = 0;
  public pageNum = 0;
  public itemsPerPage = 0;
  public filter = '';
  public items: T[] = [];
  public sortDirection = 'DESC';
  public sortField;
}
