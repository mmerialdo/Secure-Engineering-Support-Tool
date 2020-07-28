/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.riskmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.crmf.model.riskassessment.RiskScenarioReferenceModel;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the custom serialization/deserialization of the RiskScenarioReferenceModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class RiskScenarioReferenceModelSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(ThreatModelSerializerDeserializer.class.getName());
	
	 //Creates JSON from POJO
     public String getJSONStringFromRM(RiskScenarioReferenceModel rm){
		
		GsonBuilder gsonBuilder = new GsonBuilder();	
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(rm);
	}

	public RiskScenarioReferenceModel getRMFromJSONString(String rmJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();	
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();

		RiskScenarioReferenceModel rm;
		
		try{
			LOG.info("RiskScenarioReferenceModel Serialization/Deserialization rmJsonString:: " + rmJsonString.substring(0, (rmJsonString.length() > 500 ? 500 : rmJsonString.length())));
			rm = gson.fromJson(rmJsonString, RiskScenarioReferenceModel.class);
		}
		catch(Exception e){
			LOG.info("RiskScenarioReferenceModel Serialization/Deserialization error:: "+e.getMessage());
			return null;
		}
		
		if(rm == null){
			LOG.info("Conversion of json into RiskScenarioReferenceModel returned null");
			return null;
		}

		return rm;
	}

}
	

