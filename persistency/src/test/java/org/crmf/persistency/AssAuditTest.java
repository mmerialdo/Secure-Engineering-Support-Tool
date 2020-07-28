/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditTest.java"
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

import java.util.ArrayList;
import java.util.List;

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.audit.QuestionnaireService;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.project.AssprocedureService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AssAuditTest {

	PersistencySessionFactory sessionFactory;

	QuestionnaireService questionnaireService;
	AssAuditService auditService;
	AssAuditDefaultService auditDefaultService;

	private AssprojectService projectService;
	private AssprofileService profileService;
	private AssprocedureService procedureService;
	private AsstemplateService templateService;
	private UserService userService;

	String projectIdentifier = null;

	private TestData data = new TestData();

	@Before
	public void setUp() throws Exception {

		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		questionnaireService = new QuestionnaireService();
		questionnaireService.setSessionFactory(sessionFactory);

		QuestionnaireService questionnaireService = new QuestionnaireService();
		questionnaireService.setSessionFactory(sessionFactory);

		auditDefaultService = new AssAuditDefaultService();
		auditDefaultService.setSessionFactory(sessionFactory);

		auditService = new AssAuditService();
		auditService.setSessionFactory(sessionFactory);
		auditService.setQuestionnaireService(questionnaireService);

		SysprojectService sysprojectService = new SysprojectService();
		sysprojectService.setSessionFactory(sessionFactory);

		projectService = new AssprojectService();
		projectService.setSessionFactory(sessionFactory);
		projectService.setSysprjService(sysprojectService);
		projectService.setAuditService(auditService);

		userService = new UserService();
		userService.setSessionFactory(sessionFactory);

		profileService = new AssprofileService();
		profileService.setSessionFactory(sessionFactory);

		procedureService = new AssprocedureService();
		procedureService.setSessionFactory(sessionFactory);

		templateService = new AsstemplateService();
		templateService.setSessionFactory(sessionFactory);
	}

	@After
	public void tearDown() {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);
		cleaner.delete();
	}

	@Test
	@Ignore
	public void insert() throws Exception{

		String projectIdentifier = prefillProject();
		QuestionnaireModelSerializerDeserializer converter = new QuestionnaireModelSerializerDeserializer();

		List<SestAuditModel> audits = auditService.getAllForProject(projectIdentifier);
		assertEquals(1, audits.size());
		for (SestAuditModel sestaudit : audits) {
			if (sestaudit.getType() == AuditTypeEnum.SECURITY) {
				List<SestQuestionnaireModel> questionnaires = questionnaireService.getAllQuestionnaireNames(sestaudit.getIdentifier());
				assertEquals(1, questionnaires.size());
				for (SestQuestionnaireModel questionnaireModel : questionnaires) {
					questionnaireModel = questionnaireService.getQuestionnaireByCategory("01");
					if(questionnaireModel.getType() == QuestionnaireTypeEnum.MEHARI_OrganizationSecurity) {
						Questionnaire questionnaireResulted = converter.getQuestionnaireFromJSONString(questionnaireModel.getQuestionnaireModelJson());
						assertEquals(1, questionnaireResulted.getQuestions().size());
						assertEquals("01A", questionnaireResulted.getQuestions().get(0).getCategory());
						assertEquals(QuestionTypeEnum.CATEGORY, questionnaireResulted.getQuestions().get(0).getType());
						assertEquals("Roles and structures of security", questionnaireResulted.getQuestions().get(0).getValue());
						assertEquals(1, questionnaireResulted.getQuestions().get(0).getChildren().size());
					}
				}
			}
		}
	}

	@Test
	@Ignore
	public void viewJson() throws Exception {

		String projectIdentifier = prefillProject();
		SestAuditModel audit = prefill();

		List<SestAuditModel> audits = auditService.getAllForProject(projectIdentifier);
		assertEquals(2, audits.size());
		for (SestAuditModel sestaudit : audits) {
			if (sestaudit.getType() == AuditTypeEnum.GENERAL) {
				List<SestQuestionnaireModel> questionnaires = sestaudit.getSestQuestionnaireModel();
				assertEquals(1, questionnaires.size());
				for (SestQuestionnaireModel questionnaire : questionnaires) {
					assertEquals("{}", questionnaire);
				}
			}
		}
	}
	
	private String prefillProject() throws Exception {

		AssauditDefaultJSON questionnaireDefaultJSON01 = new AssauditDefaultJSON();
		questionnaireDefaultJSON01.setAtype(AuditTypeEnum.SECURITY.name());
		questionnaireDefaultJSON01.setAvalue(QuestionnaireTypeEnum.MEHARI_OrganizationSecurity.name());
		questionnaireDefaultJSON01.setCategory("01");
		questionnaireDefaultJSON01.setIx("1");
		questionnaireDefaultJSON01.setQuestionnaireJSON("{\"category\":\"01\", \"index\":1, \"type\":\"MEHARI_OrganizationSecurity\", " +
			"\"questions\":[{\"category\":\"01A\",\"index\":1,\"type\":\"CATEGORY\",\"value\":\"Roles and structures of security\"," +
			"\"children\":[{\"category\":\"01A01\",\"index\":1,\"type\":\"CATEGORY\",\"value\":\"Organization and Management of General Security\"," +
			"\"children\":[]}]}]}");
		AssauditDefaultJSON questionnaireDefaultJSON02 = new AssauditDefaultJSON();
		questionnaireDefaultJSON02.setAtype(AuditTypeEnum.SECURITY.name());
		questionnaireDefaultJSON02.setAvalue(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork.name());
		questionnaireDefaultJSON02.setCategory("02");
		questionnaireDefaultJSON02.setIx("2");
		questionnaireDefaultJSON02.setQuestionnaireJSON("{}");
		auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON01);
		auditDefaultService.insertQuestionnaire(questionnaireDefaultJSON02);

		AssessmentProject project = data.buildModelAssProject("1");

			projectIdentifier = projectService.insert(project);
			project.setIdentifier(projectIdentifier);

		return projectIdentifier;
	}

	private SestAuditModel prefill() {
		SestQuestionnaireModel quest = new SestQuestionnaireModel();
		quest.setObjType(SESTObjectTypeEnum.Audit);
		quest.setType(QuestionnaireTypeEnum.MEHARI_ExtendedNetwork);
		quest.setQuestionnaireModelJson("{}");

		List<SestQuestionnaireModel> quests = new ArrayList<SestQuestionnaireModel>();
		quests.add(quest);

		SestAuditModel audit = new SestAuditModel();
		audit.setObjType(SESTObjectTypeEnum.Audit);
		audit.setType(AuditTypeEnum.GENERAL);
		audit.setSestQuestionnaireModel(quests);

		return audit;
	}
}
