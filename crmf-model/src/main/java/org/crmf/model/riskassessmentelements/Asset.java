/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Asset.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessmentelements;

import java.util.ArrayList;

public class Asset extends Node {

	private SecondaryAssetCategoryEnum category;
	private int cost;
	private ArrayList<PrimaryAssetCategoryEnum> primaryCategories;
	private ArrayList<SecurityImpact> securityImpacts;
	private ArrayList<BusinessImpact> businessImpacts;
	private ArrayList<String> malfunctionsIds;

	public Asset(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public SecondaryAssetCategoryEnum getCategory() {
		return category;
	}

	public void setCategory(SecondaryAssetCategoryEnum category) {
		this.category = category;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public ArrayList<PrimaryAssetCategoryEnum> getPrimaryCategories() {
		return primaryCategories;
	}

	public void setPrimaryCategories(ArrayList<PrimaryAssetCategoryEnum> primaryCategories) {
		this.primaryCategories = primaryCategories;
	}

	public ArrayList<BusinessImpact> getBusinessImpacts() {
		return businessImpacts;
	}

	public void setBusinessImpacts(ArrayList<BusinessImpact> businessImpacts) {
		this.businessImpacts = businessImpacts;
	}

	public ArrayList<SecurityImpact> getSecurityImpacts() {
		return securityImpacts;
	}

	public void setSecurityImpacts(ArrayList<SecurityImpact> securityImpacts) {
		this.securityImpacts = securityImpacts;
	}

	public ArrayList<String> getMalfunctionsIds() {
		return malfunctionsIds;
	}

	public void setMalfunctionsIds(ArrayList<String> malfunctionsIds) {
		this.malfunctionsIds = malfunctionsIds;
	}
	
}