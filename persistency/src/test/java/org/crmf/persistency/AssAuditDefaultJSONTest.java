/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditDefaultJSONTest.java"
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

import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@Import({AssAuditDefaultService.class})
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class AssAuditDefaultJSONTest {

  @Autowired
  AssAuditDefaultService auditDefaultService;

  @Test
  public void insertAudit() {

    AssauditDefaultJSON questionnaireDefaultJSON = new AssauditDefaultJSON();
    questionnaireDefaultJSON.setAtype("QUESTIONNAIRE");
    questionnaireDefaultJSON.setAvalue(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork.name());
    questionnaireDefaultJSON.setCategory("01");
    questionnaireDefaultJSON.setIx("1");
    questionnaireDefaultJSON.setQuestionnaireJSON("{}");
    auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON);

    List<AssauditDefaultJSON> questionnaires = auditDefaultService.getAllQuestionnaires();
    Assertions.assertNotNull(questionnaires);
    Assertions.assertEquals(1, questionnaires.size());
  }

  @Test
  public void createJSONAudit() {

    auditDefaultService.createQuestionnaire();
  }
}
