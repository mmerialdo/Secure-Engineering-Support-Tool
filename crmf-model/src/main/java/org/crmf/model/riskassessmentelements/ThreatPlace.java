/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatPlace.java"
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

public class ThreatPlace {

	private ThreatSourceEnum catalogue;
	private String catalogueId;
	private String description;
	private String name;

	public ThreatPlace(){

	}

	public void finalize() throws Throwable {

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}