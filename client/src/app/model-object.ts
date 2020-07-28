/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="users.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

export class ModelObject {
  jsonModel: any;
  objectIdentifier: string;
  lockedBy: string;

  /**
   * Deserializes a JSON object.
   * @param {JSON} object the JSON object.
   */
  public fromJSON(object: JSON) {
    this.jsonModel = object['jsonModel'];
    this.objectIdentifier = object['objectIdentifier'];
    this.lockedBy = object['lockedBy'];
  }
}
