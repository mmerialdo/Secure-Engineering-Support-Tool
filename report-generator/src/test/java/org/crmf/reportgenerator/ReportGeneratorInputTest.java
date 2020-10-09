/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ReportGeneratorDOCX.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.reportgenerator;

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.reportgenerator.manager.ReportGeneratorDOCX;
import org.crmf.reportgenerator.manager.ReportGeneratorISO;
import org.crmf.reportgenerator.manager.ReportGeneratorInput;
import org.crmf.reportgenerator.manager.ReportGeneratorLightDOCX;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReportGeneratorInputTest {

  @Mock
  private AssAuditServiceInterface auditService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private AssprojectServiceInterface assprojectService;
  @Mock
  private ReportGeneratorDOCX reportGeneratorDOCX;
  @Mock
  private ReportGeneratorLightDOCX reportGeneratorLightDOCX;
  @Mock
  private ReportGeneratorISO reportGeneratorISO;

  @InjectMocks
  private ReportGeneratorInput manager;

  private static final String PREFIX = "report_";
  private static final String PREFIXLIGHT = "report_light_";
  private static final String PREFIXISO = "report_ISO_";
  private static final String SUFFIX = ".docx";

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editReport() throws Exception {

    String procedureIdentifier = "procedureIdentifier";
    String projectIdentifier = "projectIdentifier";
    int projectId = 1;
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier(projectIdentifier);
    SestAuditModel sestAudit = new SestAuditModel();
    sestAudit.setType(AuditTypeEnum.SECURITY);
    sestAudit.setIdentifier("auditIdentifier");
    sestAudit.setObjType(SESTObjectTypeEnum.Audit);

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(assprocedureService.getProjectdIdByIdentifier(procedureIdentifier)).thenReturn(projectId);
    when(assprojectService.getById(projectId)).thenReturn(project);
    when(assprojectService.getByIdentifierFull(projectIdentifier)).thenReturn(project);
    when(assprocedureService.getByProjectIdentifier(projectIdentifier)).thenReturn(new ArrayList<>());
    when(auditService.getByProjectAndType(projectIdentifier, AuditTypeEnum.SECURITY, true)).thenReturn(sestAudit);

    manager.editReport(procedureIdentifier);

    verify(reportGeneratorDOCX).generateReport(anyString(), any(), any(), any());
  }

  @Test
  public void editLightReport() throws Exception {

    String procedureIdentifier = "procedureIdentifier";
    String projectIdentifier = "projectIdentifier";
    int projectId = 1;
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier(projectIdentifier);
    SestAuditModel sestAudit = new SestAuditModel();
    sestAudit.setType(AuditTypeEnum.SECURITY);
    sestAudit.setIdentifier("auditIdentifier");
    sestAudit.setObjType(SESTObjectTypeEnum.Audit);

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(assprocedureService.getProjectdIdByIdentifier(procedureIdentifier)).thenReturn(projectId);
    when(assprojectService.getById(projectId)).thenReturn(project);
    when(assprojectService.getByIdentifierFull(projectIdentifier)).thenReturn(project);
    when(assprocedureService.getByProjectIdentifier(projectIdentifier)).thenReturn(new ArrayList<>());
    when(auditService.getByProjectAndType(projectIdentifier, AuditTypeEnum.SECURITY, true)).thenReturn(sestAudit);

    manager.editLightReport(procedureIdentifier, ImpactEnum.HIGH);

    verify(reportGeneratorLightDOCX).generateReport(anyString(), any(), any(), any(), any(), any());
  }

  @Test
  public void editISOReport() throws Exception {

    String procedureIdentifier = "procedureIdentifier";
    String projectIdentifier = "projectIdentifier";
    int projectId = 1;
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier(projectIdentifier);
    SestAuditModel sestAudit = new SestAuditModel();
    sestAudit.setType(AuditTypeEnum.SECURITY);
    sestAudit.setIdentifier("auditIdentifier");
    sestAudit.setObjType(SESTObjectTypeEnum.Audit);

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(assprocedureService.getProjectdIdByIdentifier(procedureIdentifier)).thenReturn(projectId);
    when(assprojectService.getById(projectId)).thenReturn(project);
    when(assprojectService.getByIdentifierFull(projectIdentifier)).thenReturn(project);
    when(assprocedureService.getByProjectIdentifier(projectIdentifier)).thenReturn(new ArrayList<>());
    when(auditService.getByProjectAndType(projectIdentifier, AuditTypeEnum.SECURITY, true)).thenReturn(sestAudit);

    manager.editISOReport(procedureIdentifier);

    verify(reportGeneratorISO).generateReport(anyString(), any(), any(), any(), any(), any());
  }

  @Test
  public void downloadLightReport() throws Exception {

    String procedureIdentifier = "someProcedureIdentifier";
    String reportType = "LIGHT"; //ISO
    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    filterMap.put(GenericFilterEnum.REPORT_TYPE, reportType);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    File file = new File(PREFIXLIGHT.concat(procedureIdentifier).concat(SUFFIX));
    if(file.createNewFile()) {
      file.deleteOnExit();
    }

    InputStreamResource is = manager.download(filter);

    Assertions.assertNotNull(is);
    if (is.isOpen()) {
      is.getInputStream().close();
      file.delete();
    }
  }

  @Test
  public void downloadISOReport() throws Exception {

    String procedureIdentifier = "someProcedureIdentifier";
    String reportType = "ISO";
    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    filterMap.put(GenericFilterEnum.REPORT_TYPE, reportType);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    File file = new File(PREFIXISO.concat(procedureIdentifier).concat(SUFFIX));
    if(file.createNewFile()) {
      file.deleteOnExit();
    }

    InputStreamResource is = manager.download(filter);

    Assertions.assertNotNull(is);
    if (is.isOpen()) {
      is.getInputStream().close();
      file.delete();
    }
  }

  @Test
  public void downloadFullReport() throws Exception {

    String procedureIdentifier = "someProcedureIdentifier";
    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    File file = new File(PREFIX.concat(procedureIdentifier).concat(SUFFIX));
    if(file.createNewFile()) {
      file.deleteOnExit();
    }

    InputStreamResource is = manager.download(filter);

    Assertions.assertNotNull(is);
    if (is.isOpen()) {
      is.getInputStream().close();
      file.delete();
    }
  }

  @Test
  public void downloadFileNotFoundException() {
    String procedureIdentifier = "someProcedureIdentifier";
    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    Assertions.assertThrows(Exception.class, () -> {
      manager.download(filter);
    });
  }
}
