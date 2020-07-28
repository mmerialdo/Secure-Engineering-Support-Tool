/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelSerializerDeserializer.java"
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

import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.MalfunctionValueScale;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.Organization;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.utility.commonserialization.SecurityImpactInstanceCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

//This class manages the custom serialization/deserialization of the AssetModel
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
//JSON structure of the AssetModel is quite different from the Java Data Model. It is then mandatory to do a set of transformations in order to manage the deserialization
public class AssetModelSerializerDeserializer {
	private static final Logger LOG = LoggerFactory.getLogger(AssetModelSerializerDeserializer.class.getName());

	//Creates JSON from POJO
	public String getJSONStringFromAM(AssetModel am){
		
		LOG.info("----------- serializer getJSONStringFromAM!!!!!");

		GsonBuilder gsonBuilder = createGsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJson(am);
	}
	
    public JsonElement getJSONElementFromAM(AssetModel am){

		LOG.info("----------- serializer getJSONStringFromAM!!!!!");
		GsonBuilder gsonBuilder = createGsonBuilder();
		gsonBuilder.serializeNulls();
		
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		return gson.toJsonTree(am);
	}

	//Creates POJO from JSON
	public AssetModel getAMFromJSONString(String json){	

		LOG.info("----------- serializer getAMFromJSONString!!!!!");
		GsonBuilder gsonBuilder = createGsonBuilder();
		
		gsonBuilder.serializeNulls();
		Gson gson = gsonBuilder.create();
		
		AssetModel am;
		
		try{
			LOG.info("Asset Serialization/Deserialization json:: " + json.substring(0, (json.length() > 500 ? 500 : json.length())));
			am = gson.fromJson(json, AssetModel.class);
		}
		catch(Exception e){
			LOG.error("Deserialization error: " + e.getMessage());
			return null;
		}
		
		if(am == null){
			LOG.info("Conversion of json into Asset Model returned null");
			return null;
		}

		return am;
	}

	public GsonBuilder createGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		//Here we add all the custom adapters for the deserialization
		gsonBuilder.registerTypeAdapter(Node.class, new NodeInstanceCreator());
		gsonBuilder.registerTypeAdapter(AssetModel.class, new AssetModelInstanceCreator());
		gsonBuilder.registerTypeAdapter(Edge.class, new EdgeInstanceCreator());
		gsonBuilder.registerTypeAdapter(Organization.class, new OrganizationInstanceCreator());
		gsonBuilder.registerTypeAdapter(BusinessProcess.class, new BusinessProcessInstanceCreator());
		gsonBuilder.registerTypeAdapter(BusinessActivity.class, new BusinessActivityInstanceCreator());
		gsonBuilder.registerTypeAdapter(Malfunction.class, new MalfunctionInstanceCreator());
		gsonBuilder.registerTypeAdapter(Asset.class, new AssetInstanceCreator());
		gsonBuilder.registerTypeAdapter(SecurityImpact.class, new SecurityImpactInstanceCreator());
		gsonBuilder.registerTypeAdapter(MalfunctionValueScale.class, new MalfunctionValueScaleInstanceCreator());
		return gsonBuilder;
	}
}
