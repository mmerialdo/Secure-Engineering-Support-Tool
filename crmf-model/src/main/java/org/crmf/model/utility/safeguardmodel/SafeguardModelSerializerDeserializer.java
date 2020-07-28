/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.safeguardmodel;

import org.crmf.model.riskassessment.SafeguardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//This class manages the custom serialization/deserialization of the SafeguardModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class SafeguardModelSerializerDeserializer {
private static final Logger LOG = LoggerFactory.getLogger(SafeguardModelSerializerDeserializer.class.getName());
	
	 //Creates JSON from POJO
     public String getJSONStringFromSM(SafeguardModel sm){
		
		GsonBuilder gsonBuilder = new GsonBuilder();	
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(sm);
	}

	public SafeguardModel getSMFromJSONString(String smJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();	
	
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();
		
		SafeguardModel sm;
		
		try{
			LOG.info("Safeguard Serialization/Deserialization smJsonString:: " + smJsonString.substring(0, (smJsonString.length() > 500 ? 500 : smJsonString.length())));
			sm = gson.fromJson(smJsonString, SafeguardModel.class);
		}
		catch(Exception e){
			LOG.info("Safeguard Serialization/Deserialization error:: " + e.getMessage());
			return null;
		}
		
		if(sm == null){
			LOG.info("Conversion of json into Safeguard Model returned null");
			return null;
		}

		return sm;
	}
}
