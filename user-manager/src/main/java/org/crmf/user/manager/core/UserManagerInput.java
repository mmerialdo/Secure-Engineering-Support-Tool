/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.user.manager.core;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

//This class manages the business logic related to the creation, updateQuestionnaireJSON and delete of SEST Users
@Service
@Qualifier("default")
public class UserManagerInput {

  private static final Logger LOG = LoggerFactory.getLogger(UserManagerInput.class.getName());
  @Autowired
  private UserService userService;
  @Autowired
  private UserPermissionManagerInput permissionManager;

  public String createUser(User user) throws Exception {

    user.setObjType(SESTObjectTypeEnum.User);
    String userId = userService.insert(user);

    //set type and identifier accordingly to the sest object just created (user)
    user.setIdentifier(userId);
    return userId;
  }

  public void saveUser(User user) {

    userService.update(user);
  }

  public void saveUserPassword(User user, String tokenUsername) {
    Set<SESTObjectTypeEnum> objects = null;
    String username = user.getUsername();
    String password = user.getPassword();
    try {
      objects = permissionManager.retrievePermissionBasedOnProfileAndType(
        tokenUsername, PermissionTypeEnum.Update, null);
    } catch (Exception ex) {
      LOG.error("Unable to read permission ", ex);
    }
    LOG.info("username " + username);
    LOG.info("tokenUsername " + tokenUsername);
    LOG.info("objects " + objects);
    if ((username != null && username.equals(tokenUsername)) ||
      objects != null && objects.contains(SESTObjectTypeEnum.User)) {

      if (password == null || password.equals("")) {
        throw new RemoteComponentException("Unable to insert user with null password");
      }
      userService.updatePassword(username, password);
    } else {
      throw new RemoteComponentException("AUTHZ_EXCEPTION");
    }
  }

  public void deleteUser(String identifier) {

    userService.deleteCascade(identifier);
  }

  public User retrieveUser(String identifier) {

    return userService.getByIdentifier(identifier);
  }

  public User retrieveUserByUsername(String username) {

    return userService.getByUsername(username);
  }

  public List<User> listUser() {

    LOG.info("called listUser");
    return userService.getAll();
  }
}
