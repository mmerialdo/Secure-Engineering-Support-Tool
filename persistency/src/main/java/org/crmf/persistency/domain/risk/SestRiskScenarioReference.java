/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestRiskScenarioReference.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.risk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestRiskScenarioReference {

  private static final Logger LOG = LoggerFactory.getLogger(SestRiskScenarioReference.class.getName());
  private Integer id;
  // asset fields
  private String assetType;
  private String secondaryAssetType;
  //vulnerability fields
  private String securityScope;
  private String vulnerabilityReferenceId;
  //threat fields
  private String threatReferenceId;

  //security measures fields
  private String dissuasion;
  private String prevention;
  private String confining;
  private String palliative;

  private String sestobjId;

  public SestRiskScenarioReference() {

  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAssetType() {
    return assetType;
  }

  public void setAssetType(String assetType) {
    this.assetType = assetType;
  }

  public String getSecurityScope() {
    return securityScope;
  }

  public void setSecurityScope(String securityScope) {
    this.securityScope = securityScope;
  }

  public String getVulnerabilityReferenceId() {
    return vulnerabilityReferenceId;
  }

  public void setVulnerabilityReferenceId(String vulnerabilityReferenceId) {
    this.vulnerabilityReferenceId = vulnerabilityReferenceId;
  }

  public String getThreatReferenceId() {
    return threatReferenceId;
  }

  public void setThreatReferenceId(String threatReferenceId) {
    this.threatReferenceId = threatReferenceId;
  }

  public String getDissuasion() {
    return dissuasion;
  }

  public void setDissuasion(String dissuasion) {
    this.dissuasion = dissuasion;
  }

  public String getPrevention() {
    return prevention;
  }

  public void setPrevention(String prevention) {
    this.prevention = prevention;
  }

  public String getConfining() {
    return confining;
  }

  public void setConfining(String confining) {
    this.confining = confining;
  }

  public String getPalliative() {
    return palliative;
  }

  public void setPalliative(String palliative) {
    this.palliative = palliative;
  }


  public String getSecondaryAssetType() {
    return secondaryAssetType;
  }


  public void setSecondaryAssetType(String secondaryAssetType) {
    this.secondaryAssetType = secondaryAssetType;
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }
}
