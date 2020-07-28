/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProfileTest.java"
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.persistency.mapper.asset.AssetService;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.risk.RiskService;
import org.crmf.persistency.mapper.safeguard.SafeguardService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssProfileTest {

	PersistencySessionFactory sessionFactory;
	private AssprofileService profileService;
	private AsstemplateService templateService;
	private AssetService assetService;
	private VulnerabilityService vulnService;
	private ThreatService threatService;
	private SafeguardService safeguardService;
	private RiskService riskService;
	AssessmentProfile profile;
	
	@Before
	public void setUp() throws Exception {

		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		profileService = new AssprofileService();
		profileService.setSessionFactory(sessionFactory);
		
		templateService = new AsstemplateService();
		templateService.setSessionFactory(sessionFactory);
		
		assetService = new AssetService();
		assetService.setSessionFactory(sessionFactory);
		
		vulnService = new VulnerabilityService();
		vulnService.setSessionFactory(sessionFactory);
		
		threatService = new ThreatService();
		threatService.setSessionFactory(sessionFactory);

		safeguardService = new SafeguardService();
		safeguardService.setSessionFactory(sessionFactory);
		
		riskService = new RiskService();
		riskService.setSessionFactory(sessionFactory);
		
		profile = prefill();
	}

	@After
	public void tearDown() throws Exception {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);
		
		cleaner.delete();
	}
	
	@Test
	public void insert() throws Exception {

		AssetModel asset = new AssetModel();
		String newIdentifier = UUID.randomUUID().toString();
		asset.setIdentifier(newIdentifier);
		assetService.insert("{ }", newIdentifier);
		
		VulnerabilityModel vuln = new VulnerabilityModel();
		newIdentifier = UUID.randomUUID().toString();
		vuln.setIdentifier(newIdentifier);
		vulnService.insert("{ }", newIdentifier);
		
		ThreatModel threat = new ThreatModel();
		newIdentifier = UUID.randomUUID().toString();
		threat.setIdentifier(newIdentifier);
		threatService.insert("{ }", newIdentifier);
		
		
		SafeguardModel sg = new SafeguardModel();
		newIdentifier = UUID.randomUUID().toString();
		sg.setIdentifier(newIdentifier);
		safeguardService.insert("{ }", newIdentifier);
		
		RiskModel risk = new RiskModel();
		newIdentifier = UUID.randomUUID().toString();
		risk.setIdentifier(newIdentifier);
		
		riskService.insert("{ }", newIdentifier);
		
		AssessmentProfile profile = new AssessmentProfile();
		profile.setName("profile02");
		profile.setDescription("profile2 for test");
		profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
		profile.setOrganization("rhea spa");
		profile.setPhase(PhaseEnum.Design);
		profile.setRiskMethodology(RiskMethodologyEnum.HTRA);
		
		AssessmentTemplate template01 = new AssessmentTemplate();
		template01.setName("template01");
		template01.setDescription("description template01");
		template01.setPhase(PhaseEnum.Initial);
		template01.setRiskMethodology(RiskMethodologyEnum.Mehari);
		template01.setAssetModel(asset);
		template01.setVulnerabilityModel(vuln);
		template01.setThreatModel(threat);
		template01.setSafeguardModel(sg);
		template01.setRiskModel(risk);
		AssessmentTemplate template02 = new AssessmentTemplate();
		template02.setName("template02");
		template02.setDescription("description template01");
		template02.setPhase(PhaseEnum.Initial);
		template02.setRiskMethodology(RiskMethodologyEnum.Mehari);
		template02.setAssetModel(asset);
		template02.setVulnerabilityModel(vuln);
		template02.setThreatModel(threat);
		template02.setSafeguardModel(sg);
		template02.setRiskModel(risk);

			template01.setIdentifier(templateService.insert(template01, null));
			template02.setIdentifier(templateService.insert(template02, null));

			profile.setTemplates(new ArrayList<AssessmentTemplate>(Arrays.asList(template01,template02)));
		String identifier = profileService.insert(profile);
			profile.setIdentifier(identifier);
		
		AssessmentProfile profileResulted = profileService.getByIdentifier(identifier);
		assertEquals("profile02", profileResulted.getName());
		assertEquals("profile2 for test", profileResulted.getDescription());
		assertEquals("rhea spa", profileResulted.getOrganization());
		assertEquals(PhaseEnum.Design, profileResulted.getPhase());
		assertEquals("HTRA", profileResulted.getRiskMethodology().name());
		
		List<AssessmentTemplate> templates = templateService.getByProfileIdentifier(identifier);
		assertEquals(2, templates.size());
	}
	
	public void getAll() throws Exception {

		AssessmentProfile profile = new AssessmentProfile();
		profile.setName("profile02");
		profile.setDescription("profile2 for test");
		profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
		profile.setOrganization("rhea spa");
		profile.setPhase(PhaseEnum.Design);
		profile.setRiskMethodology(RiskMethodologyEnum.HTRA);
		
		String identifier = profileService.insert(profile);
			profile.setIdentifier(identifier);

		List<AssessmentProfile> profileResulted = profileService.getAll();
		assertEquals(2, profileResulted.size());
	}

	private AssessmentProfile prefill() throws Exception {
		
		AssessmentProfile profile = new AssessmentProfile();
		profile.setName("profile01");
		profile.setDescription("profile for test");
		profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
		profile.setOrganization("rhea");
		profile.setPhase(PhaseEnum.Design);
		profile.setRiskMethodology(RiskMethodologyEnum.Mehari);

			String identifier = profileService.insert(profile);
			profile.setIdentifier(identifier);

		return profile;
	}
}
