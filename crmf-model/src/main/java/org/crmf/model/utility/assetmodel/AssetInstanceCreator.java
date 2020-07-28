/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetInstanceCreator.java"
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

import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessImpact;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the serialization/deserialization of Asset classes
class AssetInstanceCreator implements JsonDeserializer<Asset> , JsonSerializer<Asset> {
	
	@Override
	public JsonElement serialize(Asset asset, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
        JsonArray securityImpacts = new JsonArray();
		
        asset.getSecurityImpacts().forEach(item -> {
			JsonElement securityImpact = context.serialize(item, SecurityImpact.class);
			
			if (securityImpact != null) {
				securityImpacts.add(securityImpact);
			}
		});
		
		jsonObject.add("securityImpacts", securityImpacts);
		
		JsonArray businessImpacts = new JsonArray();
		jsonObject.add("businessImpactWeights", businessImpacts);
		
		if(asset.getCategory() != null){
			jsonObject.addProperty("category", asset.getCategory().toString());
		}
		else{
			jsonObject.addProperty("category", SecondaryAssetCategoryEnum.Data_File.toString());
		}
		jsonObject.addProperty("cost", asset.getCost());
		
		
		JsonArray primaryCategories = new JsonArray();
		
        asset.getPrimaryCategories().forEach(item -> {
			
			primaryCategories.add(item.toString());
			
		});
		
		jsonObject.add("primaryCategories", primaryCategories);
		
		
		JsonArray malfunctionsIds = new JsonArray();
		
        asset.getMalfunctionsIds().forEach(item -> {
			
        	malfunctionsIds.add(item);
			
		});
		
		jsonObject.add("malfunctionsIds", malfunctionsIds);
		
		
		
		return jsonObject;
	}

	@Override
	public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Asset asset = new Asset();
		asset.setNodeType(NodeTypeEnum.Asset);
		asset.setBusinessImpacts(new ArrayList<BusinessImpact>());
		asset.setMalfunctionsIds(new ArrayList<String>());
		asset.setSecurityImpacts(new ArrayList<SecurityImpact>());
		asset.setPrimaryCategories(new ArrayList<PrimaryAssetCategoryEnum>());

		JsonArray securityImpacts = jsonObject.get("securityImpacts").getAsJsonArray();

		securityImpacts.forEach(item -> {
			JsonElement obj = (JsonElement) item;
			SecurityImpact securityImpact = context.deserialize(obj, SecurityImpact.class);

			asset.getSecurityImpacts().add(securityImpact);
		});

		if (!jsonObject.get("category").isJsonNull()) {
			String assetCategory = jsonObject.get("category").getAsString();

			asset.setCategory(SecondaryAssetCategoryEnum.valueOf(assetCategory));
			
		}
		else{
			asset.setCategory(SecondaryAssetCategoryEnum.Data_File);
		}

		if (!jsonObject.get("cost").isJsonNull()) {
			asset.setCost(jsonObject.get("cost").getAsInt());
		} else {
			asset.setCost(0);
		}

		if (!jsonObject.get("primaryCategories").isJsonNull()) {
			JsonArray primaryCategories = jsonObject.get("primaryCategories").getAsJsonArray();

			primaryCategories.forEach(item -> {
				String primaryCategory = item.getAsString();
				
				
				
				asset.getPrimaryCategories().add(PrimaryAssetCategoryEnum.valueOf(primaryCategory));

			});
		}

		JsonArray malfunctionsIds = jsonObject.get("malfunctionsIds").getAsJsonArray();

		malfunctionsIds.forEach(item -> {
			String malfunctionId = item.getAsString();

			asset.getMalfunctionsIds().add(malfunctionId);
		});

		return asset;

	}

}
