/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelSerializatorDeserializatorCommon.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.assetmodel;

import org.crmf.model.general.SESTObjectTypeEnum;

public class AssetModelSerializatorDeserializatorCommon {

	public static final String USER = "User";
	public static final String ASSESSMENT_PROFILE = "AssessmentProfile";
	public static final String THREAT_MODEL = "ThreatModel";
	public static final String SAFEGUARD_MODEL = "SafeguardModel";
	public static final String ASSESSMENT_PROJECT = "AssessmentProject";
	public static final String RISK_MODEL = "RiskModel";
	public static final String RISK_TREATMENT_MODEL = "RiskTreatmentModel";
	public static final String AUDIT = "Audit";
	public static final String ASSET_MODEL = "AssetModel";
	public static final String VULNERABILITY_MODEL = "VulnerabilityModel";
	public static final String ASSESSMENT_TEMPLATE = "AssessmentTemplate";

	//This method returns the SESTObjectTypeEnum corresponding to the String in the argument
		public static SESTObjectTypeEnum getSESTObjecType(String objTypeString){
			if(objTypeString.equals("")){
				return SESTObjectTypeEnum.AssetModel;
			}
			else if(objTypeString.equals(USER)){
				return SESTObjectTypeEnum.User;
			}
			else if(objTypeString.equals(ASSESSMENT_PROFILE)){
				return SESTObjectTypeEnum.AssessmentProfile;
			}
			else if(objTypeString.equals(THREAT_MODEL)){
				return SESTObjectTypeEnum.ThreatModel;
			}
			else if(objTypeString.equals(SAFEGUARD_MODEL)){
				return SESTObjectTypeEnum.SafeguardModel;
			}
			else if(objTypeString.equals(ASSESSMENT_PROJECT)){
				return SESTObjectTypeEnum.AssessmentProject;
			}
			else if(objTypeString.equals(RISK_MODEL)){
				return SESTObjectTypeEnum.RiskModel;
			}
			else if(objTypeString.equals(RISK_TREATMENT_MODEL)){
				return SESTObjectTypeEnum.RiskTreatmentModel;
			}
			else if(objTypeString.equals(AUDIT)){
				return SESTObjectTypeEnum.Audit;
			}
			if(objTypeString.equals(ASSET_MODEL)){
				return SESTObjectTypeEnum.AssetModel;
			}
			else if(objTypeString.equals(VULNERABILITY_MODEL)){
				return SESTObjectTypeEnum.VulnerabilityModel;
			}
			else if(objTypeString.equals(ASSESSMENT_TEMPLATE)){
				return SESTObjectTypeEnum.AssessmentTemplate;
			}
			else{
				return SESTObjectTypeEnum.AssetModel;
			}
		}
}
