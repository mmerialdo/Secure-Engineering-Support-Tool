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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.riskassessmentelements.FunctionalMalfunctionTypeEnum;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.MalfunctionValueScale;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.TechnicalMalfunctionTypeEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;

//This class manages the deserialization of Malfunction classes
class MalfunctionInstanceCreator implements JsonDeserializer<Malfunction> , JsonSerializer<Malfunction> {

	public static final String FUNCTIONAL_CONSEQUENCE = "functionalConsequence";
	public static final String FUNCTIONAL_DESCRIPTION = "functionalDescription";
	public static final String TECHNICAL_CONSEQUENCE = "technicalConsequence";
	public static final String TECHNICAL_DESCRIPTION = "technicalDescription";
	public static final String FUNCTIONAL_TYPE = "functionalType";
	public static final String WEIGHT = "weight";
	public static final String TECHNICAL_TYPES = "technicalTypes";
	public static final String SCALES = "scales";

	@Override
	public JsonElement serialize(Malfunction mal, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(FUNCTIONAL_CONSEQUENCE, mal.getFunctionalConsequence());
		jsonObject.addProperty(FUNCTIONAL_DESCRIPTION, mal.getFunctionalDescription());
		jsonObject.addProperty(TECHNICAL_CONSEQUENCE, mal.getTechnicalConsequence());
		jsonObject.addProperty(TECHNICAL_DESCRIPTION, mal.getTechnicalDescription());
		
		if(mal.getFunctionalType() != null){
			jsonObject.addProperty(FUNCTIONAL_TYPE, mal.getFunctionalType().toString());
		}
		else{
			jsonObject.addProperty(FUNCTIONAL_TYPE, "");
		}
		
		jsonObject.addProperty(WEIGHT, mal.getWeight());
	
		JsonArray technicalTypes = new JsonArray();
			
        mal.getTechnicalTypes().forEach(item -> {
			
        	technicalTypes.add(item.toString());
		});
		
        jsonObject.add(TECHNICAL_TYPES, technicalTypes);
        
        
        JsonArray scales = new JsonArray();

        mal.getScales().forEach(item -> {
			JsonElement scale = context.serialize(item, MalfunctionValueScale.class);
			
			if (scale != null) {
				scales.add(scale);
			}
		});
        
        jsonObject.add(SCALES, scales);
		
		return jsonObject;
	}

	@Override
	public Malfunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Malfunction mal = new Malfunction();
		mal.setNodeType(NodeTypeEnum.Malfunction);
		mal.setTechnicalTypes(new ArrayList<>());
		mal.setScales(new ArrayList<>());

		if (!jsonObject.get(FUNCTIONAL_CONSEQUENCE).isJsonNull()) {
			String functionalConsequence = jsonObject.get(FUNCTIONAL_CONSEQUENCE).getAsString();
			mal.setFunctionalConsequence(functionalConsequence);
		} else {
			mal.setFunctionalConsequence("");
		}

		if (!jsonObject.get(FUNCTIONAL_DESCRIPTION).isJsonNull()) {
			String functionalDescription = jsonObject.get(FUNCTIONAL_DESCRIPTION).getAsString();
			mal.setFunctionalDescription(functionalDescription);
		} else {
			mal.setFunctionalDescription("");
		}

		if (!jsonObject.get(TECHNICAL_CONSEQUENCE).isJsonNull()) {
			String technicalConsequence = jsonObject.get(TECHNICAL_CONSEQUENCE).getAsString();
			mal.setTechnicalConsequence(technicalConsequence);
		} else {
			mal.setTechnicalConsequence("");
		}

		if (!jsonObject.get(TECHNICAL_DESCRIPTION).isJsonNull()) {
			String technicalDescription = jsonObject.get(TECHNICAL_DESCRIPTION).getAsString();
			mal.setTechnicalDescription(technicalDescription);
		} else {
			mal.setTechnicalDescription("");
		}

		if (!jsonObject.get(FUNCTIONAL_TYPE).isJsonNull()) {
			String functionalType = jsonObject.get(FUNCTIONAL_TYPE).getAsString();

			mal.setFunctionalType(FunctionalMalfunctionTypeEnum.valueOf(functionalType));
			
		}

		if (!jsonObject.get(WEIGHT).isJsonNull()) {
			mal.setWeight(jsonObject.get(WEIGHT).getAsFloat());
		} else {
			mal.setWeight(0);
		}

		if (!jsonObject.get(TECHNICAL_TYPES).isJsonNull()) {
			JsonArray technicalTypes = jsonObject.get(TECHNICAL_TYPES).getAsJsonArray();

			technicalTypes.forEach(item -> {
				String technicalType = item.getAsString();

				mal.getTechnicalTypes().add(TechnicalMalfunctionTypeEnum.valueOf(technicalType));
				
			});
		}

		JsonArray scales = jsonObject.get(SCALES).getAsJsonArray();

		scales.forEach(item -> {
			MalfunctionValueScale malfunctionValueScale = context.deserialize(item, MalfunctionValueScale.class);
			mal.getScales().add(malfunctionValueScale);
		});

		return mal;

	}

}
