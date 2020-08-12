/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Consequence.java"
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

public class Consequence {

	private String description;
	private ArrayList<SecurityImpact> securityImpacts;

	public Consequence(){
		securityImpacts = new ArrayList<SecurityImpact>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<SecurityImpact> getSecurityImpacts() {
		return securityImpacts;
	}

	public void setSecurityImpacts(ArrayList<SecurityImpact> securityImpacts) {
		this.securityImpacts = securityImpacts;
	}
}