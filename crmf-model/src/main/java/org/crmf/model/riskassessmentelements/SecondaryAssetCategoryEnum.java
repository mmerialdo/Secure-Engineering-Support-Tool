/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecondaryAssetCategoryEnum.java"
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

import java.util.Arrays;
import java.util.List;

public enum SecondaryAssetCategoryEnum {
	Personnel,
	Hardware,
	Software_Off_the_Shelf,
	Premise,
	Data_File,
	Firmware,
	Data_Message,
	Software_Configuration,
	Policy,
	Electronic_Media,
	Non_Electronic_Media,
	Communication_Network,
	Software_Custom,
	Hardware_Configuration,
	Auxiliary_Equipment,
	Service_Access_Mean,
	Data_Access_Mean;

	public static final List<SecondaryAssetCategoryEnum> SECONDARY_ASSET_CATEGORY_LIST = Arrays.asList(
		Personnel,
		Hardware,
		Software_Off_the_Shelf,
		Premise,
		Data_File,
		Firmware,
		Data_Message,
		Software_Configuration,
		Policy,
		Electronic_Media,
		Non_Electronic_Media,
		Communication_Network,
		Software_Custom,
		Hardware_Configuration,
		Auxiliary_Equipment,
		Service_Access_Mean,
		Data_Access_Mean
	);
}