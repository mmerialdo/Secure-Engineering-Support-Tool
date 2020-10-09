/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="register.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

export class BaseObject {
  public identifier: string;
  public lockedBy: string;
  public objType: string;

  /**
   * Deserializes a JSON object.
   * @param {JSON} object the JSON object.
   */
  public fromJSON(object: JSON) {
    this.identifier = object['identifier'];
    this.lockedBy = object['lockedBy'];
    this.objType = object['objType'];
  }
}
