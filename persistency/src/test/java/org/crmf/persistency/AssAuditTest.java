/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditTest.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.model.audit.*;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AssAuditTest {

  @Autowired
  private QuestionnaireService questionnaireService;
  @Autowired
  private AssAuditService auditService;
  @Autowired
  private AssAuditDefaultService auditDefaultService;
  @Autowired
  private AssprojectService projectService;
  @Autowired
  private TestData data;

  String projectIdentifier = null;

  @Test
  public void getAllQuestionnaireNames() throws Exception {

    this.data.prefillModels();
    String projectIdentifier = prefillProject();

    List<SestAuditModel> audits = auditService.getAllForProject(projectIdentifier);
    Assertions.assertEquals(1, audits.size());
    if (audits.get(0).getType() == AuditTypeEnum.SECURITY) {
      List<SestQuestionnaireModel> questionnaires = questionnaireService.getAllQuestionnaireNames(audits.get(0).getIdentifier());
      Assertions.assertEquals(2, questionnaires.size());
      for (SestQuestionnaireModel questionnaireModel : questionnaires) {
        if (questionnaireModel.getCategory() == "01") {
          Assertions.assertEquals(QuestionnaireTypeEnum.MEHARI_OrganizationSecurity, questionnaireModel.getType());
          Assertions.assertEquals("1", questionnaireModel.getIx());
          Assertions.assertEquals(null, questionnaireModel.getQuestionnaireModelJson());
        } else if (questionnaireModel.getCategory() == "02") {
          Assertions.assertEquals(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork, questionnaireModel.getType());
          Assertions.assertEquals("02", questionnaireModel.getCategory());
          Assertions.assertEquals("2", questionnaireModel.getIx());
          Assertions.assertEquals(null, questionnaireModel.getQuestionnaireModelJson());
        } else {
          Assertions.fail();
        }
      }
    }
  }

  @Test
  public void getByIdentifier() throws Exception {
    this.data.prefillModels();
    String projectIdentifier = prefillProject();
    QuestionnaireModelSerializerDeserializer converter = new QuestionnaireModelSerializerDeserializer();

    List<SestAuditModel> audits = auditService.getAllForProject(projectIdentifier);
    Assertions.assertEquals(1, audits.size());
    if (audits.get(0).getType() == AuditTypeEnum.SECURITY) {
      List<SestQuestionnaireModel> questionnaires = questionnaireService.getAllQuestionnaireNames(audits.get(0).getIdentifier());
      Assertions.assertEquals(2, questionnaires.size());
      for (SestQuestionnaireModel questionnaireModel : questionnaires) {
        SestQuestionnaireModel questionnaireModelWithJson = questionnaireService.getByIdentifier(questionnaireModel.getIdentifier());
        if (QuestionnaireTypeEnum.MEHARI_OrganizationSecurity.equals(questionnaireModelWithJson.getType())) {
          Assertions.assertEquals(QuestionnaireTypeEnum.MEHARI_OrganizationSecurity, questionnaireModelWithJson.getType());
          Assertions.assertEquals("1", questionnaireModelWithJson.getIx());
          Questionnaire questionnaireResulted = converter.getQuestionnaireFromJSONString(questionnaireModelWithJson.getQuestionnaireModelJson());
          Assertions.assertEquals(1, questionnaireResulted.getQuestions().size());
          Assertions.assertEquals("01A", questionnaireResulted.getQuestions().get(0).getCategory());
          Assertions.assertEquals(QuestionTypeEnum.CATEGORY, questionnaireResulted.getQuestions().get(0).getType());
          Assertions.assertEquals("Roles and structures of security", questionnaireResulted.getQuestions().get(0).getValue());
          Assertions.assertEquals(1, questionnaireResulted.getQuestions().get(0).getChildren().size());
          Assertions.assertEquals(QuestionTypeEnum.QUESTION, questionnaireResulted.getQuestions().get(0).getChildren().get(0).getType());
          Assertions.assertEquals("Organization and Management of General Security", questionnaireResulted.getQuestions().get(0).getChildren().get(0).getValue());
        } else {
          Assertions.assertEquals(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork, questionnaireModelWithJson.getType());
          Assertions.assertEquals("02", questionnaireModelWithJson.getCategory());
          Assertions.assertEquals("2", questionnaireModelWithJson.getIx());
        }
      }
    }
  }

  private String prefillProject() throws Exception {

    AssauditDefaultJSON questionnaireDefaultJSON01 = new AssauditDefaultJSON();
    questionnaireDefaultJSON01.setAtype("QUESTIONNAIRE");
    questionnaireDefaultJSON01.setAvalue(QuestionnaireTypeEnum.MEHARI_OrganizationSecurity.name());
    questionnaireDefaultJSON01.setCategory("01");
    questionnaireDefaultJSON01.setIx("1");
    questionnaireDefaultJSON01.setQuestionnaireJSON("{\"category\":\"01\", \"index\":1, \"type\":\"MEHARI_OrganizationSecurity\", " +
      "\"questions\":[{\"category\":\"01A\",\"index\":1,\"type\":\"CATEGORY\",\"value\":\"Roles and structures of security\"," +
      "\"children\":[{\"category\":\"01A01\",\"index\":1,\"type\":\"QUESTION\",\"value\":\"Organization and Management of General Security\"," +
      "\"children\":[]}]}]}");
    AssauditDefaultJSON questionnaireDefaultJSON02 = new AssauditDefaultJSON();
    questionnaireDefaultJSON02.setAtype("QUESTIONNAIRE");
    questionnaireDefaultJSON02.setAvalue(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork.name());
    questionnaireDefaultJSON02.setCategory("02");
    questionnaireDefaultJSON02.setIx("2");
    questionnaireDefaultJSON02.setQuestionnaireJSON("{}");
    auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON01);
    auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON02);

    AssessmentProject project = data.buildModelAssProject("1");

    projectIdentifier = projectService.insert(project);

    project.setIdentifier(projectIdentifier);

    return projectIdentifier;
  }
}
