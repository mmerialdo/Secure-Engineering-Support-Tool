/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Edge.java"
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

import org.crmf.model.general.SESTObject;

public class Edge extends SESTObject {

	private float operationalWeight;
	private ArrayList<SecurityImpact> securityImpacts;
	private Node source;
	private Node target;
	private ArrayList<BusinessImpact> businessImpactWeights;

	public Edge(){
		
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public float getOperationalWeight() {
		return operationalWeight;
	}

	public void setOperationalWeight(float operationalWeight) {
		this.operationalWeight = operationalWeight;
	}

	public ArrayList<SecurityImpact> getSecurityImpacts() {
		return securityImpacts;
	}

	public void setSecurityImpacts(ArrayList<SecurityImpact> securityImpacts) {
		this.securityImpacts = securityImpacts;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public ArrayList<BusinessImpact> getBusinessImpactWeights() {
		return businessImpactWeights;
	}

	public void setBusinessImpactWeights(ArrayList<BusinessImpact> businessImpactWeights) {
		this.businessImpactWeights = businessImpactWeights;
	}
}