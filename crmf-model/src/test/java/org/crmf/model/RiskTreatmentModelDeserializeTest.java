/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelDeserialize.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model;

import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class RiskTreatmentModelDeserializeTest {

	@Test
	public void deserializeClientFull() throws IOException {
        String riskTreatmentModelPath = ".//json/clientrisktreatmentmodel.json";
		
		File ftmJson = new File(riskTreatmentModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String tmJsonString = new String(btmJson, "UTF-8");

		RiskTreatmentModelSerializerDeserializer tmSerDes = new RiskTreatmentModelSerializerDeserializer();

		RiskTreatmentModel tm = tmSerDes.getRTMFromClientJSONString(tmJsonString);
		
		HashMap<String, Safeguard> clientResultingSafeguardMap = getSafeguardHashMap(tm.getResultingSafeguards());
		
		if(tm == null){
			Assertions.assertEquals(true, false);
		}
		else{
			Assertions.assertEquals(true, true);
		}
	}
	
	private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
		// Here we put the Safeguard on an HashMap (faster to be used later)
		HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

		for (Safeguard safeguard : safeguards) {
			String safeguardId = safeguard.getIdentifier();

			if (safeguard.getScore() != null) {

				safeguardMap.put(safeguardId, safeguard);
			}
		}
	
		return safeguardMap;
	}
	
	@Test
	public void deserializeClientDetailAsset() throws IOException {
        String riskTreatmentModelPath = ".//json/clientrisktreatmentmodeldetail.json";
		
		File ftmJson = new File(riskTreatmentModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String tmJsonString = new String(btmJson, "UTF-8");

		RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
		String assetPrimaryCategory = rtmsr.getAssetCategoryFromClientJSONString(tmJsonString);
		
		if(assetPrimaryCategory == null){

			Assertions.assertEquals(true, false);
		}
		Assertions.assertEquals(true, true);
	}
	
	@Test
	public void deserializeClientDetail() throws IOException {
        String riskTreatmentModelPath = ".//json/clientrisktreatmentmodeldetail.json";
		
		File ftmJson = new File(riskTreatmentModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String tmJsonString = new String(btmJson, "UTF-8");

		RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
		
		RiskTreatmentModel tm = rtmsr.getRTMFromClientDetailJSONString(tmJsonString);
		
		HashMap<String, Safeguard> clientResultingSafeguardMap = getSafeguardHashMap(tm.getResultingSafeguards());
		
		if(tm == null){
			Assertions.assertEquals(true, false);
		}
		else{
			Assertions.assertEquals(true, true);
		}
	}

}
