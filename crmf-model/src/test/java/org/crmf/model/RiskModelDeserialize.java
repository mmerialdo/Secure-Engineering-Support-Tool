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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.junit.Test;

public class RiskModelDeserialize {

	@Test
	public void deserializeRiskModel() throws IOException {
        String riskModelPath = ".//json/riskmodel.json";
		
		File ftmJson = new File(riskModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String rmJsonString = new String(btmJson, "UTF-8");

		RiskModelSerializerDeserializer rmSerDes = new RiskModelSerializerDeserializer();

		RiskModel rm = rmSerDes.getRMFromJSONString(rmJsonString);
		if(rm == null){
			assertEquals(true, false);
		}
		else{
			assertEquals(true, true);
		}
	
	}
}
