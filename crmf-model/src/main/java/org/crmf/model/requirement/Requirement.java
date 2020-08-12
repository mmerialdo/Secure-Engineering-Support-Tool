/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Requirement.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;

import java.util.ArrayList;

public class Requirement extends SESTObject {

	private String author;
	private String category;
	private String parentId;
	private String description;
	private String userDescription;
	private ElementTypeEnum elementType;
	private String id;
	private String lastUpdate;
	private String note;
	private ArrayList<Requirement> relatedRequirements;
	private RequirementSourceEnum source;
	private String sourceDescription;
	private String stability;
	private RequirementStatusEnum status;
	private String subCategory;
	private String tag;
	private RequirementTypeEnum type;
	private String version;
	private String title;
	private String priority;

	public Requirement(){
		relatedRequirements = new ArrayList<>();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public RequirementSourceEnum getSource() {
		return source;
	}

	public void setSource(RequirementSourceEnum source) {
		this.source = source;
	}

	public String getSourceDescription() {
		return sourceDescription;
	}

	public void setSourceDescription(String sourceDescription) {
		this.sourceDescription = sourceDescription;
	}

	public String getStability() {
		return stability;
	}

	public void setStability(String stability) {
		this.stability = stability;
	}

	public RequirementStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RequirementStatusEnum status) {
		this.status = status;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public RequirementTypeEnum getType() {
		return type;
	}

	public void setType(RequirementTypeEnum type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public ArrayList<Requirement> getRelatedRequirements() {
		return relatedRequirements;
	}

	public void setRelatedRequirements(ArrayList<Requirement> relatedRequirements) {
		this.relatedRequirements = relatedRequirements;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

}