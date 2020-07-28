/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelCloner.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.risktreatmentmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.RiskTreatment;
import org.crmf.model.riskassessmentelements.ScenarioResultEnum;

//This class is responsible for cloning RiskTreatmentModels from Json strings and POJO
public class RiskTreatmentModelCloner {

	private String identifier;

	public RiskTreatmentModel clone(RiskTreatmentModel rm) {

		identifier = UUID.randomUUID().toString();
		RiskTreatmentModel rtmNew = new RiskTreatmentModel();
		rtmNew.setIdentifier(identifier);
		
		return rtmNew;
	}

	public String clone(String rtmJson) {
		
		RiskTreatmentModelSerializerDeserializer rmsd = new RiskTreatmentModelSerializerDeserializer();
		
		RiskTreatmentModel rtm = rmsd.getRTMFromPersistencyJSONString(rtmJson);
			
		UUID uuid = UUID.randomUUID();
		rtm.setIdentifier(uuid.toString());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		rtm.setCreationTime(df.format(now));
		rtm.setUpdateTime(df.format(now));
		
		
		identifier = rtm.getIdentifier();
		return rmsd.getJSONStringFromRTM(rtm);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String create(RiskModel risks, SafeguardModel safeguards) {
		identifier = UUID.randomUUID().toString();
		RiskTreatmentModel rtmNew = new RiskTreatmentModel();
		rtmNew.setIdentifier(identifier);
		
		rtmNew.setObjType(SESTObjectTypeEnum.RiskTreatmentModel);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
			
		rtmNew.setCreationTime(df.format(now));
		rtmNew.setUpdateTime(df.format(now));
		
		rtmNew.setResultingSafeguards(safeguards.getSafeguards());
		
		for(RiskScenario scenario : risks.getScenarios()){
			
			if(!scenario.isExcluded() && scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce) && !scenario.getAssetId().equals("") &&
					!scenario.getVulnerabilityId().equals("") && !scenario.getThreatId().equals("")){
				RiskTreatment treatment = new RiskTreatment();
				String treatmentId = UUID.randomUUID().toString();
				treatment.setIdentifier(treatmentId);
				treatment.setObjType(SESTObjectTypeEnum.RiskTreatmentModel);
				
				treatment.setCurrentSeriousness(scenario.getCalculatedSeriousness());
				treatment.setRiskScenarioId(scenario.getIdentifier());
				
				rtmNew.getRiskTreatments().add(treatment);
			}
		}
		
		RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
		
		return rtmsr.getJSONStringFromRTM(rtmNew);
	}

}
