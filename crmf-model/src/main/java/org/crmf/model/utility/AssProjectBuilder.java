/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProjectBuilder.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility;

import java.util.ArrayList;
import java.util.Arrays;

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.SestAuditModel;
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
import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;

//This class creates a mock-up AssessmentProject for testing reasons
public class AssProjectBuilder implements ObjectBuilder {

	@Override
	public AssessmentProject getObject(){

		SystemParticipant syspartecipant = new SystemParticipant();
		syspartecipant.setName("sysp_one");
		syspartecipant.setRole("Admin");
		syspartecipant.setSurname("surname");
		
		SystemProject sysproject = new SystemProject();
		sysproject.setDescription("Dscription of the project");
		sysproject.setMandate("mandate .. ");
		sysproject.setName("euclid");
		sysproject.setScope("scope ...");
		sysproject.setParticipants(new ArrayList<>(Arrays.asList(syspartecipant)));

		UserRole urole = new UserRole();
		urole.setRole(UserRoleEnum.Admin);
		
		User userpm = new User();
		userpm.setName("pmname");
		userpm.setUsername("username");
		userpm.setEmail("emailNotNull@gmail.com");
		userpm.setPassword("pasS01");
		userpm.setIdentifier("105");
		userpm.setObjType(SESTObjectTypeEnum.User);
		userpm.setSurname("surname");
		userpm.setProfile(UserProfileEnum.ProjectManager);
		userpm.setRoles(new ArrayList<>(Arrays.asList(urole)));
		
		User user = new User();
		user.setName("uname");
		user.setUsername("username");
		user.setEmail("emailNotNull@gmail.com");
		user.setPassword("pasS02");
		user.setIdentifier("104");
		user.setSurname("surname");
		user.setProfile(UserProfileEnum.Administrator);
		user.setRoles(new ArrayList<>(Arrays.asList(urole)));
		user.setObjType(SESTObjectTypeEnum.User);
		
		AssessmentProfile profile = new AssessmentProfile();
		profile.setName("profile0");
		profile.setPhase(PhaseEnum.Initial);
		profile.setRiskMethodology(RiskMethodologyEnum.Mehari);
		profile.setIdentifier("106");
		profile.setCreationTime("01/01/1970");
		profile.setDescription("text of desc");
		profile.setOrganization("rhea");
	
		profile.setTemplates(new ArrayList<AssessmentTemplate>());
		profile.setUpdateTime("01/01/1970");
		profile.setObjType(SESTObjectTypeEnum.AssessmentProject);

		AssetModel asset = new AssetModel();
		VulnerabilityModel vuln = new VulnerabilityModel();
		ThreatModel threat = new ThreatModel();
		SafeguardModel sg = new SafeguardModel();
		RiskModel risk = new RiskModel();
		asset.setIdentifier("1");
		vuln.setIdentifier("1");
		threat.setIdentifier("1");
		sg.setIdentifier("1");
		risk.setIdentifier("1");
		
		AssessmentTemplate template = new AssessmentTemplate();
		template.setName("template0");
		template.setPhase(PhaseEnum.Initial);
		template.setRiskMethodology(RiskMethodologyEnum.Mehari);
		template.setAssetModel(asset);
		template.setVulnerabilityModel(vuln);
		template.setThreatModel(threat);
		template.setSafeguardModel(sg);
		template.setRiskModel(risk);
		template.setIdentifier("107");
		template.setCreationTime("01/01/1970");
		template.setDescription("text of desc");
		template.setRiskMethodology(RiskMethodologyEnum.Mehari);
		template.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

		AssessmentProcedure procedure = new AssessmentProcedure();
		procedure.setName("procedure01");
		procedure.setPhase(PhaseEnum.Design);
		procedure.setStatus(AssessmentStatusEnum.Closed);
		procedure.setAssetModel(asset);
		procedure.setVulnerabilityModel(vuln);
		procedure.setThreatModel(threat);
		procedure.setSafeguardModel(sg);
		procedure.setRiskModel(risk);
		procedure.setIdentifier("108");
		procedure.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
		procedure.setCreationTime("01/01/1970");
		procedure.setLastUserUpdate(user);
		procedure.setUpdateTime("01/01/1970");
		
		
		
		AssessmentProject project = new AssessmentProject();
		project.setName("project0");
		project.setDescription("project for test");
		project.setProjectManager(userpm);
		project.setRiskMethodology(RiskMethodologyEnum.Mehari);
		project.setStatus(AssessmentStatusEnum.OnGoing);
		project.setSystemProject(sysproject);
		project.setProfile(profile);
		project.setTemplate(template);
		project.setIdentifier("108");
		project.setObjType(SESTObjectTypeEnum.AssessmentProject);
		project.setCreationTime("01/01/1970");
		project.setUpdateTime("01/01/1970");
		
		project.setProcedures(new ArrayList<>(Arrays.asList(procedure)));
		project.setUsers(new ArrayList<>(Arrays.asList(user)));
		project.setAudits(new ArrayList<>());
		
		return project;
	}

	@Override
	public void printGson(String json) {
		// TODO Auto-generated method stub
		
	}

}
