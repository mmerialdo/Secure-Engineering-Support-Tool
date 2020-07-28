/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProjectTest.java"
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.project.AssprocedureService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssProjectTest {

	PersistencySessionFactory sessionFactory;

	private SysprojectService sysprojectService;
	private AssprojectService projectService;
	private AssprofileService profileService;
	private AssprocedureService procedureService;
	private AsstemplateService templateService;
	private UserService userService;
	private RoleService roleService;
	AssessmentProject project;
	private TestData data = new TestData();

	@Before
	public void setUp() throws Exception {

		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		sysprojectService = new SysprojectService();
		sysprojectService.setSessionFactory(sessionFactory);

		QuestionnaireService questionnaireService = new QuestionnaireService();
		questionnaireService.setSessionFactory(sessionFactory);

		AssAuditDefaultService auditDefaultService = new AssAuditDefaultService();
		auditDefaultService.setSessionFactory(sessionFactory);

		AssAuditService auditService = new AssAuditService();
		auditService.setSessionFactory(sessionFactory);
		auditService.setQuestionnaireService(questionnaireService);

		roleService = new RoleService();
		roleService.setSessionFactory(sessionFactory);

		projectService = new AssprojectService();
		projectService.setSessionFactory(sessionFactory);
		projectService.setSysprjService(sysprojectService);
		projectService.setAuditService(auditService);
		projectService.setRoleService(roleService);

		userService = new UserService();
		userService.setSessionFactory(sessionFactory);


		profileService = new AssprofileService();
		profileService.setSessionFactory(sessionFactory);

		procedureService = new AssprocedureService();
		procedureService.setSessionFactory(sessionFactory);

		templateService = new AsstemplateService();
		templateService.setSessionFactory(sessionFactory);

		project = prefill();
	}

	@After
	public void tearDown() throws Exception {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);

		cleaner.delete();
	}

	@Test
	public void insert() throws Exception{

		AssessmentProject project = data.buildModelAssProject("2");
		project.setRiskMethodology(RiskMethodologyEnum.HTRA);

		String identifier = projectService.insert(project);
			project.setIdentifier(identifier);

		AssessmentProject projectResulted = projectService.getByIdentifierFull(identifier);
		assertEquals("project02", projectResulted.getName());
		assertEquals("project for test2", projectResulted.getDescription());
		assertEquals("HTRA", projectResulted.getRiskMethodology().name());
		assertNotNull(projectResulted.getSystemProject());
		assertEquals("euclid2", projectResulted.getSystemProject().getName());
	}

	@Test
	public void insertProjectWithAudit() throws Exception{

		AssessmentProject project = data.buildModelAssProject("3");
		project.setRiskMethodology(RiskMethodologyEnum.HTRA);

		String identifier = projectService.insert(project);
			project.setIdentifier(identifier);

		AssessmentProject projectResulted = projectService.getByIdentifierFull(identifier);
		assertEquals("project03", projectResulted.getName());
		assertEquals("project for test3", projectResulted.getDescription());
		assertEquals("HTRA", projectResulted.getRiskMethodology().name());
		assertNotNull(projectResulted.getSystemProject());
		assertEquals("euclid3", projectResulted.getSystemProject().getName());
	}


	@Test
	public void delete(){

	//	AssessmentProject project = prefill();

		projectService.deleteCascade(project.getIdentifier());
		AssessmentProject projectResulted = projectService.getByIdentifierFull(project.getIdentifier());
		assertNull(projectResulted);
	}

	@Test
	public void getAll() throws Exception{

		AssessmentProject project = data.buildModelAssProject("2");
		project.setRiskMethodology(RiskMethodologyEnum.HTRA);

		String identifier = projectService.insert(project);
			project.setIdentifier(identifier);

		List<AssessmentProject> projectResulted = projectService.getAll();
		assertEquals(2, projectResulted.size());
	}

	private AssessmentProject prefill() throws Exception{

		project = data.buildModelAssProject("1");
		String identifier  = projectService.insert(project);
			project.setIdentifier(identifier);


		return project;
	}

}
