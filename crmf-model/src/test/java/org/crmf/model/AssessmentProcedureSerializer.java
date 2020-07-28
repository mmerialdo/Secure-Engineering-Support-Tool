/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureSerializer.java"
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AssessmentProcedureSerializer {

	@Test
	public void serializeAssessmentProcedure() throws IOException {
		
        String assetModelPath = ".//json/assetmodel.json";
		
		File famJson = new File(assetModelPath);
		byte[] bamJson = Files.readAllBytes(famJson.toPath());
		String amJsonString = new String(bamJson, "UTF-8");

		AssetModelSerializerDeserializer amSerDes = new AssetModelSerializerDeserializer();

		AssetModel am = amSerDes.getAMFromJSONString(amJsonString);
		
		
		AssessmentProcedure procedure = new AssessmentProcedure();
		
		UUID uuid = UUID.randomUUID();

		procedure.setIdentifier(uuid.toString());
		procedure.setName("Test Procedure");
		procedure.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
		procedure.setPhase(PhaseEnum.Initial);
		procedure.setStatus(AssessmentStatusEnum.OnGoing);
		procedure.setAssetModel(new AssetModel());
		
        GsonBuilder gsonBuilder = new GsonBuilder();
		
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
		
		JsonElement procedureJsonElement = gson.toJsonTree(procedure);
		JsonElement assetModelJsonElement = amSerDes.getJSONElementFromAM(am);
		
		
		if (!((JsonObject) procedureJsonElement).get("assetModel").isJsonNull()) {
			((JsonObject) procedureJsonElement).add("assetModel", assetModelJsonElement);
		}
			
		gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
		String procedureJson = gson.toJson(procedureJsonElement);
		
		FileWriter fw = new FileWriter(".//json/newprocedure.json");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(procedureJson);
		bw.close();
		fw.close();
		
	}

}
