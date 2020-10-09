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

import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.model.user.User;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelSerializerDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class WriteProcedureTest {

  private AssessmentProject createProject() {
    AssessmentProject project = new AssessmentProject();

    project.setName("Name of the Project");

    project.setCreationTime("01/01/2018 12.00.00");
    project.setUpdateTime("01/01/2018 12.00.00");
    UUID uuid = UUID.randomUUID();
    project.setIdentifier(uuid.toString());

    User user = new User();
    user.setName("Matteo");
    user.setSurname("Merialdo");

    project.setProjectManager(user);

    project.setSystemProject(new SystemProject());

    return project;
  }

  private AssessmentProcedure createProcedure() throws IOException {
    AssessmentProcedure procedure = new AssessmentProcedure();

    procedure.setName("Name of the Procedure");
    procedure.setStatus(AssessmentStatusEnum.OnGoing);
    procedure.setCreationTime("01/01/2018 12.00.00");
    procedure.setUpdateTime("01/01/2018 12.00.00");
    UUID uuid = UUID.randomUUID();
    procedure.setIdentifier(uuid.toString());

    InputStream resourceAsset = new ClassPathResource("assetModelTest.json").getInputStream();
    byte[] bamJson = resourceAsset.readAllBytes();
    String amJsonString = new String(bamJson, "UTF-8");

    AssetModelSerializerDeserializer amSerDes = new AssetModelSerializerDeserializer();

    AssetModel am = amSerDes.getAMFromJSONString(amJsonString);

    procedure.setAssetModel(am);

    InputStream resourceThreat = new ClassPathResource("threatModelTest.json").getInputStream();
    byte[] btmJson = resourceThreat.readAllBytes();
    String tmJsonString = new String(btmJson, "UTF-8");

    ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();

    ThreatModel tm = tmSerDes.getTMFromJSONString(tmJsonString);

    procedure.setThreatModel(tm);

    InputStream resourceVulnerability = new ClassPathResource("vulnerabilityModelTest.json").getInputStream();
    byte[] bvmJson = resourceVulnerability.readAllBytes();
    String vmJsonString = new String(bvmJson, "UTF-8");

    VulnerabilityModelSerializerDeserializer vmSerDes = new VulnerabilityModelSerializerDeserializer();

    VulnerabilityModel vm = vmSerDes.getVMFromJSONString(vmJsonString);

    procedure.setVulnerabilityModel(vm);

    InputStream resourceRiskModel = new ClassPathResource("riskModelTest.json").getInputStream();
    byte[] brmJson = resourceRiskModel.readAllBytes();
    String rmJsonString = new String(brmJson, "UTF-8");

    RiskModelSerializerDeserializer rmSerDes = new RiskModelSerializerDeserializer();

    RiskModel rm = rmSerDes.getRMFromJSONString(rmJsonString);

    procedure.setRiskModel(rm);

    InputStream resourceTreatment = new ClassPathResource("riskTreatmentModelTest.json").getInputStream();
    byte[] brtmJson = resourceTreatment.readAllBytes();
    String rtmJsonString = new String(brtmJson, "UTF-8");

    RiskTreatmentModelSerializerDeserializer rtmSerDes = new RiskTreatmentModelSerializerDeserializer();
    RiskTreatmentModel rtm = rtmSerDes.getRTMFromPersistencyJSONString(rtmJsonString);

    procedure.setRiskTreatmentModel(rtm);

    InputStream resourceSafeguard = new ClassPathResource("safeguardModelTest.json").getInputStream();
    byte[] bsmJson = resourceSafeguard.readAllBytes();
    String smJsonString = new String(bsmJson, "UTF-8");

    SafeguardModelSerializerDeserializer smSerDes = new SafeguardModelSerializerDeserializer();

    SafeguardModel sm = smSerDes.getSMFromJSONString(smJsonString);

    procedure.setSafeguardModel(sm);

    return procedure;
  }

  @Test
  public void writeProcedureHeader() throws Exception {
    AssessmentProject project = createProject();
    AssessmentProcedure procedure = createProcedure();
		/*ReportGeneratorDOCX reportgeneratorDOCX = new ReportGeneratorDOCX();
		
		reportgeneratorDOCX.generateReport(PREFIX.concat(procedure.getIdentifier()).concat(SUFFIX), procedure, project);*/

		/*ReportGeneratorLightDOCX reportGeneratorLightDOCX = new ReportGeneratorLightDOCX();
		reportGeneratorLightDOCX.generateReport(PREFIX.concat(procedure.getIdentifier()).concat(SUFFIX), procedure, project, ImpactEnum.HIGH, audit);*/

  }

}
