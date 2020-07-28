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
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.user.UserServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInputInterface;


public class LockViewInput implements LockViewInputInterface {

  private UserPermissionManagerInputInterface permissionManager;
  private UserServiceInterface userService;
  private SestObjServiceInterface sestObjService;

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

  public String getlock(GenericFilter filter) throws Exception {

    String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
    SESTObject sestObj = sestObjService.getByIdentifier(identifier);
    return sestObj.getLockedBy();
  }

  public UserPermissionManagerInputInterface getPermissionManager() {
    return permissionManager;
  }

  public SestObjServiceInterface getSestObjService() {
    return sestObjService;
  }

  public UserServiceInterface getUserService() {
    return userService;
  }

  public void setUserService(UserServiceInterface userService) {
    this.userService = userService;
  }

  public void setPermissionManager(UserPermissionManagerInputInterface permissionManager) {
    this.permissionManager = permissionManager;
  }

  public void setSestObjService(SestObjServiceInterface sestObjService) {
    this.sestObjService = sestObjService;
  }
}
