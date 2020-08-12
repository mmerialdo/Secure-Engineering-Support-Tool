/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemProject.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessment;

import org.crmf.model.general.SESTObject;
import org.crmf.model.requirement.Requirement;

import java.util.ArrayList;

public class SystemProject extends SESTObject {

	private String boundaries;
	private String description;
	private String interfaces;
	private String mandate;
	private String name;
	private String physicalLocations;
	private String scope;
	private ArrayList<SystemParticipant> participants = new ArrayList<>();
	private ArrayList<Requirement> requirements = new ArrayList<>();

	public String getDescription(){
		return description;
	}

	public String getMandate(){
		return mandate;
	}

	public String getName(){
		return name;
	}

	public String getScope(){
		return scope;
	}

	public void setDescription(String newVal){
		description = newVal;
	}

	public void setMandate(String newVal){
		mandate = newVal;
	}

	public void setName(String newVal){
		name = newVal;
	}

	public void setScope(String newVal){
		scope = newVal;
	}

	public String getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(String boundaries) {
		this.boundaries = boundaries;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getPhysicalLocations() {
		return physicalLocations;
	}

	public void setPhysicalLocations(String physicalLocations) {
		this.physicalLocations = physicalLocations;
	}

	public ArrayList<SystemParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(ArrayList<SystemParticipant> participants) {
		this.participants = participants;
	}

	public ArrayList<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<Requirement> requirements) {
		this.requirements = requirements;
	}
	
}