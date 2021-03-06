/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssauditDefaultTest.java"
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

import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessmentelements.GeneralScore;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.ScoreEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatClassEnum;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.riskassessmentelements.Vulnerability;
import org.crmf.model.riskassessmentelements.VulnerabilityExploitabilityEnum;
import org.crmf.model.riskassessmentelements.VulnerabilityScoreEnum;
import org.crmf.model.riskassessmentelements.VulnerabilitySourceEnum;
import org.crmf.persistency.mapper.risk.RiskService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@Import({RiskService.class, VulnerabilityService.class, ThreatService.class})
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class RiskServiceTest {

  @Autowired
  RiskService riskService;
  @Autowired
  VulnerabilityService vulnerabilityService;
  @Autowired
  ThreatService threatService;

  @BeforeEach
  public void setUp() throws Exception {

    this.insertVulnerability();
    this.insertThreat();
  }

  @Test
  public void insertRiskScenarioReference() throws Exception{

    RiskScenarioReference riskScenarioReference = new RiskScenarioReference();
    riskScenarioReference.setAssetType(PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control);
    riskScenarioReference.setSupportingAsset(SecondaryAssetCategoryEnum.Communication_Network);
    riskScenarioReference.setAice(SecurityImpactScopeEnum.Accountability);
    riskScenarioReference.setVulnerabilityCode("vulnearability.abc");
    riskScenarioReference.setEventType("ER.P");
    riskScenarioReference.setEventSubType("Pro");
    riskScenarioReference.setPlace("Exp");
    riskScenarioReference.setTime("");
    riskScenarioReference.setAccess("Ain");
    riskScenarioReference.setProcess("");
    riskScenarioReference.setActor("Ual");
    riskScenarioReference.setDissuasion("03D01");
    riskScenarioReference.setPrevention("03D02");
    riskScenarioReference.setConfining("03D03");
    riskScenarioReference.setPalliative("03D04");
    riskService.insertRiskScenarioReference(riskScenarioReference);
    List<RiskScenarioReference> scenarios = riskService.getRiskScenarioReference();
    Assertions.assertNotNull(scenarios);
    Assertions.assertEquals(1, scenarios.size());
    Assertions.assertEquals("ER.P", scenarios.get(0).getEventType());
    Assertions.assertEquals("vulnearability.abc", scenarios.get(0).getVulnerabilityCode());
    Assertions.assertEquals("03D04", scenarios.get(0).getPalliative());
  }

  @Test
  public void editRiskScenarioReference() throws Exception{

    RiskScenarioReference riskScenarioReference = new RiskScenarioReference();
    riskScenarioReference.setAssetType(PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control);
    riskScenarioReference.setSupportingAsset(SecondaryAssetCategoryEnum.Communication_Network);
    riskScenarioReference.setAice(SecurityImpactScopeEnum.Accountability);
    riskScenarioReference.setVulnerabilityCode("vulnearability.abc");
    riskScenarioReference.setEventType("ER.P");
    riskScenarioReference.setEventSubType("Pro");
    riskScenarioReference.setPlace("Exp");
    riskScenarioReference.setTime("");
    riskScenarioReference.setAccess("Ain");
    riskScenarioReference.setProcess("");
    riskScenarioReference.setActor("Ual");
    riskScenarioReference.setDissuasion("03D01");
    riskScenarioReference.setPrevention("03D02");
    riskScenarioReference.setConfining("03D03");
    riskScenarioReference.setPalliative("03D04");
    String identifier = riskService.insertRiskScenarioReference(riskScenarioReference);
    riskScenarioReference.setIdentifier(identifier);
    riskScenarioReference.setConfining("03D05");
    riskService.editRiskScenarioReference(riskScenarioReference);
    List<RiskScenarioReference> scenarios = riskService.getRiskScenarioReference();
    Assertions.assertNotNull(scenarios);
    Assertions.assertEquals(1, scenarios.size());
    Assertions.assertEquals("03D05", scenarios.get(0).getConfining());
  }

  @Test
  public void deleteRiskScenarioReference() throws Exception{

    RiskScenarioReference riskScenarioReference = new RiskScenarioReference();
    riskScenarioReference.setAssetType(PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control);
    riskScenarioReference.setSupportingAsset(SecondaryAssetCategoryEnum.Communication_Network);
    riskScenarioReference.setAice(SecurityImpactScopeEnum.Accountability);
    riskScenarioReference.setVulnerabilityCode("vulnearability.abc");
    riskScenarioReference.setEventType("ER.P");
    riskScenarioReference.setEventSubType("Pro");
    riskScenarioReference.setPlace("Exp");
    riskScenarioReference.setTime("");
    riskScenarioReference.setAccess("Ain");
    riskScenarioReference.setProcess("");
    riskScenarioReference.setActor("Ual");
    riskScenarioReference.setDissuasion("03D01");
    riskScenarioReference.setPrevention("03D02");
    riskScenarioReference.setConfining("03D03");
    riskScenarioReference.setPalliative("03D04");
    String identifier = riskService.insertRiskScenarioReference(riskScenarioReference);
    riskService.deleteRiskScenarioReference(new ArrayList<String>() {{add(identifier);}});
    List<RiskScenarioReference> scenarios = riskService.getRiskScenarioReference();
    Assertions.assertNotNull(scenarios);
    Assertions.assertEquals(0, scenarios.size());
  }

  public String insertVulnerability() throws Exception{

    Vulnerability vulnerability = new Vulnerability();
    vulnerability.setCatalogue(VulnerabilitySourceEnum.MEHARI);
    vulnerability.setCatalogueId("vulnearability.abc");
    vulnerability.setName("vulnearability.abc");
    vulnerability.setPhase(PhaseEnum.Design);
    GeneralScore score = new GeneralScore();
    score.setScoringType(ScoreEnum.CWSS);
    score.setScore(VulnerabilityScoreEnum.HIGH);
    score.setExploitability(VulnerabilityExploitabilityEnum.HIGH);
    vulnerability.setScore(score);
    vulnerability.setDescription("vulnerabilityname");

    return vulnerabilityService.insertVulnerabilityReference(vulnerability);
  }

  public String insertThreat() throws Exception{

    Threat threat = new Threat();
    threat.setCatalogue(ThreatSourceEnum.MEHARI);
    threat.setCatalogueId("ER.P.Pro-Exp--Ain--Ual");
    threat.setName("ER.P.Pro-Exp--Ain--Ual");
    threat.setPhase(PhaseEnum.Design);
    ThreatScore score = new ThreatScore();
    score.setLikelihood(LikelihoodEnum.HIGH);
    score.setScore(ThreatScoreEnum.HIGH);
    threat.setScore(score);
    threat.setDescription("threatname");
    threat.setThreatClass(ThreatClassEnum.Accidental);

    return threatService.insertThreatReference(threat);
  }
}
