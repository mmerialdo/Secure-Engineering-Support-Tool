/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestRiskModel.java"
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

import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestRiskModel {

	private static final Logger LOG = LoggerFactory.getLogger(SestRiskModel.class.getName());
	private Integer id;
	private String sestobjId;
	private String riskModelJson;

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

	public String getRiskModelJson() {
		return riskModelJson;
	}

	public void setRiskModelJson(String riskModelJson) {
		this.riskModelJson = riskModelJson;
	}

	public RiskModel convertToModel() {
		LOG.info("SestRiskModel convertToModel");
		try {
			RiskModelSerializerDeserializer riskModelDeserializer = new RiskModelSerializerDeserializer();

			return riskModelDeserializer.getRMFromJSONString(this.getRiskModelJson());
		} catch (Exception ex) {
			LOG.error("Unable to deserialize risk model!!! "+this.getSestobjId(), ex);
			return null;
		}
	}

}
