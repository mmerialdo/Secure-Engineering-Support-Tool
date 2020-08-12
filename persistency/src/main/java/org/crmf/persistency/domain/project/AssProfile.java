/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProfile.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.project;

import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AssProfile {
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  private Integer id;

  private String name;

  private String description;

  private Date creationTime;

  private Date updateTime;

  private String organization;

  private String phase;

  private String methodology;

  private String sestobjId;

  private ArrayList<AssTemplate> templates = new ArrayList<>();

  DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
  private static final Logger LOG = LoggerFactory.getLogger(AssProfile.class.getName());

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization == null ? null : organization.trim();
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase == null ? null : phase.trim();
  }

  public String getMethodology() {
    return methodology;
  }

  public void setMethodology(String methodology) {
    this.methodology = methodology == null ? null : methodology.trim();
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public ArrayList<AssTemplate> getTemplates() {
    return templates;
  }

  public void setTemplates(ArrayList<AssTemplate> templates) {
    this.templates = templates;
  }

  public AssessmentProfile convertToModel() {

    AssessmentProfile assprofile = new AssessmentProfile();
    assprofile.setName(this.name);
    assprofile.setDescription(this.description);
    assprofile.setOrganization(this.organization);
    if (this.phase != null) {
      assprofile.setPhase(PhaseEnum.valueOf(this.phase));
    }
    if (this.methodology != null) {
      assprofile.setRiskMethodology(RiskMethodologyEnum.valueOf(this.methodology));
    }

    if (this.creationTime != null) {
      assprofile.setCreationTime(df.format(this.creationTime));
    }
    if (this.updateTime != null) {
      assprofile.setUpdateTime(df.format(this.updateTime));
    }
    if (this.sestobjId != null) {
      assprofile.setIdentifier(String.valueOf(this.sestobjId));
    }

    ArrayList<AssessmentTemplate> templatesDM = new ArrayList<>();
    if (templates != null && !templates.isEmpty()) {
      for (AssTemplate assTemplate : templates) {
        AssessmentTemplate template = new AssessmentTemplate();
        template.setIdentifier(String.valueOf(assTemplate.getSestobjId()));
        templatesDM.add(template);
      }
    }

    // assprofile.setObjType(this.);

    return assprofile;
  }

  public void convertFromModel(AssessmentProfile assprofile) {

    this.setName(assprofile.getName());
    this.setDescription(assprofile.getDescription());
    this.setOrganization(assprofile.getOrganization());

    if (assprofile.getPhase() != null) {
      this.setPhase(assprofile.getPhase().name());
    }

    if (assprofile.getRiskMethodology() != null) {
      this.setMethodology(assprofile.getRiskMethodology().name());
    }

    try {
      if (assprofile.getCreationTime() != null) {
        this.setCreationTime(new Date(df.parse(assprofile.getCreationTime()).getTime()));
      }
      if (assprofile.getUpdateTime() != null) {
        this.setUpdateTime(new Date(df.parse(assprofile.getUpdateTime()).getTime()));
      }
    } catch (ParseException e) {
      LOG.error(e.getMessage());
    }

    if (assprofile.getIdentifier() != null) {
      this.setSestobjId(assprofile.getIdentifier());
    }

    ArrayList<AssessmentTemplate> templatesDM = assprofile.getTemplates();
    if (templatesDM != null && !templatesDM.isEmpty()) {
      for (AssessmentTemplate assTemplate : templatesDM) {
        AssTemplate template = new AssTemplate();
        template.setSestobjId(assTemplate.getIdentifier());
        templates.add(template);
      }
    }
    // this.setId(id);
  }

  @Override
  public String toString() {
    return "AssProfile [id=" + id + ", name=" + name + ", description=" + description + ", creationTime="
      + creationTime + ", updateTime=" + updateTime + ", organization=" + organization + ", phase=" + phase
      + ", methodology=" + methodology + ", sestobjId=" + sestobjId + ", templates=" + templates + ", df="
      + df + "]";
  }

}