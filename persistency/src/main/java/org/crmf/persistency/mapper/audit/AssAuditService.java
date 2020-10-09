/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditService.java"
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

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.AssTemplate;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.project.AssprojectMapper;
import org.crmf.persistency.mapper.project.AsstemplateMapper;
import org.crmf.persistency.mapper.safeguard.SafeguardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//This class manages the database interactions related to the Audit
@Service
@Qualifier("default")
public class AssAuditService implements AssAuditServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AssAuditService.class.getName());

  @Autowired
  private AssAuditDefaultService auditDefaultService;
  @Autowired
  private SqlSession sqlSession;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.audit.AuditServiceInterfcae#getAllForProject(
   * java.lang.String)
   */
  @Override
  public List<SestAuditModel> getAllForProject(String identifier) {
    LOG.info("called getAllForProject");
    List<SestAuditModel> auditsToSend = new ArrayList<>();
    AssAuditMapper auditeMapper = sqlSession.getMapper(AssAuditMapper.class);
    QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);

    List<SestAuditModel> audits = auditeMapper.getAllForProject(identifier);
    for (SestAuditModel audit : audits) {
      LOG.info("questionnaireMapper.getTypeByAuditId(audit.getId()) " + questionnaireMapper.getByAuditId(audit.getId()));
      audit.setObjType(SESTObjectTypeEnum.Audit);
      List<SestQuestionnaireModel> questionnaires = questionnaireMapper.getAllQuestionnaireNames(audit.getId()).
        stream().map(quest -> {
        quest.setObjType(SESTObjectTypeEnum.Audit);
        LOG.info("quest " + quest.getIdentifier());
        return quest;
      }).collect(Collectors.toList());
      audit.setSestQuestionnaireModel(questionnaires);
      auditsToSend.add(audit);
      LOG.info("added audit to listAudit");
    }
    return auditsToSend;
  }

  @Override
  public SestAuditModel getByProjectAndType(String identifier, AuditTypeEnum type, boolean includeModels) {

    LOG.info("called getByProjectAndType");

    SestAuditModel audit = null;
    AssAuditMapper auditeMapper = sqlSession.getMapper(AssAuditMapper.class);
    QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
    SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

    audit = auditeMapper.getByProjectAndType(identifier, type.name());
    LOG.info("called getByProjectAndType " + audit.getProjectId());
    List<SestQuestionnaireModel> questionnaires = null;
    if (includeModels) {
      questionnaires = questionnaireMapper.getByAuditId(audit.getId());
      LOG.info("called getByProjectAndType questionnaires " + questionnaires.size());
    } else {
      questionnaires = questionnaireMapper.getAllQuestionnaireNames(audit.getId());
    }
    questionnaires = questionnaires.stream().map(quest -> {
      quest.setObjType(SESTObjectTypeEnum.Audit);
      return quest;
    }).collect(Collectors.toList());
    audit.setSestQuestionnaireModel(questionnaires);
    Sestobj sestObj = sestobjMapper.getByIdentifier(audit.getIdentifier());
    audit.setObjType(SESTObjectTypeEnum.valueOf(sestObj.getObjtype()));
    audit.setLockedBy(sestObj.getLockedBy());
    LOG.info("called sestObj.getLockedBy() " + sestObj.getLockedBy());
    return audit;
  }

  @Override
  public int getProjectIdByIdentifier(String identifier) {
    LOG.info("called getProjectIdByIdentifier " + identifier);
    Integer projectId = null;
    try {
      AssAuditMapper auditeMapper = sqlSession.getMapper(AssAuditMapper.class);
      projectId = auditeMapper.getProjectIdByIdentifier(identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }

    return projectId;
  }

  @Override
  public void update(SestAuditModel auditDM) {

    LOG.info("Update Audit");
    try {
      QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);

      List<SestQuestionnaireModel> questionnaires = auditDM.getSestQuestionnaireModel();
      LOG.info("Update questionnaire " + questionnaires.size());
      for (SestQuestionnaireModel questionnaire : questionnaires) {
        LOG.info("Update questionnaire " + questionnaire.getType());
        LOG.info("Update questionnaire " + questionnaire.getIdentifier());
        LOG.info("Update questionnaire " + questionnaire.getId());
        LOG.info("Update questionnaire " + questionnaire.getAuditId());
        LOG.info("Update questionnaire " + auditDM.getIdentifier());
        LOG.info("Update questionnaire " + questionnaire.getQuestionnaireModelJson());
        questionnaireMapper.update(questionnaire.getIdentifier(), questionnaire.getQuestionnaireModelJson());
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public SestAuditModel insert(SestAuditModel auditModel, Integer projectId) {

    LOG.info("Insert Audit");

    // get the hashmap with safeguard - answers relation from the templates'
    // safeguardmodel
    HashMap<String, ArrayList<String>> safeguards = getSafeguardModel(projectId);

    Sestobj sestobj = null;

    try {
      AssAuditMapper auditMapper = sqlSession.getMapper(AssAuditMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.Audit.name());
      sestobjMapper.insert(sestobj);

      auditModel.setIdentifier(sestobj.getIdentifier());
      auditModel.setProjectId(projectId);
      auditModel.setType(AuditTypeEnum.SECURITY);
      auditMapper.insert(auditModel);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return auditModel;
  }

  public void insertDefaultQuestionnaires(Integer projectId) throws Exception {

    LOG.info("Insert Default Questionnaire");

    SestAuditModel auditModel = insert(new SestAuditModel(), projectId);
    int auditId = auditModel.getId();
    try {
      AssAuditDefaultMapper auditDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
      List<AssauditDefaultJSON> questionnaireDefaultList = auditDefaultMapper.getAllQuestionnaireNames();
      questionnaireDefaultList.forEach(questionnaireDefault -> {

        AssauditDefaultJSON questionnaireDefaultWithJSON = auditDefaultMapper.getByCategory(questionnaireDefault.getCategory());
        if (questionnaireDefaultWithJSON != null && questionnaireDefaultWithJSON.getQuestionnaireJSON() != null) {
          SestQuestionnaireModel questionnaireModel = new SestQuestionnaireModel();
          questionnaireModel.setType(QuestionnaireTypeEnum.valueOf(questionnaireDefault.getAvalue()));
          questionnaireModel.setCategory(questionnaireDefault.getCategory());
          questionnaireModel.setAuditId(auditId);
          questionnaireModel.setIx(questionnaireDefault.getIx());
          questionnaireModel.setCategory(questionnaireDefault.getCategory());
          questionnaireModel.setQuestionnaireModelJson(questionnaireDefaultWithJSON.getQuestionnaireJSON());
          insert(questionnaireModel, auditId);
        }
      });
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public Integer insert(SestQuestionnaireModel questionnaireModel, Integer auditId) {

    LOG.info("Insert Questionnaire");

    Sestobj sestobj = null;

    try {
      QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.Audit.name());
      sestobjMapper.insert(sestobj);

      questionnaireModel.setIdentifier(sestobj.getIdentifier());
      questionnaireModel.setAuditId(auditId);
      questionnaireMapper.insert(questionnaireModel);

      return questionnaireModel.getId();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
  }

  @Override
  public List<Question> getSafeguardByIdentifier() {

    LOG.info("getSafeguardByIdentifier ");
    List<Question> safeguards = new ArrayList<>();
    AssAuditDefaultMapper auditDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);
    List<AssauditDefaultJSON> defaultSafeguards = auditDefaultMapper.getSafeguardByIdentifier();
    defaultSafeguards.forEach(defaultSafeguard -> {
      LOG.info("getSafeguardByIdentifier " + defaultSafeguard.getAvalue());
      Question question = new Question();
      question.setCategory(defaultSafeguard.getCategory());
      question.setValue(defaultSafeguard.getAvalue());
      safeguards.add(question);
    });
    return safeguards;
  }

  /**
   * Returns the hashmap with safeguard - answers relation from the templates'
   * safeguardmodel
   *
   * @param projectId
   * @return
   */
  private HashMap<String, ArrayList<String>> getSafeguardModel(Integer projectId) {

    LOG.info("getSafeguardModel by projectId {} ", projectId);
    HashMap<String, ArrayList<String>> sgMap = new HashMap<>();
    try {

      SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);

      Integer safeguardId = null;
      // get template for project
      AssTemplate template = templateMapper.getById(projectMapper.getById(projectId).getTemplateId());
      if (template != null) {
        safeguardId = template.getSafeguardId();
      } else {
        LOG.info("template is null");
        return sgMap;
      }

      // get safeguard model for procedure
      SestSafeguardModel safeguardModelDB = safeguardMapper.getById(safeguardId);
      SafeguardModel safeguardModel = null;
      if (safeguardModelDB != null) {
        safeguardModel = safeguardModelDB.convertToModel();
      } else {
        LOG.info("safeguard model is null");
        return sgMap;
      }

      // sets values to hashmap
      List<Safeguard> safeguards = safeguardModel.getSafeguards();
      if (safeguards != null && !safeguards.isEmpty()) {
        getSafeguardModelChildren(sgMap, safeguards);
      } else {
        LOG.info("empty safeguard list into the model");
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return sgMap;
    }
    return sgMap;
  }

  /**
   * Sets values to hash map for safeguard-answers relation
   *
   * @param sgMap
   * @param safeguards
   */
  private void getSafeguardModelChildren(HashMap<String, ArrayList<String>> sgMap, List<Safeguard> safeguards) {

    for (Safeguard safeguard : safeguards) {

      String value = mapScoreToAnswer(((Safeguard) safeguard).getScore());
      String comment = (((Safeguard) safeguard).getUserDescription() == null ? ""
        : ((Safeguard) safeguard).getUserDescription());
      LOG.info("Adding {} with values {} , {} ", safeguard.getCatalogueId(), value, comment);
      sgMap.put(safeguard.getCatalogueId(), new ArrayList<>(Arrays.asList(value, comment)));

      getSafeguardModelChildren(sgMap, safeguard.getChildren());

      ArrayList<SecurityRequirement> secreqList = safeguard.getRelatedSecurityRequirements();
      if (secreqList != null && !secreqList.isEmpty()) {
        getRelatedSecurityRequirementChildren(sgMap, secreqList, safeguard.getCatalogueId());
      } else {
        LOG.info("no sec requirement related");
      }
    }
  }

  /**
   * Sets values to hash map for gasf-answers relation
   *
   * @param sgMap
   * @param secreqs
   * @param safeguardCatalogueId
   */
  private void getRelatedSecurityRequirementChildren(HashMap<String, ArrayList<String>> sgMap,
                                                     ArrayList<SecurityRequirement> secreqs, String safeguardCatalogueId) {

    for (SecurityRequirement secreq : secreqs) {

      String value = mapScoreToAnswer(((SecurityRequirement) secreq).getScore());
      String comment = (((SecurityRequirement) secreq).getUserDescription() == null ? ""
        : ((SecurityRequirement) secreq).getUserDescription());
      LOG.info("Adding {} with values {} , {} ", secreq.getId(), value, comment);
      sgMap.put(safeguardCatalogueId + secreq.getId(), new ArrayList<>(Arrays.asList(value, comment)));
      getRelatedSecurityRequirementChildren(sgMap, secreq.getChildren(), safeguardCatalogueId);
    }
  }

  private String mapScoreToAnswer(SafeguardScoreEnum score) {

    switch (score) {
      case NONE:
        return "";
      case LOW:
        return "1";
      case MEDIUM:
        return "2";
      case VERY_HIGH:
        return "3";
      case HIGH:
        return "4";
      default:
        return "";
    }
  }

  public void delete(String identifier) {

    LOG.info("deleteCascade questionnaire " + identifier);
    AssAuditMapper auditMapper = sqlSession.getMapper(AssAuditMapper.class);
    auditMapper.deleteByIdentifier(identifier);
  }

  public void createDefaultQuestionnaire() {

    LOG.info("createDefaultQuestionnaire auditDefaultService " + auditDefaultService);
    auditDefaultService.createQuestionnaire();
  }
}
