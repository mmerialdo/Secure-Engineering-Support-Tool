/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelDeserialize.java"
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

import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AssetModelDeserializeTest {

	@Test
	public void deserializeAssetModel() throws IOException {
		
		String assetModelPath = ".//json/assetmodel.json";
		
		File famJson = new File(assetModelPath);
		byte[] bamJson = Files.readAllBytes(famJson.toPath());
		String amJsonString = new String(bamJson, "UTF-8");

		AssetModelSerializerDeserializer amSerDes = new AssetModelSerializerDeserializer();

		AssetModel am = amSerDes.getAMFromJSONString(amJsonString);
		if(am == null){
			Assertions.assertEquals(true, false);
		}
		else{
			Assertions.assertEquals(true, true);
		}

	}

}
