/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelDeserialize.java"
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

import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.junit.Test;

public class ThreatModelDeserialize {

	@Test
	public void deserializeThreatModel() throws IOException {
        String threatModelPath = ".//json/threatmodel.json";
		
		File ftmJson = new File(threatModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String tmJsonString = new String(btmJson, "UTF-8");

		ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();

		ThreatModel tm = tmSerDes.getTMFromJSONString(tmJsonString);
		if(tm == null){
			assertEquals(true, false);
		}
		else{
			assertEquals(true, true);
		}
	
	}

}
