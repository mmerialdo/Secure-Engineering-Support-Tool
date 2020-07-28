/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfile.java"
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
import org.crmf.model.general.SESTObject;

public class AssessmentProfile extends SESTObject {

	private String creationTime;
	private String description;
	private String name;
	private String organization;
	private PhaseEnum phase;
	private RiskMethodologyEnum riskMethodology;
	private String updateTime;
	private ArrayList<AssessmentTemplate> templates;


	public AssessmentProfile(){

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

	public String getOrganization(){
		return organization;
	}

	public RiskMethodologyEnum getRiskMethodology(){
		return riskMethodology;
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

	public void setOrganization(String newVal){
		organization = newVal;
	}

	public void setRiskMethodology(RiskMethodologyEnum newVal){
		riskMethodology = newVal;
	}

	public void setUpdateTime(String newVal){
		updateTime = newVal;
	}

	public PhaseEnum getPhase() {
		return phase;
	}

	public void setPhase(PhaseEnum phase) {
		this.phase = phase;
	}

	public ArrayList<AssessmentTemplate> getTemplates() {
		return templates;
	}

	public void setTemplates(ArrayList<AssessmentTemplate> templates) {
		this.templates = templates;
	}

	
}