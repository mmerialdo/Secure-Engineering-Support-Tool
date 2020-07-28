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

	    //This method returns the SESTObjectTypeEnum corresponding to the String in the argument 
		public static SESTObjectTypeEnum getSESTObjecType(String objTypeString){
			if(objTypeString.equals("")){
				return SESTObjectTypeEnum.AssetModel;
			}
			else if(objTypeString.equals("User")){
				return SESTObjectTypeEnum.User;
			}
			else if(objTypeString.equals("AssessmentProfile")){
				return SESTObjectTypeEnum.AssessmentProfile;
			}
			else if(objTypeString.equals("ThreatModel")){
				return SESTObjectTypeEnum.ThreatModel;
			}
			else if(objTypeString.equals("SafeguardModel")){
				return SESTObjectTypeEnum.SafeguardModel;
			}
			else if(objTypeString.equals("AssessmentProject")){
				return SESTObjectTypeEnum.AssessmentProject;
			}
			else if(objTypeString.equals("RiskModel")){
				return SESTObjectTypeEnum.RiskModel;
			}
			else if(objTypeString.equals("RiskTreatmentModel")){
				return SESTObjectTypeEnum.RiskTreatmentModel;
			}
			else if(objTypeString.equals("Audit")){
				return SESTObjectTypeEnum.Audit;
			}
			if(objTypeString.equals("AssetModel")){
				return SESTObjectTypeEnum.AssetModel;
			}
			else if(objTypeString.equals("VulnerabilityModel")){
				return SESTObjectTypeEnum.VulnerabilityModel;
			}
			else if(objTypeString.equals("AssessmentTemplate")){
				return SESTObjectTypeEnum.AssessmentTemplate;
			}
			else{
				return SESTObjectTypeEnum.AssetModel;
			}
		}
}
