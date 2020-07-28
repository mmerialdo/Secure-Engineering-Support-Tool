/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelSerializerDeserializer.java"
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

import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//This class manages the custom serialization/deserialization of the RiskTreatmentModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class RiskTreatmentModelSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(RiskTreatmentModelSerializerDeserializer.class.getName());

	/**
	 * Creates JSON from POJO The created JSON is adapted for the RiskTreatment
	 * View 1 (grouped view)
	 * 
	 * @param RiskTreatmentModel
	 * @return Json
	 */
	public String getClientFullJSONStringFromRTM(RiskModel rm, AssetModel am, SafeguardModel sm,
			RiskTreatmentModel rtm) {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(RiskTreatmentModel.class,
				new RiskTreatmentModelClientFullInstanceCreator(rm, am, sm));
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(rtm);
	}

	/**
	 * Creates JSON from POJO The created JSON is adapted for the RiskTreatment
	 * View 2 (single asset view)
	 * 
	 * @param RiskTreatmentModel
	 * @return Json
	 */
	public String getClientDetailJSONStringFromRTM(RiskModel riskModel, AssetModel assetModel, SafeguardModel safeguardModel,
			RiskTreatmentModel rtm, String assetPrimaryCategory) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(RiskTreatmentModel.class, new RiskTreatmentModelClientDetailInstanceCreator(
				riskModel, assetModel, safeguardModel, assetPrimaryCategory));
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(rtm);
	}

	/**
	 * Creates JSON from POJO The created JSON is to be saved in the persistency
	 * 
	 * @param RiskTreatmentModel
	 * @return Json
	 */
	public String getJSONStringFromRTM(RiskTreatmentModel rtm) {

		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(rtm);
	}

	public RiskTreatmentModel getRTMFromPersistencyJSONString(String rtmJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();

		RiskTreatmentModel rtm;

		try {
			LOG.info("RiskTreatmentModel Serialization/Deserialization rtmJsonString:: "
					+ rtmJsonString.substring(0, (rtmJsonString.length() > 500 ? 500 : rtmJsonString.length())));
			rtm = gson.fromJson(rtmJsonString, RiskTreatmentModel.class);
		} catch (Exception e) {
			LOG.error("RiskTreatmentModel Serialization/Deserialization error:: " + e.getMessage(), e);
			return null;
		}

		if (rtm == null) {
			LOG.error("Conversion of json into RiskTreatmentModel returned null");
			return null;
		}

		return rtm;
	}
	
	public RiskTreatmentModel getRTMFromClientJSONString(String rtmJsonString) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(RiskTreatmentModel.class,
				new RiskTreatmentModelClientFullInstanceCreator());
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();

		RiskTreatmentModel rtm;

		try {
			LOG.info("getRTMFromClientJSONString Serialization/Deserialization rtmJsonString:: "
					+ rtmJsonString.substring(0, (rtmJsonString.length() > 500 ? 500 : rtmJsonString.length())));
			rtm = gson.fromJson(rtmJsonString, RiskTreatmentModel.class);
		} catch (Exception e) {
			LOG.error("getRTMFromClientJSONString Serialization/Deserialization error:: " + e.getMessage(), e);
			return null;
		}

		if (rtm == null) {
			LOG.error("Conversion of json into RiskTreatmentModel returned null");
			return null;
		}

		return rtm;
	}

	public RiskTreatmentModel getRTMFromClientDetailJSONString(String riskTreatmentModel) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(RiskTreatmentModel[].class,
				new RiskTreatmentModelClientDetailInstanceCreator());
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();

		RiskTreatmentModel[] rtms;

		try {
			LOG.info("getRTMFromClientDetailJSONString Serialization/Deserialization rtmJsonString:: "
					+ riskTreatmentModel.substring(0, (riskTreatmentModel.length() > 500 ? 500 : riskTreatmentModel.length())));
			rtms = gson.fromJson(riskTreatmentModel, RiskTreatmentModel[].class);
		} catch (Exception e) {
			LOG.error("getRTMFromClientDetailJSONString Serialization/Deserialization error:: " + e.getMessage(), e);
			return null;
		}

		if (rtms == null || rtms[0] == null) {
			LOG.error("Conversion of json into RiskTreatmentModel returned null");
			return null;
		}

		return rtms[0];
	}

	public String getAssetCategoryFromClientJSONString(String riskTreatmentModel) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Asset[].class,
				new RiskTreatmentModelClientDetailAssetCategoryInstanceCreator());
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();

		Asset[] assets;

		try {
			LOG.info("getAssetCategoryFromClientJSONString Serialization/Deserialization rtmJsonString:: "
					+ riskTreatmentModel.substring(0, (riskTreatmentModel.length() > 500 ? 500 : riskTreatmentModel.length())));
			
			assets = gson.fromJson(riskTreatmentModel, Asset[].class);
		} catch (Exception e) {
			LOG.error("getAssetCategoryFromClientJSONString Serialization/Deserialization error:: " + e.getMessage(), e);
			return null;
		}

		if (assets == null || assets[0] == null || assets[0].getPrimaryCategories() == null) {
			LOG.error("Conversion of json into RiskTreatmentModel returned null");
			return null;
		}
		if(assets[0].getPrimaryCategories() != null){
			return assets[0].getPrimaryCategories().get(0).toString();
		}

		return null;
	}

}
