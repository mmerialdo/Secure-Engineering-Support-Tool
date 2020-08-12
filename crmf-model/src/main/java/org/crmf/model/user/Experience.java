/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Experience.java"
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

public class Experience {

	private String experienceData;
	private String updateTime;
	private String updateUsername;

	public String getExperienceData(){
		return experienceData;
	}

	public String getUpdateTime(){
		return updateTime;
	}

	public String getUpdateUsername(){
		return updateUsername;
	}

	public void setExperienceData(String newVal){
		experienceData = newVal;
	}

	public void setUpdateTime(String newVal){
		updateTime = newVal;
	}

	public void setUpdateUsername(String newVal){
		updateUsername = newVal;
	}
}