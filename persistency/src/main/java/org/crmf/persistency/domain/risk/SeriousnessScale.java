/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SeriousnessScale.java"
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

public class SeriousnessScale {
	public SeriousnessScale(){
		
	}
	
	public SeriousnessScale(int impact, int likelihood, int seriousness){
		this.impact = impact;
		this.likelihood = likelihood;
		
		this.seriousness = seriousness;
	}
	private int profileId;
	private int projectId;
	
	private int impact;
	private int likelihood;
	
	private int seriousness;

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

	public int getImpact() {
		return impact;
	}

	public void setImpact(int impact) {
		this.impact = impact;
	}

	public int getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(int likelihood) {
		this.likelihood = likelihood;
	}

	public int getSeriousness() {
		return seriousness;
	}

	public void setSeriousness(int seriousness) {
		this.seriousness = seriousness;
	}
}
