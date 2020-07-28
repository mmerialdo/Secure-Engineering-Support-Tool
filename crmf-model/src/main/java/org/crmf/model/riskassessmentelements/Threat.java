/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Threat.java"
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
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.PhaseEnum;

public class Threat extends SESTObject {

	private ArrayList<SecondaryAssetCategoryEnum> affectedAssetsCategories;
	private boolean assessmentThreat;
	private String author;
	private boolean canBeSelected;
	private ThreatSourceEnum catalogue;
	private String catalogueId;
	private ArrayList<Threat> children;
	private String description;
	private ElementTypeEnum elementType;
	private String lastUpdate;
	private String name;
	private PhaseEnum phase;
	private ArrayList<String> prerequisites;
	private String rawText;
	private ArrayList<String> referenceUrls;
	private ArrayList<Threat> relatedThreats;
	private ThreatClassEnum threatClass;
	private ArrayList<String> associatedVulnerabilities;
	private ApplicablePlatform applicablePlatform;
	private ThreatActor actor;
	private ThreatEvent event;
	private ThreatScore score;
	private ThreatPlace place;
	private ThreatTime time;
	private ThreatAccess access;
	private ThreatProcess process;
	private ArrayList<Mitigation> mitigations;
	private ArrayList<SecurityRequirement> relatedSecurityRequirements;

	public Threat(){
		affectedAssetsCategories = new ArrayList<SecondaryAssetCategoryEnum>();
		associatedVulnerabilities = new ArrayList<String> ();
		assessmentThreat = false;
		mitigations = new ArrayList<Mitigation> ();
		relatedThreats = new ArrayList<Threat>();
		children = new ArrayList<Threat>();
		relatedSecurityRequirements = new ArrayList<SecurityRequirement>();
	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	

	public boolean isAssessmentThreat() {
		return assessmentThreat;
	}

	public void setAssessmentThreat(boolean assessmentThreat) {
		this.assessmentThreat = assessmentThreat;
	}

	public ThreatSourceEnum getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(ThreatSourceEnum catalogue) {
		this.catalogue = catalogue;
	}

	public String getCatalogueId() {
		return catalogueId;
	}

	public void setCatalogueId(String catalogueId) {
		this.catalogueId = catalogueId;
	}

	public ArrayList<Threat> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Threat> children) {
		this.children = children;
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

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public ArrayList<String> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(ArrayList<String> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public String getRawText() {
		return rawText;
	}

	public void setRawText(String rawText) {
		this.rawText = rawText;
	}

	public ArrayList<String> getReferenceUrls() {
		return referenceUrls;
	}

	public void setReferenceUrls(ArrayList<String> referenceUrls) {
		this.referenceUrls = referenceUrls;
	}

	public ArrayList<Threat> getRelatedThreats() {
		return relatedThreats;
	}

	public void setRelatedThreats(ArrayList<Threat> relatedThreats) {
		this.relatedThreats = relatedThreats;
	}

	public ThreatClassEnum getThreatClass() {
		return threatClass;
	}

	public void setThreatClass(ThreatClassEnum threatClass) {
		this.threatClass = threatClass;
	}

	public ArrayList<String> getAssociatedVulnerabilities() {
		return associatedVulnerabilities;
	}

	public void setAssociatedVulnerabilities(ArrayList<String> associatedVulnerabilities) {
		this.associatedVulnerabilities = associatedVulnerabilities;
	}

	public ApplicablePlatform getApplicablePlatform() {
		return applicablePlatform;
	}

	public void setApplicablePlatform(ApplicablePlatform applicablePlatform) {
		this.applicablePlatform = applicablePlatform;
	}


	public ThreatScore getScore() {
		return score;
	}

	public void setScore(ThreatScore score) {
		this.score = score;
	}

	public ArrayList<Mitigation> getMitigations() {
		return mitigations;
	}

	public void setMitigations(ArrayList<Mitigation> mitigations) {
		this.mitigations = mitigations;
	}

	public ArrayList<SecondaryAssetCategoryEnum> getAffectedAssetsCategories() {
		return affectedAssetsCategories;
	}

	public void setAffectedAssetsCategories(ArrayList<SecondaryAssetCategoryEnum> affectedAssetsCategories) {
		this.affectedAssetsCategories = affectedAssetsCategories;
	}

	public ThreatActor getActor() {
		return actor;
	}

	public void setActor(ThreatActor actor) {
		this.actor = actor;
	}

	public ThreatEvent getEvent() {
		return event;
	}

	public void setEvent(ThreatEvent event) {
		this.event = event;
	}

	public ThreatPlace getPlace() {
		return place;
	}

	public void setPlace(ThreatPlace place) {
		this.place = place;
	}

	public ThreatTime getTime() {
		return time;
	}

	public void setTime(ThreatTime time) {
		this.time = time;
	}

	public ThreatAccess getAccess() {
		return access;
	}

	public void setAccess(ThreatAccess access) {
		this.access = access;
	}

	public ThreatProcess getProcess() {
		return process;
	}

	public void setProcess(ThreatProcess process) {
		this.process = process;
	}

	public boolean isCanBeSelected() {
		return canBeSelected;
	}

	public void setCanBeSelected(boolean canBeSelected) {
		this.canBeSelected = canBeSelected;
	}

	public ArrayList<SecurityRequirement> getRelatedSecurityRequirements() {
		return relatedSecurityRequirements;
	}

	public void setRelatedSecurityRequirements(ArrayList<SecurityRequirement> relatedSecurityRequirements) {
		this.relatedSecurityRequirements = relatedSecurityRequirements;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}