/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssTemplateTest.java"
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

import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.persistency.mapper.asset.AssetService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
import org.crmf.persistency.mapper.project.AssprocedureService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.risk.RiskService;
import org.crmf.persistency.mapper.safeguard.SafeguardService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AssTemplateTest {

  @Autowired
  private QuestionnaireService questionnaireService;
  @Autowired
  private AssAuditService auditService;
  @Autowired
  private AssprocedureService procedureService;
  @Autowired
  private AsstemplateService templateService;
  @Autowired
  private AssprojectService projectService;
  @Autowired
  private AssprofileService profileService;
  @Autowired
  private UserService userService;
  @Autowired
  private AssetService assetService;
  @Autowired
  private VulnerabilityService vulnService;
  @Autowired
  private ThreatService threatService;
  @Autowired
  private RiskService riskService;
  @Autowired
  private SafeguardService safeguardService;
  @Autowired
  private TestData data;

  private AssessmentTemplate template;

  @BeforeEach
  public void setUp() {
    this.data.prefillModels();
  }

  @Test
  public void insert() throws Exception {

    template = buildModelAssTemplate("2");
    template.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = null;
    try {
      identifier = templateService.insert(template, null);
      template.setIdentifier(identifier);
    } catch (Exception e) {
    }

    AssessmentTemplate templateResulted = templateService.getByIdentifier(identifier);
    Assertions.assertEquals("template02", templateResulted.getName());
    Assertions.assertEquals("description template02", templateResulted.getDescription());
    Assertions.assertEquals("HTRA", templateResulted.getRiskMethodology().name());
    Assertions.assertEquals("Initial", templateResulted.getPhase().name());
  }

  @Test
  public void getAll() throws Exception {

    template = buildModelAssTemplate("3");
    template.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = null;

    identifier = templateService.insert(template, null);
    template.setIdentifier(identifier);


    List<AssessmentTemplate> templateResulted = templateService.getAll();
    Assertions.assertEquals(2, templateResulted.size());
  }

  private AssessmentTemplate prefill() throws Exception {

    template = buildModelAssTemplate("1");
    String identifier = templateService.insert(template, null);
    template.setIdentifier(identifier);

    return template;
  }

  //builds template object, inserts 1 project with 1 template
  private AssessmentTemplate buildModelAssTemplate(String ix) throws Exception {

    String projectIdentifier = prefillProject();

    AssetModel asset = new AssetModel();
    String newIdentifier = UUID.randomUUID().toString();
    asset.setIdentifier(newIdentifier);
    assetService.insert("{ }", newIdentifier);

    VulnerabilityModel vuln = new VulnerabilityModel();
    newIdentifier = UUID.randomUUID().toString();
    vuln.setIdentifier(newIdentifier);
    vulnService.insert("{ }", newIdentifier);

    ThreatModel threat = new ThreatModel();
    newIdentifier = UUID.randomUUID().toString();
    threat.setIdentifier(newIdentifier);
    threatService.insert("{ }", newIdentifier);


    SafeguardModel sg = new SafeguardModel();
    newIdentifier = UUID.randomUUID().toString();
    sg.setIdentifier(newIdentifier);
    safeguardService.insert("{ }", newIdentifier);

    RiskModel risk = new RiskModel();
    newIdentifier = UUID.randomUUID().toString();
    risk.setIdentifier(newIdentifier);
    riskService.insert("{ }", newIdentifier);

    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setPhase(PhaseEnum.Design);
    procedure.setStatus(AssessmentStatusEnum.Closed);
    procedure.setAssetModel(asset);
    procedure.setVulnerabilityModel(vuln);
    procedure.setThreatModel(threat);
    procedure.setSafeguardModel(sg);
    procedure.setRiskModel(risk);
    procedureService.insert(procedure, projectIdentifier);

    AssessmentTemplate template = new AssessmentTemplate();
    template.setName("template0" + ix);
    template.setDescription("description template0" + ix);
    template.setPhase(PhaseEnum.Initial);
    template.setRiskMethodology(RiskMethodologyEnum.Mehari);
    template.setAssetModel(asset);
    template.setVulnerabilityModel(vuln);
    template.setThreatModel(threat);
    template.setSafeguardModel(sg);
    template.setRiskModel(risk);
//		template.setAssociatedProcedure(procedure);

    return template;
  }

  private String prefillProject() throws Exception {

    AssessmentProject project = data.buildModelAssProject("1");
    String projectIdentifier = projectService.insert(project);
    project.setIdentifier(projectIdentifier);
    return projectIdentifier;
  }
}