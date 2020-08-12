/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityRequirement.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.requirement;

import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.model.riskassessmentelements.Vulnerability;

import java.util.ArrayList;

public class SecurityRequirement extends Requirement {

	private String sourcesJson;
	private ArrayList<Vulnerability> relatedVulnerabilities;
	private SafeguardScoreEnum score;
	private ArrayList<SecurityRequirement> children = new ArrayList<>();
	

	public SecurityRequirement(){
		score = SafeguardScoreEnum.NONE;
		relatedVulnerabilities = new ArrayList<>();
		children = new ArrayList<>();
	}

	public ArrayList<Vulnerability> getRelatedVulnerabilities() {
		return relatedVulnerabilities;
	}

	public void setRelatedVulnerabilities(ArrayList<Vulnerability> relatedVulnerabilities) {
		this.relatedVulnerabilities = relatedVulnerabilities;
	}

	public SafeguardScoreEnum getScore() {
		return score;
	}

	public void setScore(SafeguardScoreEnum score) {
		this.score = score;
	}

	public ArrayList<SecurityRequirement> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<SecurityRequirement> securityRequirements) {
		this.children = securityRequirements;
	}

	public String getSourcesJson() {
		return sourcesJson;
	}

	public void setSourcesJson(String sourcesJson) {
		this.sourcesJson = sourcesJson;
	}
}