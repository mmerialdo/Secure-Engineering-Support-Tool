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

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;

import java.util.ArrayList;
import java.util.List;

public class RiskScenarioReferenceModel extends SESTObject {

	private String creationTime;
	private String updateTime;
	private List<RiskScenarioReference> scenarios;

	public RiskScenarioReferenceModel(){
		scenarios = new ArrayList<RiskScenarioReference>();
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

	public List<RiskScenarioReference> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<RiskScenarioReference> scenarios) {
		this.scenarios = scenarios;
	}

	
}