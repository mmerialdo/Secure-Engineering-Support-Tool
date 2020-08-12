/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="QuestionnaireInstanceCreator.java"
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
import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.general.SESTObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class manages the serialization/deserialization of Questionnaire classes
public class QuestionnaireInstanceCreator implements JsonDeserializer<Questionnaire>, JsonSerializer<Questionnaire> {

  public static final String ISO_13 = "iso13";
  private static final String ISO_5 = "iso5";
  private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireInstanceCreator.class.getName());
  public static final String ISO_13_INFO = "iso13_info";
  public static final String MAX = "max";
  public static final String MIN = "min";
  public static final String WEIGHT = "weight";
  public static final String TARGET = "target";
  public static final String PREVIOUS = "previous";
  public static final String V_5 = "v5";
  public static final String V_4 = "v4";
  public static final String V_3 = "v3";
  public static final String V_2 = "v2";
  public static final String V_1 = "v1";
  public static final String CHILDREN = "children";
  public static final String DATA = "data";
  public static final String TYPE = "type";
  public static final String CATEGORY = "category";
  public static final String INDEX = "index";
  public static final String VALUE = "value";
  public static final String IDENTIFIER = "identifier";
  public static final String REFERENCE_OBJECT = "referenceObject";
  public static final String COMMENT_VALUE = "commentValue";
  public static final String DESCRIPTION = "description";

  public QuestionnaireInstanceCreator() {
    super();
  }

  @Override
  public JsonElement serialize(Questionnaire questionnaire, Type arg1, JsonSerializationContext context) {

    return serializeQuestionnaire(context, questionnaire);
  }

  @Override
  public Questionnaire deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
    throws JsonParseException {

    return deserializeQuestionnaire(json, context);
  }

  private Questionnaire deserializeQuestionnaire(JsonElement json, JsonDeserializationContext context) {
    JsonObject jsonObject = json.getAsJsonObject();
    Questionnaire questionnaire = new Questionnaire();

    if (jsonObject == null || !jsonObject.has(DATA) || !jsonObject.has(CHILDREN)
      || jsonObject.get(DATA).isJsonNull() || jsonObject.get(CHILDREN).isJsonNull()) {

      return null;
    }

    JsonObject data = jsonObject.getAsJsonObject(DATA);
    JsonArray children = jsonObject.getAsJsonArray(CHILDREN);

    if (data != null && !data.isJsonNull() && data.has(INDEX)) {
      questionnaire.setCategory((data.has(CATEGORY) && !data.get(CATEGORY).isJsonNull())
        ? data.get(CATEGORY).getAsString() : "");
      questionnaire.setIndex((data.has(INDEX) && !data.get(INDEX).isJsonNull())
        ? data.get(INDEX).getAsString() : null);
      questionnaire.setType((!data.get(TYPE).isJsonNull())
        ? QuestionnaireTypeEnum.valueOf(data.get(TYPE).getAsString()) : null);

      questionnaire
        .setIdentifier((!data.get(IDENTIFIER).isJsonNull()) ? data.get(IDENTIFIER).getAsString() : "");
    }

    if (children != null && !children.isJsonNull()) {
      ArrayList<Question> questions = deserializeQuestion(children, context);
      questionnaire.setQuestions(questions);
    }

    return questionnaire;
  }

  private ArrayList<Question> deserializeQuestion(JsonArray questionsjson, JsonDeserializationContext context) {

    ArrayList<Question> questions = new ArrayList<>();

    for (int i = 0; i < questionsjson.size(); i++) {
      Question question = new Question();
      JsonObject element = questionsjson.get(i).getAsJsonObject();
      JsonObject data = element.getAsJsonObject(DATA);

      question.setCategory((!data.get(CATEGORY).isJsonNull()) ? data.get(CATEGORY).getAsString() : "");
      question.setIndex((data.has(INDEX) && !data.get(INDEX).isJsonNull())
        ? data.get(INDEX).getAsString() : null);
      question.setType(
        (!data.get(TYPE).isJsonNull()) ? QuestionTypeEnum.valueOf(data.get(TYPE).getAsString()) : null);
      question.setIdentifier((!data.get(IDENTIFIER).isJsonNull()) ? data.get(IDENTIFIER).getAsString() : "");
      question.setValue((!data.get(VALUE).isJsonNull()) ? data.get(VALUE).getAsString() : null);
      question.setReferenceObject((data.has(REFERENCE_OBJECT) && !data.get(REFERENCE_OBJECT).isJsonNull())
        ? context.deserialize(data.get(REFERENCE_OBJECT), SESTObject.class) : null);

      Map<AnswerTypeEnum, String> answers = new HashMap<>();

      LOG.info("deserializeQuestion data.get(\"v1\") " + data.has(V_1) + data.get(V_1) +
        " data.get(\"category\") " + data.get(CATEGORY));
      if (data.has(V_1) && !data.get(V_1).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_V1, data.get(V_1).getAsString());
      }
      if (data.has(V_2) && !data.get(V_2).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_V2, data.get(V_2).getAsString());
      }
      if (data.has(WEIGHT) && !data.get(WEIGHT).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_W, data.get(WEIGHT).getAsString());
      }
      if (data.has(MIN) && !data.get(MIN).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_Min, data.get(MIN).getAsString());
      }
      if (data.has(MAX) && !data.get(MAX).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_Max, data.get(MAX).getAsString());
      }
      if (data.has(ISO_5) && !data.get(ISO_5).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_ISO5, data.get(ISO_5).getAsString());
      }
      if (data.has(ISO_13) && !data.get(ISO_13).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_ISO13, data.get(ISO_13).getAsString());
      }
      if (data.has(ISO_13_INFO) && !data.get(ISO_13_INFO).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_ISO13_info, data.get(ISO_13_INFO).getAsString());
      }
      if (data.has(COMMENT_VALUE) && !data.get(COMMENT_VALUE).isJsonNull()) {
        answers.put(AnswerTypeEnum.Comment, data.get(COMMENT_VALUE).getAsString());
      }
      if (data.has(DESCRIPTION) && !data.get(DESCRIPTION).isJsonNull()) {
        answers.put(AnswerTypeEnum.Description, data.get(DESCRIPTION).getAsString());
      }
      if (data.has(V_4) && !data.get(V_4).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_V4, data.get(V_4).getAsString());
      }
      if (data.has(V_5) && !data.get(V_5).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_V5, data.get(V_5).getAsString());
      }
      if (data.has(PREVIOUS) && !data.get(PREVIOUS).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_Prev, data.get(PREVIOUS).getAsString());
      }
      if (data.has(TARGET) && !data.get(TARGET).isJsonNull()) {
        answers.put(AnswerTypeEnum.MEHARI_R_Target, data.get(TARGET).getAsString());
      }
      JsonArray children = element.getAsJsonArray(CHILDREN);
      LOG.info("BEfore GASF || MEHARI " + question.getType() + ",question.getCategory().length() " + question.getCategory().length() +
        children);
      if (question.getType().equals(QuestionTypeEnum.CATEGORY) &&
        children != null && !children.isJsonNull()) {
        JsonObject childSafeguard = children.get(0).getAsJsonObject();
        if (childSafeguard.has(DATA) && !childSafeguard.get(DATA).isJsonNull() &&
          childSafeguard.getAsJsonObject(DATA).has(CATEGORY) &&
          !childSafeguard.getAsJsonObject(DATA).get(CATEGORY).isJsonNull()) {
          String dataCategory = childSafeguard.getAsJsonObject(DATA).get(CATEGORY).getAsString();
          if (question.getCategory().length() > 3 &&
            dataCategory.equals(QuestionTypeEnum.GASF.name()) || dataCategory.equals(QuestionTypeEnum.MEHARI.name())) {
            String valueGasf = "";
            String valueMehari = "";
            for (JsonElement childObject : children) {
              JsonObject dataObject = childObject.getAsJsonObject().getAsJsonObject(DATA);

              if (dataObject.get(CATEGORY).getAsString().equals(QuestionTypeEnum.GASF.name())) {
                LOG.info("GASF found {}", dataObject.get(CATEGORY));
                ArrayList<Question> gasfchildren = deserializeQuestion(
                  childObject.getAsJsonObject().getAsJsonArray(CHILDREN), context);
                question.setGasf(gasfchildren);

                LOG.info("GASF found dataObject.get(\"v1\") {}", dataObject.get(V_1));
                if (dataObject.has(V_1) && !dataObject.get(V_1).isJsonNull()) {
                  LOG.info("GASF found {}, valueGasf {}", dataObject.get(CATEGORY), valueGasf);
                  valueGasf = dataObject.get(V_1).getAsString();
                }
              } else {
                ArrayList<Question> questionschildren = deserializeQuestion(
                  childObject.getAsJsonObject().getAsJsonArray(CHILDREN), context);
                question.setChildren(questionschildren);

                LOG.info("MEHARI found dataObject.get(\"v1\") {}", dataObject.get("v1"));
                if (dataObject.has(V_1) && !dataObject.get(V_1).isJsonNull()) {
                  valueMehari = dataObject.get(V_1).getAsString();
                }
              }
            }

            answers.put(AnswerTypeEnum.MEHARI_R_V2, valueMehari);
            answers.put(AnswerTypeEnum.MEHARI_R_V3, valueGasf);
          } else {
            if (children != null && !children.isJsonNull()) {
              ArrayList<Question> questionschildren = deserializeQuestion(children, context);
              question.setChildren(questionschildren);
            }
          }
        }
      }

      question.setAnswers(answers);
      questions.add(question);
    }
    return questions;
  }

  public JsonObject serializeQuestionnaire(JsonSerializationContext context, Questionnaire questionnaire) {
    JsonObject questionnaireJsonObject = new JsonObject();

    JsonObject questionnaireData = new JsonObject();
    questionnaireData.addProperty(CATEGORY, questionnaire.getCategory());
    questionnaireData.addProperty(INDEX, questionnaire.getIndex());
    questionnaireData.addProperty(TYPE, (questionnaire.getType() != null) ? questionnaire.getType().name() : null);
    questionnaireData.addProperty(IDENTIFIER, questionnaire.getIdentifier());
    questionnaireJsonObject.add(DATA, questionnaireData);

    JsonArray questionnaireQuestions = new JsonArray();
    List<Question> questions = questionnaire.getQuestions();
    if (questions != null && !questions.isEmpty()) {

      serializeQuestion(context, questionnaireQuestions, questions);
      questionnaireJsonObject.add(CHILDREN, questionnaireQuestions);
    }
    return questionnaireJsonObject;
  }

  private void serializeQuestion(JsonSerializationContext context, JsonArray
    jsonObject, List<Question> questions) {

    for (Question question : questions) {
      JsonObject questionJsonObject = new JsonObject();

      JsonObject questionData = new JsonObject();
      questionData.addProperty(CATEGORY, question.getCategory());
      questionData.addProperty(INDEX, question.getIndex());
      questionData.addProperty(TYPE, question.getType().name());
      questionData.addProperty(VALUE, question.getValue());
      questionData.addProperty(IDENTIFIER, question.getIdentifier());
      questionData.add(REFERENCE_OBJECT, context.serialize(question.getReferenceObject(), SESTObject.class));

      // JsonArray answersElem = new JsonArray();
      Map<AnswerTypeEnum, String> answers = question.getAnswers();
      if (answers != null && !answers.isEmpty()) {
        questionData.addProperty(V_1, answers.get(AnswerTypeEnum.MEHARI_R_V1));
        questionData.addProperty(V_2, answers.get(AnswerTypeEnum.MEHARI_R_V2));
        questionData.addProperty(V_3, answers.get(AnswerTypeEnum.MEHARI_R_V3));
        questionData.addProperty(V_4, answers.get(AnswerTypeEnum.MEHARI_R_V4));
        questionData.addProperty(V_5, answers.get(AnswerTypeEnum.MEHARI_R_V5));
        questionData.addProperty(PREVIOUS, answers.get(AnswerTypeEnum.MEHARI_R_Prev));
        questionData.addProperty(TARGET, answers.get(AnswerTypeEnum.MEHARI_R_Target));
        questionData.addProperty(WEIGHT, answers.get(AnswerTypeEnum.MEHARI_W));
        questionData.addProperty(MIN, answers.get(AnswerTypeEnum.MEHARI_Min));
        questionData.addProperty(MAX, answers.get(AnswerTypeEnum.MEHARI_Max));
        questionData.addProperty(ISO_5, answers.get(AnswerTypeEnum.MEHARI_ISO5));
        questionData.addProperty(ISO_13, answers.get(AnswerTypeEnum.MEHARI_ISO13));
        questionData.addProperty(ISO_13_INFO, answers.get(AnswerTypeEnum.MEHARI_ISO13_info));
        questionData.addProperty(COMMENT_VALUE, answers.get(AnswerTypeEnum.Comment));
        questionData.addProperty(DESCRIPTION, answers.get(AnswerTypeEnum.Description));
      }

      questionJsonObject.add(DATA, questionData);

      List<Question> gasfChilden = question.getGasf();
      if (question.getType().equals(QuestionTypeEnum.CATEGORY) && question.getCategory().length() > 3 &&
        gasfChilden != null && !gasfChilden.isEmpty()) {

        JsonArray questionList = new JsonArray();
        List<Question> questionsChilden = question.getChildren();

        JsonObject gasfChildJsonObject = new JsonObject();
        JsonObject gasfChildrenElem = new JsonObject();

        String v2 = (questionData.has(V_2) && !questionData.get(V_2).isJsonNull()) ?
          questionData.get(V_2).getAsString() : "";
        String v3 = (questionData.has(V_3) && !questionData.get(V_3).isJsonNull()) ?
          questionData.get(V_3).getAsString() : "";

        gasfChildrenElem.addProperty(CATEGORY, QuestionTypeEnum.GASF.name());
        gasfChildrenElem.addProperty(TYPE, QuestionTypeEnum.CATEGORY.name());
        gasfChildrenElem.addProperty(V_1, v3);

        JsonArray gasfChildrenList = new JsonArray();
        serializeQuestion(context, gasfChildrenList, gasfChilden);
        gasfChildJsonObject.add(DATA, gasfChildrenElem);
        gasfChildJsonObject.add(CHILDREN, gasfChildrenList);

        questionList.add(gasfChildJsonObject);

        JsonObject questionChildJsonObject = new JsonObject();
        JsonObject questionsChildrenElem = new JsonObject();
        questionsChildrenElem.addProperty(CATEGORY, QuestionTypeEnum.MEHARI.name());
        questionsChildrenElem.addProperty(TYPE, QuestionTypeEnum.CATEGORY.name());
        questionsChildrenElem.addProperty(V_1, v2);
        JsonArray questionsChildrenList = new JsonArray();
        serializeQuestion(context, questionsChildrenList, questionsChilden);
        questionChildJsonObject.add(DATA, questionsChildrenElem);
        questionChildJsonObject.add(CHILDREN, questionsChildrenList);

        questionList.add(questionChildJsonObject);

        questionJsonObject.add(CHILDREN, questionList);

      } else {

        JsonArray questionsChildenElem = new JsonArray();
        List<Question> questionsChilden = question.getChildren();
        if (questionsChilden != null && !questionsChilden.isEmpty()) {

          serializeQuestion(context, questionsChildenElem, questionsChilden);
          questionJsonObject.add(CHILDREN, questionsChildenElem);
        }
      }
      jsonObject.add(questionJsonObject);
    }
  }

}