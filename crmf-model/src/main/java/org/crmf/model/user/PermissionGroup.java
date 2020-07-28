/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PermissionGroup.java"
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

import java.util.List;

import org.crmf.model.general.SESTObject;

public class PermissionGroup {

	private List<SESTObject> permissionedObjects;
	private String permissionGroupId;
	private PermissionTypeEnum type;
	private String userId;

	public PermissionGroup(){

	}
	
	public PermissionGroup(PermissionTypeEnum pType, String pUserId){
       this.type = pType;
       this.userId = pUserId;
	}

	public void finalize() throws Throwable {

	}

	public PermissionTypeEnum getType(){
		return type;
	}

	public void setType(PermissionTypeEnum newVal){
		type = newVal;
	}

	public List<SESTObject> getPermissionedObjects() {
		return permissionedObjects;
	}

	public void setPermissionedObjects(List<SESTObject> permissionedObjects) {
		this.permissionedObjects = permissionedObjects;
	}

	public String getPermissionGroupId() {
		return permissionGroupId;
	}

	public void setPermissionGroupId(String permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}