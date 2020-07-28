/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.threatmodel;

import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//This class manages the custom serialization/deserialization of the ThreatModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class ThreatModelSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(ThreatModelSerializerDeserializer.class.getName());
	
	/**
	 * Creates JSON from POJO
	 * @param ThreatModel
	 * @return Json
	 */
     public String getJSONStringFromTM(ThreatModel tm){
		
		GsonBuilder gsonBuilder = new GsonBuilder();	
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(tm);
	}

	public ThreatModel getTMFromJSONString(String tmJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();	
		gsonBuilder.registerTypeAdapter(ThreatScore.class, new ThreatScoreInstanceCreator());
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();
		
		ThreatModel tm;
		
		try{
			LOG.info("Threat Serialization/Deserialization tmJsonString:: "+tmJsonString.substring(0, (tmJsonString.length() > 500 ? 500 : tmJsonString.length())));
			tm = gson.fromJson(tmJsonString, ThreatModel.class);
		}
		catch(Exception e){
			LOG.info("Threat Serialization/Deserialization error:: "+e.getMessage());
			return null;
		}
		
		if(tm == null){
			LOG.info("Conversion of json into Threat Model returned null");
			return null;
		}

		return tm;
	}

}
