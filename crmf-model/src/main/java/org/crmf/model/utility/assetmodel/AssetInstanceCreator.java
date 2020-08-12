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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;

import java.lang.reflect.Type;
import java.util.ArrayList;

//This class manages the serialization/deserialization of Asset classes
class AssetInstanceCreator implements JsonDeserializer<Asset> , JsonSerializer<Asset> {

	public static final String CATEGORY = "category";
	public static final String COST = "cost";
	public static final String BUSINESS_IMPACT_WEIGHTS = "businessImpactWeights";
	public static final String PRIMARY_CATEGORIES = "primaryCategories";
	public static final String MALFUNCTIONS_IDS = "malfunctionsIds";
	public static final String SECURITY_IMPACTS = "securityImpacts";

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
		
		jsonObject.add(SECURITY_IMPACTS, securityImpacts);
		
		JsonArray businessImpacts = new JsonArray();
		jsonObject.add(BUSINESS_IMPACT_WEIGHTS, businessImpacts);
		
		if(asset.getCategory() != null){
			jsonObject.addProperty(CATEGORY, asset.getCategory().toString());
		}
		else{
			jsonObject.addProperty(CATEGORY, SecondaryAssetCategoryEnum.Data_File.toString());
		}
		jsonObject.addProperty(COST, asset.getCost());
		
		
		JsonArray primaryCategories = new JsonArray();
		
        asset.getPrimaryCategories().forEach(item -> {
			
			primaryCategories.add(item.toString());
			
		});
		
		jsonObject.add(PRIMARY_CATEGORIES, primaryCategories);
		
		
		JsonArray malfunctionsIds = new JsonArray();
		
        asset.getMalfunctionsIds().forEach(item -> {
			
        	malfunctionsIds.add(item);
			
		});
		
		jsonObject.add(MALFUNCTIONS_IDS, malfunctionsIds);
		
		
		
		return jsonObject;
	}

	@Override
	public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Asset asset = new Asset();
		asset.setNodeType(NodeTypeEnum.Asset);
		asset.setBusinessImpacts(new ArrayList<>());
		asset.setMalfunctionsIds(new ArrayList<>());
		asset.setSecurityImpacts(new ArrayList<>());
		asset.setPrimaryCategories(new ArrayList<>());

		JsonArray securityImpacts = jsonObject.get(SECURITY_IMPACTS).getAsJsonArray();

		securityImpacts.forEach(item -> {
			SecurityImpact securityImpact = context.deserialize(item, SecurityImpact.class);

			asset.getSecurityImpacts().add(securityImpact);
		});

		if (!jsonObject.get(CATEGORY).isJsonNull()) {
			String assetCategory = jsonObject.get(CATEGORY).getAsString();

			asset.setCategory(SecondaryAssetCategoryEnum.valueOf(assetCategory));
			
		}
		else{
			asset.setCategory(SecondaryAssetCategoryEnum.Data_File);
		}

		if (!jsonObject.get(COST).isJsonNull()) {
			asset.setCost(jsonObject.get(COST).getAsInt());
		} else {
			asset.setCost(0);
		}

		if (!jsonObject.get(PRIMARY_CATEGORIES).isJsonNull()) {
			JsonArray primaryCategories = jsonObject.get(PRIMARY_CATEGORIES).getAsJsonArray();

			primaryCategories.forEach(item -> {
				String primaryCategory = item.getAsString();
				
				
				
				asset.getPrimaryCategories().add(PrimaryAssetCategoryEnum.valueOf(primaryCategory));

			});
		}

		JsonArray malfunctionsIds = jsonObject.get(MALFUNCTIONS_IDS).getAsJsonArray();

		malfunctionsIds.forEach(item -> {
			String malfunctionId = item.getAsString();

			asset.getMalfunctionsIds().add(malfunctionId);
		});

		return asset;
	}
}
