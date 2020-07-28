/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModel.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.RiskScenario;

public class RiskModel extends SESTObject {

	private String creationTime;
	private String updateTime;
	private ArrayList<RiskScenario> scenarios;

	public RiskModel(){
		scenarios = new ArrayList<RiskScenario>();
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public ArrayList<RiskScenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(ArrayList<RiskScenario> scenarios) {
		this.scenarios = scenarios;
	}

	
}