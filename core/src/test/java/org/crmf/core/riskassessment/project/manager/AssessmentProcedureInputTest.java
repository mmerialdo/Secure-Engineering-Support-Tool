/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.core.riskassessment.project.manager;

import org.crmf.core.riskassessment.utility.RiskAssessmentModelsCloner;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.domain.asset.SestAssetModel;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.risk.SestRiskTreatmentModel;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.domain.vulnerability.SestVulnerabilityModel;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentProcedureInputTest {

  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private AssprojectServiceInterface assprojectService;
  @Mock
  private AssetServiceInterface assetModelService;
  @Mock
  private VulnerabilityServiceInterface vulnerabilityModelService;
  @Mock
  private ThreatServiceInterface threatModelService;
  @Mock
  private SafeguardServiceInterface safeguardModelService;
  @Mock
  private RiskServiceInterface riskModelService;
  @Mock
  private RiskTreatmentServiceInterface riskTreatmentModelService;
  @Mock
  private AssAuditServiceInterface auditService;
  @Mock
  private UserPermissionManagerInput permissionManager;
  @Mock
  private RiskModelManagerInput riskModelInput;
  @Mock
  private AssessmentTemplateInput templateInput;
  @Mock
  private RiskAssessmentModelsCloner modelsCloner;

  @InjectMocks
  private AssessmentProcedureInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createAssessmentProcedureFromTemplate() throws Exception {

    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier("procedureIdentifier");
    procedure.setPhase(PhaseEnum.Initial);
    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier("projectIdentifier");
    project.setTemplate(template);
    SestAuditModel audit = new SestAuditModel();
    audit.setSestQuestionnaireModel(new ArrayList<>());
    project.setAudits(Arrays.asList(audit));

    SestRiskTreatmentModel treatmentModel = new SestRiskTreatmentModel();
    treatmentModel.setSestobjId("treatmentModelIdentifier");
    treatmentModel.setRiskTreatmentModelJson("{}");

    Map<SESTObjectTypeEnum, String> newModelsMap = new HashMap<>();
    newModelsMap.put(SESTObjectTypeEnum.SafeguardModel, "safeguardModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.RiskTreatmentModel, "treatmentModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.RiskModel, "riskModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.AssetModel, "assetModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.ThreatModel, "threatModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.VulnerabilityModel, "vulnerabilityModelIdentifier");

    when(assprojectService.getByIdentifierFull("projectIdentifier")).thenReturn(project);
    when(templateInput.loadAssessmentTemplateByIdentifier("templateIdentifier")).thenReturn(template);
    when(modelsCloner.createModelsCopy(template, project)).thenReturn(newModelsMap);
    when(riskTreatmentModelService.getByIdentifier("treatmentModelIdentifier")).thenReturn(treatmentModel);

    when(assetModelService.getByIdentifier("assetModelIdentifier")).thenReturn(new SestAssetModel());
    when(vulnerabilityModelService.getByIdentifier("vulnerabilityModelIdentifier")).thenReturn(new SestVulnerabilityModel());
    when(threatModelService.getByIdentifier("threatModelIdentifier")).thenReturn(new SestThreatModel());
    when(safeguardModelService.getByIdentifier("safeguardModelIdentifier")).thenReturn(new SestSafeguardModel());
    when(riskModelService.getByIdentifier("riskModelIdentifier")).thenReturn(new SestRiskModel());
    when(riskTreatmentModelService.getByIdentifier("treatmentModelIdentifier")).thenReturn(treatmentModel);
    when(assprocedureService.insert(any(), anyString())).thenReturn(procedure);

    manager.createAssessmentProcedure(procedure, "projectIdentifier");

    verify(riskModelInput, times(1)).createAssessmentProcedure(procedure);
  }

  @Test
  public void createAssessmentProcedureFromProcedure() throws Exception {

    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier("procedureIdentifier");
    procedure.setPhase(PhaseEnum.Requirements);
    procedure.setStatus(AssessmentStatusEnum.OnGoing);
    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier("projectIdentifier");
    project.setTemplate(template);
    SestAuditModel audit = new SestAuditModel();
    audit.setSestQuestionnaireModel(new ArrayList<>());
    project.setAudits(Arrays.asList(audit));

    SestRiskTreatmentModel treatmentModel = new SestRiskTreatmentModel();
    treatmentModel.setSestobjId("treatmentModelIdentifier");
    treatmentModel.setRiskTreatmentModelJson("{}");

    Map<SESTObjectTypeEnum, String> newModelsMap = new HashMap<>();
    newModelsMap.put(SESTObjectTypeEnum.SafeguardModel, "safeguardModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.RiskTreatmentModel, "treatmentModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.RiskModel, "riskModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.AssetModel, "assetModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.ThreatModel, "threatModelIdentifier");
    newModelsMap.put(SESTObjectTypeEnum.VulnerabilityModel, "vulnerabilityModelIdentifier");

    when(assprojectService.getByIdentifierFull("projectIdentifier")).thenReturn(project);
    when(templateInput.loadAssessmentTemplateByIdentifier("templateIdentifier")).thenReturn(template);
    when(modelsCloner.createModelsCopy(template, project)).thenReturn(newModelsMap);
    when(assprocedureService.getByProjectIdentifier("projectIdentifier")).thenReturn(Arrays.asList(procedure));

    when(assetModelService.getByIdentifier("assetModelIdentifier")).thenReturn(new SestAssetModel());
    when(vulnerabilityModelService.getByIdentifier("vulnerabilityModelIdentifier")).thenReturn(new SestVulnerabilityModel());
    when(threatModelService.getByIdentifier("threatModelIdentifier")).thenReturn(new SestThreatModel());
    when(safeguardModelService.getByIdentifier("safeguardModelIdentifier")).thenReturn(new SestSafeguardModel());
    when(riskModelService.getByIdentifier("riskModelIdentifier")).thenReturn(new SestRiskModel());
    when(riskTreatmentModelService.getByIdentifier("treatmentModelIdentifier")).thenReturn(treatmentModel);
    when(assprocedureService.insert(any(), anyString())).thenReturn(procedure);

    manager.createAssessmentProcedure(procedure, "projectIdentifier");

    verify(riskModelInput, times(1)).createAssessmentProcedure(procedure);
  }

  @Test
  public void editAssessmentProcedure() throws Exception {
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier("procedureIdentifier");

    manager.editAssessmentProcedure(procedure);

    verify(assprocedureService, times(1)).update(any());
  }

  @Test
  public void deleteAssessmentProcedure() throws Exception {

    manager.deleteAssessmentProcedure("procedureIdentifier");

    verify(assprocedureService, times(1)).deleteCascade("procedureIdentifier");
  }

  @Test
  public void loadAssessmentProcedure() throws Exception {
    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.IDENTIFIER, "procedureIdentifier");
    filter.setFilterMap(map);

    manager.loadAssessmentProcedure(filter);

    verify(assprocedureService, times(1)).getByIdentifierFull("procedureIdentifier");
  }

  @Test
  public void loadAssessmentProcedureByProject() throws Exception {
    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
    filter.setFilterMap(map);

    manager.loadAssessmentProcedure(filter);

    verify(assprocedureService, times(1)).getByProjectIdentifier("projectIdentifier");
  }

  @Test
  public void loadAssessmentProcedureList() throws Exception {

    manager.loadAssessmentProcedureList();

    verify(assprocedureService, times(1)).getAll();
  }
}
