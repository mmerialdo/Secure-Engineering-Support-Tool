/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModel.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.Safeguard;

import java.util.ArrayList;

public class SafeguardModel extends SESTObject {

	private String creationTime;
	private String updateTime;
	private ArrayList<Safeguard> safeguards;

	public SafeguardModel(){
		safeguards = new ArrayList<>();
	}

	public String getUpdateTime(){
		return updateTime;
	}

	public void setUpdateTime(String newVal){
		updateTime = newVal;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public ArrayList<Safeguard> getSafeguards() {
		return safeguards;
	}

	public void setSafeguards(ArrayList<Safeguard> safeguards) {
		this.safeguards = safeguards;
	}
	
}