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

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AssAuditDefaultJSONTest {

  PersistencySessionFactory sessionFactory;
  AssAuditDefaultService auditDefaultService;

  @Before
  public void setUp() throws Exception {

    sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    auditDefaultService = new AssAuditDefaultService();
    auditDefaultService.setSessionFactory(sessionFactory);
  }

 /* @After
  public void tearDown() throws Exception {
    CleanDatabaseService cleaner = new CleanDatabaseService();
    cleaner.setSessionFactory(sessionFactory);
    cleaner.delete();
  } */

  @Ignore
  @Test
  public void insertAudit() {

    AssauditDefaultJSON questionnaireDefaultJSON = new AssauditDefaultJSON();
    questionnaireDefaultJSON.setAtype(AuditTypeEnum.SECURITY.name());
    questionnaireDefaultJSON.setAvalue(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork.name());
    questionnaireDefaultJSON.setCategory("01B");
    questionnaireDefaultJSON.setIx("1");
    questionnaireDefaultJSON.setQuestionnaireJSON("{}");
    auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON);

    List<AssauditDefaultJSON> questionnaires = auditDefaultService.getAllQuestionnaires();
    assertNotNull(questionnaires);
    assertEquals(1, questionnaires.size());
  }

  @Test
  public void createJSONAudit() {

    auditDefaultService.createQuestionnaire();
  }
}
