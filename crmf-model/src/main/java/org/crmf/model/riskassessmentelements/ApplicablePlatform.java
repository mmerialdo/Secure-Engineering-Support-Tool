/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ApplicablePlatform.java"
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

public class ApplicablePlatform {

	private String architecturalParadigms;
	private String hardwareArchitectures;
	private String operatingSystems;
	private String platformNotes;
	private String products;
	private String programmingLanguages;
	private String technologyClasses;

	public ApplicablePlatform(){

	}

	public void finalize() throws Throwable {

	}
	
	public String getArchitecturalParadigms() {
		return architecturalParadigms;
	}

	public void setArchitecturalParadigms(String architecturalParadigms) {
		this.architecturalParadigms = architecturalParadigms;
	}

	public String getHardwareArchitectures() {
		return hardwareArchitectures;
	}

	public void setHardwareArchitectures(String hardwareArchitectures) {
		this.hardwareArchitectures = hardwareArchitectures;
	}

	public String getOperatingSystems() {
		return operatingSystems;
	}

	public void setOperatingSystems(String operatingSystems) {
		this.operatingSystems = operatingSystems;
	}

	public String getPlatformNotes() {
		return platformNotes;
	}

	public void setPlatformNotes(String platformNotes) {
		this.platformNotes = platformNotes;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public String getProgrammingLanguages() {
		return programmingLanguages;
	}

	public void setProgrammingLanguages(String programmingLanguages) {
		this.programmingLanguages = programmingLanguages;
	}

	public String getTechnologyClasses() {
		return technologyClasses;
	}

	public void setTechnologyClasses(String technologyClasses) {
		this.technologyClasses = technologyClasses;
	}
}