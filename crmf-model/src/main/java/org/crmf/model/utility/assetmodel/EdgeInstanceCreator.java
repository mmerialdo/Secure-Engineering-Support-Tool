/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="EdgeInstanceCreator.java"
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
import java.util.ArrayList;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessmentelements.BusinessImpact;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.SecurityImpact;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the deserialization of Edge classes
class EdgeInstanceCreator implements JsonDeserializer<Edge>, JsonSerializer<Edge> {
	
	@Override
	public JsonElement serialize(Edge edge, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("operationalWeight", edge.getOperationalWeight());
		jsonObject.addProperty("identifier", edge.getIdentifier());

		if(edge.getObjType() != null){
			jsonObject.addProperty("objType", edge.getObjType().toString());
		}
		else{
			jsonObject.addProperty("objType", SESTObjectTypeEnum.AssetModel.toString());
		}
		
		JsonArray businessImpacts = new JsonArray();
		jsonObject.add("businessImpactWeights", businessImpacts);
		
		jsonObject.addProperty("source", edge.getSource().getIdentifier());
		jsonObject.addProperty("target", edge.getTarget().getIdentifier());
		
		JsonArray securityImpacts = new JsonArray();
		
		edge.getSecurityImpacts().forEach(item -> {
			JsonElement securityImpact = context.serialize(item, SecurityImpact.class);
			
			if (securityImpact != null) {
				securityImpacts.add(securityImpact);
			}
		});
		
		jsonObject.add("securityImpacts", securityImpacts);
		
		
		return jsonObject;
	}

	@Override
	public Edge deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		String source = jsonObject.get("source").getAsString();
		String target = jsonObject.get("target").getAsString();

		Node nodeSource = new Node();
		nodeSource.setIdentifier(source);

		Node nodeTarget = new Node();
		nodeTarget.setIdentifier(target);

		Edge edge = new Edge();
		edge.setSource(nodeSource);
		edge.setTarget(nodeTarget);

		if (!jsonObject.get("operationalWeight").isJsonNull()) {
			edge.setOperationalWeight(jsonObject.get("operationalWeight").getAsFloat());
		} else {
			edge.setOperationalWeight(0);
		}

		if (!jsonObject.get("identifier").isJsonNull()) {
			edge.setIdentifier(jsonObject.get("identifier").getAsString());
		} else {
			return null;
		}

		if (!jsonObject.get("objType").isJsonNull()) {
			String objType = jsonObject.get("objType").getAsString();
			edge.setObjType(AssetModelSerializatorDeserializatorCommon.getSESTObjecType(objType));
		} else {
			edge.setObjType(SESTObjectTypeEnum.AssetModel);
		}

		edge.setBusinessImpactWeights(new ArrayList<BusinessImpact>());
		edge.setSecurityImpacts(new ArrayList<SecurityImpact>());

		if (!jsonObject.get("securityImpacts").isJsonNull()) {
			JsonArray securityImpacts = jsonObject.get("securityImpacts").getAsJsonArray();

			securityImpacts.forEach(item -> {
				JsonElement obj = (JsonElement) item;
				SecurityImpact securityImpact = context.deserialize(obj, SecurityImpact.class);

				edge.getSecurityImpacts().add(securityImpact);
			});
		}

		return edge;
	}

}
