/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="TestData.java"
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

import java.util.ArrayList;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.persistency.mapper.asset.AssetService;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
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

public class TestData {

	public PersistencySessionFactory sessionFactory;
	public AssprojectService projectService;
	public AssprofileService profileService;
	public AssprocedureService procedureService;
	public AsstemplateService templateService;
	public UserService userService;
	public AssetService assetService;
	public VulnerabilityService vulnService;
	public ThreatService threatService;
	public SafeguardService sgService;
	public AssessmentProject project;
	public RiskService riskService;

	AssetModel asset = new AssetModel();
	VulnerabilityModel vuln = new VulnerabilityModel();
	ThreatModel threat = new ThreatModel();
	SafeguardModel sg = new SafeguardModel();
	RiskModel risk = new RiskModel();

	public TestData() {


		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		QuestionnaireService questionnaireService = new QuestionnaireService();
		questionnaireService.setSessionFactory(sessionFactory);

		AssAuditDefaultService auditDefaultService = new AssAuditDefaultService();
		auditDefaultService.setSessionFactory(sessionFactory);

		AssAuditService auditService = new AssAuditService();
		auditService.setSessionFactory(sessionFactory);
		auditService.setQuestionnaireService(questionnaireService);

		SysprojectService sysprojectService = new SysprojectService();
		sysprojectService.setSessionFactory(sessionFactory);

		procedureService = new AssprocedureService();
		procedureService.setSessionFactory(sessionFactory);
		
		templateService = new AsstemplateService();
		templateService.setSessionFactory(sessionFactory);

		userService = new UserService();
		userService.setSessionFactory(sessionFactory);

		profileService = new AssprofileService();
		profileService.setSessionFactory(sessionFactory);

		projectService = new AssprojectService();
		projectService.setSessionFactory(sessionFactory);
		projectService.setSysprjService(sysprojectService);
		projectService.setAuditService(auditService);

		assetService = new AssetService();
		assetService.setSessionFactory(sessionFactory);
		
		String newIdentifier = UUID.randomUUID().toString();
		asset.setIdentifier(newIdentifier);
		assetService.insert("{ }", newIdentifier);
		
		vulnService = new VulnerabilityService();
		vulnService.setSessionFactory(sessionFactory);
		
	    String newVulnIdentifier = UUID.randomUUID().toString();
	    vuln.setIdentifier(newVulnIdentifier);
		vulnService.insert("{ }", newVulnIdentifier);
		
		threatService = new ThreatService();
		threatService.setSessionFactory(sessionFactory);
		
	    String newThreatIdentifier = UUID.randomUUID().toString();
	    threat.setIdentifier(newThreatIdentifier);
	    threatService.insert("{ }", newThreatIdentifier);

	    sgService = new SafeguardService();
	    sgService.setSessionFactory(sessionFactory);
		
	    String newSgIdentifier = UUID.randomUUID().toString();
	    sg.setIdentifier(newSgIdentifier);
	    sgService.insert("{ }", newSgIdentifier);
		
		riskService = new RiskService();
		riskService.setSessionFactory(sessionFactory);
		
		newIdentifier = UUID.randomUUID().toString();
		risk.setIdentifier(newIdentifier);
		riskService.insert("{ }", newIdentifier);
	}

	public AssessmentProject buildModelAssProject(String ix) throws Exception{

		SystemProject sysproject = new SystemProject();
		sysproject.setDescription("Dscription of the project" + ix);
		sysproject.setMandate("mandate .. " + ix);
		sysproject.setName("euclid" + ix);
		sysproject.setScope("scope ..." + ix);

		User userpm = new User();
		userpm.setName("pmname" + ix);
		userpm.setUsername("username" + ix);
		userpm.setEmail("emailNotNull@gmail.com" + ix);
		userpm.setPassword("pasS01" + ix);
		userpm.setObjType(SESTObjectTypeEnum.User);
		userpm.setProfile(UserProfileEnum.ProjectManager);
		String userIdentifier = userService.insert(userpm);
		userpm.setIdentifier(userIdentifier);

		AssessmentTemplate template = new AssessmentTemplate();
		template.setName("template0" + ix);
		template.setPhase(PhaseEnum.Initial);
		template.setRiskMethodology(RiskMethodologyEnum.Mehari);
		template.setAssetModel(asset);
		template.setVulnerabilityModel(vuln);
		template.setThreatModel(threat);
		template.setSafeguardModel(sg);
		template.setRiskModel(risk);
//		template.setAssociatedProcedure(procedure);

		String templatedentifier = templateService.insert(template, null);
		template.setIdentifier(templatedentifier);

		AssessmentProfile profile = new AssessmentProfile();
		profile.setName("profile0" + ix);
		profile.setPhase(PhaseEnum.Initial);
		profile.setRiskMethodology(RiskMethodologyEnum.Mehari);
		
		ArrayList<AssessmentTemplate> templates = new ArrayList();
		templates.add(template);
		profile.setTemplates(templates);

		String profiledentifier = profileService.insert(profile);
		profile.setIdentifier(profiledentifier);

		AssessmentProject project = new AssessmentProject();
		project.setName("project0" + ix);
		project.setDescription("project for test" + ix);
		project.setProjectManager(userpm);
		project.setRiskMethodology(RiskMethodologyEnum.Mehari);
		project.setStatus(AssessmentStatusEnum.OnGoing);
		project.setSystemProject(sysproject);
		project.setProfile(profile);
		project.setTemplate(template);
		// project.setProcedures(procedures);
		// project.setUsers(users);

		
		return project;
	}

	//TODO: 21/07/2017 never called: check if useful
	public AssessmentProcedure buildModelAssProcedure(String ix, String projectIdentifier) throws Exception {

		AssessmentProcedure procedure = new AssessmentProcedure();
		procedure.setPhase(PhaseEnum.Design);
		procedure.setStatus(AssessmentStatusEnum.Closed);
		procedure.setAssetModel(asset);
		procedure.setVulnerabilityModel(vuln);
		procedure.setThreatModel(threat);
		procedure.setSafeguardModel(sg);
		procedure.setRiskModel(risk);

		String procedureidentifier = procedureService.insert(procedure, projectIdentifier).getIdentifier();
		procedure.setIdentifier(procedureidentifier);

		return procedure;
	}
	
}