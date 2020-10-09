/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditDefaultService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ibatis.session.SqlSession;
import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.ISOControl;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.domain.secrequirement.SecRequirement;
import org.crmf.persistency.domain.secrequirement.SecRequirementSafeguard;
import org.crmf.persistency.mapper.secrequirement.SecRequirementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the Audit
@Component
public class AssAuditDefaultService {

  private static final Logger LOG = LoggerFactory.getLogger(AssAuditDefaultService.class.getName());
  private final SqlSession sqlSession;

  public AssAuditDefaultService(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  public static final String inputfilename = "questionnaire.csv";
  public static final String outputfilename = "questionnaire.sql";

  public static final String text_insert = "INSERT INTO ASSAUDIT_DEFAULT(" +
    "`ID`,`IX`,`ATYPE`,`AVALUE`,`CATEGORY`,`PARENT`,`VW`,`VMAX`,`VMIN`,`VTYPE`,`VISO13`,`VISO5`) VALUES(";
  public static final String text_question = "QUESTION";
  public static final String text_category = "CATEGORY";
  public static final String text_questionnaire = "QUESTIONNAIRE";
  public static final String text_separator1 = "','";
  public static final String text_separator_comma = " , ";
  public static final String text_separator2 = ",'";
  public static final String text_separator_end = "'); ";
  public static final String regexQuestionnaire = "\\d+";
  public static final String regexCategory1 = "\\d+[A-Z]";
  public static final String regexCategory2 = "\\d+[A-Z]\\d+";
  public static final String regexQuestion = "\\d+[A-Z]+\\d+-\\d+";

  private ISOControls isoControls;

  public void createQuestionnaire() {

    List<AssauditDefaultJSON> questionnaires = this.getAllQuestionnaireNames();
    if (questionnaires != null) {
      LOG.info("createDefaultQuestionnaire " + questionnaires.size());
      for (AssauditDefaultJSON questionnaire : questionnaires) {
        String questionnaireJson = this.getQuestionnaireJSON(questionnaire.getCategory());
        LOG.info("createDefaultQuestionnaire " + questionnaire.getCategory());
        questionnaire.setQuestionnaireJSON(questionnaireJson);
        this.updateQuestionnaireJSON(questionnaire);
      }
    }
  }

  public void insertQuestionnaire(AssauditDefaultJSON assauditDefaultJSON) {

    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      auditeDefaultMapper.insert(assauditDefaultJSON);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }


  public void updateQuestionnaireJSON(AssauditDefaultJSON assauditDefaultJSON) {
    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      auditeDefaultMapper.updateQuestionnaireJSON(assauditDefaultJSON);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public List<AssauditDefaultJSON> getAllQuestionnaires() {

    List<AssauditDefaultJSON> questionnaires;
    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      questionnaires = auditeDefaultMapper.getAllQuestionnaires();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }

    return questionnaires;
  }

  public List<AssauditDefaultJSON> getAllQuestionnaireNames() {

    List<AssauditDefaultJSON> questionnaires = new ArrayList<>();
    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      questionnaires = auditeDefaultMapper.getAllQuestionnaireNames();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return questionnaires;
  }

  public AssauditDefaultJSON getQuestionnaireByCategory(String category) {

    AssauditDefaultJSON questionnaire = null;
    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);

      questionnaire = auditeDefaultMapper.getByCategory(category);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return questionnaire;
  }


  public String getQuestionnaireJSON(String category) {

    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      SecRequirementMapper requirementMapper = sqlSession.getMapper(SecRequirementMapper.class);
      AssauditDefaultJSON auditQuestionnaire = auditeDefaultMapper.getByCategory(category);

      if (auditQuestionnaire != null) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setObjType(SESTObjectTypeEnum.Audit);
        questionnaire.setIndex(auditQuestionnaire.getIx());

        questionnaire.setType(QuestionnaireTypeEnum.valueOf(auditQuestionnaire.getAvalue()));
        questionnaire.setCategory(auditQuestionnaire.getCategory());

        List<AssauditDefaultJSON> auditQuestions = auditeDefaultMapper
          .getAllByParentCategory(auditQuestionnaire.getCategory());
        for (AssauditDefaultJSON auditQuestion : auditQuestions) {

          questionnaire.getQuestions().add(getQuestion(auditeDefaultMapper, requirementMapper,
            auditQuestion));
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

        return gson.toJson(questionnaire);
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
    return null;
  }

  private Question getQuestion(AssAuditDefaultMapper auditeDefaultMapper, SecRequirementMapper requirementMapper,
                               AssauditDefaultJSON question) {

    Question child = new Question();
    child.setObjType(SESTObjectTypeEnum.Audit);
    child.setCategory(question.getCategory());
    child.setType(QuestionTypeEnum.valueOf(question.getAtype()));
    child.setValue(question.getAvalue());
    child.setIndex(question.getIx());

    child.getAnswers().put(AnswerTypeEnum.MEHARI_W, question.getVw());
    child.getAnswers().put(AnswerTypeEnum.MEHARI_Max, question.getVmax());
    child.getAnswers().put(AnswerTypeEnum.MEHARI_Min, question.getVmin());
    child.getAnswers().put(AnswerTypeEnum.MEHARI_ISO13, question.getViso13());
    child.getAnswers().put(AnswerTypeEnum.MEHARI_ISO5, question.getViso5());
    String isoControlsInfo = this.getISOControlsDetails(question.getViso13());
    child.getAnswers().put(AnswerTypeEnum.MEHARI_ISO13_info, isoControlsInfo);

    List<AssauditDefaultJSON> auditQuestions = auditeDefaultMapper
      .getAllByParentCategory(question.getCategory());
    int k = 0;
    if (auditQuestions != null) {
      for (AssauditDefaultJSON questionDefault : auditQuestions) {

        child.getChildren().add(
          getQuestion(auditeDefaultMapper, requirementMapper, questionDefault));
      }
    }

    // manages GASF question
    List<SecRequirementSafeguard> secreqs = requirementMapper.getRequirementsAssocBySafeguard(question.getId());
    if (secreqs != null && !secreqs.isEmpty()) {
      for (SecRequirementSafeguard requirement : secreqs) {
        SecRequirement secRequirement = requirementMapper.getSecRequirementById(requirement.getRequirementId());
        if (secRequirement != null) {
          Question childSecReq = getQuestionGASF(secRequirement, requirement.getContribution().toString());
          if (childSecReq != null) {
            child.getGasf().add(childSecReq);
          }
        }
      }
    }

    return child;
  }

  private Question getQuestionGASF(SecRequirement secreq, String contribution) {

    Question secreqQuestion = new Question();
    secreqQuestion.setObjType(SESTObjectTypeEnum.Audit);
    secreqQuestion.setCategory(secreq.getReqId());
    secreqQuestion.setType(QuestionTypeEnum.GASF);
    secreqQuestion.setValue(secreq.getTitle());
    secreqQuestion.setIndex("01");

    secreqQuestion.getAnswers().put(AnswerTypeEnum.MEHARI_W, contribution);
    secreqQuestion.getAnswers().put(AnswerTypeEnum.Description, secreq.getDescription());
    secreqQuestion.getAnswers().put(AnswerTypeEnum.MEHARI_R_V4, secreq.getNote());
    secreqQuestion.getAnswers().put(AnswerTypeEnum.MEHARI_R_V5, secreq.getSourceDescription());

    //Do not delete. This is commented because the actual mapping GASF/Safeguards is horizontal (it does not manage trees)
//		List<SecRequirement> secreqChildren = requirementMapper.getSecRequirementChildren(secreq.getId());
//		LOG.info("secreqChildren "+secreqChildren);
//		if(secreqChildren != null && secreqChildren.size() > 0){
//			LOG.info("secreqChildren reqid "+secreq.getReqId());
//			LOG.info("secreqChildren size "+secreqChildren.size());
//			for (SecRequirement secRequirement : secreqChildren) {
//
//				LOG.info("secreqChildren child reqid "+secRequirement.getReqId());
//				Question childQuestion = getQuestionGASF(requirementMapper, secRequirement);
//				secreqQuestion.getChildren().add(childQuestion);
//			}
//		}

    return secreqQuestion;
  }

  private String getISOControlsDetails(String isoIds) {

    String controlsInfo = "[";
    if (isoIds != null && !isoIds.equals("")) {
      String[] controlsId = isoIds.split(";");
      for (String controlId : controlsId) {

        if (this.isoControls != null && this.isoControls.getControls() != null) {
          for (ISOControl control : this.isoControls.getControls()) {
            if (control.getControlId().equals(controlId.trim())) {
              if (!controlsInfo.equals("[")) {
                controlsInfo = controlsInfo.concat(",");
              }
              controlsInfo = controlsInfo.concat(control.getControlJson());
            }
          }
        }
      }
    }
    controlsInfo = controlsInfo.concat("]");
    return controlsInfo;
  }

 /* public static void main(String[] args) {

    AssAuditDefaultService service = new AssAuditDefaultService();
    int id = (args == null || args.length == 0) ? 0 : Integer.valueOf(args[0]);
    for (int i = 1; i < 15; i++) {
      id = service.parseFile(String.valueOf(i), ++id, null);
    }
  } */

  public void importAudit() {

    try {
      AssAuditDefaultMapper auditeDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      int id = 0;
      for (int i = 1; i < 15; i++) {
        id = this.parseFile(String.valueOf(i), ++id, auditeDefaultMapper);
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  private int parseFile(String index, int id, AssAuditDefaultMapper auditeDefaultMapper) {
    PrintWriter pr = null;
    try {
      pr = new PrintWriter(index + outputfilename);
      String filename = index.concat(inputfilename);

      byte[] encoded = Files.readAllBytes(Paths.get(filename));
      String fileS = new String(encoded, StandardCharsets.UTF_8);

      String[] values = fileS.split("\\|", -1);

      for (int i = 0; i < values.length - 8; i += 8) {
        if (i == 0) {

          pr.println(buildQuestionnaire(values, id));
        } else {

          String category = values[i + 0].trim();
          String value = values[i + 1];
          String w = values[i + 2];
          String max = values[i + 3];
          String min = values[i + 4];
          String type = values[i + 5];
          String iso13 = values[i + 6];
          String iso5 = values[i + 7];

          ++id;
          String ix = getIX(category).trim();
          String parent = getParent(category).trim();
          value = value.replace("\'", "`");
          value = value.replace("\"", "");

          if (auditeDefaultMapper != null) {
            AssauditDefaultJSON item = new AssauditDefaultJSON();
            item.setId(id);
            item.setCategory(category);
            item.setIx(ix);
            item.setAvalue(value);
            item.setVw(w);
            item.setVmax(max);
            item.setVmin(min);
            item.setAtype(type);
            item.setViso13(iso13);
            item.setViso5(iso5);
            item.setParent(parent);
            auditeDefaultMapper.insert(item);
          }

          String question = text_insert.concat(String.valueOf(id)).concat(text_separator2).concat(ix)
            .concat(text_separator1).concat(getType(category).trim()).concat(text_separator1).concat(value)
            .concat(text_separator1).concat(category).concat(text_separator1).concat(parent)
            .concat(text_separator1).concat(w).concat(text_separator1).concat(max)
            .concat(text_separator1).concat(min).concat(text_separator1).concat(type)
            .concat(text_separator1).concat(iso13).concat(text_separator1).concat(iso5).concat(text_separator_end);

          pr.println(question);
        }
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
    } catch (IOException e) {
      // TODO Auto-generated catch block
    } finally {
      if (pr != null) {
        pr.close();
      }
    }
    return id;
  }

  private String buildQuestionnaire(String[] values, int id) {
    String category = values[0].trim();
    String value = values[1];
    value = value.replace('\'', '`');

    if (category.length() == 1) {
      category = "0".concat(category);
    }
    return text_insert.concat(String.valueOf(id)).concat(text_separator2).concat(category)
      .concat(text_separator1).concat(text_questionnaire).concat(text_separator1).concat(value)
      .concat(text_separator1).concat(category).concat(text_separator1).concat("")
      .concat(text_separator1).concat("").concat(text_separator1).concat("")
      .concat(text_separator1).concat("").concat(text_separator1).concat("")
      .concat(text_separator1).concat("").concat(text_separator1).concat("").concat(text_separator_end);
  }

  private String getType(String value) {

    if (value.matches(regexCategory2) || value.matches(regexCategory1))
      return text_category;
    else if (value.matches(regexQuestion))
      return text_question;
    else
      return "";
  }

  private String getParent(String value) {

    if (value.matches(regexCategory1))
      return value.substring(0, 2);
    else if (value.matches(regexCategory2))
      return value.substring(0, 3);
    else if (value.matches(regexQuestion))
      return value.substring(0, 5);
    else if (value.matches(regexQuestionnaire))
      return value;
    else
      return "";
  }

  private String getIX(String value) {

    if (value.matches(regexCategory1))
      return value.substring(0, 2);
    else if (value.matches(regexCategory2))
      return value.substring(3, 5);
    else if (value.matches(regexQuestion))
      return value.substring(6, 8);
    else if (value.matches(regexQuestionnaire))
      return value;
    else
      return "";
  }

  public ISOControls getIsoControls() {
    return isoControls;
  }

  public void setIsoControls(ISOControls isoControls) {
    this.isoControls = isoControls;
  }
}
