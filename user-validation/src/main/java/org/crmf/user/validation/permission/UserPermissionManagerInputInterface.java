/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserPermissionManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.user.validation.permission;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;

import java.util.Set;


public interface UserPermissionManagerInputInterface {

  /*
   Update the permissions associated to a specific user, targeting the type of elements associated to the role in input,
   whether it is added (isAdded = true) or removed (isAdded = false)
   role:  whose rights must be added or removed
   isAdded: true if the role rights must be added, false if they must be removed
   */
  void updatePermissionOnRoleChange(AssessmentProject project) throws Exception;  //ok

  /*
  Check permission based on role and profile of objectType
   */
  boolean isSestObjectTypeAllowed(String username, PermissionTypeEnum type, SESTObjectTypeEnum objectType, String projectIdentifier);

  /*
  Check permission based on role and profile of objectType
   */
  boolean isProjectObjectTypeAllowed(String userIdentifier, PermissionTypeEnum type, SESTObjectTypeEnum objectType, String projectIdentifier);

  /*
   Retrieves the permissions of the user on the type of object.
   */
  Set<SESTObjectTypeEnum> retrievePermissionBasedOnProfileAndType(String username, PermissionTypeEnum type,
                                                                  String projectIdentifier) throws Exception;

  /*
   Retrieves project permission based on permission type and username, the result contains objects' identifiers on which the user have permits.
   */
  Set<String> retrieveProjectPermissionBasedOnRoleAndType(String username, PermissionTypeEnum type) throws Exception;
}
