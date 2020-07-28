/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="NodeInstanceCreator.java"
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
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.BusinessActivity;
import org.crmf.model.riskassessmentelements.BusinessProcess;
import org.crmf.model.riskassessmentelements.Edge;
import org.crmf.model.riskassessmentelements.Malfunction;
import org.crmf.model.riskassessmentelements.Node;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.Organization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//This class manages the deserialization of Node classes. This class is not currently used
class NodeInstanceCreator implements JsonDeserializer<Node> {

	@Override
	public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Node newNode = new Node();

		if (!jsonObject.get("nodeType").isJsonNull()) {
			String nodeType = jsonObject.get("nodeType").getAsString();

			if (nodeType.equals(NodeTypeEnum.Organization.toString())) {
				newNode = context.deserialize(jsonObject, Organization.class);
			} else if (nodeType.equals(NodeTypeEnum.BusinessProcess.toString())) {
				newNode = context.deserialize(jsonObject, BusinessProcess.class);
			} else if (nodeType.equals(NodeTypeEnum.BusinessActivity.toString())) {
				newNode = context.deserialize(jsonObject, BusinessActivity.class);
			} else if (nodeType.equals(NodeTypeEnum.Asset.toString())) {
				newNode = context.deserialize(jsonObject, Asset.class);
			} else if (nodeType.equals(NodeTypeEnum.Malfunction.toString())) {
				newNode = context.deserialize(jsonObject, Malfunction.class);
			}
		}

		newNode.setObjType(SESTObjectTypeEnum.AssetModel);

		if (!jsonObject.get("assessmentNode").isJsonNull()) {
			newNode.setAssessmentNode(jsonObject.get("assessmentNode").getAsBoolean());
		} else {
			newNode.setAssessmentNode(false);
		}

		if (!jsonObject.get("goal").isJsonNull()) {
			newNode.setGoal(jsonObject.get("goal").getAsString());
		} else {
			newNode.setGoal("");
		}

		if (!jsonObject.get("description").isJsonNull()) {
			newNode.setDescription(jsonObject.get("description").getAsString());
		} else {
			newNode.setDescription("");
		}

		if (!jsonObject.get("name").isJsonNull()) {
			newNode.setName(jsonObject.get("name").getAsString());
		} else {
			newNode.setName("");
		}

		if (!jsonObject.get("systemParticipantOwnerId").isJsonNull()) {
			newNode.setSystemParticipantOwnerId(jsonObject.get("systemParticipantOwnerId").getAsString());
		} else {
			newNode.setSystemParticipantOwnerId("");
		}

		if (!jsonObject.get("identifier").isJsonNull()) {
			newNode.setIdentifier(jsonObject.get("identifier").getAsString());
		} else {
			newNode.setIdentifier("");
		}

		ArrayList<String> relatedRequirementsIdsArrayList = new ArrayList<String>();
		if (!jsonObject.get("relatedRequirementsIds").isJsonNull()) {
			JsonArray relatedRequirementsIds = jsonObject.get("relatedRequirementsIds").getAsJsonArray();

			relatedRequirementsIds.forEach(item -> {
				String relatedRequirementsId = item.getAsString();

				relatedRequirementsIdsArrayList.add(relatedRequirementsId);
			});
		}

		newNode.setRelatedRequirementsIds(relatedRequirementsIdsArrayList);

		ArrayList<Edge> childrenList = new ArrayList<Edge>();
		if (!jsonObject.get("children").isJsonNull()) {
			JsonArray children = jsonObject.get("children").getAsJsonArray();
			children.forEach(item -> {
				String child = item.getAsString();
				Edge edge = new Edge();
				edge.setIdentifier(child);
				childrenList.add(edge);
			});
		}
		newNode.setChildren(childrenList);

		ArrayList<Edge> parentsList = new ArrayList<Edge>();
		if (!jsonObject.get("parents").isJsonNull()) {
			JsonArray parents = jsonObject.get("parents").getAsJsonArray();
			parents.forEach(item -> {
				String parent = item.getAsString();
				Edge edge = new Edge();
				edge.setIdentifier(parent);
				parentsList.add(edge);
			});
		}
		newNode.setParents(parentsList);

		return newNode;
	}

}
