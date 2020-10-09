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
import org.crmf.persistency.mapper.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//This class manages the business logic related to the retrival of passwords and user identifiers from the persistency (these methods are invoked during the authentication of the Users) 
@Service
public class UserAuthentication {

  @Autowired
  private UserService userService;

  public String getPasswordForUser(String username) {

    User user = userService.getByUsername(username);

    return (user != null) ? user.getPassword() : null;
  }

  public boolean isPasswordExpired(String username) {

    return userService.isPasswordExpired(username);
  }
}
