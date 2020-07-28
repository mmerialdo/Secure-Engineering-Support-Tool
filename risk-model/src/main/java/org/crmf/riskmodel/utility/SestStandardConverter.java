/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestStandardConverter.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.riskmodel.utility;

import java.util.ArrayList;

import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages how SEST normalizes risk scenario CSV strings into the SEST Data Model
public class SestStandardConverter {

	private static final Logger LOG = LoggerFactory.getLogger(SestStandardConverter.class.getName());
	/*
	 Translate the Vulnerability supporting Asset into our ENUM standard
	 */
	public static ArrayList<SecondaryAssetCategoryEnum> checkSecondaryAssetCategory(String category){
		ArrayList<SecondaryAssetCategoryEnum> categories = new ArrayList<>();
		
		if(category.equals("File")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("Files")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("data file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("e-mail file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("fax file")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("data")){
			categories.add(SecondaryAssetCategoryEnum.Data_File);
		}
		if(category.equals("Electronic_Media")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
		}
		if(category.equals("Media")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("Access means")){
			categories.add(SecondaryAssetCategoryEnum.Data_Access_Mean);
		}
		if(category.equals("PC")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
		}
		if(category.equals("Document")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("printouts")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("Messages")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("Transaction")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("data transferred")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("screen")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("Message")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("transaction")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("e-mails")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("fax")){
			categories.add(SecondaryAssetCategoryEnum.Data_Message);
		}
		if(category.equals("fax mail")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("post mail")){
			categories.add(SecondaryAssetCategoryEnum.Non_Electronic_Media);
		}
		if(category.equals("premises")){
			categories.add(SecondaryAssetCategoryEnum.Premise);
		}
		if(category.equals("Software")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("application")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("program file")){
			categories.add(SecondaryAssetCategoryEnum.Software_Off_the_Shelf);
			categories.add(SecondaryAssetCategoryEnum.Software_Custom);
		}
		if(category.equals("software configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("connection process")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("security configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
		}
		if(category.equals("hardware configuration")){
			categories.add(SecondaryAssetCategoryEnum.Hardware_Configuration);
		}
		if(category.equals("equipment configuration")){
			categories.add(SecondaryAssetCategoryEnum.Software_Configuration);
			categories.add(SecondaryAssetCategoryEnum.Hardware_Configuration);
		}
		if(category.equals("telecom equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("network equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("auxiliary elements")){
			categories.add(SecondaryAssetCategoryEnum.Auxiliary_Equipment);
		}
		if(category.equals("Server")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("work stations")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("shared equipment")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("application server")){
			categories.add(SecondaryAssetCategoryEnum.Hardware);
		}
		if(category.equals("media storing program")){
			categories.add(SecondaryAssetCategoryEnum.Electronic_Media);
		}
		if(category.equals("account")){
			categories.add(SecondaryAssetCategoryEnum.Service_Access_Mean);
		}
		if(category.equals("compliance of processes")){
			categories.add(SecondaryAssetCategoryEnum.Policy);
		}
		
		if(categories.size() == 0){
			LOG.error("Unable to latch secondary category for this " + category);
		}
		
		return categories;
	}
	
	/*
	 Translate the primary Asset Type into our ENUM standard
	 */
	public static PrimaryAssetCategoryEnum checkPrimaryAssetCategory(String category){
		if(category.equals("D01")){
			return PrimaryAssetCategoryEnum.Data_DataFile_Database;
		}
		if(category.equals("D02")){
			return PrimaryAssetCategoryEnum.Data_Shared_Office_File;
		}
		if(category.equals("D03")){
			return PrimaryAssetCategoryEnum.Data_Personal_Office_File;
		}
		if(category.equals("D04")){
			return PrimaryAssetCategoryEnum.Data_Physical_File;
		}
		if(category.equals("D05")){
			return PrimaryAssetCategoryEnum.Data_Physical_File;
		}
		if(category.equals("D06")){
			return PrimaryAssetCategoryEnum.Data_Exchanged_Message;
		}
		if(category.equals("D06a")){
			return PrimaryAssetCategoryEnum.Data_Exchanged_Message;
		}
		if(category.equals("D06b")){
			return PrimaryAssetCategoryEnum.Data_Exchanged_Message;
		}
		if(category.equals("D06c")){
			return PrimaryAssetCategoryEnum.Data_Exchanged_Message;
		}
		if(category.equals("D07")){
			return PrimaryAssetCategoryEnum.Data_Digital_Mail;
		}
		if(category.equals("D08")){
			return PrimaryAssetCategoryEnum.Data_Physical_Mail;
		}
		if(category.equals("D09")){
			return PrimaryAssetCategoryEnum.Data_Physical_Archive;
		}
		if(category.equals("D10")){
			return PrimaryAssetCategoryEnum.Data_IT_Archive;
		}
		if(category.equals("D11")){
			return PrimaryAssetCategoryEnum.Data_Published_Data;
		}
		if(category.equals("G01")){
			return PrimaryAssetCategoryEnum.Service_User_Workspace;
		}
		if(category.equals("G02")){
			return PrimaryAssetCategoryEnum.Service_Telecommunication_Service;
		}
		if(category.equals("R01")){
			return PrimaryAssetCategoryEnum.Service_Extended_Network_Service;
		}
		if(category.equals("R02")){
			return PrimaryAssetCategoryEnum.Service_Local_Network_Service;
		}
		if(category.equals("S01")){
			return PrimaryAssetCategoryEnum.Service_Application_Service;
		}
		if(category.equals("S02")){
			return PrimaryAssetCategoryEnum.Service_Shared_Service;
		}
		if(category.equals("S03")){
			return PrimaryAssetCategoryEnum.Service_User_Hardware;
		}
		if(category.equals("S04")){
			return PrimaryAssetCategoryEnum.Service_Common_Service;
		}
		if(category.equals("S05")){
			return PrimaryAssetCategoryEnum.Service_Web_editing_Service;
		}
		if(category.equals("C01")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_Personal_Information_Protection;
		}
		if(category.equals("C02")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_Financial_Communication;
		}
		if(category.equals("C03")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control;
		}
		if(category.equals("C04")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_Intellectual_Property;
		}
		if(category.equals("C05")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_Protection_Of_Information_Systems;
		}
		if(category.equals("C06")){
			return PrimaryAssetCategoryEnum.Compliance_Policy_People_And_Environment_Safety;
		}
		
		LOG.error("Unable to latch primary category for this " + category);
		
		return null;
	}
	
	public static SecurityImpactScopeEnum  translateScope(String scope){
		if(scope.toUpperCase().equals("A"))
			return SecurityImpactScopeEnum.Availability;
		if(scope.toUpperCase().equals("I"))
			return SecurityImpactScopeEnum.Integrity;
		if(scope.toUpperCase().equals("C"))
			return SecurityImpactScopeEnum.Confidentiality;
		if(scope.toUpperCase().equals("E"))
			return SecurityImpactScopeEnum.Efficiency;
		
		return null;
	}
}
