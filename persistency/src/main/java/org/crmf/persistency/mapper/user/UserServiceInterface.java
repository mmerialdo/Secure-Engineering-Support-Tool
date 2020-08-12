/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserServiceInterface.java"
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

import org.crmf.model.user.User;

import java.util.List;

public interface UserServiceInterface {

  String insert(User user);

  void update(User user);

  void updatePassword(String username, String password);

  void deleteCascade(String identifier);

  User getByUsername(String username);

  User getById(Integer userId);

  User getByIdentifier(String identifier);

  List<User> getAll();

  boolean isPasswordExpired(String username);

}