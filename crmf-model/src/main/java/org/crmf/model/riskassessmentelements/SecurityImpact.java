/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityImpact.java"
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

public class SecurityImpact {

	private ImpactEnum impact;
	private SecurityImpactScopeEnum scope;
	private ArrayList<String> technicalImpacts;

	public SecurityImpact(){
		technicalImpacts = new ArrayList<>();
	}

	public ImpactEnum getImpact() {
		return impact;
	}

	public void setImpact(ImpactEnum impact) {
		this.impact = impact;
	}

	public SecurityImpactScopeEnum getScope() {
		return scope;
	}

	public void setScope(SecurityImpactScopeEnum scope) {
		this.scope = scope;
	}

	public ArrayList<String> getTechnicalImpacts() {
		return technicalImpacts;
	}

	public void setTechnicalImpacts(ArrayList<String> technicalImpacts) {
		this.technicalImpacts = technicalImpacts;
	}
}