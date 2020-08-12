/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProcedure.java"
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

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.PhaseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssProcedure {
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  private Integer id;

  private Date creationTime;

  private Date updateTime;

  private String name;

  private String phase;

  private String status;

  private Integer projectId;

  private Integer assetId;

  private Integer threatId;

  private Integer vulnerabilityId;

  private Integer safeguardId;

  private Integer riskModelId;

  private Integer riskTreatmentModelId;

  private Integer lastUserUpdateId;

  private String sestobjId;

  DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

  private static final Logger LOG = LoggerFactory.getLogger(AssProcedure.class.getName());


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status == null ? null : status.trim();
  }

  public Integer getAssetId() {
    return assetId;
  }

  public void setAssetId(Integer assetId) {
    this.assetId = assetId;
  }

  public Integer getThreatId() {
    return threatId;
  }

  public void setThreatId(Integer threatId) {
    this.threatId = threatId;
  }

  public Integer getVulnerabilityId() {
    return vulnerabilityId;
  }

  public void setVulnerabilityId(Integer vulnerabilityId) {
    this.vulnerabilityId = vulnerabilityId;
  }

  public Integer getSafeguardId() {
    return safeguardId;
  }

  public void setSafeguardId(Integer safeguardId) {
    this.safeguardId = safeguardId;
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

  public Integer getRiskModelId() {
    return riskModelId;
  }

  public void setRiskModelId(Integer riskModelId) {
    this.riskModelId = riskModelId;
  }

  public Integer getRiskTreatmentModelId() {
    return riskTreatmentModelId;
  }

  public void setRiskTreatmentModelId(Integer riskTreatmentModelId) {
    this.riskTreatmentModelId = riskTreatmentModelId;
  }

  public Integer getLastUserUpdateId() {
    return lastUserUpdateId;
  }

  public void setLastUserUpdateId(Integer lastUserUpdateId) {
    this.lastUserUpdateId = lastUserUpdateId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public AssessmentProcedure convertToModel() {

    AssessmentProcedure assprocedure = new AssessmentProcedure();
    assprocedure.setObjType(SESTObjectTypeEnum.AssessmentProcedure);

    assprocedure.setStatus(AssessmentStatusEnum.valueOf(this.status));
    assprocedure.setName(this.name);
    assprocedure.setPhase(PhaseEnum.valueOf(this.phase));

    if (this.creationTime != null) {
      assprocedure.setCreationTime(df.format(this.creationTime));
    }
    if (this.updateTime != null) {
      assprocedure.setUpdateTime(df.format(this.updateTime));
    }
    if (this.sestobjId != null) {
      assprocedure.setIdentifier(String.valueOf(this.sestobjId));
    }

    return assprocedure;
  }

  public void convertFromModel(AssessmentProcedure assprocedure) {

    this.setName(assprocedure.getName());
    this.setPhase(assprocedure.getPhase().name());
    this.setStatus(assprocedure.getStatus().name());

    try {
      if (assprocedure.getCreationTime() != null) {
        this.setCreationTime(new Date(df.parse(assprocedure.getCreationTime()).getTime()));
      }
      if (assprocedure.getUpdateTime() != null) {
        this.setUpdateTime(new Date(df.parse(assprocedure.getUpdateTime()).getTime()));
      }
    } catch (ParseException e) {
      LOG.error(e.getMessage());
    }

    if (assprocedure.getIdentifier() != null) {
      this.setSestobjId(assprocedure.getIdentifier());
    }
    if (assprocedure.getLastUserUpdate() != null && assprocedure.getLastUserUpdate().getIdentifier() != null) {
      this.setLastUserUpdateId(Integer.valueOf(assprocedure.getLastUserUpdate().getIdentifier()));
    }

    //TODO: comment all models when they are integrated

    // (removing this if code before integration is finished leads to an insert error into the DB
    // cause the models cannot be null)

//		if (assprocedure.getAssetModel() != null) {
//			this.setAssetId(Integer.valueOf(assprocedure.getAssetModel().getIdentifier()));
//		}
//		if (assprocedure.getRiskModel() != null && assprocedure.getRiskModel().getIdentifier() != null) {
//			this.setRiskModelId(Integer.valueOf(assprocedure.getRiskModel().getIdentifier()));
//		}
//		if (assprocedure.getSafeguardModel() != null && assprocedure.getSafeguardModel().getIdentifier() != null) {
//			this.setSafeguardId(Integer.valueOf(assprocedure.getSafeguardModel().getIdentifier()));
//		}
//		if (assprocedure.getThreatModel() != null && assprocedure.getThreatModel().getIdentifier() != null) {
//			this.setThreatId(Integer.valueOf(assprocedure.getThreatModel().getIdentifier()));
//		}
//		if (assprocedure.getVulnerabilityModel() != null && assprocedure.getVulnerabilityModel().getIdentifier() != null) {
//			this.setVulnerabilityId(Integer.valueOf(assprocedure.getVulnerabilityModel().getIdentifier()));
//		}
    // this.setId(id);
  }
}