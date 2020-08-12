/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.user.manager.rest;

import com.google.gson.Gson;
import org.apache.camel.component.shiro.security.ShiroSecurityToken;
import org.crmf.model.user.User;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.user.manager.core.UserManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

//This class manages the business logic behind the webservices related to the User management
public class UserRestServer implements UserRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserRestServer.class.getName());
  private UserManagerInputInterface userInput;

  @Override
  public List<User> loadUserList(String token) throws Exception {
    try {
      LOG.info("listUser");
      return userInput.listUser();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  @Override
  public String createUser(User user) throws Exception {
    try {
      LOG.info("createUser " + user);
      String result = userInput.createUser(user);

      if (result == null) {
        throw new Exception("COMMAND_EXCEPTION");
      } else {
        return result;
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  @Override
  public void editUser(User user) throws Exception {
    try {
      LOG.info("editUser" + user);
      userInput.saveUser(user);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  @Override
  public void editUserPassword(User user, String token) throws Exception {

    LOG.info("saveUser" + user);
    Gson gson = new Gson();
    String decryptedtoken = new String(
      Base64.getDecoder().decode(token.getBytes()),
      StandardCharsets.UTF_8);
    ShiroSecurityToken securityToken = gson.fromJson(decryptedtoken, ShiroSecurityToken.class);
    userInput.saveUserPassword(user, securityToken.getUsername());
  }

  @Override
  public User loadUser(GenericFilter filter) throws Exception {
    try {
      LOG.info("retrieveUser " + filter);
      User result = userInput.retrieveUser(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));

      if (result == null) {
        throw new Exception("COMMAND_EXCEPTION");
      } else {
        return result;
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  @Override
  public String deleteUser(String identifier) throws Exception {
    try {
      LOG.info("deleteUser" + identifier);
      userInput.deleteUser(identifier);

      return identifier;
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  public UserManagerInputInterface getUserInput() {
    return userInput;
  }

  public void setUserInput(UserManagerInputInterface userInput) {
    this.userInput = userInput;
  }
}
