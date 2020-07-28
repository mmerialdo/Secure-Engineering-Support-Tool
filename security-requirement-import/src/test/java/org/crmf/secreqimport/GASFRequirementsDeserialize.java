/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="GASFRequirementsDeserialize.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.secreqimport;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.utility.secrequirement.SecurityRequirementSerializerDeserializer;
import org.junit.Test;

public class GASFRequirementsDeserialize {

	@Test
	public void deserializeGASFJson() throws IOException {
		String gasfJsonPath = ".//json/gasfRequirements.json";
		
		File fvmJson = new File(gasfJsonPath);
		byte[] bamJson = Files.readAllBytes(fvmJson.toPath());
		String vmJsonString = new String(bamJson, "UTF-8");

		SecurityRequirementSerializerDeserializer srSerDes = new SecurityRequirementSerializerDeserializer();

		ArrayList<SecurityRequirement> srs = srSerDes.getSRsFromGASFJSONString(vmJsonString);
		
        String jsonString = srSerDes.getJSONStringFromSRs(srs);
		
		FileWriter fw = new FileWriter(".//json/newGasfRequirements.json");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(jsonString);
		bw.close();
		fw.close();
		
		if(srs == null){
			assertEquals(true, false);
		}
		else{
			assertEquals(true, true);
		}
	}

}
