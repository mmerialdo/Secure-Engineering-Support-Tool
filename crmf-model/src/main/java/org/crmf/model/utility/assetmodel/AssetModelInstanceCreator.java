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

import java.lang.reflect.Type;
import java.util.ArrayList;

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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the deserialization of AssetModel classes
//As can be seen, within this class all the other deserializators are called
class AssetModelInstanceCreator implements JsonDeserializer<AssetModel>, JsonSerializer<AssetModel> {
	
	private void serializeNode(JsonObject nodeObject, Node node){
		
		
		if(node.getObjType() != null){
			nodeObject.addProperty("objType", node.getObjType().toString());
		}
		else{
			nodeObject.addProperty("objType", SESTObjectTypeEnum.AssetModel.toString());
		}
		
		nodeObject.addProperty("assessmentNode", node.isAssessmentNode());
		nodeObject.addProperty("goal", node.getGoal());
		nodeObject.addProperty("description", node.getDescription());
		nodeObject.addProperty("name", node.getName());
		nodeObject.addProperty("systemParticipantOwnerId", node.getSystemParticipantOwnerId());
		nodeObject.addProperty("identifier", node.getIdentifier());
		nodeObject.addProperty("nodeType", node.getNodeType().toString());
		

        JsonArray relatedRequirementsIds = new JsonArray();
		
        node.getRelatedRequirementsIds().forEach(item -> {
			
        	relatedRequirementsIds.add(item);
		});
	
        nodeObject.add("relatedRequirementsIds", relatedRequirementsIds);
        

        JsonArray children = new JsonArray();
		
        node.getChildren().forEach(item -> {
			
        	children.add(item.getIdentifier());
		});
	
        nodeObject.add("children", children);
        

        JsonArray parents = new JsonArray();
		
        node.getParents().forEach(item -> {
			
        	parents.add(item.getIdentifier());
		});
		
        nodeObject.add("parents", parents);

	}
	
	@Override
	public JsonElement serialize(AssetModel am, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty("creationTime", am.getCreationTime());
		jsonObject.addProperty("updateTime", am.getUpdateTime());
		jsonObject.addProperty("identifier", am.getIdentifier());
		

		if(am.getObjType() != null){
			jsonObject.addProperty("objType", am.getObjType().toString());
		}
		else{
			jsonObject.addProperty("objType", SESTObjectTypeEnum.AssetModel.toString());
		}
		
		
		Gson gson = new Gson();
		JsonElement element = gson.fromJson (am.getGraphJson(), JsonElement.class);
		JsonArray graphJson = element.getAsJsonArray();
		
		jsonObject.add("graphJson", graphJson);
		
		
		JsonArray edges = new JsonArray();
		
		am.getEdges().forEach(item -> {
			JsonElement edge = context.serialize(item, Edge.class);
			
			if (edge != null) {
				edges.add(edge);
			}
		});
		
		jsonObject.add("edges", edges);
		
		
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
		
		jsonObject.add("nodes", nodes);
		
		
		return jsonObject;
	}

	@Override
	public AssetModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		AssetModel am = new AssetModel();
		am.setEdges(new ArrayList<Edge>());
		am.setOrganizations(new ArrayList<Organization>());
		am.setBusinessProcesses(new ArrayList<BusinessProcess>());
		am.setBusinessActivities(new ArrayList<BusinessActivity>());
		am.setMalfunctions(new ArrayList<Malfunction>());
		am.setAssets(new ArrayList<Asset>());

		if (!jsonObject.get("creationTime").isJsonNull()) {
			am.setCreationTime(jsonObject.get("creationTime").getAsString());
		} else {
			am.setCreationTime("");
		}
		if (!jsonObject.get("updateTime").isJsonNull()) {
			am.setUpdateTime(jsonObject.get("updateTime").getAsString());
		} else {
			am.setUpdateTime("");
		}
		if (!jsonObject.get("identifier").isJsonNull()) {
			am.setIdentifier(jsonObject.get("identifier").getAsString());
		} else {
			am.setIdentifier("");
		}
		if (!jsonObject.get("objType").isJsonNull()) {
			am.setObjType(AssetModelSerializatorDeserializatorCommon.getSESTObjecType(jsonObject.get("objType").getAsString()));
		} else {
			am.setObjType(AssetModelSerializatorDeserializatorCommon.getSESTObjecType(""));
		}
		if (!jsonObject.get("graphJson").isJsonNull()) {
			am.setGraphJson(jsonObject.get("graphJson").toString());
		} else {
			am.setGraphJson("");
		}

		JsonArray edges = jsonObject.get("edges").getAsJsonArray();
		edges.forEach(item -> {
			JsonElement obj = (JsonElement) item;
			Edge edge = context.deserialize(obj, Edge.class);

			if (edge != null) {
				am.getEdges().add(edge);
			}
		});

		JsonArray nodes = jsonObject.get("nodes").getAsJsonArray();
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

			if (!jsonNode.get("nodeType").isJsonNull()) {
				String nodeType = jsonNode.get("nodeType").getAsString();

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

			if (!jsonNode.get("assessmentNode").isJsonNull()) {
				node.setAssessmentNode(jsonNode.get("assessmentNode").getAsBoolean());
			} else {
				node.setAssessmentNode(false);
			}

			if (!jsonNode.get("goal").isJsonNull()) {
				node.setGoal(jsonNode.get("goal").getAsString());
			} else {
				node.setGoal("");
			}

			if (!jsonNode.get("description").isJsonNull()) {
				node.setDescription(jsonNode.get("description").getAsString());
			} else {
				node.setDescription("");
			}

			if (!jsonNode.get("name").isJsonNull()) {
				node.setName(jsonNode.get("name").getAsString());
			} else {
				node.setName("");
			}

			if (!jsonNode.get("systemParticipantOwnerId").isJsonNull()) {
				node.setSystemParticipantOwnerId(jsonNode.get("systemParticipantOwnerId").getAsString());
			} else {
				node.setSystemParticipantOwnerId("");
			}

			if (!jsonNode.get("identifier").isJsonNull()) {
				node.setIdentifier(jsonNode.get("identifier").getAsString());
			} else {
				node.setIdentifier("");
			}

			ArrayList<String> relatedRequirementsIdsArrayList = new ArrayList<String>();
			if (!jsonNode.get("relatedRequirementsIds").isJsonNull()) {
				JsonArray relatedRequirementsIds = jsonNode.get("relatedRequirementsIds").getAsJsonArray();

				relatedRequirementsIds.forEach(itemString -> {
					String relatedRequirementsId = itemString.getAsString();

					relatedRequirementsIdsArrayList.add(relatedRequirementsId);
				});
			}

			node.setRelatedRequirementsIds(relatedRequirementsIdsArrayList);

			ArrayList<Edge> childrenList = new ArrayList<Edge>();
			if (!jsonNode.get("children").isJsonNull()) {
				JsonArray children = jsonNode.get("children").getAsJsonArray();
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

			ArrayList<Edge> parentsList = new ArrayList<Edge>();
			if (!jsonNode.get("parents").isJsonNull()) {
				JsonArray parents = jsonNode.get("parents").getAsJsonArray();
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
