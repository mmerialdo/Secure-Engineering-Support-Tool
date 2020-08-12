/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PrimaryAssetCategoryEnum.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/


package org.crmf.model.riskassessmentelements;

public enum PrimaryAssetCategoryEnum {
	Data_DataFile_Database("Data files and data bases accessed by applications"),
	Data_Shared_Office_File("Shared office files and data"),
	Data_Personal_Office_File("Personal office files and data"),
	Data_Physical_File("Written or printed files and data"),
	Data_Exchanged_Message("Exchanged messages, screen views, data individually sensitive"),
	Data_Digital_Mail("Electronic mailing"),
	Data_Physical_Mail("Written or printed mailing"),
	Data_Physical_Archive("Physical data archive"),
	Data_IT_Archive("Digital data archive"),
	Data_Published_Data("Data and information published on public or internal sites"),

	Service_User_Workspace("User workspace and environment"),
	Service_Telecommunication_Service("Telecommunication Services"),
	Service_Extended_Network_Service("Extended Network Service"),
	Service_Local_Network_Service("Local Area Network Service"),

	Service_Application_Service("Services provided by applications"),
	Service_Shared_Service("Shared office Services"),
	Service_User_Hardware("Equipment provided to end users"),
	Service_Common_Service("Common system Services"),
	Service_Web_editing_Service("Web editing Service (internal or public)"),

	Compliance_Policy_Personal_Information_Protection("Compliance to law or regulations relative to personal information protection"),
	Compliance_Policy_Financial_Communication("Compliance to law or regulations relative to financial communication"),
	Compliance_Policy_Digital_Accounting_Control("Compliance to law or regulations relative to digital accounting control"),
	Compliance_Policy_Intellectual_Property("Compliance to law or regulations relative to intellectual property"),
	Compliance_Policy_Protection_Of_Information_Systems("Compliance to law or regulations relative to the protection of information systems"),
	Compliance_Policy_People_And_Environment_Safety("Compliance to law or regulations relative to people safety and protection of environment");

	private String value;
	public String getValue() {
	    return value;
	}
	PrimaryAssetCategoryEnum(String value) {
	  this.value = value;
	}
}

