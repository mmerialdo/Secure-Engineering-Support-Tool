/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModel.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessment;

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.Organization;

import java.util.List;

public class AssetModel extends SESTObject {

  private String creationTime;
  private String graphJson;
  private String updateTime;
  private List<Edge> edges;
  private List<Organization> organizations;
  private List<BusinessProcess> businessProcesses;
  private List<BusinessActivity> businessActivities;
  private List<Asset> assets;
  private List<Malfunction> malfunctions;

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String newVal) {
    updateTime = newVal;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getGraphJson() {
    return graphJson;
  }

  public void setGraphJson(String graphJson) {
    this.graphJson = graphJson;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public void setEdges(List<Edge> edges) {
    this.edges = edges;
  }

  public List<Organization> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(List<Organization> organizations) {
    this.organizations = organizations;
  }

  public List<BusinessProcess> getBusinessProcesses() {
    return businessProcesses;
  }

  public void setBusinessProcesses(List<BusinessProcess> businessProcesses) {
    this.businessProcesses = businessProcesses;
  }

  public List<BusinessActivity> getBusinessActivities() {
    return businessActivities;
  }

  public void setBusinessActivities(List<BusinessActivity> businessActivities) {
    this.businessActivities = businessActivities;
  }

  public List<Asset> getAssets() {
    return assets;
  }

  public void setAssets(List<Asset> assets) {
    this.assets = assets;
  }

  public List<Malfunction> getMalfunctions() {
    return malfunctions;
  }

  public void setMalfunctions(List<Malfunction> malfunctions) {
    this.malfunctions = malfunctions;
  }

}