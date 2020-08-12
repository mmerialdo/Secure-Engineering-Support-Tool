/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="BusinessProcessInstanceCreator.java"
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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.ProcessTypeEnum;

import java.lang.reflect.Type;

//This class manages the deserialization of BusinessProcess classes
class BusinessProcessInstanceCreator implements JsonDeserializer<BusinessProcess> , JsonSerializer<BusinessProcess> {

	public static final String TYPE = "type";

	@Override
	public JsonElement serialize(BusinessProcess bp, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		if(bp.getType() != null){
			jsonObject.addProperty("type", bp.getType().toString());
		}
		else{
			jsonObject.addProperty("type", "");
		}
		
		
		return jsonObject;
	}

	@Override
	public BusinessProcess deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		BusinessProcess bp = new BusinessProcess();
		bp.setNodeType(NodeTypeEnum.BusinessProcess);

		if (!jsonObject.get("type").isJsonNull()) {
			String type = jsonObject.get(TYPE).getAsString();

			if (type.equals(ProcessTypeEnum.Business.toString())) {
				bp.setType(ProcessTypeEnum.Business);
			} else if (type.equals(ProcessTypeEnum.Transverse.toString())) {
				bp.setType(ProcessTypeEnum.Transverse);
			}
		}

		return bp;

	}

}