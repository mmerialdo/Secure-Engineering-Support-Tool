/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ReportGeneratorInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.reportgenerator.manager;

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//This class manages the interactions the the sest-proxy bundle and the invoke to the business logic related to the report generation
@Service
public class ReportGeneratorInput {

  @Autowired
  @Qualifier("default")
  private AssAuditServiceInterface auditService;
  @Autowired
  @Qualifier("default")
  private AssprocedureServiceInterface assprocedureService;
  @Autowired
  @Qualifier("default")
  private AssprojectServiceInterface assprojectService;
  @Autowired
  private ReportGeneratorDOCX reportGeneratorDOCX;
  @Autowired
  private ReportGeneratorLightDOCX reportGeneratorLightDOCX;
  @Autowired
  private ReportGeneratorISO reportGeneratorISO;

  private static final String PREFIX = "report_";
  private static final String PREFIXLIGHT = "report_light_";
  private static final String PREFIXISO = "report_ISO_";
  private static final String SUFFIX = ".docx";

  private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorInput.class.getName());

  public String editReport(String procedureId) throws Exception {

    try {
      //Loading the Procedure from the repository
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureId);
      int pId = assprocedureService.getProjectdIdByIdentifier(procedureId);
      String projectId = assprojectService.getById(pId).getIdentifier();

      AssessmentProject project = assprojectService.getByIdentifierFull(projectId);
      project.setProcedures((ArrayList<AssessmentProcedure>) assprocedureService.getByProjectIdentifier(project.getIdentifier()));

      SestAuditModel sestAudit = auditService.getByProjectAndType(projectId, AuditTypeEnum.SECURITY, true);
      AuditModelSerializerDeserializer auditModelSerializerDeserializer = new AuditModelSerializerDeserializer();
      Audit audit = auditModelSerializerDeserializer.getAuditFromAuditModel(sestAudit, true);

      //generating the report
      reportGeneratorDOCX.generateReport(PREFIX.concat(procedureId).concat(SUFFIX), procedure, project, audit);
    } catch (Exception e) {
      LOG.error("Unable to generate report! ", e);
      throw new Exception(e);
    }
    return PREFIX.concat(procedureId);
  }

  public String editLightReport(String procedureId, ImpactEnum threshold) throws Exception {

    try {
      //Loading the Procedure from the repository
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureId);
      int pId = assprocedureService.getProjectdIdByIdentifier(procedureId);
      String projectId = assprojectService.getById(pId).getIdentifier();

      AssessmentProject project = assprojectService.getByIdentifierFull(projectId);
      project.setProcedures((ArrayList<AssessmentProcedure>) assprocedureService.getByProjectIdentifier(project.getIdentifier()));

      SestAuditModel sestAudit = auditService.getByProjectAndType(projectId, AuditTypeEnum.SECURITY, true);
      AuditModelSerializerDeserializer auditModelSerializerDeserializer = new AuditModelSerializerDeserializer();
      Audit audit = auditModelSerializerDeserializer.getAuditFromAuditModel(sestAudit, true);
      //We create a close of the Audit since we will use it to attempt the optimization
      Audit auditFinal = auditModelSerializerDeserializer.getAuditFromAuditModel(sestAudit, true);

      //generating the report
      reportGeneratorLightDOCX.generateReport(PREFIXLIGHT.concat(procedureId).concat(SUFFIX), procedure, project, threshold, audit, auditFinal);
    } catch (Exception e) {
      LOG.error("Unable to generate light report! ", e);
      throw new Exception(e);
    }

    return PREFIX.concat(procedureId);
  }

  public String editISOReport(String procedureId) throws Exception {

    try {
      //Loading the Procedure from the repository
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureId);
      int pId = assprocedureService.getProjectdIdByIdentifier(procedureId);
      String projectId = assprojectService.getById(pId).getIdentifier();

      AssessmentProject project = assprojectService.getByIdentifierFull(projectId);
      project.setProcedures((ArrayList<AssessmentProcedure>) assprocedureService.getByProjectIdentifier(project.getIdentifier()));

      SestAuditModel sestAudit = auditService.getByProjectAndType(projectId, AuditTypeEnum.SECURITY, true);
      AuditModelSerializerDeserializer auditModelSerializerDeserializer = new AuditModelSerializerDeserializer();
      Audit audit = auditModelSerializerDeserializer.getAuditFromAuditModel(sestAudit, true);
      //We create a close of the Audit since we will use it to attempt the optimization
      Audit auditFinal = auditModelSerializerDeserializer.getAuditFromAuditModel(sestAudit, true);

      //generating the report
      InputStream resource = new ClassPathResource("ISO27002.json").getInputStream();
      byte[] bamJson = resource.readAllBytes();
      String amJsonString = new String(bamJson, "UTF-8");

      ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

      ISOControls controls = amSerDes.getISOControlsFromJSONString(amJsonString);

      reportGeneratorISO.generateReport(PREFIXISO.concat(procedureId).concat(SUFFIX), procedure, project, audit, auditFinal, controls);
    } catch (Exception e) {
      LOG.error("Unable to generate light report! ", e);
      throw new Exception(e);
    }

    return PREFIX.concat(procedureId);
  }

  public InputStreamResource download(GenericFilter filter) throws Exception {

    LOG.info("downloadReport process ");
    String procedureId = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
    String reportType = filter.getFilterValue(GenericFilterEnum.REPORT_TYPE);
    LOG.info("downloadReport procedure Id " + procedureId);
    LOG.info("downloadReport report Type " + reportType);
    String prefix = PREFIX;
    if (reportType != null) {
      switch (reportType) {
        case "LIGHT":
          prefix = PREFIXLIGHT;
          break;
        case "ISO":
          prefix = PREFIXISO;
          break;
      }
    }

    File file = new File(prefix.concat(procedureId).concat(SUFFIX));
    if (!file.exists()) {
      throw new Exception("REPORT_MISSING");
    } else {
      return new InputStreamResource(new FileInputStream(file));
    }
  }
}
