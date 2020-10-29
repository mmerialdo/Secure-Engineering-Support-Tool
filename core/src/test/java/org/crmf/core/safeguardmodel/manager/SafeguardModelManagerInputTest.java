/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.core.safeguardmodel.manager;

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SafeguardModelManagerInputTest {
  @Mock
  private SafeguardServiceInterface safeguardService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private RiskServiceInterface riskModelService;

  @InjectMocks
  private SafeguardModelManagerInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void loadSafeguardModel() throws Exception {

    String procedureIdentifier = "someProcedureIdentifier";
    String safeguardModelIdentifier = "someSGIdentifier";
    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    SafeguardModel safeguardModel = new SafeguardModel();
    safeguardModel.setIdentifier(safeguardModelIdentifier);
    SestSafeguardModel sestSafeguardModel = new SestSafeguardModel();
    sestSafeguardModel.setSestobjId(safeguardModelIdentifier);
    sestSafeguardModel.setSafeguardModelJson("some safeguard model json");
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    procedure.setSafeguardModel(safeguardModel);

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(safeguardService.getByIdentifier(safeguardModelIdentifier)).thenReturn(sestSafeguardModel);

    String safeguardModelJson = manager.loadSafeguardModel(filter);

    Assertions.assertEquals(sestSafeguardModel.getSafeguardModelJson(), safeguardModelJson);
  }

  @Test
  public void loadSafeguardModelNullProcedure() {
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(new HashMap<>());

    Assertions.assertThrows(Exception.class, () -> {
      manager.loadSafeguardModel(filter);
    });
  }

  @Test
  public void editSafeguardModelWithValue() {

    String safeguardModelIdentifier = "safeguardModelIdentifier";
    BuildObjects buildObjects = new BuildObjects(safeguardModelIdentifier, this.auditQuestionnaireJson()).invoke();
    SafeguardModel safeguardModel = buildObjects.getSafeguardModel();
    AssessmentProject project = buildObjects.getProject();

    // call edit safeguard with different score
    manager.editSafeguardModel(project);

    Assertions.assertEquals(SafeguardScoreEnum.VERY_HIGH, safeguardModel.getSafeguards().get(0).getScore());

    verify(safeguardService).update(anyString(), anyString());
    verify(assprocedureService).update(any());
  }

  @Test
  public void createSafeguardModel() {

    String safeguardModelIdentifier = "safeguardModelIdentifier";
    BuildObjects buildObjects = new BuildObjects(safeguardModelIdentifier, this.auditQuestionnaireJson()).invoke();
    SafeguardModel safeguardModel = buildObjects.getSafeguardModel();
    AssessmentProject project = buildObjects.getProject();
    safeguardModel.setSafeguards(new ArrayList<>());

    when(riskModelService.getRiskScenarioReference()).thenReturn(new ArrayList<>());

    manager.createSafeguardModel(safeguardModel, project);

    Assertions.assertEquals(1, safeguardModel.getSafeguards().size());
    Assertions.assertEquals("01A01", safeguardModel.getSafeguards().get(0).getCatalogueId());
    Assertions.assertEquals(SafeguardScoreEnum.VERY_HIGH, safeguardModel.getSafeguards().get(0).getScore());
  }

  @Test
  public void createSafeguardModelNewSecurityRequirement() {

    String safeguardModelIdentifier = "safeguardModelIdentifier";
    BuildObjects buildObjects = new BuildObjects(safeguardModelIdentifier, this.auditQuestionnaireJsonWithSecurityRequirement()).invoke();
    SafeguardModel safeguardModel = buildObjects.getSafeguardModel();
    AssessmentProject project = buildObjects.getProject();
    safeguardModel.setSafeguards(new ArrayList<>());

    when(riskModelService.getRiskScenarioReference()).thenReturn(new ArrayList<>());

    manager.createSafeguardModel(safeguardModel, project);

    Assertions.assertEquals(1, safeguardModel.getSafeguards().size());
    Assertions.assertEquals("01A01", safeguardModel.getSafeguards().get(0).getCatalogueId());
    Assertions.assertEquals(1, safeguardModel.getSafeguards().get(0).getRelatedSecurityRequirements().size());
    Assertions.assertEquals("GASF_0041", safeguardModel.getSafeguards().get(0).getRelatedSecurityRequirements().get(0).getId());
    //safeguard takes score from questionnaire json
    Assertions.assertEquals(SafeguardScoreEnum.VERY_HIGH, safeguardModel.getSafeguards().get(0).getScore());
  }

  @Test
  public void editSafeguardModelUpdateSecurityRequirement() {

    String safeguardModelIdentifier = "safeguardModelIdentifier";
    BuildObjects buildObjects = new BuildObjects(safeguardModelIdentifier, this.auditQuestionnaireJsonWithSecurityRequirement()).invoke();
    SafeguardModel safeguardModel = buildObjects.getSafeguardModel();
    AssessmentProject project = buildObjects.getProject();
    SecurityRequirement securityRequirement = new SecurityRequirement();
    securityRequirement.setId("GASF_0041");
    securityRequirement.setScore(SafeguardScoreEnum.HIGH);
    ArrayList<SecurityRequirement> securityRequirements = new ArrayList<>();
    securityRequirements.add(securityRequirement);
    safeguardModel.getSafeguards().get(0).setRelatedSecurityRequirements(securityRequirements);

    when(riskModelService.getRiskScenarioReference()).thenReturn(new ArrayList<>());

    manager.editSafeguardModel(project);

    Assertions.assertEquals(1, safeguardModel.getSafeguards().size());
    Assertions.assertEquals("01A01", safeguardModel.getSafeguards().get(0).getCatalogueId());
    Assertions.assertEquals(SafeguardScoreEnum.VERY_HIGH, safeguardModel.getSafeguards().get(0).getScore());
    // TODO review children vs gasf
 //   Assertions.assertEquals(1, safeguardModel.getSafeguards().get(0).getRelatedSecurityRequirements().size());
    // security requirement value can be 1 or none
 //   Assertions.assertEquals(SafeguardScoreEnum.LOW, safeguardModel.getSafeguards().get(0).getRelatedSecurityRequirements().get(0).getScore());
  }

  private String auditQuestionnaireJson() {
    return "{\"category\":\"01\",\n" +
      "   \"index\":\"1\",\n" +
      "   \"type\":\"MEHARI_OrganizationSecurity\",\n" +
      "   \"questions\":[{\n" +
      "         \"category\":\"01A\",\n" +
      "         \"index\":\"1\",\n" +
      "         \"type\":\"CATEGORY\",\n" +
      "         \"value\":\"Roles and Structures of Security\",\n" +
      "         \"children\":[ {\"category\":\"01A01\",\n" +
      "               \"index\":\"1\",\n" +
      "               \"type\":\"CATEGORY\",\n" +
      "               \"value\":\"Organization and Management of General Security\",\n" +
      "               \"children\":[{\"category\":\"01A01-01\",\n" +
      "                     \"index\":\"1\",\n" +
      "                     \"type\":\"QUESTION\",\n" +
      "                     \"value\":\"Are dedicated managers  (computer and telecom security, general security in the workplace..\",\n" +
      "                     \"children\":[],\n" +
      "                     \"gasf\":[],\n" +
      "                     \"answers\":{\"MEHARI_W\":\"4\",\"MEHARI_Min\":\"\",\"MEHARI_ISO13\":\"\",\"MEHARI_ISO13_info\":\"[]\",\"MEHARI_Max\":\"2\",\"MEHARI_ISO5\":\"\"},\n" +
      "                     \"referenceObject\":null,\n" +
      "                     \"identifier\":null,\n" +
      "                     \"objType\":\"Audit\",\n" +
      "                     \"lockedBy\":\"null\\n\" }]," +
      "               \"gasf\":[],\n" +
      "               \"answers\":{ \"MEHARI_R_V1\":\"4\", \"Comment\":\"some text\", \"MEHARI_W\":\"4\",\"MEHARI_Min\":\"\",\"MEHARI_ISO13\":\"\"," +
      "               \"MEHARI_ISO13_info\":\"[]\",\"MEHARI_Max\":\"2\",\"MEHARI_ISO5\":\"\"},\n" +
      "               \"referenceObject\":null,\n" +
      "               \"identifier\":null,\n" +
      "               \"objType\":\"Audit\",\n" +
      "               \"lockedBy\":\"null\"}]}]}";
  }

  private String auditQuestionnaireJsonWithSecurityRequirement() {
    return "{\"category\":\"01\",\n" +
      "   \"index\":\"1\",\n" +
      "   \"type\":\"MEHARI_OrganizationSecurity\",\n" +
      "   \"questions\":[{\n" +
      "         \"category\":\"01A\",\n" +
      "         \"index\":\"1\",\n" +
      "         \"type\":\"CATEGORY\",\n" +
      "         \"value\":\"Roles and Structures of Security\",\n" +
      "         \"children\":[ {\"category\":\"01A01\",\n" +
      "               \"index\":\"1\",\n" +
      "               \"type\":\"CATEGORY\",\n" +
      "               \"value\":\"Organization and Management of General Security\",\n" +
      "               \"children\":[{\"category\":\"01A01-01\",\n" +
      "                     \"index\":\"1\",\n" +
      "                     \"type\":\"QUESTION\",\n" +
      "                     \"value\":\"Are dedicated managers  (computer and telecom security, general security in the workplace..\",\n" +
      "                     \"children\":[],\n" +
      "                     \"gasf\":[],\n" +
      "                     \"answers\": {\"MEHARI_W\":\"4\",\"MEHARI_Min\":\"\",\"MEHARI_ISO13\":\"\",\"MEHARI_ISO13_info\":\"[]\",\"MEHARI_Max\":\"2\",\"MEHARI_ISO5\":\"\"},\n" +
      "                     \"referenceObject\":null,\n" +
      "                     \"identifier\":null,\n" +
      "                     \"objType\":\"Audit\",\n" +
      "                     \"lockedBy\":\"null\\n\" }]," +
      "              \"gasf\": [{\"category\": \"GASF_0041\",\"index\": \"01\",\"type\": \"GASF\",\n" +
      "              \"value\": \"Define assumptions on system environment\",\"children\": [],\"gasf\": [],\n" +
      "              \"answers\": {\"MEHARI_R_V1\":\"1\",\"MEHARI_W\":\"2\"},\n" +
      "              \"referenceObject\": null,\"identifier\": null,\"objType\": \"Audit\",\"lockedBy\": null}],\n" +
      "               \"answers\":{ \"MEHARI_R_V1\":\"4\", \"Comment\":\"some text\", \"MEHARI_W\":\"4\",\"MEHARI_Min\":\"\",\"MEHARI_ISO13\":\"\",\n" +
      "               \"MEHARI_ISO13_info\":\"[]\",\"MEHARI_Max\":\"2\",\"MEHARI_ISO5\":\"\"},\n" +
      "               \"referenceObject\":null,\n" +
      "               \"identifier\":null,\n" +
      "               \"objType\":\"Audit\",\n" +
      "               \"lockedBy\":\"null\"}]}]}";
  }

  private class BuildObjects {
    private String safeguardModelIdentifier;
    private String auditQuestionnaireJson;
    private SafeguardModel safeguardModel;
    private AssessmentProject project;

    public BuildObjects(String safeguardModelIdentifier, String auditQuestionnaireJson) {
      this.safeguardModelIdentifier = safeguardModelIdentifier;
      this.auditQuestionnaireJson = auditQuestionnaireJson;
    }

    public SafeguardModel getSafeguardModel() {
      return safeguardModel;
    }

    public AssessmentProject getProject() {
      return project;
    }

    public BuildObjects invoke() {
      Safeguard safeguard1 = new Safeguard();
      safeguard1.setCatalogueId("01A01");
      safeguard1.setScore(SafeguardScoreEnum.LOW);
      ArrayList<Safeguard> safeguards = new ArrayList<>();
      safeguards.add(safeguard1);
      safeguardModel = new SafeguardModel();
      safeguardModel.setIdentifier(safeguardModelIdentifier);
      safeguardModel.setSafeguards(safeguards);
      AssessmentProcedure procedure = new AssessmentProcedure();
      procedure.setStatus(AssessmentStatusEnum.OnGoing);
      procedure.setIdentifier("procedureIdentifier");
      procedure.setSafeguardModel(safeguardModel);
      ArrayList<AssessmentProcedure> procedures = new ArrayList<>();
      procedures.add(procedure);
      SestQuestionnaireModel questionnaireModel = new SestQuestionnaireModel();
      questionnaireModel.setQuestionnaireModelJson(auditQuestionnaireJson);
      ArrayList questionnaireModelList = new ArrayList<>();
      questionnaireModelList.add(questionnaireModel);
      SestAuditModel auditModel = new SestAuditModel();
      auditModel.setSestQuestionnaireModel(questionnaireModelList);
      auditModel.setType(AuditTypeEnum.SECURITY);
      auditModel.setIdentifier("auditModelIdentifier");
      auditModel.setObjType(SESTObjectTypeEnum.Audit);
      ArrayList<SestAuditModel> audits = new ArrayList<>();
      audits.add(auditModel);
      project = new AssessmentProject();
      project.setProcedures(procedures);
      project.setAudits(audits);
      return this;
    }
  }
}
