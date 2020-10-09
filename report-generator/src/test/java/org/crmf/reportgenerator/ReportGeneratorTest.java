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

import org.crmf.model.audit.Audit;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.audit.Questionnaire;
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
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.user.User;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelSerializerDeserializer;
import org.crmf.reportgenerator.manager.AssetModelWriter;
import org.crmf.reportgenerator.manager.CommonWriter;
import org.crmf.reportgenerator.manager.ReportGeneratorDOCX;
import org.crmf.reportgenerator.manager.ReportGeneratorISO;
import org.crmf.reportgenerator.manager.ReportGeneratorLightDOCX;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ReportGeneratorDOCX.class, ReportGeneratorLightDOCX.class, ReportGeneratorISO.class,
  AssetModelWriter.class, CommonWriter.class})
@ActiveProfiles("test")
public class ReportGeneratorTest {

  @Autowired
  private ReportGeneratorDOCX reportgeneratorDOCX;
  @Autowired
  ReportGeneratorLightDOCX reportGeneratorLightDOCX;
  @Autowired
  ReportGeneratorISO reportGeneratorISO;

  private static final String PREFIX = "report_";
  private static final String PREFIXLIGHT = "report_light_";
  private static final String PREFIXISO = "report_ISO_";
  private static final String SUFFIX = ".docx";

  @Test
  public void writeProcedureHeader() throws Exception {

    AssessmentProject project = createProject();
    AssessmentProcedure procedure1 = createProcedure();
    procedure1.setStatus(AssessmentStatusEnum.Closed);
    AssessmentProcedure procedure2 = createProcedure();

    project.getProcedures().add(procedure1);
    project.getProcedures().add(procedure2);

    Audit audit = createAudit();
    Audit auditFinal = createAudit();

    reportgeneratorDOCX.generateReport(PREFIX.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, audit);

    reportGeneratorLightDOCX.generateReport(PREFIXLIGHT.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, ImpactEnum.MEDIUM, audit, auditFinal);

    InputStream resource = new ClassPathResource("ISO27002.json").getInputStream();
    byte[] bamJson = resource.readAllBytes();
    String amJsonString = new String(bamJson, "UTF-8");

    ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

    ISOControls controls = amSerDes.getISOControlsFromJSONString(amJsonString);

    reportGeneratorISO.generateReport(PREFIXISO.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, audit, auditFinal, controls);
  }

  private Audit createAudit() throws IOException {

    InputStream resource = new ClassPathResource("Audit ApplicationSecurity.json").getInputStream();
    byte[] bamJson1 = resource.readAllBytes();
    String q1JsonString = new String(bamJson1, "UTF-8");

    Audit audit = new Audit();
    audit.setQuestionnaires(new ArrayList<Questionnaire>());
    QuestionnaireModelSerializerDeserializer questionnaireDeserializer = new QuestionnaireModelSerializerDeserializer();
    Questionnaire questionnaire = questionnaireDeserializer.getQuestionnaireFromJSONString(q1JsonString);
    audit.getQuestionnaires().add(questionnaire);

    return audit;
  }

  private AssessmentProject createProject() {
    AssessmentProject project = new AssessmentProject();

    project.setName("Name of the Project");
    project.setProcedures(new ArrayList<AssessmentProcedure>());

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

    InputStream resource = new ClassPathResource("assetModelTest.json").getInputStream();
    byte[] bamJson = resource.readAllBytes();
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

    InputStream resourceVuln = new ClassPathResource("vulnerabilityModelTest.json").getInputStream();
    byte[] bvmJson = resourceVuln.readAllBytes();
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

    InputStream resourceTreatement = new ClassPathResource("riskTreatmentModelTest.json").getInputStream();
    byte[] brtmJson = resourceTreatement.readAllBytes();
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

}
