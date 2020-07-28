/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelCloner.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.riskmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.crmf.model.riskassessment.RiskModel;

//This class is responsible for cloning RiskModels from Json strings and POJO
public class RiskModelCloner {
private String identifier;
	
	public RiskModel clone(RiskModel rm){
		
		RiskModelSerializerDeserializer rmsd = new RiskModelSerializerDeserializer();
		String rmJson = rmsd.getJSONStringFromRM(rm);
		
		RiskModel rmNew = rmsd.getRMFromJSONString(rmJson);

		UUID uuid = UUID.randomUUID();
		rmNew.setIdentifier(uuid.toString());
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		rmNew.setCreationTime(df.format(now));
		rmNew.setUpdateTime(df.format(now));
		
		identifier = rmNew.getIdentifier();
		return rmNew;
	}
	
public String clone(String rmJson){
		
	RiskModelSerializerDeserializer rmsd = new RiskModelSerializerDeserializer();
		
	RiskModel rm = rmsd.getRMFromJSONString(rmJson);
		
		UUID uuid = UUID.randomUUID();
		rm.setIdentifier(uuid.toString());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date now = new Date();
		rm.setCreationTime(df.format(now));
		rm.setUpdateTime(df.format(now));
		
		
		identifier = rm.getIdentifier();
		return rmsd.getJSONStringFromRM(rm);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
