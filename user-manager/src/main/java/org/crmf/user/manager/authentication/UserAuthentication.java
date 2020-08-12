/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserAuthentication.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.user.manager.authentication;

import org.crmf.model.user.User;
import org.crmf.persistency.mapper.user.UserServiceInterface;

//This class manages the business logic related to the retrival of passwords and user identifiers from the persistency (these methods are invoked during the authentication of the Users) 
public class UserAuthentication implements UserAuthenticationInterface {

  private UserServiceInterface userService;

  @Override
  public String getPasswordForUser(String username) {

    User user = userService.getByUsername(username);

    return (user != null) ? user.getPassword() : null;
  }

  public boolean isPasswordExpired(String username) {

    return userService.isPasswordExpired(username);
  }

  public UserServiceInterface getUserService() {
    return userService;
  }

  public void setUserService(UserServiceInterface userService) {
    this.userService = userService;
  }

}
