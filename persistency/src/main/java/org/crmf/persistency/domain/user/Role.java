/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Role.java"
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

import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;

public class Role {
  private Integer id;

  private String role;

  private Integer userId;

  private Integer projectId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role == null ? null : role.trim();
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public UserRole convertToModel() {

    UserRole role = new UserRole();
    role.setRole(UserRoleEnum.valueOf(this.getRole()));
    //role.setProject(this.getProjectId());

    return role;
  }

  public void convertFromModel(UserRole role) {

    this.setRole(role.getRole().name());
//		this.setUserId(userId);
//		this.setProjectId(role.getProject().getId..);
//		this.setId(id);
  }

  @Override
  public String toString() {
    return "Role [id=" + id + ", role=" + role + ", userId=" + userId + ", projectId=" + projectId + "]";
  }

}