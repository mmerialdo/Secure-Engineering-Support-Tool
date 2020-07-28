/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="MalfunctionInstanceCreator.java"
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

import org.crmf.model.riskassessmentelements.FunctionalMalfunctionTypeEnum;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.MalfunctionValueScale;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.TechnicalMalfunctionTypeEnum;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the deserialization of Malfunction classes
class MalfunctionInstanceCreator implements JsonDeserializer<Malfunction> , JsonSerializer<Malfunction> {
	
	@Override
	public JsonElement serialize(Malfunction mal, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("functionalConsequence", mal.getFunctionalConsequence());
		jsonObject.addProperty("functionalDescription", mal.getFunctionalDescription());
		jsonObject.addProperty("technicalConsequence", mal.getTechnicalConsequence());
		jsonObject.addProperty("technicalDescription", mal.getTechnicalDescription());
		
		if(mal.getFunctionalType() != null){
			jsonObject.addProperty("functionalType", mal.getFunctionalType().toString());
		}
		else{
			jsonObject.addProperty("functionalType", "");
		}
		
		jsonObject.addProperty("weight", mal.getWeight());
	
		JsonArray technicalTypes = new JsonArray();
			
        mal.getTechnicalTypes().forEach(item -> {
			
        	technicalTypes.add(item.toString());
		});
		
        jsonObject.add("technicalTypes", technicalTypes);
        
        
        JsonArray scales = new JsonArray();

        mal.getScales().forEach(item -> {
			JsonElement scale = context.serialize(item, MalfunctionValueScale.class);
			
			if (scale != null) {
				scales.add(scale);
			}
		});
        
        jsonObject.add("scales", scales);
		
		return jsonObject;
	}

	@Override
	public Malfunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Malfunction mal = new Malfunction();
		mal.setNodeType(NodeTypeEnum.Malfunction);
		mal.setTechnicalTypes(new ArrayList<TechnicalMalfunctionTypeEnum>());
		mal.setScales(new ArrayList<MalfunctionValueScale>());

		if (!jsonObject.get("functionalConsequence").isJsonNull()) {
			String functionalConsequence = jsonObject.get("functionalConsequence").getAsString();
			mal.setFunctionalConsequence(functionalConsequence);
		} else {
			mal.setFunctionalConsequence("");
		}

		if (!jsonObject.get("functionalDescription").isJsonNull()) {
			String functionalDescription = jsonObject.get("functionalDescription").getAsString();
			mal.setFunctionalDescription(functionalDescription);
		} else {
			mal.setFunctionalDescription("");
		}

		if (!jsonObject.get("technicalConsequence").isJsonNull()) {
			String technicalConsequence = jsonObject.get("technicalConsequence").getAsString();
			mal.setTechnicalConsequence(technicalConsequence);
		} else {
			mal.setTechnicalConsequence("");
		}

		if (!jsonObject.get("technicalDescription").isJsonNull()) {
			String technicalDescription = jsonObject.get("technicalDescription").getAsString();
			mal.setTechnicalDescription(technicalDescription);
		} else {
			mal.setTechnicalDescription("");
		}

		if (!jsonObject.get("functionalType").isJsonNull()) {
			String functionalType = jsonObject.get("functionalType").getAsString();

			mal.setFunctionalType(FunctionalMalfunctionTypeEnum.valueOf(functionalType));
			
		}

		if (!jsonObject.get("weight").isJsonNull()) {
			mal.setWeight(jsonObject.get("weight").getAsFloat());
		} else {
			mal.setWeight(0);
		}

		if (!jsonObject.get("technicalTypes").isJsonNull()) {
			JsonArray technicalTypes = jsonObject.get("technicalTypes").getAsJsonArray();

			technicalTypes.forEach(item -> {
				String technicalType = item.getAsString();

				mal.getTechnicalTypes().add(TechnicalMalfunctionTypeEnum.valueOf(technicalType));
				
			});
		}

		JsonArray scales = jsonObject.get("scales").getAsJsonArray();

		scales.forEach(item -> {
			JsonElement obj = (JsonElement) item;
			MalfunctionValueScale malfunctionValueScale = context.deserialize(obj, MalfunctionValueScale.class);

			mal.getScales().add(malfunctionValueScale);
		});

		return mal;

	}

}
