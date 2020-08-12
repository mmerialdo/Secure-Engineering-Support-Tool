/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelCloner.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.safeguardmodel;

import org.crmf.model.riskassessment.SafeguardModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

//This class is responsible for cloning SafeguardModels from Json strings and POJO
public class SafeguardModelCloner {
	public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	private String identifier;
	
	public SafeguardModel clone(SafeguardModel sm){
		
		SafeguardModelSerializerDeserializer smsd = new SafeguardModelSerializerDeserializer();
		String smJson = smsd.getJSONStringFromSM(sm);
		
		SafeguardModel smNew = smsd.getSMFromJSONString(smJson);

		UUID uuid = UUID.randomUUID();
		smNew.setIdentifier(uuid.toString());
		
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		Date now = new Date();
		smNew.setCreationTime(df.format(now));
		smNew.setUpdateTime(df.format(now));
		
		identifier = smNew.getIdentifier();
		return smNew;
	}
	
public String clone(String smJson){
		
	SafeguardModelSerializerDeserializer smsd = new SafeguardModelSerializerDeserializer();
		
	SafeguardModel sm = smsd.getSMFromJSONString(smJson);
		
		UUID uuid = UUID.randomUUID();
		sm.setIdentifier(uuid.toString());
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		Date now = new Date();
		sm.setCreationTime(df.format(now));
		sm.setUpdateTime(df.format(now));
		
		identifier = sm.getIdentifier();
		return smsd.getJSONStringFromSM(sm);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
