package org.crmf.reportgenerator;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.crmf.model.audit.*;
import org.crmf.model.riskassessment.*;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.user.User;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.crmf.model.utility.audit.AuditInstanceCreator;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelSerializerDeserializer;
import org.crmf.reportgenerator.manager.ReportGeneratorDOCX;
import org.crmf.reportgenerator.manager.ReportGeneratorISO;
import org.crmf.reportgenerator.manager.ReportGeneratorLightDOCX;

public class Test {
	private static final String PREFIX = "report_";
	private static final String PREFIXLIGHT = "report_light_";
	private static final String PREFIXISO = "report_ISO_";
	private static final String SUFFIX = ".docx";

	@org.junit.Test
	public void writeProcedureHeader() throws Exception {
		try{
		AssessmentProject project = createProject();
		AssessmentProcedure procedure1 = createProcedure();
		procedure1.setStatus(AssessmentStatusEnum.Closed);
		AssessmentProcedure procedure2 = createProcedure();

		project.getProcedures().add(procedure1);
		project.getProcedures().add(procedure2);

		Audit audit = createAudit();
		Audit auditFinal = createAudit();

	    ReportGeneratorDOCX reportgeneratorDOCX = new ReportGeneratorDOCX();
	    reportgeneratorDOCX.generateReport(PREFIX.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, audit);

		ReportGeneratorLightDOCX reportGeneratorLightDOCX = new ReportGeneratorLightDOCX();
		reportGeneratorLightDOCX.generateReport(PREFIXLIGHT.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, ImpactEnum.MEDIUM, audit, auditFinal);

		String isoControlsPath = ".//ISO27002.json";

			File famJson = new File(isoControlsPath);
			byte[] bamJson = Files.readAllBytes(famJson.toPath());
			String amJsonString = new String(bamJson, "UTF-8");

			ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

			ISOControls controls = amSerDes.getISOControlsFromJSONString(amJsonString);

		ReportGeneratorISO reportGeneratorISO = new ReportGeneratorISO();
		reportGeneratorISO.generateReport(PREFIXISO.concat(procedure2.getIdentifier()).concat(SUFFIX), procedure2, project, audit, auditFinal, controls);
		}
		catch(Exception e){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println(exceptionAsString);
		}
	}

	private Audit createAudit() throws IOException {

		String questionnaire1ModelPath = ".//Audit ApplicationSecurity.json";

		File famJson1 = new File(questionnaire1ModelPath);
		byte[] bamJson1 = Files.readAllBytes(famJson1.toPath());
		String q1JsonString = new String(bamJson1, "UTF-8");

		Audit audit = new Audit();
		audit.setQuestionnaires(new ArrayList<Questionnaire>());
		QuestionnaireModelSerializerDeserializer questionnaireDeserializer = new QuestionnaireModelSerializerDeserializer();
		Questionnaire questionnaire = questionnaireDeserializer.getQuestionnaireFromJSONString(q1JsonString);
		audit.getQuestionnaires().add(questionnaire);

		return audit;
	}

	private AssessmentProject createProject(){
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

	private AssessmentProcedure createProcedure() throws IOException{
		AssessmentProcedure procedure = new AssessmentProcedure();

		procedure.setName("Name of the Procedure");
		procedure.setStatus(AssessmentStatusEnum.OnGoing);
		procedure.setCreationTime("01/01/2018 12.00.00");
		procedure.setUpdateTime("01/01/2018 12.00.00");
		UUID uuid = UUID.randomUUID();
		procedure.setIdentifier(uuid.toString());

		String assetModelPath = ".//assetModelTest.json";

		File famJson = new File(assetModelPath);
		byte[] bamJson = Files.readAllBytes(famJson.toPath());
		String amJsonString = new String(bamJson, "UTF-8");

		AssetModelSerializerDeserializer amSerDes = new AssetModelSerializerDeserializer();

		AssetModel am = amSerDes.getAMFromJSONString(amJsonString);

		procedure.setAssetModel(am);

		String threatModelPath = ".//threatModelTest.json";

		File ftmJson = new File(threatModelPath);
		byte[] btmJson = Files.readAllBytes(ftmJson.toPath());
		String tmJsonString = new String(btmJson, "UTF-8");

		ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();

		ThreatModel tm = tmSerDes.getTMFromJSONString(tmJsonString);

		procedure.setThreatModel(tm);

		String vulnerabilityModelPath = ".//vulnerabilityModelTest.json";

		File fvmJson = new File(vulnerabilityModelPath);
		byte[] bvmJson = Files.readAllBytes(fvmJson.toPath());
		String vmJsonString = new String(bvmJson, "UTF-8");

		VulnerabilityModelSerializerDeserializer vmSerDes = new VulnerabilityModelSerializerDeserializer();

		VulnerabilityModel vm = vmSerDes.getVMFromJSONString(vmJsonString);

		procedure.setVulnerabilityModel(vm);

		String riskModelPath = ".//riskModelTest.json";

		File rmJson = new File(riskModelPath);
		byte[] brmJson = Files.readAllBytes(rmJson.toPath());
		String rmJsonString = new String(brmJson, "UTF-8");

		RiskModelSerializerDeserializer rmSerDes = new RiskModelSerializerDeserializer();

		RiskModel rm = rmSerDes.getRMFromJSONString(rmJsonString);

		procedure.setRiskModel(rm);

		String riskTreatmentModelPath = ".//riskTreatmentModelTest.json";

		File rtmJson = new File(riskTreatmentModelPath);
		byte[] brtmJson = Files.readAllBytes(rtmJson.toPath());
		String rtmJsonString = new String(brtmJson, "UTF-8");

		RiskTreatmentModelSerializerDeserializer rtmSerDes = new RiskTreatmentModelSerializerDeserializer();
		RiskTreatmentModel rtm = rtmSerDes.getRTMFromPersistencyJSONString(rtmJsonString);

		procedure.setRiskTreatmentModel(rtm);

		String safeguardModelPath = ".//safeguardModelTest.json";

		File smJson = new File(safeguardModelPath);
		byte[] bsmJson = Files.readAllBytes(smJson.toPath());
		String smJsonString = new String(bsmJson, "UTF-8");

		SafeguardModelSerializerDeserializer smSerDes = new SafeguardModelSerializerDeserializer();

		SafeguardModel sm = smSerDes.getSMFromJSONString(smJsonString);

		procedure.setSafeguardModel(sm);

		return procedure;
	}

}
