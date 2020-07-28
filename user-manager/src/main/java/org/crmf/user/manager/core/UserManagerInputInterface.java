/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserManagerInputInterface.java"
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

import java.util.List;

import org.crmf.model.user.User;

public interface UserManagerInputInterface {

  String createUser(User user) throws Exception;

  void saveUser(User user) throws Exception;

  void saveUserPassword(User user, String tokenUsername) throws Exception;

  void deleteUser(String identifier) throws Exception;

  User retrieveUser(String identifier) throws Exception;

  User retrieveUserByUsername(String username);

  List<User> listUser() throws Exception;
}
