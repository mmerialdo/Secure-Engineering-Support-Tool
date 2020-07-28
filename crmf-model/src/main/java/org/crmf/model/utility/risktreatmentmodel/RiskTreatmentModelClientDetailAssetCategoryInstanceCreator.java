/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelClientDetailAssetCategoryInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.risktreatmentmodel;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

// The json structure sent by the client is quite different from the
// Java RiskTreatmentModel structure
// we then need to deserialize accordingly in order to get the
// AssetCategory of one asset (which must be the same for all Assets in
// the Json)
public class RiskTreatmentModelClientDetailAssetCategoryInstanceCreator implements JsonDeserializer<Asset[]> {

	private static final Logger LOG = LoggerFactory
			.getLogger(RiskTreatmentModelClientDetailAssetCategoryInstanceCreator.class.getName());

	@Override
	public Asset[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		Asset[] assets = new Asset[1];
		Asset asset = new Asset();
		assets[0] = asset;
		asset.setPrimaryCategories(new ArrayList<>());

		JsonArray fullRiskTreatmentDetailJsonArray = json.getAsJsonArray();

		for (JsonElement item : fullRiskTreatmentDetailJsonArray) {
			// This is a single Asset
			JsonObject assetJsonObject = item.getAsJsonObject();

			if (assetJsonObject.has("data")) {

				JsonObject assetDataJsonObject = assetJsonObject.getAsJsonObject("data");

				if (assetDataJsonObject.has("primaryAssetCategory")) {
					asset.getPrimaryCategories().add(PrimaryAssetCategoryEnum.valueOf(assetDataJsonObject.get("primaryAssetCategory").getAsString()));
					return assets;
				}
				else{
					LOG.error("deserialize RiskTreatmentModel json from client has no primaryAssetCategory field: " + assetDataJsonObject.getAsString());
				}

			}

		}

		return assets;
	}

}
