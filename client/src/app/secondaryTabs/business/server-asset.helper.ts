/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="server-asset.helper.ts.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {UUID} from 'angular2-uuid';

export class ServerAssetHelper {

  public static retrieveOrganizations(serverAsset: any) {
    const allOrganizations: any[] = serverAsset.nodes.filter(node => node.nodeType === 'Organization');

    return allOrganizations;
  }

  public static retrieveBusinessProcessesMap(serverAsset: any): Map<any, any[]> {
    const businessProcessesMap = new Map<any, any[]>();
    const allProcesses: any[] = serverAsset.nodes.filter(node => node.nodeType === 'BusinessProcess');

    allProcesses.forEach(p => {
      const organisations: any[] = [];
      p.parents.forEach(parent => {
        organisations.push(this.traverseFromChildrenToParent(p, parent, serverAsset));
      });

      businessProcessesMap.set(p, organisations);
    });

    return businessProcessesMap;
  }

  /*public static retrieveActivitiesMap(organisation: any, serverAsset: any): Map<any, any[]> {
    const activitiesMap = new Map<any, any[]>();
    const businessProcesses: any[] = this.getAllBusinessProcessesForOrganisation(organisation, serverAsset);
    businessProcesses.forEach(bp => {
      const businessProcessEdges: any[] = serverAsset.edges.filter(edge => edge.source === bp.identifier);
      businessProcessEdges.forEach(bpe => {
        const businessProcessEdgeTarget = bpe.target;
        const activityPointedToThisBusinessProcess = serverAsset.nodes.filter( node => node.nodeType === 'BusinessActivity' &&
          node.identifier === businessProcessEdgeTarget)[0];
        if (activitiesMap.has(activityPointedToThisBusinessProcess)) {
          activitiesMap.get(activityPointedToThisBusinessProcess).push(bp);
        } else {
          const bpArray: any[] = [];
          bpArray.push(bp);
          activitiesMap.set(activityPointedToThisBusinessProcess, bpArray);
        }
      });
    });
    serverAsset.nodes.filter( node => node.nodeType === 'BusinessActivity').filter(n => n.parents.length === 0)
      .forEach( orphan => activitiesMap.set(orphan, []));
    return activitiesMap;
  }*/

  public static retrieveActivitiesMap(serverAsset: any): Map<any, any[]> {
    const activitiesMap = new Map<any, any[]>();
    const allActivities: any[] = this.getAllBusinessActivities(serverAsset);

    let count = 0;
    allActivities.forEach(activity => {
      const processes: any[] = [];
      activity.parents.forEach(parent => {
        processes.push(this.traverseFromChildrenToParent(activity, parent, serverAsset));
      });
      activitiesMap.set(activity, processes);
    });

    return activitiesMap;
  }

  /*public static retrieveAssetsMap(organisation: any, serverAsset: any): Map<any, any[]> {
    const assetsMap = new Map<any, any[]>();
    const allAssets: any[] = this.getAllAssetsForOrganisation(organisation, serverAsset);
    allAssets.forEach(asset => {
      const activities: any[] = [];
        asset.parents.forEach(parent => {
          activities.push(this.traverseFromChildrenToParent(parent, serverAsset));
        });
        assetsMap.set(asset, activities);
    });
    return assetsMap;
  }*/

  public static retrieveAssetsMap(serverAsset: any): Map<any, any[]> {
    const assetsMap = new Map<any, any[]>();
    const allAssets: any[] = this.getAllAssets(serverAsset);

    allAssets.forEach(asset => {
      const activities: any[] = [];
      asset.parents.forEach(parent => {
        activities.push(this.traverseFromChildrenToParent(asset, parent, serverAsset));
      });
      assetsMap.set(asset, activities);
    });
    return assetsMap;
  }

  /*public static retrieveMalfunctionsMap(organisation: any, serverAsset: any): Map<any, any[]> {
    const malfunctionsMap = new Map<any, any[]>();
    const allMafunctions: any[] = this.getAllMalfunctionsForOrganisation(organisation, serverAsset);
    allMafunctions.forEach(malfunction => {
      const activitities: any[] = [];
      malfunction.parents.forEach(parent => {
        activitities.push(this.traverseFromChildrenToParent(parent, serverAsset)); // change to set
      });
      malfunctionsMap.set(malfunction, activitities);
    });
    return malfunctionsMap;
  }*/

  public static retrieveMalfunctionsMap(serverAsset: any): Map<any, any[]> {
    const malfunctionsMap = new Map<any, any[]>();
    const allMalfunctions: any[] = this.getAllMalfunctions(serverAsset);

    allMalfunctions.forEach(malfunction => {
      const activitities: any[] = [];
      malfunction.parents.forEach(parent => {
        activitities.push(this.traverseFromChildrenToParent(malfunction, parent, serverAsset)); // change to set
      });
      malfunctionsMap.set(malfunction, activitities);
    });
    return malfunctionsMap;
  }

  /*public static getAllBusinessProcessesForOrganisation(organisation: any, serverAsset: any): any[] {
    const organisationIdentifier = organisation.identifier;
    const organizationEdges: any[] = serverAsset.edges.filter( edge => edge.source === organisationIdentifier).
    map(target => target.target);
    // all business processes for organisation
    const businessProcesses: any[] = serverAsset.nodes.filter( node => organizationEdges.includes(node.identifier));
    serverAsset.nodes.filter( node => node.nodeType === 'BusinessProcess').filter(n => n.parents.length === 0)
      .forEach( orphan => businessProcesses.push(orphan));
    return businessProcesses;
  }*/

  public static getAllBusinessProcesses(serverAsset: any): any[] {
    const allBusinessProcesses: any[] = serverAsset.nodes.filter(node => node.nodeType === 'BusinessProcess');

    return allBusinessProcesses;
  }


  /*public static getAllBusinessActivitiesForOrganisation(organisation: any, serverAsset: any): any[] {
    const businessActivities = new Set<any>();
    const businessProcesses: any[] = this.getAllBusinessProcessesForOrganisation(organisation, serverAsset);
    businessProcesses.forEach(bp => {
      bp.children.forEach(bpId => businessActivities.add(
        this.traverseFromParentToChildren(bpId, serverAsset)));
    });
    serverAsset.nodes.filter( node => node.nodeType === 'BusinessActivity').filter(n => n.parents.length === 0)
      .forEach( orphan => businessActivities.add(orphan));
    return [...businessActivities];
  }*/

  public static getAllBusinessActivities(serverAsset: any): any[] {
    const allBusinessActivities: any[] = serverAsset.nodes.filter(node => node.nodeType === 'BusinessActivity');

    return allBusinessActivities;
  }

  public static getBusinessActivityChildren(serverAsset: any): any[] {
    const children = new Set<any>();
    const activities: any[] = this.getAllBusinessActivities(serverAsset);
    activities.forEach(a => {
      a.children.forEach(activityId => children.add(
        this.traverseFromParentToChildren(a, activityId, serverAsset)));
    });
    return [...children];
  }

  /*public static getAllAssetsForOrganisation(organisation: any, serverAsset: any): any[] {
    return this.getBusinessActivityChildren(organisation, serverAsset).filter(a => a.nodeType === 'Asset');
  }*/

  public static getAllAssets(serverAsset: any): any[] {
    const allAssets: any[] = serverAsset.nodes.filter(node => node.nodeType === 'Asset');

    return allAssets;
  }

  /*public static getAllMalfunctionsForOrganisation(organisation: any, serverAsset: any): any[] {
    return this.getBusinessActivityChildren(organisation, serverAsset).filter(a => a.nodeType === 'Malfunction');
  }*/

  public static getAllMalfunctions(serverAsset: any): any[] {
    const allMalfunctions: any[] = serverAsset.nodes.filter(node => node.nodeType === 'Malfunction');

    return allMalfunctions;
  }

  //This function retrieves the parent node of a children
  public static traverseFromChildrenToParent(nodeChild, edgeIdentifier: string, serverAsset: any): any {
    const parentEdge: any = serverAsset.edges.filter(edge => edge.identifier === edgeIdentifier)[0];

    if (parentEdge) {
      const sourceNode = serverAsset.nodes.filter(node => node.identifier === parentEdge.source)[0];
      const targetNode = serverAsset.nodes.filter(node => node.identifier === parentEdge.target)[0];

      if (sourceNode && sourceNode.identifier === nodeChild.identifier) {
        return targetNode;
      } else {
        return sourceNode;
      }
    } else {
      console.log('ERROR : parentEdge undefined! ' + edgeIdentifier);
      console.log(serverAsset);
      console.log(nodeChild);
    }
    //return serverAsset.nodes.filter(node => node.identifier === parentEdge.source)[0];
  }

  //This function retrieves the children node of a parent
  public static traverseFromParentToChildren(nodeParent, edgeIdentifier: string, serverAsset: any): any {
    const childEdge: any = serverAsset.edges.filter(edge => edge.identifier === edgeIdentifier)[0];
    //return serverAsset.nodes.filter(node => node.identifier === childEdge.target)[0];

    if (childEdge) {
      const sourceNode = serverAsset.nodes.filter(node => node.identifier === childEdge.source)[0];
      const targetNode = serverAsset.nodes.filter(node => node.identifier === childEdge.target)[0];

      if (targetNode && targetNode.identifier === nodeParent.identifier) {
        return sourceNode;
      } else {
        return targetNode;
      }
    } else {
      console.log('ERROR : childEdge undefined! ' + edgeIdentifier);
      console.log(serverAsset);
      console.log(nodeParent);
    }
  }

  // param - id of child (edge)
  public static createMalfunctionNode(
    name: string,
    technicalDescription: string,
    functionalDescription: string,
    technicalConsequence: string,
    functionalConsequence: string,
    technicalTypes: string[],
    functionalType: string,
    scales: any[]
  ): any {
    return {
      'assetCategory': null,
      'functionalConsequence': functionalConsequence,
      'functionalDescription': functionalDescription,
      'functionalType': functionalType,
      'technicalConsequence': technicalConsequence,
      'technicalDescription': technicalDescription,
      'weight': 0.0,
      'technicalTypes': technicalTypes,
      'scales': scales,
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': name,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': UUID.UUID(),
      'objType': 'AssetModel',
      'nodeType': 'Malfunction'
    };
  }

  public static createAssetNode(name: string, description: string, ownerId: string): any {
    return {
      'category': null,
      'cost': 0,
      'primaryCategories': [],
      'businessImpacts': null,
      'securityImpacts': [],
      'malfunctionsIds': [],
      'assessmentNode': true,
      'description': description,
      'goal': null,
      'name': name,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': ownerId,
      'children': [],
      'parents': [],
      'identifier': UUID.UUID(),
      'objType': 'AssetModel',
      'nodeType': 'Asset'
    };
  }

  public static createActivityNode(name: string, description: string, ownerId: string, goal: string): any {
    return {
      'objType': 'AssetModel',
      'assessmentNode': true,
      'goal': goal,
      'description': description,
      'name': name,
      'systemParticipantOwnerId': ownerId,
      'identifier': UUID.UUID(),
      'nodeType': 'BusinessActivity',
      'relatedRequirementsIds': [],
      'children': [],
      'parents': []
    };
  }

  public static createOrganizationNode(name: string, description: string, ownerId: string, goal: string): any {
    return {
      'objType': 'AssetModel',
      'assessmentNode': true,
      'goal': goal,
      'description': description,
      'name': name,
      'systemParticipantOwnerId': ownerId,
      'identifier': UUID.UUID(),
      'nodeType': 'Organization',
      'relatedRequirementsIds': [],
      'children': [],
      'parents': []
    };
  }

  public static createEdgeBetweenChildAndParent(child: any, parent: any): any {
    return {
      'operationalWeight': 0,
      'securityImpacts': [],
      'source': parent.identifier,
      'target': child.identifier,
      'businessImpactWeights': null,
      'identifier': UUID.UUID(),
      'objType': 'AssetModel'
    };
  }

  public static createBusinessProcessNode(name: string, description: string, goal: string, ownerId: string, type: string): any {
    return {
      'objType': 'AssetModel',
      'assessmentNode': true,
      'goal': goal,
      //'type': type,
      'businessType': type, //type will be substituted by '[Assets Component] Store Process', which is a construct to activate the Actions. We need to temporary store the real data on and additional field, to be converted when we save the model
      'description': description,
      'name': name,
      'systemParticipantOwnerId': ownerId,
      'identifier': UUID.UUID(),
      'nodeType': 'BusinessProcess',
      'relatedRequirementsIds': [],
      'children': [],
      'parents': []
    };
  }

  public static addRelationsToParent(parent: any, relations: string[]): any {
    return parent.children.push(...relations);
  }

  public static addRelationsToChild(child: any, relations: string[]): any {
    child.parents.push(...relations);
  }

  public static findNodeByName(findName: string, serverAsset: any): any {
    return serverAsset.nodes.find(node => node.name === findName);
  }

  public static findNodeByIdentifier(identifier: string, serverAsset: any): any {
    return serverAsset.nodes.find(node => node.identifier === identifier);
  }

  // child node identifier TODO to be checked
  public static removeEdgeByChildId(childId: string, serverAsset: any): any {
    const edgeToDelete = serverAsset.edges.find(e => e.target === childId);
    serverAsset.edges = serverAsset.edges.filter(e => e.target !== childId);
    return edgeToDelete;
  }

  public static removeNodeById(nodeId: string, serverAsset: any): any {
    const nodeToDelete = serverAsset.nodes.find(n => n.identifier === nodeId);
    serverAsset.nodes = serverAsset.nodes.filter(n => n.identifier !== nodeId);
    return nodeToDelete;
  }

  public static removeMalfunctionFromAssets(malfunctionId: string, serverAsset: any): any {
    serverAsset.nodes.forEach(node => {
      if (node.nodeType === 'Asset') {
        node.malfunctionIds = node.malfunctionIds.filter(e => e !== malfunctionId);
      }
    });
  }

  public static removeMalfunctionFromAsset(malfunction: any, asset: any) {
    asset.malfunctionsIds = asset.malfunctionsIds.filter(e => e !== malfunction.identifier);
  }

  public static removeEdgeById(edgeId: string, serverAsset: any): any {
    const edgeToDelete = serverAsset.edges.find(e => e.identifier === edgeId);
    serverAsset.edges = serverAsset.edges.filter(e => e.identifier !== edgeId);
    return edgeToDelete;
  }

  //We remove from all the nodes of the ServerAsset all the parents edges with a specific ID
  public static removeEdgeFromChildren(edgeId: string, serverAsset: any): any {
    serverAsset.nodes.forEach(node => {
      node.parents = node.parents.filter(e => e !== edgeId);
    });
  }

  //We remove from all the nodes of the ServerAsset all the children edges with a specific ID
  public static removeEdgeFromParents(edgeId: string, serverAsset: any): any {
    serverAsset.nodes.forEach(node => {
      node.children = node.children.filter(e => e !== edgeId);
    });
  }

  //TODO to check
  public static findEdgeByParentAndChild(parentId: string, childId: string, serverAsset: any): any {
    serverAsset.edges.forEach(edge => {
      if (edge.source === parentId && edge.target === childId) {
        return edge;
      } else if (edge.source === childId && edge.target === parentId) {
        return edge;
      }
    });
    return null;
  }

  public static findChildrenEdges(nodeId: string, serverAsset: any): any {
    return serverAsset.nodes.find(node => node.identifier === nodeId).children;
  }

  public static findParentsEdges(nodeId: string, serverAsset: any): any {
    return serverAsset.nodes.find(node => node.identifier === nodeId).parents;
  }

  // it returns the maximum impact
  public static maxImpactValue(newValue, oldValue): string {
    if (newValue === null) {
      return oldValue;
    }
    if (oldValue === null) {
      return newValue;
    }

    if (newValue === 'CRITICAL') {
      return 'CRITICAL';
    }

    if (oldValue === 'CRITICAL') {
      return 'CRITICAL';
    }

    if (newValue === 'HIGH') {
      return 'HIGH';
    }

    if (oldValue === 'HIGH') {
      return 'HIGH';
    }

    if (newValue === 'MEDIUM') {
      return 'MEDIUM';
    }

    if (oldValue === 'MEDIUM') {
      return 'MEDIUM';
    }
    return 'LOW';
  }

  public static findAssetsWithMalfunction(malfunction: any, serverAsset: any): any[] {
    const allAssets: any[] = this.getAllAssets(serverAsset);

    let assets = [];
    for (let asset of allAssets) {
      for (const malfunctionId in asset.malfunctionsIds) {
        if (asset.malfunctionsIds[malfunctionId] === malfunction.identifier) {
          assets.push(asset);
          break;
        }
      }

    }
    return assets;
  }

  // to associate the impact values to an asset depending on its Malfunctions
  public static associateImpactToAsset(asset: any, serverAsset: any) {
    asset.securityImpacts = [];

    let assetAvailability = null;
    let assetEfficiency = null;
    let assetConfidentiality = null;
    let assetIntegrity = null;

    let malfunctions = new Set<any>();

    for (const malfunctionId in asset.malfunctionsIds) {
      const malfunction = this.findNodeByIdentifier(asset.malfunctionsIds[malfunctionId], serverAsset);
      malfunctions.add(malfunction);
    }
    if (malfunctions.size === 0) {
      return;
    }

    for (let malf of malfunctions) {
      if (malf.technicalTypes.count === 0) {
        continue;
      }
      for (const techType in malf.technicalTypes) {
        if (malf.technicalTypes[techType] === 'Availability_Loss') {
          for (const scale in malf.scales) {
            assetAvailability = this.maxImpactValue(malf.scales[scale].seriousness, assetAvailability);
          }

        }
        if (malf.technicalTypes[techType] === 'Integrity_Loss') {
          for (const scale in malf.scales) {
            assetIntegrity = this.maxImpactValue(malf.scales[scale].seriousness, assetIntegrity);
          }
        }
        if (malf.technicalTypes[techType] === 'Confidentiality_Loss') {
          for (const scale in malf.scales) {
            assetConfidentiality = this.maxImpactValue(malf.scales[scale].seriousness, assetConfidentiality);
          }
        }
        if (malf.technicalTypes[techType] === 'Efficiency_Loss') {
          for (const scale in malf.scales) {
            assetEfficiency = this.maxImpactValue(malf.scales[scale].seriousness, assetEfficiency);
          }
        }
      }

    }

    if (assetAvailability != null) {
      asset.securityImpacts.push({
        impact: assetAvailability,
        scope: 'Availability',
        tecnicalImpacts: []
      });
    }

    if (assetIntegrity != null) {
      asset.securityImpacts.push({
        impact: assetIntegrity,
        scope: 'Integrity',
        tecnicalImpacts: []
      });
    }

    if (assetConfidentiality != null) {
      asset.securityImpacts.push({
        impact: assetConfidentiality,
        scope: 'Confidentiality',
        tecnicalImpacts: []
      });
    }

    if (assetEfficiency != null) {
      asset.securityImpacts.push({
        impact: assetEfficiency,
        scope: 'Efficiency',
        tecnicalImpacts: []
      });
    }
  }

  // to associate the impact values to the edges of an asset depending on its Malfunctions and Its Impacts
  //this method is not really useful, since the Impact on the Edges is used only in the Graphical View
  //Hence we decided to not develop it for the moment (the Graphical View can color its edges automatically)
  public static associateImpactToAssetEdges(asset: any, serverAsset: any) {

  }

  public static getActivityRelatedAssets(activity: any, serverAsset: any): any[] {
    let children = [];
    let assets = [];

    activity.children.forEach(edgeId => children.push(
      this.traverseFromParentToChildren(activity, edgeId, serverAsset)));


    for (let child of children) {
      if (child.nodeType === 'Asset') {
        assets.push(child);
      }
    }
    return assets;
  }
}

