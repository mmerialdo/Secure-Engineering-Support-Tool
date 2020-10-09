/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="VulnerabilityModelManagerRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.user.manager.lock;

import org.crmf.model.general.SESTObject;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.general.SestObjService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockViewInput {

  @Autowired
  private UserPermissionManagerInput permissionManager;
  @Autowired
  private UserService userService;
  @Autowired
  private SestObjService sestObjService;

  public String lock(GenericFilter filter) throws Exception {

    String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
    String projectIdentifier = filter.getFilterValue(GenericFilterEnum.PROJECT);
    String userIdentifier = filter.getFilterValue(GenericFilterEnum.USER);
    User user = userService.getByIdentifier(userIdentifier);

    SESTObject sestObj = sestObjService.getByIdentifier(identifier);
    if ((sestObj.getLockedBy() == null &&
      permissionManager.isProjectObjectTypeAllowed(userIdentifier, PermissionTypeEnum.Update, sestObj.getObjType(), projectIdentifier))
      || (sestObj.getLockedBy() != null &&
      permissionManager.isProjectObjectTypeAllowed(userIdentifier, PermissionTypeEnum.Update, SESTObjectTypeEnum.AssessmentProject, projectIdentifier))) {

      return sestObjService.updateLock(identifier, user.getUsername());
    }
    return null;
  }

  public String unlock(GenericFilter filter) throws Exception {

    String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
    String projectIdentifier = filter.getFilterValue(GenericFilterEnum.PROJECT);
    String userIdentifier = filter.getFilterValue(GenericFilterEnum.USER);
    User user = userService.getByIdentifier(userIdentifier);

    SESTObject sestObj = sestObjService.getByIdentifier(identifier);
    if (sestObj.getLockedBy() != null && (sestObj.getLockedBy().equals(user.getUsername()) ||
      permissionManager.isProjectObjectTypeAllowed(userIdentifier, PermissionTypeEnum.Update, SESTObjectTypeEnum.AssessmentProject, projectIdentifier))) {

      return sestObjService.updateLock(identifier, null);
    }
    return null;
  }

  public String getlock(GenericFilter filter) {

    String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
    SESTObject sestObj = sestObjService.getByIdentifier(identifier);
    return sestObj.getLockedBy();
  }
}
