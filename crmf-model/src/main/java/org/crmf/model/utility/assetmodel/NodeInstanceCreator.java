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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.crmf.model.general.SESTObjectTypeEnum;
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

//This class manages the deserialization of Node classes. This class is not currently used
class NodeInstanceCreator implements JsonDeserializer<Node> {

	public static final String NODE_TYPE = "nodeType";
	public static final String ASSESSMENT_NODE = "assessmentNode";
	public static final String GOAL = "goal";
	public static final String DESCRIPTION = "description";
	public static final String NAME = "name";
	public static final String SYSTEM_PARTICIPANT_OWNER_ID = "systemParticipantOwnerId";
	public static final String IDENTIFIER = "identifier";
	public static final String RELATED_REQUIREMENTS_IDS = "relatedRequirementsIds";
	public static final String CHILDREN = "children";
	public static final String PARENTS = "parents";

	@Override
	public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		Node newNode = new Node();

		if (!jsonObject.get(NODE_TYPE).isJsonNull()) {
			String nodeType = jsonObject.get(NODE_TYPE).getAsString();

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

		if (!jsonObject.get(ASSESSMENT_NODE).isJsonNull()) {
			newNode.setAssessmentNode(jsonObject.get(ASSESSMENT_NODE).getAsBoolean());
		} else {
			newNode.setAssessmentNode(false);
		}

		if (!jsonObject.get(GOAL).isJsonNull()) {
			newNode.setGoal(jsonObject.get(GOAL).getAsString());
		} else {
			newNode.setGoal("");
		}

		if (!jsonObject.get(DESCRIPTION).isJsonNull()) {
			newNode.setDescription(jsonObject.get(DESCRIPTION).getAsString());
		} else {
			newNode.setDescription("");
		}

		if (!jsonObject.get(NAME).isJsonNull()) {
			newNode.setName(jsonObject.get(NAME).getAsString());
		} else {
			newNode.setName("");
		}

		if (!jsonObject.get(SYSTEM_PARTICIPANT_OWNER_ID).isJsonNull()) {
			newNode.setSystemParticipantOwnerId(jsonObject.get(SYSTEM_PARTICIPANT_OWNER_ID).getAsString());
		} else {
			newNode.setSystemParticipantOwnerId("");
		}

		if (!jsonObject.get(IDENTIFIER).isJsonNull()) {
			newNode.setIdentifier(jsonObject.get(IDENTIFIER).getAsString());
		} else {
			newNode.setIdentifier("");
		}

		ArrayList<String> relatedRequirementsIdsArrayList = new ArrayList<>();
		if (!jsonObject.get(RELATED_REQUIREMENTS_IDS).isJsonNull()) {
			JsonArray relatedRequirementsIds = jsonObject.get(RELATED_REQUIREMENTS_IDS).getAsJsonArray();

			relatedRequirementsIds.forEach(item -> {
				String relatedRequirementsId = item.getAsString();

				relatedRequirementsIdsArrayList.add(relatedRequirementsId);
			});
		}

		newNode.setRelatedRequirementsIds(relatedRequirementsIdsArrayList);

		ArrayList<Edge> childrenList = new ArrayList<>();
		if (!jsonObject.get(CHILDREN).isJsonNull()) {
			JsonArray children = jsonObject.get(CHILDREN).getAsJsonArray();
			children.forEach(item -> {
				String child = item.getAsString();
				Edge edge = new Edge();
				edge.setIdentifier(child);
				childrenList.add(edge);
			});
		}
		newNode.setChildren(childrenList);

		ArrayList<Edge> parentsList = new ArrayList<>();
		if (!jsonObject.get(PARENTS).isJsonNull()) {
			JsonArray parents = jsonObject.get(PARENTS).getAsJsonArray();
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
