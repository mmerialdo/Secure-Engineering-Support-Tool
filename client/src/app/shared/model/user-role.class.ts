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

export class UserRole {

  public role: string;
  public user: string;
  public projectIdentifier: string;
  public projectName: string;

  public fromJSON(object: JSON) {
    this.role = object['role'];
    this.user = object['user'];
    this.projectIdentifier = object['projectIdentifier'];
    this.projectName = object['projectName'];
  }
}
