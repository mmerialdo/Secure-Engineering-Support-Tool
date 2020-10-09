/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelDeserialize.java"
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

import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RiskModelDeserializeTest {

	@Test
	public void deserializeRiskModel() throws IOException {
        String riskModelPath = ".//json/riskmodel.json";
		
		File ftmJson = new File(riskModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String rmJsonString = new String(btmJson, "UTF-8");

		RiskModelSerializerDeserializer rmSerDes = new RiskModelSerializerDeserializer();

		RiskModel rm = rmSerDes.getRMFromJSONString(rmJsonString);
		if(rm == null){
			Assertions.assertEquals(true, false);
		}
		else{
			Assertions.assertEquals(true, true);
		}
	
	}
}
