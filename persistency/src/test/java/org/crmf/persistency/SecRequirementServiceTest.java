/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="SecRequirementServiceTest.jaca"
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
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.secrequirement.SecRequirementService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/************************************************************************
 * Created: 20/12/2019                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class SecRequirementServiceTest {
  PersistencySessionFactory sessionFactory;
  SecRequirementService secRequirementService;
  AssAuditDefaultService auditDefaultService;

  @Before
  public void setUp() throws Exception {

    sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    auditDefaultService = new AssAuditDefaultService();
    auditDefaultService.setSessionFactory(sessionFactory);

    secRequirementService = new SecRequirementService();
    secRequirementService.setSessionFactory(sessionFactory);
  }

  @After
  public void tearDown() throws Exception {

    CleanDatabaseService cleaner = new CleanDatabaseService();
    cleaner.setSessionFactory(sessionFactory);
    cleaner.delete();
  }

  @Test
  @Ignore
  public void insertSecRequirementSafeguardTest() {

    AssauditDefaultJSON questionnaireDefaultJSON01 = new AssauditDefaultJSON();
    questionnaireDefaultJSON01.setAtype(AuditTypeEnum.SECURITY.name());
    questionnaireDefaultJSON01.setAvalue(QuestionnaireTypeEnum.MEHARI_OrganizationSecurity.name());
    questionnaireDefaultJSON01.setCategory("01");
    questionnaireDefaultJSON01.setIx("1");
    questionnaireDefaultJSON01.setQuestionnaireJSON("{\"category\":\"01\", \"index\":1, \"type\":\"MEHARI_OrganizationSecurity\", " +
      "\"questions\":[{\"category\":\"01A\",\"index\":1,\"type\":\"CATEGORY\",\"value\":\"Roles and structures of security\"," +
      "\"children\":[{\"category\":\"01A01\",\"index\":1,\"type\":\"CATEGORY\",\"value\":\"Organization and Management of General Security\"," +
      "\"children\":[]}]}]}");
    SecurityRequirement sec01 = new SecurityRequirement();
    sec01.setIdentifier("GASF_0041");
    sec01.setCategory("3");
    sec01.setId("GASF_0041");
    sec01.setScore(SafeguardScoreEnum.HIGH);
    sec01.setSourceDescription("gasf GASF_0041");
    SecurityRequirement sec02 = new SecurityRequirement();
    sec02.setIdentifier("GASF_0042");
    sec02.setCategory("2");
    sec02.setId("GASF_0042");
    sec02.setSourceDescription("gasf GASF_0042");
    auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON01);
    secRequirementService.insertSecurityRequirement(sec01);
    secRequirementService.insertSecurityRequirement(sec02);

    String[] gasfSecReq1 = {"GASF_0041","01A01","3"};
    String[] gasfSecReq2 = {"GASF_0042","01A01","2"};
    secRequirementService.insertSecRequirementSafeguard(Arrays.asList(gasfSecReq1, gasfSecReq2), null);

    List<AssauditDefaultJSON> questionnaires = auditDefaultService.getAllQuestionnaires();
    assertEquals(1, questionnaires.size());
    assertTrue(questionnaires.get(0).getQuestionnaireJSON().contains("GASF"));
  }
}
