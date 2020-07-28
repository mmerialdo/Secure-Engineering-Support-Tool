/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionnaireModelSerializerDeserializer {
  private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireModelSerializerDeserializer.class.getName());

  public String getClientJSONStringFromQuestionnaireModel(SestQuestionnaireModel questionnaireModel) {

    Questionnaire questionnaire = getQuestionnaireFromJSONString(questionnaireModel.getQuestionnaireModelJson());
    questionnaire.setIdentifier(questionnaireModel.getIdentifier());

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Questionnaire.class, new QuestionnaireInstanceCreator());
    gsonBuilder.serializeNulls();

    Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

    return gson.toJson(questionnaire);
  }

  public String getJSONStringFromQuestionnaire(Questionnaire questionnaire) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

    return gson.toJson(questionnaire);
  }

  public Questionnaire getQuestionnaireFromJSONString(String questionnaireJson) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    Gson gson = gsonBuilder.create();

    Questionnaire questionnaire;
    try {
      questionnaire = gson.fromJson(questionnaireJson, Questionnaire.class);
    } catch (Exception e) {
      LOG.info("Audit Serialization/Deserialization error:: " + e.getMessage());
      return null;
    }

    if (questionnaire == null) {
      LOG.info("Conversion of json into Questionnaire Model returned null");
      return null;
    }

    return questionnaire;
  }
}
