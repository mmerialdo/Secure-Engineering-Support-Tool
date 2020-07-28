/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirement.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.secrequirement;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.RequirementSourceEnum;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.model.requirement.RequirementTypeEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;

public class SecRequirement {
	private Integer id;

	private String author;

	private String category;

	private Integer parent;

	private String description;

	private String elementType;

	private String reqId;

	private String lastupd;

	private String note;

	private String source;

	private String sourceDescription;

	private String stability;

	private String status;

	private String subcategory;

	private String tag;

	private String title;

	private String typology;

	private String version;

	private String sestobjId;

	private Integer sysprojectId;
	
	private String priority;

	private String score;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author == null ? null : author.trim();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category == null ? null : category.trim();
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType == null ? null : elementType.trim();
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getLastupd() {
		return lastupd;
	}

	public void setLastupd(String lastupd) {
		this.lastupd = lastupd == null ? null : lastupd.trim();
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note == null ? null : note.trim();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source == null ? null : source.trim();
	}

	public String getSourceDescription() {
		return sourceDescription;
	}

	public void setSourceDescription(String sourceDescription) {
		this.sourceDescription = sourceDescription == null ? null : sourceDescription.trim();
	}

	public String getStability() {
		return stability;
	}

	public void setStability(String stability) {
		this.stability = stability == null ? null : stability.trim();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory == null ? null : subcategory.trim();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag == null ? null : tag.trim();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getTypology() {
		return typology;
	}

	public void setTypology(String typology) {
		this.typology = typology == null ? null : typology.trim();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version == null ? null : version.trim();
	}

	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId == null ? null : sestobjId.trim();
	}

	public Integer getSysprojectId() {
		return sysprojectId;
	}

	public void setSysprojectId(Integer sysprojectId) {
		this.sysprojectId = sysprojectId;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public org.crmf.model.requirement.SecurityRequirement convertToModel() {

		org.crmf.model.requirement.SecurityRequirement secrequirement = new org.crmf.model.requirement.SecurityRequirement();

		secrequirement.setAuthor(this.author);
		secrequirement.setCategory(this.category);
		// requirement.setChildren();
		secrequirement.setDescription(this.description);
		secrequirement.setId(this.reqId);
		secrequirement.setLastUpdate(this.lastupd);
		secrequirement.setNote(this.note);
		// requirement.setRelatedRequirements(relatedRequirements);
		secrequirement.setStability(this.stability);
		secrequirement.setSubCategory(this.subcategory);
		secrequirement.setTag(this.tag);
		secrequirement.setTitle(this.title);
		secrequirement.setVersion(this.version);
		secrequirement.setObjType(SESTObjectTypeEnum.SafeguardModel);
		secrequirement.setSourcesJson(this.sourceDescription);

		if (this.elementType != null) {
			secrequirement.setElementType(ElementTypeEnum.valueOf(this.elementType));
		}
		if (this.source != null) {
			secrequirement.setSource(RequirementSourceEnum.valueOf(this.source));
		}
		if (this.status != null) {
			secrequirement.setStatus(RequirementStatusEnum.valueOf(this.status));
		}
		if (this.typology != null) {
			secrequirement.setType(RequirementTypeEnum.valueOf(this.typology));
		}
		if (this.sestobjId != null) {
			secrequirement.setIdentifier(this.sestobjId);
		}

		return secrequirement;
	}

	public void convertFromModel(org.crmf.model.requirement.SecurityRequirement secrequirement) {

		this.setAuthor(secrequirement.getAuthor());
		this.setCategory(secrequirement.getCategory());
		// this.setChildren();
		this.setDescription(secrequirement.getDescription());
		this.setReqId(secrequirement.getId());
		this.setLastupd(secrequirement.getLastUpdate());
		this.setNote(secrequirement.getNote());
		// this.setRelatedthiss(relatedthis9s);
		this.setDescription(secrequirement.getDescription());
		this.setStability(secrequirement.getStability());
		this.setSubcategory(secrequirement.getSubCategory());
		this.setTag(secrequirement.getTag());
		this.setTitle(secrequirement.getTitle());
		this.setVersion(secrequirement.getVersion());
		this.setSourceDescription(secrequirement.getSourcesJson());

		if (secrequirement.getElementType() != null) {
			this.setElementType(secrequirement.getElementType().name());
		}
		if (secrequirement.getSource() != null) {
			this.setSource(secrequirement.getSource().name());
		}
		if (secrequirement.getStatus() != null) {
			this.setStatus(secrequirement.getStatus().name());
		}
		if (secrequirement.getType() != null) {
			this.setTypology(secrequirement.getType().name());
		}
		if (secrequirement.getIdentifier() != null) {
			this.setSestobjId(secrequirement.getIdentifier());
		}
	}
}
