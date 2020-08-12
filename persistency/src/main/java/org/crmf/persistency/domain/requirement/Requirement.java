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

package org.crmf.persistency.domain.requirement;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.RequirementSourceEnum;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.model.requirement.RequirementTypeEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;

public class Requirement {
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

  public org.crmf.model.requirement.Requirement convertToModel() {

    org.crmf.model.requirement.Requirement requirement = new org.crmf.model.requirement.Requirement();

    requirement.setAuthor(this.author);
    requirement.setCategory(this.category);
//		requirement.setChildren();
    requirement.setDescription(this.description);
    requirement.setId(this.reqId);
    requirement.setLastUpdate(this.lastupd);
    requirement.setNote(this.note);
//		requirement.setRelatedRequirements(relatedRequirements);
    requirement.setSourceDescription(this.sourceDescription);
    requirement.setStability(this.stability);
    requirement.setSubCategory(this.subcategory);
    requirement.setTag(this.tag);
    requirement.setTitle(this.title);
    requirement.setVersion(this.version);
    requirement.setObjType(SESTObjectTypeEnum.AssetModel);

    if (this.elementType != null) {
      requirement.setElementType(ElementTypeEnum.valueOf(this.elementType));
    }
    if (this.source != null) {
      requirement.setSource(RequirementSourceEnum.valueOf(this.source));
    }
    if (this.status != null) {
      requirement.setStatus(RequirementStatusEnum.valueOf(this.status));
    }
    if (this.typology != null) {
      requirement.setType(RequirementTypeEnum.valueOf(this.typology));
    }
    if (this.sestobjId != null) {
      requirement.setIdentifier(String.valueOf(this.sestobjId));
    }

    return requirement;
  }

  public void convertFromModel(org.crmf.model.requirement.Requirement requirement) {

    this.setAuthor(requirement.getAuthor());
    this.setCategory(requirement.getCategory());
//		this.setChildren();
    this.setDescription(requirement.getDescription());
    this.setReqId(requirement.getId());
    this.setLastupd(requirement.getLastUpdate());
    this.setNote(requirement.getNote());
//		this.setRelatedthiss(relatedthis9s);
    this.setSourceDescription(requirement.getSourceDescription());
    this.setStability(requirement.getStability());
    this.setSubcategory(requirement.getSubCategory());
    this.setTag(requirement.getTag());
    this.setTitle(requirement.getTitle());
    this.setVersion(requirement.getVersion());

    if (requirement.getElementType() != null) {
      this.setElementType(requirement.getElementType().name());
    }
    if (requirement.getSource() != null) {
      this.setSource(requirement.getSource().name());
    }
    if (requirement.getStatus() != null) {
      this.setStatus(requirement.getStatus().name());
    }
    if (requirement.getType() != null) {
      this.setTypology(requirement.getType().name());
    }
    if (requirement.getIdentifier() != null) {
      this.setSestobjId(requirement.getIdentifier());
    }
  }
}