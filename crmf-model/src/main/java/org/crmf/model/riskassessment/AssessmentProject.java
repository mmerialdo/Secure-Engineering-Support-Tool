/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProject.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessment;

import java.util.ArrayList;

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.general.SESTObject;
import org.crmf.model.user.User;

public class AssessmentProject extends SESTObject {

	private String creationTime;
	private String description;
	private String name;
	private RiskMethodologyEnum riskMethodology;
	private AssessmentStatusEnum status;
	private String updateTime;
	private ArrayList<AssessmentProcedure> procedures;
	private AssessmentProfile profile;
	private AssessmentTemplate template;
	private SystemProject systemProject;
	private ArrayList<User> users;
	private User projectManager;
	private ArrayList<SestAuditModel> audits;

	public AssessmentProject(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	public String getCreationTime(){
		return creationTime;
	}

	public String getDescription(){
		return description;
	}

	public String getName(){
		return name;
	}

	public RiskMethodologyEnum getRiskMethodology(){
		return riskMethodology;
	}

	public AssessmentStatusEnum getStatus(){
		return status;
	}

	public String getUpdateTime(){
		return updateTime;
	}

	public void setCreationTime(String newVal){
		creationTime = newVal;
	}

	public void setDescription(String newVal){
		description = newVal;
	}

	public void setName(String newVal){
		name = newVal;
	}

	public void setRiskMethodology(RiskMethodologyEnum newVal){
		riskMethodology = newVal;
	}

	public void setStatus(AssessmentStatusEnum newVal){
		status = newVal;
	}

	public void setUpdateTime(String newVal){
		updateTime = newVal;
	}

	public ArrayList<AssessmentProcedure> getProcedures() {
		return procedures;
	}

	public void setProcedures(ArrayList<AssessmentProcedure> procedures) {
		this.procedures = procedures;
	}

	public AssessmentProfile getProfile() {
		return profile;
	}

	public void setProfile(AssessmentProfile profile) {
		this.profile = profile;
	}

	public AssessmentTemplate getTemplate() {
		return template;
	}

	public void setTemplate(AssessmentTemplate template) {
		this.template = template;
	}

	public SystemProject getSystemProject() {
		return systemProject;
	}

	public void setSystemProject(SystemProject systemProject) {
		this.systemProject = systemProject;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public User getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(User projectManager) {
		this.projectManager = projectManager;
	}

	public ArrayList<SestAuditModel> getAudits() {
		return audits;
	}

	public void setAudits(ArrayList<SestAuditModel> audits) {
		this.audits = audits;
	}
	
}