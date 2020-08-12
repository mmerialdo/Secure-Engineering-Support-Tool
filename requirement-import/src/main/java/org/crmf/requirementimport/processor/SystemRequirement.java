/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirement.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport.processor;

public class SystemRequirement {
  private String id;
  private String description;
  private String notes;
  private String source;
  private String priority;
  private String version;
  private String issuechanged;
  private String stability;
  private String type;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getIssuechanged() {
    return issuechanged;
  }

  public void setIssuechanged(String issuechanged) {
    this.issuechanged = issuechanged;
  }

  public String getStability() {
    return stability;
  }

  public void setStability(String stability) {
    this.stability = stability;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "SystemRequirement [id=" + id + ", description=" + description + ", notes=" + notes + ", source="
      + source + ", priority=" + priority + ", version=" + version + ", issuechanged=" + issuechanged
      + ", stability=" + stability + ", type=" + type + "]";
  }

}
