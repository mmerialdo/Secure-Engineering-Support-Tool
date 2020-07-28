/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="vulnerabilities.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {PermissionType} from './permission-type.class';

export class Permission {
  update: string[] = [];
  read: number[] = [];
  create: PermissionType[] = [];
  view: PermissionType[] = [];

  /**
   * Deserializes a JSON object.
   * @param {JSON} object the JSON object.
   */
  public fromJSON(object: JSON) {

    this.update = object['update'];
    this.read = object['read'];
    this.create = object['create'];
    this.view = object['view'];
  }
}
