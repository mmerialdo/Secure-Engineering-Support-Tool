/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelSerialize.java"
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
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.junit.Test;

public class RiskModelSerialize {

	@Test
	public void serializeRiskModel() throws IOException {
        RiskModelSerializerDeserializer rmSerDes = new RiskModelSerializerDeserializer();
		
		RiskModel rm = new RiskModel();
		UUID uuid = UUID.randomUUID();
		rm.setIdentifier(uuid.toString());
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		rm.setCreationTime(df.format(now));
		rm.setUpdateTime(df.format(now));
		rm.setObjType(SESTObjectTypeEnum.RiskModel);
		
		RiskScenario scenario = new RiskScenario();
		scenario.setAssetId("42605102-2b52-4f44-b159-3ae28489da28");
		scenario.setVulnerabilityId("579f0196-abf8-4f76-95d0-ec7978faff31");
		scenario.setThreatId("2fa73505-08b9-4717-bd6a-d9d59bc84a37");
		
		scenario.setImpactScope(SecurityImpactScopeEnum.Availability);
		scenario.setIntrinsicLikelihood(LikelihoodEnum.HIGH);
		scenario.setIntrinsicImpact(ImpactEnum.HIGH);
		scenario.setIntrinsicSeriousness(ImpactEnum.HIGH);
		
		scenario.setCalculatedLikelihood(LikelihoodEnum.HIGH);
		scenario.setCalculatedImpact(ImpactEnum.HIGH);
		scenario.setCalculatedSeriousness(ImpactEnum.HIGH);
		
		scenario.setObjType(SESTObjectTypeEnum.RiskModel);
		scenario.setDescription("production incident within the production premises (Accidental)");
		
		scenario.setIdentifier(UUID.randomUUID().toString());
		
		rm.getScenarios().add(scenario);
		
		
		String jsonString = rmSerDes.getJSONStringFromRM(rm);
		
		FileWriter fw = new FileWriter(".//json/riskmodel.json");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(jsonString);
		bw.close();
		fw.close();
	}

}
