/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelInstanceCreator.java"
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
import org.crmf.model.riskassessmentelements.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssetModelValidator {

  private static final Logger LOG = LoggerFactory.getLogger(AssetModelValidator.class.getName());

  public AssetModel validateAssetModel(AssetModel am) {

    this.validateAssetParent(am);
    this.validateMalfunctionParent(am);
    this.validateActivityParent(am);
    this.validateProcessParent(am);
    this.validateActivityChildrenNotExisting(am);
    this.validateProcessChildrenNotExisting(am);
    this.validateOrganizationChildrenNotExisting(am);

    return am;
  }

  private AssetModel validateAssetParent(AssetModel am) {
    List<Edge> edgeToRemove = new ArrayList<>();
    am.getAssets().forEach(asset -> {
      for (Edge edge : asset.getParents()) {
        List<BusinessActivity> activities = am.getBusinessActivities().stream().filter(activity ->
          (activity.getIdentifier().equals(edge.getTarget().getIdentifier()) ||
            activity.getIdentifier().equals(edge.getSource().getIdentifier()))).collect(Collectors.toList());
        if (activities.isEmpty()) {
          edgeToRemove.add(edge);
          continue;
        }
        if (!edgeToRemove.contains(edge)) {
          edgeToRemove.addAll(getEdgesDupplicated(asset.getParents(), edge));
        }
      }
    });
    if (!edgeToRemove.isEmpty()) {
      this.removeEdgeActivity(edgeToRemove, am);
      this.removeEdgeAsset(edgeToRemove, am);
    }
    return am;
  }

  private AssetModel validateMalfunctionParent(AssetModel am) {
    List<Edge> edgeToRemove = new ArrayList<>();
    am.getMalfunctions().forEach(malfunction -> {
      for (Edge edge : malfunction.getParents()) {
        List<BusinessActivity> activities = am.getBusinessActivities().stream().filter(activity ->
          (activity.getIdentifier().equals(edge.getTarget().getIdentifier()) ||
            activity.getIdentifier().equals(edge.getSource().getIdentifier()))).collect(Collectors.toList());
        if (activities.isEmpty()) {
          edgeToRemove.add(edge);
          continue;
        }
        if (!edgeToRemove.contains(edge)) {
          edgeToRemove.addAll(getEdgesDupplicated(malfunction.getParents(), edge));
        }
      }
    });
    if (!edgeToRemove.isEmpty()) {
      this.removeEdgeActivity(edgeToRemove, am);
      this.removeEdgeMalfunction(edgeToRemove, am);
    }
    return am;
  }

  private AssetModel validateActivityParent(AssetModel am) {
    List<Edge> edgeToRemove = new ArrayList<>();
    am.getBusinessActivities().forEach(activity -> {
      for (Edge edge : activity.getParents()) {
        List<BusinessProcess> processes = am.getBusinessProcesses().stream().filter(process ->
          (process.getIdentifier().equals(edge.getTarget().getIdentifier()) ||
            process.getIdentifier().equals(edge.getSource().getIdentifier()))).collect(Collectors.toList());

        if (processes.isEmpty()) {
          edgeToRemove.add(edge);
          continue;
        }
        if (!edgeToRemove.contains(edge)) {
          edgeToRemove.addAll(getEdgesDupplicated(activity.getParents(), edge));
        }
      }
    });
    if (!edgeToRemove.isEmpty()) {
      this.removeEdgeActivityProcess(edgeToRemove, am);
    }
    return am;
  }

  private AssetModel validateProcessParent(AssetModel am) {
    List<Edge> edgeToRemove = new ArrayList<>();
    am.getBusinessProcesses().forEach(process -> {
      for (Edge edge : process.getParents()) {
        List<Organization> organizations = am.getOrganizations().stream().filter(organization ->
          (organization.getIdentifier().equals(edge.getTarget().getIdentifier()) ||
            organization.getIdentifier().equals(edge.getSource().getIdentifier()))).collect(Collectors.toList());
        if (organizations.isEmpty()) {
          edgeToRemove.add(edge);
          continue;
        }
        if (!edgeToRemove.contains(edge)) {
          edgeToRemove.addAll(getEdgesDupplicated(process.getParents(), edge));
        }
      }
    });
    if (!edgeToRemove.isEmpty()) {
      this.removeEdgeProcessOrganization(edgeToRemove, am);
    }
    return am;
  }

  private AssetModel validateActivityChildrenNotExisting(AssetModel am) {
    List<Edge> activitiesChildrenNotExisting = new ArrayList<>();
    am.getBusinessActivities().forEach(activity ->

      activity.getChildren().forEach(child -> {

        List<Asset> assets = am.getAssets().stream().filter(asset ->
          (asset.getIdentifier().equals(child.getTarget().getIdentifier()) ||
            asset.getIdentifier().equals(child.getSource().getIdentifier()))).collect(Collectors.toList());

        if (assets.isEmpty()) {
          List<Malfunction> malfunctions = am.getMalfunctions().stream().filter(malfunction ->
            (malfunction.getIdentifier().equals(child.getTarget().getIdentifier()) ||
              malfunction.getIdentifier().equals(child.getSource().getIdentifier()))).collect(Collectors.toList());

          if (malfunctions.isEmpty()) {
            activitiesChildrenNotExisting.add(child);
          }
        }
      }));

    if (!activitiesChildrenNotExisting.isEmpty()) {
      this.removeEdgeActivity(activitiesChildrenNotExisting, am);
    }
    return am;
  }

  private AssetModel validateProcessChildrenNotExisting(AssetModel am) {
    List<Edge> processChildrenNotExisting = new ArrayList<>();
    am.getBusinessProcesses().forEach(process ->

      process.getChildren().forEach(child -> {
        List<BusinessActivity> activities = am.getBusinessActivities().stream().filter(activity ->
          (activity.getIdentifier().equals(child.getTarget().getIdentifier()) ||
            activity.getIdentifier().equals(child.getSource().getIdentifier()))).collect(Collectors.toList());
        if (activities.isEmpty()) {
          processChildrenNotExisting.add(child);
        }
      }));

    if (!processChildrenNotExisting.isEmpty()) {
      this.removeEdgeActivityProcess(processChildrenNotExisting, am);
    }
    return am;
  }

  private AssetModel validateOrganizationChildrenNotExisting(AssetModel am) {
    List<Edge> organizationChildrenNotExisting = new ArrayList<>();
    am.getOrganizations().forEach(organization ->

      organization.getChildren().forEach(child -> {
        List<BusinessProcess> processes = am.getBusinessProcesses().stream().filter(process ->
          (process.getIdentifier().equals(child.getTarget().getIdentifier()) ||
            process.getIdentifier().equals(child.getSource().getIdentifier()))).collect(Collectors.toList());
        if (processes.isEmpty()) {
          organizationChildrenNotExisting.add(child);
        }
      }));

    if (!organizationChildrenNotExisting.isEmpty()) {
      this.removeEdgeProcessOrganization(organizationChildrenNotExisting, am);
    }
    return am;
  }

  private List<Edge> getEdgesDupplicated(List<Edge> edges, Edge edge) {
    return edges.stream().filter(edge2 ->
      !edge.getIdentifier().equals(edge2.getIdentifier()) && edge.getTarget() != null && edge.getSource() != null &&
        edge.getTarget().getIdentifier() != null && edge.getSource().getIdentifier() != null &&
        edge2.getTarget().getIdentifier() != null && edge2.getSource().getIdentifier() != null &&
        ((edge.getTarget().getIdentifier().equals(edge2.getTarget().getIdentifier()) &&
          edge.getSource().getIdentifier().equals(edge2.getSource().getIdentifier())) ||
          (edge.getSource().getIdentifier().equals(edge2.getTarget().getIdentifier()) &&
            edge.getTarget().getIdentifier().equals(edge2.getSource().getIdentifier())))
    ).collect(Collectors.toList());
  }

  private void removeEdgeAsset(List<Edge> edgeToRemove, AssetModel am) {

    if (!edgeToRemove.isEmpty()) {
      edgeToRemove.forEach(edge ->
        LOG.info("removeEdgeAsset identifier {}, source {}, targe {}", edge.getIdentifier(),
          (edge.getSource() != null) ? edge.getSource().getIdentifier() : "source null",
          (edge.getTarget() != null) ? edge.getTarget().getIdentifier() : "target null"));
      am.getEdges().removeAll(edgeToRemove);
      am.getAssets().forEach(asset -> {
        asset.getParents().removeAll(edgeToRemove);
      });
    }
  }

  private void removeEdgeMalfunction(List<Edge> edgeToRemove, AssetModel am) {

    if (!edgeToRemove.isEmpty()) {
      edgeToRemove.forEach(edge ->
        LOG.info("removeEdgeMalfunction identifier {}, source {}, targe {}", edge.getIdentifier(),
          (edge.getSource() != null) ? edge.getSource().getIdentifier() : "source null",
          (edge.getTarget() != null) ? edge.getTarget().getIdentifier() : "target null"));
      am.getEdges().removeAll(edgeToRemove);
      am.getMalfunctions().forEach(malfunction -> {
        malfunction.getParents().removeAll(edgeToRemove);
      });
    }
  }

  private void removeEdgeActivity(List<Edge> edgeToRemove, AssetModel am) {

    if (!edgeToRemove.isEmpty()) {
      edgeToRemove.forEach(edge ->
        LOG.info("removeEdgeActivity identifier {}, source {}, targe {}", edge.getIdentifier(),
          (edge.getSource() != null) ? edge.getSource().getIdentifier() : "source null",
          (edge.getTarget() != null) ? edge.getTarget().getIdentifier() : "target null"));
      am.getEdges().removeAll(edgeToRemove);
      am.getBusinessActivities().forEach(activity -> {
        activity.getChildren().removeAll(edgeToRemove);
        activity.getParents().removeAll(edgeToRemove);
      });
    }
  }

  private void removeEdgeActivityProcess(List<Edge> edgeToRemove, AssetModel am) {

    if (!edgeToRemove.isEmpty()) {
      edgeToRemove.forEach(edge ->
        LOG.info("removeEdgeActivityProcess identifier {}, source {}, targe {}", edge.getIdentifier(),
          (edge.getSource() != null) ? edge.getSource().getIdentifier() : "source null",
          (edge.getTarget() != null) ? edge.getTarget().getIdentifier() : "target null"));
      am.getEdges().removeAll(edgeToRemove);
      am.getBusinessActivities().forEach(activity -> {
        activity.getChildren().removeAll(edgeToRemove);
        activity.getParents().removeAll(edgeToRemove);
      });
      am.getBusinessProcesses().forEach(process -> {
        process.getChildren().removeAll(edgeToRemove);
        process.getParents().removeAll(edgeToRemove);
      });
    }
  }

  private void removeEdgeProcessOrganization(List<Edge> edgeToRemove, AssetModel am) {

    if (!edgeToRemove.isEmpty()) {
      edgeToRemove.forEach(edge ->
        LOG.info("removeEdgeProcessOrganization identifier {}, source {}, targe {}", edge.getIdentifier(),
          (edge.getSource() != null) ? edge.getSource().getIdentifier() : "source null",
          (edge.getTarget() != null) ? edge.getTarget().getIdentifier() : "target null"));
      am.getEdges().removeAll(edgeToRemove);
      am.getBusinessProcesses().forEach(process -> {
        process.getChildren().removeAll(edgeToRemove);
        process.getParents().removeAll(edgeToRemove);
      });
      am.getOrganizations().forEach(organization -> {
        organization.getChildren().removeAll(edgeToRemove);
      });
    }
  }
}
