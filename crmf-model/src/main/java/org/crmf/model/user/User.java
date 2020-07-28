/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="User.java"
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

import java.util.ArrayList;

import org.crmf.model.general.SESTObject;

public class User extends SESTObject {

	private String email;
	private String name;
	private String password;
	private UserProfileEnum profile;
	private String surname;
	private String username;
	private ArrayList<Experience> experiences;
	private ArrayList<UserSession> sessions;
	private ArrayList<UserRole> roles = new ArrayList<UserRole>();
	private ArrayList<PermissionGroup> permissionGroups;

	public User(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	public String getEmail(){
		return email;
	}

	public String getName(){
		return name;
	}

	public String getPassword(){
		return password;
	}

	public UserProfileEnum getProfile(){
		return profile;
	}

	public String getSurname(){
		return surname;
	}

	public String getUsername(){
		return username;
	}

	public void setEmail(String newVal){
		email = newVal;
	}

	public void setName(String newVal){
		name = newVal;
	}

	public void setPassword(String newVal){
		password = newVal;
	}

	public void setProfile(UserProfileEnum newVal){
		profile = newVal;
	}

	public void setSurname(String newVal){
		surname = newVal;
	}

	public void setUsername(String newVal){
		username = newVal;
	}

	public ArrayList<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}

	public ArrayList<UserSession> getSessions() {
		return sessions;
	}

	public void setSessions(ArrayList<UserSession> sessions) {
		this.sessions = sessions;
	}

	public ArrayList<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<UserRole> roles) {
		this.roles = roles;
	}

	public ArrayList<PermissionGroup> getPermissionGroups() {
		return permissionGroups;
	}

	public void setPermissionGroups(ArrayList<PermissionGroup> permissionGroups) {
		this.permissionGroups = permissionGroups;
	}
	
}