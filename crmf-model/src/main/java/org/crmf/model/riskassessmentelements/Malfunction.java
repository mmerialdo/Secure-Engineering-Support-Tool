/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Malfunction.java"
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

public class Malfunction extends Node {

	private String functionalConsequence;
	private String functionalDescription;
	private FunctionalMalfunctionTypeEnum functionalType;
	private String technicalConsequence;
	private String technicalDescription;
	private float weight;
	private ArrayList<TechnicalMalfunctionTypeEnum> technicalTypes;
	private ArrayList<MalfunctionValueScale> scales;

	public Malfunction(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public String getFunctionalConsequence() {
		return functionalConsequence;
	}

	public void setFunctionalConsequence(String functionalConsequence) {
		this.functionalConsequence = functionalConsequence;
	}

	public String getFunctionalDescription() {
		return functionalDescription;
	}

	public void setFunctionalDescription(String functionalDescription) {
		this.functionalDescription = functionalDescription;
	}

	public FunctionalMalfunctionTypeEnum getFunctionalType() {
		return functionalType;
	}

	public void setFunctionalType(FunctionalMalfunctionTypeEnum functionalType) {
		this.functionalType = functionalType;
	}

	public String getTechnicalConsequence() {
		return technicalConsequence;
	}

	public void setTechnicalConsequence(String technicalConsequence) {
		this.technicalConsequence = technicalConsequence;
	}

	public String getTechnicalDescription() {
		return technicalDescription;
	}

	public void setTechnicalDescription(String technicalDescription) {
		this.technicalDescription = technicalDescription;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public ArrayList<TechnicalMalfunctionTypeEnum> getTechnicalTypes() {
		return technicalTypes;
	}

	public void setTechnicalTypes(ArrayList<TechnicalMalfunctionTypeEnum> technicalTypes) {
		this.technicalTypes = technicalTypes;
	}

	public ArrayList<MalfunctionValueScale> getScales() {
		return scales;
	}

	public void setScales(ArrayList<MalfunctionValueScale> scales) {
		this.scales = scales;
	}
}