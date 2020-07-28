/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="BusinessActivityInstanceCreator.java"
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

import java.lang.reflect.Type;

import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the deserialization of BusinessActivity classes
class BusinessActivityInstanceCreator implements JsonDeserializer<BusinessActivity> , JsonSerializer<BusinessActivity> {
	
	@Override
	public JsonElement serialize(BusinessActivity ba, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		
		return jsonObject;
	}

	@Override
	public BusinessActivity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		BusinessActivity ba = new BusinessActivity();
		ba.setNodeType(NodeTypeEnum.BusinessActivity);

		return ba;
	}

}