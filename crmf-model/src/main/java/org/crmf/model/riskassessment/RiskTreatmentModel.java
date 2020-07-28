/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModel.java"
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
import org.crmf.model.riskassessmentelements.RiskTreatment;
import org.crmf.model.riskassessmentelements.Safeguard;

public class RiskTreatmentModel  extends SESTObject {
	private String creationTime;
	private String updateTime;
	private ArrayList<RiskTreatment> riskTreatments;
	private ArrayList<Safeguard> resultingSafeguards;
	
	public RiskTreatmentModel(){
		riskTreatments = new ArrayList<>();
		resultingSafeguards = new ArrayList<>();
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

	public ArrayList<RiskTreatment> getRiskTreatments() {
		return riskTreatments;
	}

	public void setRiskTreatments(ArrayList<RiskTreatment> riskTreatments) {
		this.riskTreatments = riskTreatments;
	}

	public ArrayList<Safeguard> getResultingSafeguards() {
		return resultingSafeguards;
	}

	public void setResultingSafeguards(ArrayList<Safeguard> resultingSafeguards) {
		this.resultingSafeguards = resultingSafeguards;
	}
}
