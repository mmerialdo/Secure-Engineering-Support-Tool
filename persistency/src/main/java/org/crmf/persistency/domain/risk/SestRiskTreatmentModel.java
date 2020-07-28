/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestRiskTreatmentModel.java"
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

import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestRiskTreatmentModel {

	private Integer id;
	String sestobjId;
	String riskTreatmentModelJson;
	
	private static final Logger LOG = LoggerFactory.getLogger(SestRiskTreatmentModel.class.getName());
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSestobjId() {
		return sestobjId;
	}
	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}
	public String getRiskTreatmentModelJson() {
		return riskTreatmentModelJson;
	}
	public void setRiskTreatmentModelJson(String riskTreatmentModelJson) {
		this.riskTreatmentModelJson = riskTreatmentModelJson;
	}

	public RiskTreatmentModel convertToModel() {
		LOG.info("SestRiskTreatmentModel convertToModel");
		try {
			RiskTreatmentModelSerializerDeserializer riskTreatmentModelDeserializer = new RiskTreatmentModelSerializerDeserializer();

			RiskTreatmentModel rtmodel = new RiskTreatmentModel();
			rtmodel.setIdentifier(this.sestobjId);
			rtmodel = riskTreatmentModelDeserializer.getRTMFromPersistencyJSONString(riskTreatmentModelJson);
			
			return rtmodel;
			
		} catch (Exception ex) {
			LOG.error("Unable to deserialize risk model!!! "+this.getSestobjId(), ex);
			return null;
		}
	}

}
