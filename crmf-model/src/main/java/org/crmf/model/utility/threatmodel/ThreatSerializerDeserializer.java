/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatSerializerDeserializer.java"
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

import org.crmf.model.riskassessmentelements.Threat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//This class manages the custom deserialization of the Threat
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class ThreatSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(ThreatSerializerDeserializer.class.getName());

	/**
	 * Creates JSON from POJO
	 * @param Vulnerability
	 * @return Json
	 */
     public String getJSONStringFromTM(Threat threat){
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(threat);
	}
}
