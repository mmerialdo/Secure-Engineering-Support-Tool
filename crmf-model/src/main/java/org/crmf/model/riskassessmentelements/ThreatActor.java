/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatActor.java"
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

public class ThreatActor {

	private ThreatSourceEnum catalogue;
	private String catalogueId;
	private String description;
	private String goal;
	private String motive;
	private String name;
	private ActorExpertiseEnum resources;
	private ActorExpertiseEnum skills;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getMotive() {
		return motive;
	}

	public void setMotive(String motive) {
		this.motive = motive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ActorExpertiseEnum getResources() {
		return resources;
	}

	public void setResources(ActorExpertiseEnum resources) {
		this.resources = resources;
	}

	public ActorExpertiseEnum getSkills() {
		return skills;
	}

	public void setSkills(ActorExpertiseEnum skills) {
		this.skills = skills;
	}
}