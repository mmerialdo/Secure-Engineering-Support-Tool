/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Sestuser.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.user;

import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;

public class Sestuser {
  private Integer id;

  private String email;

  private String name;

  private String password;

  private String profile;

  private String surname;

  private String username;

  private String sestobjId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email == null ? null : email.trim();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password == null ? null : password.trim();
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile == null ? null : profile.trim();
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname == null ? null : surname.trim();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username == null ? null : username.trim();
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public User convertToModel() {

    User user = new User();
    user.setName(this.name);
    user.setSurname(this.surname);
    user.setEmail(this.email);
    user.setUsername(this.username);
    user.setPassword(this.password);
    if (this.profile != null) {
      user.setProfile(UserProfileEnum.valueOf(this.profile));
    }
    if (this.sestobjId != null) {
      user.setIdentifier(String.valueOf(this.sestobjId));
    }
    // user.setObjType(SESTObjectTypeEnum.valueOf(this.));

    return user;
  }

  public void convertFromModel(User user) {

    this.setName(user.getName());
    this.setSurname(user.getSurname());
    this.setEmail(user.getEmail());
    this.setUsername(user.getUsername());
    this.setPassword(user.getPassword());
    if (user.getProfile() != null) {
      this.setProfile(user.getProfile().name());
    }
    if (user.getIdentifier() != null) {
      this.setSestobjId(user.getIdentifier());
    }
    // this.setId(id);
  }
}