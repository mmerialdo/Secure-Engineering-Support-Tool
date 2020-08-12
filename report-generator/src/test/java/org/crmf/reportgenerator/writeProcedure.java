package org.crmf.reportgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

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

public class writeProcedure {
	private static final String PREFIX = "report_";
	private static final String SUFFIX = ".docx";
	
	private AssessmentProject createProject(){
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

	@org.junit.Test
	public void writeProcedureHeader() throws Exception {
		AssessmentProject project = createProject();
		AssessmentProcedure procedure = createProcedure();
		/*ReportGeneratorDOCX reportgeneratorDOCX = new ReportGeneratorDOCX();
		
		reportgeneratorDOCX.generateReport(PREFIX.concat(procedure.getIdentifier()).concat(SUFFIX), procedure, project);*/

		/*ReportGeneratorLightDOCX reportGeneratorLightDOCX = new ReportGeneratorLightDOCX();
		reportGeneratorLightDOCX.generateReport(PREFIX.concat(procedure.getIdentifier()).concat(SUFFIX), procedure, project, ImpactEnum.HIGH, audit);*/
		
	}

}
