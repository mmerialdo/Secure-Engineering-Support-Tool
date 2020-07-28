/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatment.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessmentelements;

import org.crmf.model.general.SESTObject;

public class RiskTreatment extends SESTObject {

	private ImpactEnum currentSeriousness;
	private ImpactEnum resultingSeriousness;
	private String riskScenarioId;

	public RiskTreatment(){
		currentSeriousness = ImpactEnum.LOW;
		resultingSeriousness = ImpactEnum.LOW;
	}

	public ImpactEnum getCurrentSeriousness() {
		return currentSeriousness;
	}

	public void setCurrentSeriousness(ImpactEnum currentSeriousness) {
		this.currentSeriousness = currentSeriousness;
	}

	public ImpactEnum getResultingSeriousness() {
		return resultingSeriousness;
	}

	public void setResultingSeriousness(ImpactEnum resultingSeriousness) {
		this.resultingSeriousness = resultingSeriousness;
	}

	public String getRiskScenarioId() {
		return riskScenarioId;
	}

	public void setRiskScenarioId(String riskScenarioId) {
		this.riskScenarioId = riskScenarioId;
	}
}
