/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelCloner.java"
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

import org.crmf.model.riskassessment.AssetModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

//This class is responsible for cloning AssetModels from Json strings and POJO
public class AssetModelCloner {
	public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	private String identifier;
	
	public AssetModel clone(AssetModel am){
		
		AssetModelSerializerDeserializer amsd = new AssetModelSerializerDeserializer();
		String amJson = amsd.getJSONStringFromAM(am);
		
		AssetModel amNew = amsd.getAMFromJSONString(amJson);
		
		UUID uuid = UUID.randomUUID();
		amNew.setIdentifier(uuid.toString());
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		Date now = new Date();
		amNew.setCreationTime(df.format(now));
		amNew.setUpdateTime(df.format(now));
		
		
		identifier = amNew.getIdentifier();
		return amNew;
	}
	
	public String clone(String amJson){
		
		AssetModelSerializerDeserializer amsd = new AssetModelSerializerDeserializer();
		
		AssetModel am = amsd.getAMFromJSONString(amJson);
		
		UUID uuid = UUID.randomUUID();
		am.setIdentifier(uuid.toString());
		
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		Date now = new Date();
		am.setCreationTime(df.format(now));
		am.setUpdateTime(df.format(now));
		
		
		identifier = am.getIdentifier();
		return amsd.getJSONStringFromAM(am);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
