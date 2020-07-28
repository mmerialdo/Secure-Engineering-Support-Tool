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

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the interactions the the sest-proxy bundle and the invoke to the business logic related to the report generation
public class ReportGeneratorInput implements ReportGeneratorInputInterface {

	private AssAuditServiceInterface auditService;
	private AssprocedureServiceInterface assprocedureService;
	private AssprojectServiceInterface assprojectService;
	private ReportGeneratorDOCX reportGeneratorDOCX;
	private ReportGeneratorLightDOCX reportGeneratorLightDOCX;
	private ReportGeneratorISO reportGeneratorISO;
	private static final String PREFIX = "report_";
	private static final String PREFIXLIGHT = "report_light_";
	private static final String PREFIXISO = "report_ISO_";
	private static final String SUFFIX = ".docx";

	private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorInput.class.getName());
	
	@Override
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
			
			throw new Exception("COMMAND_EXCEPTION", e);
		}
		
		return PREFIX.concat(procedureId);
	}

	@Override
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
		}
		catch (Exception e) {
			LOG.error("Unable to generate light report! ", e);

			throw new Exception("COMMAND_EXCEPTION", e);
		}

		return PREFIX.concat(procedureId);
	}

	@Override
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
			//GABRI TODO: metti questo file dove vuoi, ovviamente
			String isoControlsPath = "ISO27002.json";

			File famJson = new File(isoControlsPath);
			byte[] bamJson = Files.readAllBytes(famJson.toPath());
			String amJsonString = new String(bamJson, "UTF-8");

			ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

			ISOControls controls = amSerDes.getISOControlsFromJSONString(amJsonString);

			reportGeneratorISO.generateReport(PREFIXISO.concat(procedureId).concat(SUFFIX), procedure, project, audit, auditFinal, controls);
		}
		catch (Exception e) {
			LOG.error("Unable to generate light report! ", e);

			throw new Exception("COMMAND_EXCEPTION", e);
		}

		return PREFIX.concat(procedureId);
	}

	public ReportGeneratorDOCX getReportGeneratorDOCX() {
		return reportGeneratorDOCX;
	}

	public void setReportGeneratorDOCX(ReportGeneratorDOCX reportGeneratorDOCX) {
		this.reportGeneratorDOCX = reportGeneratorDOCX;
	}

	public AssprojectServiceInterface getAssprojectService() {
		return assprojectService;
	}

	public void setAssprojectService(AssprojectServiceInterface assprojectService) {
		this.assprojectService = assprojectService;
	}

	public AssprocedureServiceInterface getAssprocedureService() {
		return assprocedureService;
	}

	public void setAssprocedureService(AssprocedureServiceInterface assprocedureService) {
		this.assprocedureService = assprocedureService;
	}

	public ReportGeneratorLightDOCX getReportGeneratorLightDOCX() {
		return reportGeneratorLightDOCX;
	}

	public void setReportGeneratorLightDOCX(ReportGeneratorLightDOCX reportGeneratorLightDOCX) {
		this.reportGeneratorLightDOCX = reportGeneratorLightDOCX;
	}

	public ReportGeneratorISO getReportGeneratorISO() {
		return reportGeneratorISO;
	}

	public void setReportGeneratorISO(ReportGeneratorISO reportGeneratorISO) {
		this.reportGeneratorISO = reportGeneratorISO;
	}

	public AssAuditServiceInterface getAuditService() {
		return auditService;
	}

	public void setAuditService(AssAuditServiceInterface auditService) {
		this.auditService = auditService;
	}
}
