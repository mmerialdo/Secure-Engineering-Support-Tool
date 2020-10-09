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

import {BaseObject} from './base-object.class';
import {UserRole} from './user-role.class';

export class User extends BaseObject {

  public email: string;
  public name: string;
  public password: string;
  public profile: string;
  public surname: string;
  public username: string;
  public roles: UserRole[] = [];
// private ArrayList<Experience> experiences;
// private ArrayList<PermissionGroup> permissionGroups;

  /**
   * Deserializes a JSON object.
   * @param {JSON} object the JSON object.
   */
  public fromJSON(object: JSON) {
    super.fromJSON(object);
    this.username = object['username'];
    this.email = object['email'];
    this.name = object['name'];
    this.surname = object['surname'];
    this.password = object['password'];
    this.profile = object['profile'];

    // iterates and deserialize roles related to user
    for (const roleJSON of object['roles']) {
      const role = new UserRole();
      role.fromJSON(roleJSON);
      this.roles.push(role);
    }
  }
}
