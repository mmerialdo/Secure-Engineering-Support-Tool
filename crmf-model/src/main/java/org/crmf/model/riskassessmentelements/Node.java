/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Node.java"
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

import java.util.ArrayList;

public class Node extends SESTObject {

	private boolean assessmentNode;
	private String description;
	private String goal;
	private String name;
	private NodeTypeEnum nodeType;
	private String systemParticipantOwnerId;
	private ArrayList<String> relatedRequirementsIds;
	private ArrayList<Edge> children;
	private ArrayList<Edge> parents;

	public boolean isAssessmentNode() {
		return assessmentNode;
	}

	public void setAssessmentNode(boolean assessmentNode) {
		this.assessmentNode = assessmentNode;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeTypeEnum getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeTypeEnum nodeType) {
		this.nodeType = nodeType;
	}

	public String getSystemParticipantOwnerId() {
		return systemParticipantOwnerId;
	}

	public void setSystemParticipantOwnerId(String systemParticipantOwnerId) {
		this.systemParticipantOwnerId = systemParticipantOwnerId;
	}

	public ArrayList<String> getRelatedRequirementsIds() {
		return relatedRequirementsIds;
	}

	public void setRelatedRequirementsIds(ArrayList<String> relatedRequirementsIds) {
		this.relatedRequirementsIds = relatedRequirementsIds;
	}

	public ArrayList<Edge> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Edge> children) {
		this.children = children;
	}

	public ArrayList<Edge> getParents() {
		return parents;
	}

	public void setParents(ArrayList<Edge> parents) {
		this.parents = parents;
	}
}