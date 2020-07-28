/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="QuestionnaireTypeEnum.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.audit;

public enum QuestionnaireTypeEnum {
	MEHARI_OrganizationSecurity("Organization of security"),
	MEHARI_SitesSecurity("Sites Security"),
	MEHARI_SecurityPremises("Security of Premises"),
	MEHARI_ExtendedNetwork("Wide-area Network (intersite)"),
	MEHARI_LAN("Local Area Network (LAN)"),
	MEHARI_NetworkOperations("Network Operations"),
	MEHARI_SecurityAndArchitecture("Security and Architecture of Systems"),
	MEHARI_ITProductionEnvironment("IT Production Environment"),
	MEHARI_ApplicationSecurity("Application Security"),
	MEHARI_SecurityOfProjectsAndDevelopment("Security of Application Projects and Developments"),
	MEHARI_UserWorkEquipment("Protection of users' work equipment"),
	MEHARI_TelecommunicationOperation("Telecommunications operations"),
	MEHARI_ManagementProcesses("Management Processes"),
	MEHARI_InformationSecurityManagement("Information Security management");

	private String description;

	QuestionnaireTypeEnum(String value) {
		this.description = value;
	}

	public String getDescription() {
		return description;
	}

	public static QuestionnaireTypeEnum getDescription(String value) {
		if(value == null)
			throw new IllegalArgumentException();
		for(QuestionnaireTypeEnum v : values())
			if(value.equalsIgnoreCase(v.getDescription())) return v;
		throw new IllegalArgumentException();
	}
}