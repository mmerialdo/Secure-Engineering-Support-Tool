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
package org.crmf.core.audit;

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInput;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditInputTest {
  @Mock
  private AssAuditServiceInterface assauditService;
  @Mock
  private QuestionnaireServiceInterface questionnaireService;
  @Mock
  private AssprojectServiceInterface assprojectService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private SafeguardModelManagerInput safeguardModelInput;
  @Mock
  private RiskModelManagerInput riskModelInput;

  @InjectMocks
  private AuditInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editAssetModel() {

    ModelObject model = new ModelObject();
    model.setObjectIdentifier("auditIdentifier");
    model.setJsonModel("{}");
    SestAuditModel auditModel = new SestAuditModel();
    auditModel.setIdentifier("auditIdentifier");
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier("projectIdentifier");
    project.setAudits(new ArrayList<>(Arrays.asList(auditModel)));
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier("procedureIdentifier");
    procedure.setStatus(AssessmentStatusEnum.OnGoing);
    List<AssessmentProcedure> procedures = new ArrayList<>();
    procedures.add(procedure);

    when(assauditService.getProjectIdByIdentifier("auditIdentifier")).thenReturn(1);
    when(assprojectService.getById(1)).thenReturn(project);
    when(assprocedureService.getByProjectIdentifier("projectIdentifier")).thenReturn(procedures);

    manager.editAudit(model);

    verify(safeguardModelInput, times(1)).editSafeguardModel(project);
    verify(riskModelInput, times(1)).editSafeguardModel("procedureIdentifier");
  }

  @Test
  public void loadAudit() {

    manager.loadAudit("auditIdentifier", AuditTypeEnum.SECURITY, true);
    verify(assauditService, times(1)).
      getByProjectAndType("auditIdentifier", AuditTypeEnum.SECURITY, true);
  }
}
