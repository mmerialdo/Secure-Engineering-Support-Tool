/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Assaudit.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.audit;

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;

public class Assaudit {
  private Integer id;

  private String atype;

  private Integer projectId;

  private Integer profileId;

  private String sestobjId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype == null ? null : atype.trim();
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public Integer getProfileId() {
    return profileId;
  }

  public void setProfileId(Integer profileId) {
    this.profileId = profileId;
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public Audit convertToModel() {

    Audit audit = new Audit();

    if (this.atype != null) {
      audit.setType(AuditTypeEnum.valueOf(this.atype));
    }
    if (this.sestobjId != null) {
      audit.setIdentifier(this.sestobjId);
    }
//		audit.setQuestionnaires(questionnaires);

    return audit;
  }

  public void convertFromModel(Audit audit) {

    if (audit.getType() != null) {
      this.setAtype(audit.getType().name());
    }
    if (audit.getIdentifier() != null) {
      this.setSestobjId(audit.getIdentifier());
    }
//		this.setId(id);
//		this.setProfileId(profileId);
//		this.setProjectId(projectId);
  }
}