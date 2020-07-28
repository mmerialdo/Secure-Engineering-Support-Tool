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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.crmf.model.audit.Answer;
import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.general.SESTObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//This class manages the serialization/deserialization of Questionnaire classes
public class QuestionnaireInstanceCreator implements JsonDeserializer<Questionnaire>, JsonSerializer<Questionnaire> {

  private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireInstanceCreator.class.getName());

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

    if (jsonObject == null || !jsonObject.has("data") || !jsonObject.has("children")
      || jsonObject.get("data").isJsonNull() || jsonObject.get("children").isJsonNull()) {

      return null;
    }

    JsonObject data = jsonObject.getAsJsonObject("data");
    JsonArray children = jsonObject.getAsJsonArray("children");

    if (data != null && !data.isJsonNull() && data.has("index")) {
      questionnaire.setCategory((data.has("category") && !data.get("category").isJsonNull())
        ? data.get("category").getAsString() : "");
      questionnaire.setIndex((data.has("index") && !data.get("index").isJsonNull())
        ? data.get("index").getAsString() : null);
      questionnaire.setType((!data.get("type").isJsonNull())
        ? QuestionnaireTypeEnum.valueOf(data.get("type").getAsString()) : null);

      questionnaire
        .setIdentifier((!data.get("identifier").isJsonNull()) ? data.get("identifier").getAsString() : "");
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
      JsonObject data = element.getAsJsonObject("data");

      question.setCategory((!data.get("category").isJsonNull()) ? data.get("category").getAsString() : "");
      question.setIndex((data.has("index") && !data.get("index").isJsonNull())
        ? data.get("index").getAsString() : null);
      question.setType(
        (!data.get("type").isJsonNull()) ? QuestionTypeEnum.valueOf(data.get("type").getAsString()) : null);
      question.setIdentifier((!data.get("identifier").isJsonNull()) ? data.get("identifier").getAsString() : "");
      question.setValue((!data.get("value").isJsonNull()) ? data.get("value").getAsString() : null);
      question.setReferenceObject((data.has("referenceObject") && !data.get("referenceObject").isJsonNull())
        ? context.deserialize(data.get("referenceObject"), SESTObject.class) : null);

      ArrayList<Answer> answers = new ArrayList<>();

      LOG.info("deserializeQuestion data.get(\"v1\") " + data.has("v1") + data.get("v1") +
        " data.get(\"category\") " + data.get("category"));
      if (data.has("v1")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(1);
        answerValue.setType(AnswerTypeEnum.MEHARI_R_V1);
        answerValue.setValue((data.has("v1") && !data.get("v1").isJsonNull())
          ? data.get("v1").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("v2")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(2);
        answerValue.setType(AnswerTypeEnum.MEHARI_R_V2);
        answerValue.setValue((data.has("v2") && !data.get("v2").isJsonNull())
          ? data.get("v2").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("weight")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(3);
        answerValue.setType(AnswerTypeEnum.MEHARI_W);
        answerValue.setValue((data.has("weight") && !data.get("weight").isJsonNull())
          ? data.get("weight").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("min")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(4);
        answerValue.setType(AnswerTypeEnum.MEHARI_Min);
        answerValue.setValue((data.has("min") && !data.get("min").isJsonNull())
          ? data.get("min").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("max")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(5);
        answerValue.setType(AnswerTypeEnum.MEHARI_Max);
        answerValue.setValue((data.has("max") && !data.get("max").isJsonNull())
          ? data.get("max").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("iso5")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(6);
        answerValue.setType(AnswerTypeEnum.MEHARI_ISO5);
        answerValue.setValue((data.has("iso5") && !data.get("iso5").isJsonNull())
          ? data.get("iso5").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("iso13")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(7);
        answerValue.setType(AnswerTypeEnum.MEHARI_ISO13);
        answerValue.setValue((data.has("iso13") && !data.get("iso13").isJsonNull())
          ? data.get("iso13").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("iso13_info")) {
        Answer answerValue = new Answer();
        answerValue.setIndex(8);
        answerValue.setType(AnswerTypeEnum.MEHARI_ISO13_info);
        answerValue.setValue((data.has("iso13_info") && !data.get("iso13_info").isJsonNull())
          ? data.get("iso13_info").getAsString() : null);
        answers.add(answerValue);
      }

      if (data.has("commentValue")) {
        Answer answerComment = new Answer();
        answerComment.setIndex(9);
        answerComment.setType(AnswerTypeEnum.Comment);
        answerComment.setValue((data.has("commentValue") && !data.get("commentValue").isJsonNull())
          ? data.get("commentValue").getAsString() : null);
        answers.add(answerComment);
      }

      if (data.has("description")) {
        Answer answerDescription = new Answer();
        answerDescription.setIndex(10);
        answerDescription.setType(AnswerTypeEnum.Description);
        answerDescription.setValue((data.has("description") && !data.get("description").isJsonNull())
          ? data.get("description").getAsString() : null);
        answers.add(answerDescription);
      }

      if (data.has("v4")) {
        Answer answerPrevious = new Answer();
        answerPrevious.setIndex(11);
        answerPrevious.setType(AnswerTypeEnum.MEHARI_R_V4);
        answerPrevious.setValue((data.has("v4") && !data.get("v4").isJsonNull())
          ? data.get("v4").getAsString() : null);
        answers.add(answerPrevious);
      }

      if (data.has("v5")) {
        Answer answerTreatment = new Answer();
        answerTreatment.setIndex(12);
        answerTreatment.setType(AnswerTypeEnum.MEHARI_R_V5);
        answerTreatment.setValue((data.has("v5") && !data.get("v5").isJsonNull())
          ? data.get("v5").getAsString() : null);
        answers.add(answerTreatment);
      }

      if (data.has("previous")) {
        Answer answerTreatment = new Answer();
        answerTreatment.setIndex(13);
        answerTreatment.setType(AnswerTypeEnum.MEHARI_R_Prev);
        answerTreatment.setValue((data.has("previous") && !data.get("previous").isJsonNull())
          ? data.get("previous").getAsString() : null);
        answers.add(answerTreatment);
      }

      if (data.has("target")) {
        Answer answerTreatment = new Answer();
        answerTreatment.setIndex(14);
        answerTreatment.setType(AnswerTypeEnum.MEHARI_R_Target);
        answerTreatment.setValue((data.has("target") && !data.get("target").isJsonNull())
          ? data.get("target").getAsString() : null);
        answers.add(answerTreatment);
      }
      LOG.info("BEfore GASF || MEHARI");
      JsonArray children = element.getAsJsonArray("children");
      LOG.info("BEfore GASF || MEHARI " + question.getType() + ",question.getCategory().length() " + question.getCategory().length() +
        children);
      if (question.getType().equals(QuestionTypeEnum.CATEGORY) &&
        children != null && !children.isJsonNull()) {
        LOG.info("BEfore GASF || MEHARI 1");
        JsonObject childSafeguard = children.get(0).getAsJsonObject();
        LOG.info("BEfore GASF || MEHARI 2");
        if (childSafeguard.has("data") && !childSafeguard.get("data").isJsonNull() &&
          childSafeguard.getAsJsonObject("data").has("category") &&
          !childSafeguard.getAsJsonObject("data").get("category").isJsonNull()) {
          String dataCategory = childSafeguard.getAsJsonObject("data").get("category").getAsString();
          LOG.info("BEfore GASF || MEHARI 3 "+dataCategory);
          if (question.getCategory().length() > 3 &&
            dataCategory.equals(QuestionTypeEnum.GASF.name()) || dataCategory.equals(QuestionTypeEnum.MEHARI.name())) {
            LOG.info("GASF || MEHARI");
            String valueGasf = "";
            String valueMehari = "";
            for (JsonElement childObject : children) {
              JsonObject dataObject = childObject.getAsJsonObject().getAsJsonObject("data");

              if (dataObject.get("category").getAsString().equals(QuestionTypeEnum.GASF.name())) {
                LOG.info("GASF found "+dataObject.get("category"));
                ArrayList<Question> gasfchildren = deserializeQuestion(
                  childObject.getAsJsonObject().getAsJsonArray("children"), context);
                question.setGasf(gasfchildren);

                LOG.info("GASF found dataObject.get(\"v1\") "+dataObject.get("v1"));
                if (dataObject.has("v1") && !dataObject.get("v1").isJsonNull()) {
                  LOG.info("GASF found "+dataObject.get("category") + ", valueGasf " + valueGasf);
                  valueGasf = dataObject.get("v1").getAsString();
                }
              } else {
                ArrayList<Question> questionschildren = deserializeQuestion(
                  childObject.getAsJsonObject().getAsJsonArray("children"), context);
                question.setChildren(questionschildren);

                LOG.info("MEHARI found dataObject.get(\"v1\") "+dataObject.get("v1"));
                if (dataObject.has("v1") && !dataObject.get("v1").isJsonNull()) {
                  valueMehari = dataObject.get("v1").getAsString();
                }
              }
            }
            LOG.info("valueGasf " + valueGasf);
            LOG.info("valueMehari " + valueMehari);

            Answer answerMehari = new Answer();
            answerMehari.setIndex(15);
            answerMehari.setType(AnswerTypeEnum.MEHARI_R_V2);
            answerMehari.setValue(valueMehari);
            answers.add(answerMehari);

            Answer answerGasf = new Answer();
            answerGasf.setIndex(16);
            answerGasf.setType(AnswerTypeEnum.MEHARI_R_V3);
            answerGasf.setValue(valueGasf);
            answers.add(answerGasf);
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
    questionnaireData.addProperty("category", questionnaire.getCategory());
    questionnaireData.addProperty("index", questionnaire.getIndex());
    questionnaireData.addProperty("type", (questionnaire.getType() != null) ? questionnaire.getType().name() : null);
    questionnaireData.addProperty("identifier", questionnaire.getIdentifier());
    questionnaireJsonObject.add("data", questionnaireData);

    JsonArray questionnaireQuestions = new JsonArray();
    List<Question> questions = questionnaire.getQuestions();
    if (questions != null && questions.size() > 0) {

      serializeQuestion(context, questionnaireQuestions, questions);
      questionnaireJsonObject.add("children", questionnaireQuestions);
    }
    return questionnaireJsonObject;
  }

  private void serializeQuestion(JsonSerializationContext context, JsonArray
    jsonObject, List<Question> questions) {

    for (Question question : questions) {
      JsonObject questionJsonObject = new JsonObject();

      JsonObject questionData = new JsonObject();
      questionData.addProperty("category", question.getCategory());
      questionData.addProperty("index", question.getIndex());
      questionData.addProperty("type", question.getType().name());
      questionData.addProperty("value", question.getValue());
      questionData.addProperty("identifier", question.getIdentifier());
      questionData.add("referenceObject", context.serialize(question.getReferenceObject(), SESTObject.class));

      // JsonArray answersElem = new JsonArray();
      List<Answer> answers = question.getAnswers();
      if (answers != null && answers.size() > 0) {
        for (Answer answer : answers) {
          switch (answer.getType()) {
            case MEHARI_R_V1:
              questionData.addProperty("v1", answer.getValue());
              break;
            case MEHARI_R_V2:
              questionData.addProperty("v2", answer.getValue());
              break;
            case MEHARI_R_V3:
              questionData.addProperty("v3", answer.getValue());
              break;
            case MEHARI_R_V4:
              questionData.addProperty("v4", answer.getValue());
              break;
            case MEHARI_R_V5:
              questionData.addProperty("v5", answer.getValue());
              break;
            case MEHARI_R_Prev:
              questionData.addProperty("previous", answer.getValue());
              break;
            case MEHARI_R_Target:
              questionData.addProperty("target", answer.getValue());
              break;
            case MEHARI_W:
              questionData.addProperty("weight", answer.getValue());
              break;
            case MEHARI_Min:
              questionData.addProperty("min", answer.getValue());
              break;
            case MEHARI_Max:
              questionData.addProperty("max", answer.getValue());
              break;
            case MEHARI_ISO5:
              questionData.addProperty("iso5", answer.getValue());
              break;
            case MEHARI_ISO13:
              questionData.addProperty("iso13", answer.getValue());
              break;
            case MEHARI_ISO13_info:
              questionData.addProperty("iso13_info", answer.getValue());
              break;
            case Comment:
              questionData.addProperty("commentValue", answer.getValue());
              break;
            case Description:
              questionData.addProperty("description", answer.getValue());
              break;
            default:
          }
        }
      }

      questionJsonObject.add("data", questionData);

      List<Question> gasfChilden = question.getGasf();
      if (question.getType().equals(QuestionTypeEnum.CATEGORY) && question.getCategory().length() > 3 &&
        gasfChilden != null && gasfChilden.size() > 0) {

        JsonArray questionList = new JsonArray();
        List<Question> questionsChilden = question.getChildren();

        JsonObject gasfChildJsonObject = new JsonObject();
        JsonObject gasfChildrenElem = new JsonObject();

        String v2 = (questionData.has("v2") && !questionData.get("v2").isJsonNull()) ?
          questionData.get("v2").getAsString() : "";
        String v3 = (questionData.has("v3") && !questionData.get("v3").isJsonNull()) ?
          questionData.get("v3").getAsString() : "";

        gasfChildrenElem.addProperty("category", QuestionTypeEnum.GASF.name());
        gasfChildrenElem.addProperty("type", "CATEGORY");
        gasfChildrenElem.addProperty("v1", v3);

        JsonArray gasfChildrenList = new JsonArray();
        serializeQuestion(context, gasfChildrenList, gasfChilden);
        gasfChildJsonObject.add("data", gasfChildrenElem);
        gasfChildJsonObject.add("children", gasfChildrenList);

        questionList.add(gasfChildJsonObject);

        JsonObject questionChildJsonObject = new JsonObject();
        JsonObject questionsChildrenElem = new JsonObject();
        questionsChildrenElem.addProperty("category", QuestionTypeEnum.MEHARI.name());
        questionsChildrenElem.addProperty("type", "CATEGORY");
        questionsChildrenElem.addProperty("v1", v2);
        JsonArray questionsChildrenList = new JsonArray();
        serializeQuestion(context, questionsChildrenList, questionsChilden);
        questionChildJsonObject.add("data", questionsChildrenElem);
        questionChildJsonObject.add("children", questionsChildrenList);

        questionList.add(questionChildJsonObject);

        questionJsonObject.add("children", questionList);

      } else {

        JsonArray questionsChildenElem = new JsonArray();
        List<Question> questionsChilden = question.getChildren();
        if (questionsChilden != null && questionsChilden.size() > 0) {

          serializeQuestion(context, questionsChildenElem, questionsChilden);
          questionJsonObject.add("children", questionsChildenElem);
        }
      }
      jsonObject.add(questionJsonObject);
    }
  }

}