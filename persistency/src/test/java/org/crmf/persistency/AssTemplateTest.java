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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

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
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.project.AssprocedureService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.mapper.risk.RiskService;
import org.crmf.persistency.mapper.safeguard.SafeguardService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssTemplateTest {

  public QuestionnaireService questionnaireService;
  public AssAuditService auditService;

  PersistencySessionFactory sessionFactory;
  private AssprocedureService procedureService;
  private AsstemplateService templateService;
  AssessmentTemplate template;

  private AssprojectService projectService;
  private AssprofileService profileService;
  private UserService userService;

  private AssetService assetService;
  private VulnerabilityService vulnService;
  private ThreatService threatService;
  private RiskService riskService;
  private SafeguardService safeguardService;

  private TestData data = new TestData();

  @Before
  public void setUp() {

    sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    procedureService = new AssprocedureService();
    procedureService.setSessionFactory(sessionFactory);

    templateService = new AsstemplateService();
    templateService.setSessionFactory(sessionFactory);

    userService = new UserService();
    userService.setSessionFactory(sessionFactory);

    profileService = new AssprofileService();
    profileService.setSessionFactory(sessionFactory);

    questionnaireService = new QuestionnaireService();
    questionnaireService.setSessionFactory(sessionFactory);

    auditService = new AssAuditService();
    auditService.setSessionFactory(sessionFactory);
    auditService.setQuestionnaireService(questionnaireService);

    SysprojectService sysprojectService = new SysprojectService();
    sysprojectService.setSessionFactory(sessionFactory);

    QuestionnaireService questionnaireService = new QuestionnaireService();
    questionnaireService.setSessionFactory(sessionFactory);

    AssAuditDefaultService auditDefaultService = new AssAuditDefaultService();
    auditDefaultService.setSessionFactory(sessionFactory);

    AssAuditService auditService = new AssAuditService();
    auditService.setSessionFactory(sessionFactory);
    auditService.setQuestionnaireService(questionnaireService);

    projectService = new AssprojectService();
    projectService.setSessionFactory(sessionFactory);
    projectService.setSysprjService(sysprojectService);
    projectService.setAuditService(auditService);

    assetService = new AssetService();
    assetService.setSessionFactory(sessionFactory);

    vulnService = new VulnerabilityService();
    vulnService.setSessionFactory(sessionFactory);

    threatService = new ThreatService();
    threatService.setSessionFactory(sessionFactory);

    riskService = new RiskService();
    riskService.setSessionFactory(sessionFactory);

    safeguardService = new SafeguardService();
    safeguardService.setSessionFactory(sessionFactory);
    //template = prefill();
  }

  @After
  public void tearDown() throws Exception {

    CleanDatabaseService cleaner = new CleanDatabaseService();
    cleaner.setSessionFactory(sessionFactory);

    cleaner.delete();
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
    assertEquals("template02", templateResulted.getName());
    assertEquals("description template02", templateResulted.getDescription());
    assertEquals("HTRA", templateResulted.getRiskMethodology().name());
    assertEquals("Initial", templateResulted.getPhase().name());
  }

  @Test
  public void getAll() throws Exception {

    template = buildModelAssTemplate("3");
    template.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = null;

    identifier = templateService.insert(template, null);
    template.setIdentifier(identifier);


    List<AssessmentTemplate> templateResulted = templateService.getAll();
    assertEquals(2, templateResulted.size());
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