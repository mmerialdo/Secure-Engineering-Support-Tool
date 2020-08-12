/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.audit;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.Questionnaire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//This class manages the serialization/deserialization of Audit classes
public class AuditInstanceCreator implements JsonDeserializer<Audit>, JsonSerializer<Audit> {

	public static final String IDENTIFIER = "identifier";
	public static final String TYPE = "type";
	public static final String DATA = "data";
	public static final String CHILDREN = "children";
	private QuestionnaireInstanceCreator convertor = new QuestionnaireInstanceCreator();
	private static final Logger LOG = LoggerFactory.getLogger(AuditInstanceCreator.class.getName());

	@Override
	public JsonElement serialize(Audit audit, Type arg1, JsonSerializationContext context) {

		JsonObject jsonObject = new JsonObject();

		JsonObject auditData = new JsonObject();
		auditData.addProperty(IDENTIFIER, audit.getIdentifier());
		auditData.addProperty(TYPE, audit.getType().name());

		jsonObject.add(DATA, auditData);

		JsonArray questionnaries = new JsonArray();
		List<Questionnaire> questionnaires = audit.getQuestionnaires();
		for (Questionnaire questionnaire : questionnaires) {

			JsonObject questionnaireJsonObject = convertor.serializeQuestionnaire(context, questionnaire);

			questionnaries.add(questionnaireJsonObject);
		}

		jsonObject.add(CHILDREN, questionnaries);
		return null;
	}

	@Override
	public Audit deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
			throws JsonParseException {

		Audit audit = new Audit();
		JsonObject jsonObject = json.getAsJsonObject();
		JsonArray children = jsonObject.getAsJsonArray("questionnaires");

		ArrayList<Questionnaire> questionnaires = new ArrayList<>();

		if (children != null && !children.isJsonNull()) {
			for (int i = 0; i < children.size(); i++) {
				Questionnaire questionnaire = convertor.deserialize(children.get(i), arg1, context);
				if (questionnaire != null) {
					questionnaires.add(questionnaire);
				}
			}
			audit.setQuestionnaires(questionnaires);
		}
		return audit;
	}

}
