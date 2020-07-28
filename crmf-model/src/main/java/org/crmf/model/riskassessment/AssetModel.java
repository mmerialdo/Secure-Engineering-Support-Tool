/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModel.java"
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

import java.util.ArrayList;

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.Organization;

public class AssetModel extends SESTObject {

	private String creationTime;
	private String graphJson;
	private String updateTime;
	private ArrayList<Edge> edges;
	private ArrayList<Organization> organizations;
	private ArrayList<BusinessProcess> businessProcesses;
	private ArrayList<BusinessActivity> businessActivities;
	private ArrayList<Asset> assets;
	private ArrayList<Malfunction> malfunctions;

	public AssetModel(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	public String getUpdateTime(){
		return updateTime;
	}

	public void setUpdateTime(String newVal){
		updateTime = newVal;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getGraphJson() {
		return graphJson;
	}

	public void setGraphJson(String graphJson) {
		this.graphJson = graphJson;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	public ArrayList<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(ArrayList<Organization> organizations) {
		this.organizations = organizations;
	}

	public ArrayList<BusinessProcess> getBusinessProcesses() {
		return businessProcesses;
	}

	public void setBusinessProcesses(ArrayList<BusinessProcess> businessProcesses) {
		this.businessProcesses = businessProcesses;
	}

	public ArrayList<BusinessActivity> getBusinessActivities() {
		return businessActivities;
	}

	public void setBusinessActivities(ArrayList<BusinessActivity> businessActivities) {
		this.businessActivities = businessActivities;
	}

	public ArrayList<Asset> getAssets() {
		return assets;
	}

	public void setAssets(ArrayList<Asset> assets) {
		this.assets = assets;
	}

	public ArrayList<Malfunction> getMalfunctions() {
		return malfunctions;
	}

	public void setMalfunctions(ArrayList<Malfunction> malfunctions) {
		this.malfunctions = malfunctions;
	}

}