/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedure.java"
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.crmf.model.general.SESTObject;
import org.crmf.model.user.User;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AssessmentProcedure extends SESTObject {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentProcedure.class.getName());
	private String creationTime;
	private String name;
	private PhaseEnum phase;
	private AssessmentStatusEnum status;
	private String updateTime;
	private List<AssessmentTemplate> generatedTemplates;
	private AssetModel assetModel;
	private ThreatModel threatModel;
	private VulnerabilityModel vulnerabilityModel;
	private SafeguardModel safeguardModel;
	private RiskTreatmentModel riskTreatmentModel;
	private User lastUserUpdate;
	private RiskModel riskModel;

	public String getCreationTime() {
		return creationTime;
	}

	public AssessmentStatusEnum getStatus() {
		return status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setCreationTime(String newVal) {
		creationTime = newVal;
	}

	public void setStatus(AssessmentStatusEnum newVal) {
		status = newVal;
	}

	public void setUpdateTime(String newVal) {
		updateTime = newVal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PhaseEnum getPhase() {
		return phase;
	}

	public void setPhase(PhaseEnum phase) {
		this.phase = phase;
	}

	public List<AssessmentTemplate> getGeneratedTemplates() {
		return generatedTemplates;
	}

	public void setGeneratedTemplates(List<AssessmentTemplate> generatedTemplates) {
		this.generatedTemplates = generatedTemplates;
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

	public User getLastUserUpdate() {
		return lastUserUpdate;
	}

	public void setLastUserUpdate(User lastUserUpdate) {
		this.lastUserUpdate = lastUserUpdate;
	}

	public RiskModel getRiskModel() {
		return riskModel;
	}

	public void setRiskModel(RiskModel riskModel) {
		this.riskModel = riskModel;
	}

	/*
	 Converts this procedure into a json properly formatted and ready to be
	 sent through Apache Camel.
	 */
	public String convertToJson() {
		
		AssetModelSerializerDeserializer assetSerializer = new AssetModelSerializerDeserializer();
		GsonBuilder gsonBuilder = assetSerializer.createGsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

		JsonElement procedureJsonElement = gson.toJsonTree(this);

		return gson.toJson(procedureJsonElement);
	}

	public RiskTreatmentModel getRiskTreatmentModel() {
		return riskTreatmentModel;
	}

	public void setRiskTreatmentModel(RiskTreatmentModel riskTreatmentModel) {
		this.riskTreatmentModel = riskTreatmentModel;
	}

}