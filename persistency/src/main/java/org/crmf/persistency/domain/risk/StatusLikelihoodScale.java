/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="StatusLikelihoodScale.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.risk;

public class StatusLikelihoodScale {
public StatusLikelihoodScale(){
		
	}
	
	public StatusLikelihoodScale(int profileId, int projectId, int intrinsincLikelihood, String threatClass, int dissiuasion, int prevention, int calculatedLikelihood){
		this.profileId = profileId;
		this.projectId = projectId;
		
		this.intrinsincLikelihood = intrinsincLikelihood;
		
		this.threatClass = threatClass;
		
		this.dissiuasion = dissiuasion;
		this.prevention = prevention;
		
		this.calculatedLikelihood = calculatedLikelihood;
				
	}

	private int profileId;
	private int projectId;
	
	private int intrinsincLikelihood;
	
	private String threatClass;
	
	private int dissiuasion;
	private int prevention;
	
	private int calculatedLikelihood;

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getIntrinsincLikelihood() {
		return intrinsincLikelihood;
	}

	public void setIntrinsincLikelihood(int intrinsincLikelihood) {
		this.intrinsincLikelihood = intrinsincLikelihood;
	}

	public String getThreatClass() {
		return threatClass;
	}

	public void setThreatClass(String threatClass) {
		this.threatClass = threatClass;
	}

	public int getDissiuasion() {
		return dissiuasion;
	}

	public void setDissiuasion(int dissiuasion) {
		this.dissiuasion = dissiuasion;
	}

	public int getPrevention() {
		return prevention;
	}

	public void setPrevention(int prevention) {
		this.prevention = prevention;
	}

	public int getCalculatedLikelihood() {
		return calculatedLikelihood;
	}

	public void setCalculatedLikelihood(int calculatedLikelihood) {
		this.calculatedLikelihood = calculatedLikelihood;
	}
}
