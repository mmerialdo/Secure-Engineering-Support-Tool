/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureSerializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.assetmodel;

import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.Organization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AssetModelValidatorTest {

  @Test
  public void validateAssetModelRemoveAssetActivityDupplicate() {
    AssetModel am = this.createAssetModel();
    Asset asset = am.getAssets().get(0);
    BusinessActivity activity = am.getBusinessActivities().get(0);
    Edge edge1 = new Edge(asset, activity);
    edge1.setIdentifier(UUID.randomUUID().toString());
    Edge edge2 = new Edge(activity, asset);
    edge2.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    am.getEdges().add(edge2);
    List<Edge> parents = asset.getParents();
    List<Edge> children = activity.getChildren();
    parents.add(edge1);
    parents.add(edge2);
    children.add(edge1);
    children.add(edge2);

    Assertions.assertEquals(6, am.getEdges().size());
    Assertions.assertEquals(3, am.getAssets().get(0).getParents().size());
    Assertions.assertEquals(4, am.getBusinessActivities().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getAssets().get(0).getParents().size());
    Assertions.assertEquals(2, am.getBusinessActivities().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveMalfunctionActivityDupplicate() {
    AssetModel am = this.createAssetModel();
    Malfunction malfunction = am.getMalfunctions().get(0);
    BusinessActivity activity = am.getBusinessActivities().get(0);
    Edge edge1 = new Edge(malfunction, activity);
    edge1.setIdentifier(UUID.randomUUID().toString());
    Edge edge2 = new Edge(activity, malfunction);
    edge2.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    am.getEdges().add(edge2);
    List<Edge> parents = malfunction.getParents();
    List<Edge> children = activity.getChildren();
    parents.add(edge1);
    parents.add(edge2);
    children.add(edge1);
    children.add(edge2);

    Assertions.assertEquals(6, am.getEdges().size());
    Assertions.assertEquals(3, am.getMalfunctions().get(0).getParents().size());
    Assertions.assertEquals(4, am.getBusinessActivities().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getMalfunctions().get(0).getParents().size());
    Assertions.assertEquals(2, am.getBusinessActivities().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveProcessActivityDupplicate() {
    AssetModel am = this.createAssetModel();
    BusinessProcess process = am.getBusinessProcesses().get(0);
    BusinessActivity activity = am.getBusinessActivities().get(0);
    Edge edge1 = new Edge(process, activity);
    edge1.setIdentifier(UUID.randomUUID().toString());
    Edge edge2 = new Edge(activity, process);
    edge2.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    am.getEdges().add(edge2);
    List<Edge> children = process.getChildren();
    List<Edge> parents = activity.getParents();
    parents.add(edge1);
    parents.add(edge2);
    children.add(edge1);
    children.add(edge2);

    Assertions.assertEquals(6, am.getEdges().size());
    Assertions.assertEquals(3, am.getBusinessProcesses().get(0).getChildren().size());
    Assertions.assertEquals(3, am.getBusinessActivities().get(0).getParents().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getBusinessProcesses().get(0).getChildren().size());
    Assertions.assertEquals(1, am.getBusinessActivities().get(0).getParents().size());
  }

  @Test
  public void validateAssetModelRemoveOrganizationProcessDupplicate() {
    AssetModel am = this.createAssetModel();
    BusinessProcess process = am.getBusinessProcesses().get(0);
    Organization organization = am.getOrganizations().get(0);
    Edge edge1 = new Edge(process, organization);
    edge1.setIdentifier(UUID.randomUUID().toString());
    Edge edge2 = new Edge(organization, process);
    edge2.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    am.getEdges().add(edge2);
    List<Edge> parents = process.getParents();
    List<Edge> children = organization.getChildren();
    parents.add(edge1);
    parents.add(edge2);
    children.add(edge1);
    children.add(edge2);

    Assertions.assertEquals(6, am.getEdges().size());
    Assertions.assertEquals(3, am.getBusinessProcesses().get(0).getParents().size());
    Assertions.assertEquals(3, am.getOrganizations().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getBusinessProcesses().get(0).getParents().size());
    Assertions.assertEquals(1, am.getOrganizations().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveActivityChildrenNotExisting() {
    AssetModel am = this.createAssetModel();
    BusinessActivity activity = am.getBusinessActivities().get(0);
    Edge edge1 = new Edge(new Asset(), activity);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> children = activity.getChildren();
    children.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(3, am.getBusinessActivities().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(2, am.getBusinessActivities().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveProcessChildrenNotExisting() {
    AssetModel am = this.createAssetModel();
    BusinessProcess process = am.getBusinessProcesses().get(0);
    Edge edge1 = new Edge(new BusinessActivity(), process);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> children = process.getChildren();
    children.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(2, am.getBusinessProcesses().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getBusinessProcesses().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveOrganizationChildrenNotExisting() {
    AssetModel am = this.createAssetModel();
    Organization organization = am.getOrganizations().get(0);
    Edge edge1 = new Edge(new BusinessProcess(), organization);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> children = organization.getChildren();
    children.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(2, am.getOrganizations().get(0).getChildren().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getOrganizations().get(0).getChildren().size());
  }

  @Test
  public void validateAssetModelRemoveProcessParentNotExisting() {
    AssetModel am = this.createAssetModel();
    BusinessProcess process = am.getBusinessProcesses().get(0);
    Edge edge1 = new Edge(new Organization(), process);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> parent = process.getParents();
    parent.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(2, am.getBusinessProcesses().get(0).getParents().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getBusinessProcesses().get(0).getParents().size());
  }

  @Test
  public void validateAssetModelRemoveActivityParentNotExisting() {
    AssetModel am = this.createAssetModel();
    BusinessActivity activity = am.getBusinessActivities().get(0);
    Edge edge1 = new Edge(new BusinessProcess(), activity);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> parent = activity.getParents();
    parent.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(2, am.getBusinessActivities().get(0).getParents().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getBusinessActivities().get(0).getParents().size());
  }

  @Test
  public void validateAssetModelRemoveAssetParentNotExisting() {
    AssetModel am = this.createAssetModel();
    Asset assets = am.getAssets().get(0);
    Edge edge1 = new Edge(new BusinessActivity(), assets);
    edge1.setIdentifier(UUID.randomUUID().toString());
    am.getEdges().add(edge1);
    List<Edge> parent = assets.getParents();
    parent.add(edge1);

    Assertions.assertEquals(5, am.getEdges().size());
    Assertions.assertEquals(2, am.getAssets().get(0).getParents().size());

    AssetModelValidator validator = new AssetModelValidator();
    am = validator.validateAssetModel(am);

    Assertions.assertEquals(4, am.getEdges().size());
    Assertions.assertEquals(1, am.getAssets().get(0).getParents().size());
  }

  private AssetModel createAssetModel() {
    AssetModel am = new AssetModel();
    List<Organization> organizations = new ArrayList<>();
    List<BusinessProcess> processes = new ArrayList<>();
    List<BusinessActivity> activities = new ArrayList<>();
    List<Malfunction> malfunctions = new ArrayList<>();
    List<Asset> assets = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    Organization organization = new Organization();
    organization.setIdentifier("organization01");
    BusinessProcess process = new BusinessProcess();
    process.setIdentifier("process01");
    BusinessActivity activity = new BusinessActivity();
    activity.setIdentifier("activity01");
    Malfunction malfunction = new Malfunction();
    malfunction.setIdentifier("malfunction01");
    Asset asset = new Asset();
    asset.setIdentifier("asset01");

    Edge organizationProcess = this.buildEdge(organization, process);
    Edge processActivity = this.buildEdge(process, activity);
    Edge activityAsset = this.buildEdge(activity, asset);
    Edge activityMalfunction = this.buildEdge(activity, malfunction);
    organization.setChildren(new LinkedList<>(Arrays.asList(organizationProcess)));
    process.setChildren(new LinkedList<>(Arrays.asList(processActivity)));
    process.setParents(new LinkedList<>(Arrays.asList(organizationProcess)));
    activity.setParents(new LinkedList<>(Arrays.asList(processActivity)));
    activity.setChildren(new LinkedList<>(Arrays.asList(activityAsset, activityMalfunction)));
    asset.setParents(new LinkedList<>(Arrays.asList(activityAsset)));
    malfunction.setParents(new LinkedList<>(Arrays.asList(activityMalfunction)));

    assets.add(asset);
    malfunctions.add(malfunction);
    processes.add(process);
    activities.add(activity);
    organizations.add(organization);

    edges.add(organizationProcess);
    edges.add(processActivity);
    edges.add(activityAsset);
    edges.add(activityMalfunction);

    am.setOrganizations(organizations);
    am.setBusinessProcesses(processes);
    am.setBusinessActivities(activities);
    am.setMalfunctions(malfunctions);
    am.setAssets(assets);
    am.setEdges(edges);

    return am;
  }

  private Edge buildEdge(Node node1, Node node2) {
    Edge edge = new Edge(node1, node2);
    edge.setIdentifier(UUID.randomUUID().toString());

    return edge;
  }
}
