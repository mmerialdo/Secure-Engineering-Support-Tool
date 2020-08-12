/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Safeguard.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.model.requirement.Requirement;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.PhaseEnum;

import java.util.ArrayList;

public class Safeguard extends SESTObject {

	private boolean assessmentSafeguard;
	private SafeguardSourceEnum catalogue;
	private String catalogueId;
	private String description;
	private String userDescription;
	private ElementTypeEnum elementType;
	private String name;
	private PhaseEnum phase;
	private SafeguardScoreEnum score;
	private SafeguardScopeEnum scope;
	private ArrayList<Safeguard> children;
	private ArrayList<SecurityRequirement> relatedSecurityRequirements;
	private ArrayList<Requirement> relatedProjectRequirements;
	private ArrayList<SafeguardScore> scores;


	public Safeguard(){
		children = new ArrayList<>();
		relatedSecurityRequirements = new ArrayList<>();
		relatedProjectRequirements = new ArrayList<>();
		score = SafeguardScoreEnum.LOW;
	}

	public boolean isAssessmentSafeguard() {
		return assessmentSafeguard;
	}

	public void setAssessmentSafeguard(boolean assessmentSafeguard) {
		this.assessmentSafeguard = assessmentSafeguard;
	}

	public SafeguardSourceEnum getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(SafeguardSourceEnum catalogue) {
		this.catalogue = catalogue;
	}

	public String getCatalogueId() {
		return catalogueId;
	}

	public void setCatalogueId(String catalogueId) {
		this.catalogueId = catalogueId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ElementTypeEnum getElementType() {
		return elementType;
	}

	public void setElementType(ElementTypeEnum elementType) {
		this.elementType = elementType;
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

	public SafeguardScoreEnum getScore() {
		return score;
	}

	public String getScoreNumber() {
		switch (this.getScore()) {
			case LOW:
				return "1";
			case MEDIUM:
				return "2";
			case HIGH:
				return "3";
			case VERY_HIGH:
				return "4";
			default:
				return "";
		}
	}

	public void setScore(SafeguardScoreEnum score) {
		this.score = score;
	}

	public void setScoreNumber(String value) {
		switch (value) {
			case "1":
				this.setScore(SafeguardScoreEnum.LOW);
				break;
			case "2":
				this.setScore(SafeguardScoreEnum.MEDIUM);
				break;
			case "3":
				this.setScore(SafeguardScoreEnum.HIGH);
				break;
			case "4":
				this.setScore(SafeguardScoreEnum.VERY_HIGH);
				break;
			default:
				this.setScore(SafeguardScoreEnum.NONE);
				break;
		}
	}

	public ArrayList<Safeguard> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Safeguard> children) {
		this.children = children;
	}

	public ArrayList<SecurityRequirement> getRelatedSecurityRequirements() {
		return relatedSecurityRequirements;
	}

	public void setRelatedSecurityRequirements(ArrayList<SecurityRequirement> relatedSecurityRequirements) {
		this.relatedSecurityRequirements = relatedSecurityRequirements;
	}

	public ArrayList<Requirement> getRelatedProjectRequirements() {
		return relatedProjectRequirements;
	}

	public void setRelatedProjectRequirements(ArrayList<Requirement> relatedProjectRequirements) {
		this.relatedProjectRequirements = relatedProjectRequirements;
	}

	public ArrayList<SafeguardScore> getScores() {
		return scores;
	}

	public void setScores(ArrayList<SafeguardScore> scores) {
		this.scores = scores;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public SafeguardScopeEnum getScope() {
		return scope;
	}

	public void setScope(SafeguardScopeEnum scope) {
		this.scope = scope;
	}
}