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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.Organization;

import java.lang.reflect.Type;
import java.util.ArrayList;

//This class manages the deserialization of AssetModel classes
//As can be seen, within this class all the other deserializators are called
class AssetModelInstanceCreator implements JsonDeserializer<AssetModel>, JsonSerializer<AssetModel> {

	public static final String OBJ_TYPE = "objType";
	public static final String ASSESSMENT_NODE = "assessmentNode";
	public static final String GOAL = "goal";
	public static final String DESCRIPTION = "description";
	public static final String NAME = "name";
	public static final String SYSTEM_PARTICIPANT_OWNER_ID = "systemParticipantOwnerId";
	public static final String IDENTIFIER = "identifier";
	public static final String NODE_TYPE = "nodeType";
	public static final String RELATED_REQUIREMENTS_IDS = "relatedRequirementsIds";
	public static final String CHILDREN = "children";
	public static final String PARENTS = "parents";
	public static final String CREATION_TIME = "creationTime";
	public static final String UPDATE_TIME = "updateTime";
	public static final String GRAPH_JSON = "graphJson";
	public static final String EDGES = "edges";
	public static final String NODES = "nodes";

	private void serializeNode(JsonObject nodeObject, Node node){
		
		
		if(node.getObjType() != null){
			nodeObject.addProperty(OBJ_TYPE, node.getObjType().toString());
		}
		else{
			nodeObject.addProperty(OBJ_TYPE, SESTObjectTypeEnum.AssetModel.toString());
		}
		
		nodeObject.addProperty(ASSESSMENT_NODE, node.isAssessmentNode());
		nodeObject.addProperty(GOAL, node.getGoal());
		nodeObject.addProperty(DESCRIPTION, node.getDescription());
		nodeObject.addProperty(NAME, node.getName());
		nodeObject.addProperty(SYSTEM_PARTICIPANT_OWNER_ID, node.getSystemParticipantOwnerId());
		nodeObject.addProperty(IDENTIFIER, node.getIdentifier());
		nodeObject.addProperty(NODE_TYPE, node.getNodeType().toString());
		

        JsonArray relatedRequirementsIds = new JsonArray();
		
        node.getRelatedRequirementsIds().forEach(item -> {
			
        	relatedRequirementsIds.add(item);
		});
	
        nodeObject.add(RELATED_REQUIREMENTS_IDS, relatedRequirementsIds);
        

        JsonArray children = new JsonArray();
		
        node.getChildren().forEach(item -> {
			
        	children.add(item.getIdentifier());
		});
	
        nodeObject.add(CHILDREN, children);
        

        JsonArray parents = new JsonArray();
		
        node.getParents().forEach(item -> {
			
        	parents.add(item.getIdentifier());
		});
		
        nodeObject.add(PARENTS, parents);

	}
	
	@Override
	public JsonElement serialize(AssetModel am, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(CREATION_TIME, am.getCreationTime());
		jsonObject.addProperty(UPDATE_TIME, am.getUpdateTime());
		jsonObject.addProperty(IDENTIFIER, am.getIdentifier());
		

		if(am.getObjType() != null){
			jsonObject.addProperty(OBJ_TYPE, am.getObjType().toString());
		}
		else{
			jsonObject.addProperty(OBJ_TYPE, SESTObjectTypeEnum.AssetModel.toString());
		}
		
		
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (am.getGraphJson(), JsonElement.class);
		JsonArray graphJson = element.getAsJsonArray();
		
		jsonObject.add(GRAPH_JSON, graphJson);
		
		
		JsonArray edges = new JsonArray();
		
		am.getEdges().forEach(item -> {
			JsonElement edge = context.serialize(item, Edge.class);
			
			if (edge != null) {
				edges.add(edge);
			}
		});
		
		jsonObject.add(EDGES, edges);
		
		
		JsonArray nodes = new JsonArray();
		
		for (Organization organization : am.getOrganizations()) {
			JsonObject orgObject = (JsonObject) context.serialize(organization, Organization.class);
			
			serializeNode(orgObject, organization);

			nodes.add(orgObject);
		}

		for (BusinessProcess bp : am.getBusinessProcesses()) {
			JsonObject orgObject = (JsonObject) context.serialize(bp, BusinessProcess.class);
			
			serializeNode(orgObject, bp);

			nodes.add(orgObject);
			
		}

		for (BusinessActivity ba : am.getBusinessActivities()) {
			JsonObject orgObject = (JsonObject) context.serialize(ba, BusinessActivity.class);
			
			serializeNode(orgObject, ba);

			nodes.add(orgObject);
			
		}

		for (Malfunction mal : am.getMalfunctions()) {
			JsonObject orgObject = (JsonObject) context.serialize(mal, Malfunction.class);
			
			serializeNode(orgObject, mal);

			nodes.add(orgObject);
			
		}

		for (Asset asset : am.getAssets()) {
			JsonObject orgObject = (JsonObject) context.serialize(asset, Asset.class);
			
			serializeNode(orgObject, asset);

			nodes.add(orgObject);
			
		}
		
		jsonObject.add(NODES, nodes);
		
		
		return jsonObject;
	}

	@Override
	public AssetModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		AssetModel am = new AssetModel();
		am.setEdges(new ArrayList<>());
		am.setOrganizations(new ArrayList<>());
		am.setBusinessProcesses(new ArrayList<>());
		am.setBusinessActivities(new ArrayList<>());
		am.setMalfunctions(new ArrayList<>());
		am.setAssets(new ArrayList<>());

		if (!jsonObject.get(CREATION_TIME).isJsonNull()) {
			am.setCreationTime(jsonObject.get(CREATION_TIME).getAsString());
		} else {
			am.setCreationTime("");
		}
		if (!jsonObject.get(UPDATE_TIME).isJsonNull()) {
			am.setUpdateTime(jsonObject.get(UPDATE_TIME).getAsString());
		} else {
			am.setUpdateTime("");
		}
		if (!jsonObject.get(IDENTIFIER).isJsonNull()) {
			am.setIdentifier(jsonObject.get(IDENTIFIER).getAsString());
		} else {
			am.setIdentifier("");
		}
		if (!jsonObject.get(OBJ_TYPE).isJsonNull()) {
			am.setObjType(AssetModelSerializatorDeserializatorCommon.getSESTObjecType(jsonObject.get(OBJ_TYPE).getAsString()));
		} else {
			am.setObjType(AssetModelSerializatorDeserializatorCommon.getSESTObjecType(""));
		}
		if (!jsonObject.get(GRAPH_JSON).isJsonNull()) {
			am.setGraphJson(jsonObject.get(GRAPH_JSON).toString());
		} else {
			am.setGraphJson("");
		}

		JsonArray edges = jsonObject.get(EDGES).getAsJsonArray();
		edges.forEach(item -> {
			Edge edge = context.deserialize(item, Edge.class);

			if (edge != null) {
				am.getEdges().add(edge);
			}
		});

		JsonArray nodes = jsonObject.get(NODES).getAsJsonArray();
		nodes.forEach(item -> {
			// JsonElement obj = (JsonElement) item;
			// Node node = context.deserialize(obj, Node.class);

			// Here we should call the deserializator of the Node class. In
			// order to improve performances, however, we directly deserialize
			// the nodes here \
			// (since we already deserialized the Edges, it is faster to
			// directly deserialize the Nodes and fill their Children and
			// Parents here
			JsonObject jsonNode = item.getAsJsonObject();

			Node node = null;

			if (!jsonNode.get(NODE_TYPE).isJsonNull()) {
				String nodeType = jsonNode.get(NODE_TYPE).getAsString();

				if (nodeType.equals(NodeTypeEnum.Organization.toString())) {
					node = context.deserialize(jsonNode, Organization.class);
				} else if (nodeType.equals(NodeTypeEnum.BusinessProcess.toString())) {
					node = context.deserialize(jsonNode, BusinessProcess.class);
				} else if (nodeType.equals(NodeTypeEnum.BusinessActivity.toString())) {
					node = context.deserialize(jsonNode, BusinessActivity.class);
				} else if (nodeType.equals(NodeTypeEnum.Asset.toString())) {
					node = context.deserialize(jsonNode, Asset.class);
				} else if (nodeType.equals(NodeTypeEnum.Malfunction.toString())) {
					node = context.deserialize(jsonNode, Malfunction.class);
				}
			} else {
				node = new Node();
			}

			node.setObjType(SESTObjectTypeEnum.AssetModel);

			if (!jsonNode.get(ASSESSMENT_NODE).isJsonNull()) {
				node.setAssessmentNode(jsonNode.get(ASSESSMENT_NODE).getAsBoolean());
			} else {
				node.setAssessmentNode(false);
			}

			if (!jsonNode.get(GOAL).isJsonNull()) {
				node.setGoal(jsonNode.get(GOAL).getAsString());
			} else {
				node.setGoal("");
			}

			if (!jsonNode.get(DESCRIPTION).isJsonNull()) {
				node.setDescription(jsonNode.get(DESCRIPTION).getAsString());
			} else {
				node.setDescription("");
			}

			if (!jsonNode.get(NAME).isJsonNull()) {
				node.setName(jsonNode.get(NAME).getAsString());
			} else {
				node.setName("");
			}

			if (!jsonNode.get(SYSTEM_PARTICIPANT_OWNER_ID).isJsonNull()) {
				node.setSystemParticipantOwnerId(jsonNode.get(SYSTEM_PARTICIPANT_OWNER_ID).getAsString());
			} else {
				node.setSystemParticipantOwnerId("");
			}

			if (!jsonNode.get(IDENTIFIER).isJsonNull()) {
				node.setIdentifier(jsonNode.get(IDENTIFIER).getAsString());
			} else {
				node.setIdentifier("");
			}

			ArrayList<String> relatedRequirementsIdsArrayList = new ArrayList<String>();
			if (!jsonNode.get(RELATED_REQUIREMENTS_IDS).isJsonNull()) {
				JsonArray relatedRequirementsIds = jsonNode.get(RELATED_REQUIREMENTS_IDS).getAsJsonArray();

				relatedRequirementsIds.forEach(itemString -> {
					String relatedRequirementsId = itemString.getAsString();

					relatedRequirementsIdsArrayList.add(relatedRequirementsId);
				});
			}

			node.setRelatedRequirementsIds(relatedRequirementsIdsArrayList);

			ArrayList<Edge> childrenList = new ArrayList<>();
			if (!jsonNode.get(CHILDREN).isJsonNull()) {
				JsonArray children = jsonNode.get(CHILDREN).getAsJsonArray();
				children.forEach(itemChild -> {
					String child = itemChild.getAsString();

					for (Edge edge : am.getEdges()) {
						if (edge.getIdentifier().equals(child)) {
							childrenList.add(edge);
							break;
						}
					}
				});
			}
			node.setChildren(childrenList);

			ArrayList<Edge> parentsList = new ArrayList<>();
			if (!jsonNode.get(PARENTS).isJsonNull()) {
				JsonArray parents = jsonNode.get(PARENTS).getAsJsonArray();
				parents.forEach(itemParent -> {
					String parent = itemParent.getAsString();

					for (Edge edge : am.getEdges()) {
						if (edge.getIdentifier().equals(parent)) {
							parentsList.add(edge);
							break;
						}
					}

				});
			}
			node.setParents(parentsList);

			if (node.getNodeType().equals(NodeTypeEnum.Organization)) {
				am.getOrganizations().add((Organization) node);
			}
			if (node.getNodeType().equals(NodeTypeEnum.BusinessProcess)) {
				am.getBusinessProcesses().add((BusinessProcess) node);
			}
			if (node.getNodeType().equals(NodeTypeEnum.BusinessActivity)) {
				am.getBusinessActivities().add((BusinessActivity) node);
			}
			if (node.getNodeType().equals(NodeTypeEnum.Malfunction)) {
				am.getMalfunctions().add((Malfunction) node);
			}
			if (node.getNodeType().equals(NodeTypeEnum.Asset)) {
				am.getAssets().add((Asset) node);
			}

		});

		// Once all the deserializations are done, we need to put the correct
		// sources and targets to the Edges
		am.getEdges().forEach(edge -> {

			String source = edge.getSource().getIdentifier();
			String target = edge.getTarget().getIdentifier();

			boolean sourceFound = false;
			boolean targetFound = false;

			for (Organization organization : am.getOrganizations()) {
				if (organization.getIdentifier().equals(source)) {
					edge.setSource(organization);
					sourceFound = true;
				}
			}

			for (BusinessProcess bp : am.getBusinessProcesses()) {
				if (!sourceFound) {
					if (bp.getIdentifier().equals(source)) {
						edge.setSource(bp);
						sourceFound = true;
					}
				}
				if (!targetFound) {
					if (bp.getIdentifier().equals(target)) {
						edge.setTarget(bp);
						targetFound = true;
					}
				}
			}

			for (BusinessActivity ba : am.getBusinessActivities()) {
				if (!sourceFound) {
					if (ba.getIdentifier().equals(source)) {
						edge.setSource(ba);
						sourceFound = true;
					}
				}
				if (!targetFound) {
					if (ba.getIdentifier().equals(target)) {
						edge.setTarget(ba);
						targetFound = true;
					}
				}
			}

			for (Malfunction mal : am.getMalfunctions()) {
				if (!sourceFound) {
					if (mal.getIdentifier().equals(source)) {
						edge.setSource(mal);
						sourceFound = true;
					}
				}
				if (!targetFound) {
					if (mal.getIdentifier().equals(target)) {
						edge.setTarget(mal);
						targetFound = true;
					}
				}
			}

			for (Asset asset : am.getAssets()) {
				if (!sourceFound) {
					if (asset.getIdentifier().equals(source)) {
						edge.setSource(asset);
						sourceFound = true;
					}
				}
				if (!targetFound) {
					if (asset.getIdentifier().equals(target)) {
						edge.setTarget(asset);
						targetFound = true;
					}
				}
			}

			

		});

		return am;
	}

}
