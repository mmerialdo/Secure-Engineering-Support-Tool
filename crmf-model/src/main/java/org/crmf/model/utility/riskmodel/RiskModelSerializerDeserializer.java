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

import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//This class manages the custom serialization/deserialization of the RiskModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class RiskModelSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(ThreatModelSerializerDeserializer.class.getName());
	
	 //Creates JSON from POJO
     public String getJSONStringFromRM(RiskModel rm){
		
		GsonBuilder gsonBuilder = new GsonBuilder();	
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(rm);
	}

	public RiskModel getRMFromJSONString(String rmJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();	
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();
		
		RiskModel rm;
		
		try{
			LOG.info("RiskModel Serialization/Deserialization rmJsonString:: " + rmJsonString.substring(0, (rmJsonString.length() > 500 ? 500 : rmJsonString.length())));
			rm = gson.fromJson(rmJsonString, RiskModel.class);
		}
		catch(Exception e){
			LOG.info("RiskModel Serialization/Deserialization error:: "+e.getMessage());
			return null;
		}
		
		if(rm == null){
			LOG.info("Conversion of json into Risk Model returned null");
			return null;
		}

		return rm;
	}

}
	

