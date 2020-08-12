/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserRole.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.user;

public class UserRole {

	private UserRoleEnum role;
	private User user;
	private String projectIdentifier;
	private String projectName;

	public UserRoleEnum getRole(){
		return role;
	}

	public void setRole(UserRoleEnum newVal){
		role = newVal;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}