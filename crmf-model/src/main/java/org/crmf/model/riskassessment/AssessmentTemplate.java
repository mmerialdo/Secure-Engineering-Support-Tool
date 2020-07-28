/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplate.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessment;

import org.crmf.model.general.SESTObject;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class AssessmentTemplate extends SESTObject {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentTemplate.class.getName());
	private String creationTime;
	private String description;
	private String name;
	private PhaseEnum phase;
	private RiskMethodologyEnum riskMethodology;
	private AssetModel assetModel;
	private ThreatModel threatModel;
	private VulnerabilityModel vulnerabilityModel;
	private SafeguardModel safeguardModel;
	private RiskModel riskModel;


	public AssessmentTemplate() {

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getCreationTime() {
		return creationTime;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public RiskMethodologyEnum getRiskMethodology() {
		return riskMethodology;
	}

	public void setCreationTime(String newVal) {
		creationTime = newVal;
	}

	public void setDescription(String newVal) {
		description = newVal;
	}

	public void setName(String newVal) {
		name = newVal;
	}

	public void setRiskMethodology(RiskMethodologyEnum newVal) {
		riskMethodology = newVal;
	}

	public PhaseEnum getPhase() {
		return phase;
	}

	public void setPhase(PhaseEnum phase) {
		this.phase = phase;
	}

	public AssetModel getAssetModel() {
		return assetModel;
	}

	public void setAssetModel(AssetModel assetModel) {
		this.assetModel = assetModel;
	}

	public ThreatModel getThreatModel() {
		return threatModel;
	}

	public void setThreatModel(ThreatModel threatModel) {
		this.threatModel = threatModel;
	}

	public VulnerabilityModel getVulnerabilityModel() {
		return vulnerabilityModel;
	}

	public void setVulnerabilityModel(VulnerabilityModel vulnerabilityModel) {
		this.vulnerabilityModel = vulnerabilityModel;
	}

	public SafeguardModel getSafeguardModel() {
		return safeguardModel;
	}

	public void setSafeguardModel(SafeguardModel safeguardModel) {
		this.safeguardModel = safeguardModel;
	}

	public RiskModel getRiskModel() {
		return riskModel;
	}

	public void setRiskModel(RiskModel riskModel) {
		this.riskModel = riskModel;
	}

	/*
	 Converts this template into a json properly formatted and ready to be
	 sent through camel.
	 */
	public String convertToJson() {
		
		AssetModelSerializerDeserializer assetSerializer = new AssetModelSerializerDeserializer();
		GsonBuilder gsonBuilder = assetSerializer.createGsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		JsonElement templateJsonElement = gson.toJsonTree(this);

		return gson.toJson(templateJsonElement);
	}
}