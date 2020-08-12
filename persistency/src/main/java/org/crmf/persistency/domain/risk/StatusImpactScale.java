/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="StatusImpactScale.java"
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

public class StatusImpactScale {
  public StatusImpactScale() {

  }

  public StatusImpactScale(int profileId, int projectId, int intrinsicImpact, String securityImpact, int confining, int palliation, int calculatedImpact) {
    this.profileId = profileId;
    this.projectId = projectId;

    this.intrinsincImpact = intrinsicImpact;

    this.securityImpact = securityImpact;

    this.confining = confining;
    this.palliation = palliation;

    this.calculatedImpact = calculatedImpact;

  }

  private int profileId;
  private int projectId;

  private int intrinsincImpact;

  private String securityImpact;

  private int confining;
  private int palliation;

  private int calculatedImpact;

  public int getProfileId() {
    return profileId;
  }

  public void setProfileId(int profileId) {
    this.profileId = profileId;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getIntrinsincImpact() {
    return intrinsincImpact;
  }

  public void setIntrinsincImpact(int intrinsincImpact) {
    this.intrinsincImpact = intrinsincImpact;
  }

  public String getSecurityImpact() {
    return securityImpact;
  }

  public void setSecurityImpact(String securityImpact) {
    this.securityImpact = securityImpact;
  }

  public int getConfining() {
    return confining;
  }

  public void setConfining(int confining) {
    this.confining = confining;
  }

  public int getPalliation() {
    return palliation;
  }

  public void setPalliation(int palliation) {
    this.palliation = palliation;
  }

  public int getCalculatedImpact() {
    return calculatedImpact;
  }

  public void setCalculatedImpact(int calculatedImpact) {
    this.calculatedImpact = calculatedImpact;
  }


}
