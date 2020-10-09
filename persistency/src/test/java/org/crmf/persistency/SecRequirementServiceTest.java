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
import org.crmf.persistency.mapper.project.SysparticipantService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.mapper.secrequirement.SecRequirementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@Import({SysparticipantService.class, SysprojectService.class})
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class SecRequirementServiceTest {

  @Autowired
  SecRequirementService secRequirementService;
  @Autowired
  AssAuditDefaultService auditDefaultService;

  @Test
  @Disabled
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
    Assertions.assertEquals(1, questionnaires.size());
    Assertions.assertTrue(questionnaires.get(0).getQuestionnaireJSON().contains("GASF"));
  }
}
