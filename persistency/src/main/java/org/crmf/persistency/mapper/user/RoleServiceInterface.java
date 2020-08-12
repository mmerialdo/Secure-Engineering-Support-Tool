/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RoleServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.user;

import org.crmf.model.general.SESTObject;
import org.crmf.model.user.User;
import org.crmf.model.user.UserRole;

import java.util.List;
import java.util.Set;

public interface RoleServiceInterface {

  Integer insert(UserRole role, String userIdentifier, String projectIdentifier) throws Exception;

  void insertUserForProject(User user, String projectIdentifier) throws Exception;

  void deleteUserFromProject(String userIdentifier, String projectIdentifier, Set<SESTObject> associatedObjects);

  List<UserRole> getByUserIdentifier(String identifier) throws Exception;

  List<User> getByProjectIdentifier(String identifier) throws Exception;

  List<UserRole> getRolesByUserAndProjectId(String userId, String projectId);
}	
