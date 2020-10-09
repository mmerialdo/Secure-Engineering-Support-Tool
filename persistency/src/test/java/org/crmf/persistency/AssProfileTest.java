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
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.risk.RiskService;
import org.crmf.persistency.mapper.safeguard.SafeguardService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AssProfileTest {

  @Autowired
  private AssprofileService profileService;
  @Autowired
  private AsstemplateService templateService;
  @Autowired
  private AssetService assetService;
  @Autowired
  private VulnerabilityService vulnService;
  @Autowired
  private ThreatService threatService;
  @Autowired
  private SafeguardService safeguardService;
  @Autowired
  private RiskService riskService;

  AssessmentProfile profile;

  @BeforeEach
  public void setUp() throws Exception {
    profile = prefill();
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

    profile.setTemplates(new ArrayList<>(Arrays.asList(template01, template02)));
    String identifier = profileService.insert(profile);
    profile.setIdentifier(identifier);

    AssessmentProfile profileResulted = profileService.getByIdentifier(identifier);
    Assertions.assertEquals("profile02", profileResulted.getName());
    Assertions.assertEquals("profile2 for test", profileResulted.getDescription());
    Assertions.assertEquals("rhea spa", profileResulted.getOrganization());
    Assertions.assertEquals(PhaseEnum.Design, profileResulted.getPhase());
    Assertions.assertEquals("HTRA", profileResulted.getRiskMethodology().name());

    List<AssessmentTemplate> templates = templateService.getByProfileIdentifier(identifier);
    Assertions.assertEquals(2, templates.size());
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
    Assertions.assertEquals(2, profileResulted.size());
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
