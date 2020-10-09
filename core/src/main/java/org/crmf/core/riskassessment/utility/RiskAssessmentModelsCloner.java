/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskAssessmentModelsCloner.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.utility;

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInput;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.utility.assetmodel.AssetModelCloner;
import org.crmf.model.utility.riskmodel.RiskModelCloner;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelCloner;
import org.crmf.model.utility.safeguardmodel.SafeguardModelCloner;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelCloner;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelCloner;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//This class holds several utility methods for cloning/copying models
@Service
public class RiskAssessmentModelsCloner{
	private static final Logger LOG = LoggerFactory.getLogger(RiskAssessmentModelsCloner.class.getName());
	public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	@Autowired
	private AssetServiceInterface assetModelService;
	@Autowired
	private VulnerabilityServiceInterface vulnerabilityModelService;
	@Autowired
	private ThreatServiceInterface threatModelService;
	@Autowired
	private SafeguardServiceInterface safeguardModelService;
	@Autowired
	private RiskServiceInterface riskModelService;
	@Autowired
	private RiskTreatmentServiceInterface riskTreatmentModelService;
	@Autowired
	private SafeguardModelManagerInput safeguardModelInput;
	@Autowired
	private UserPermissionManagerInput permissionManager;

    /*Create a copy of template associated models (AssetModel, ThreatModel, VulnerabilityModel, SafeguardModel, RiskModel)
    This method is called when creating a procedure
    setting a new SEST Identifier for each of them and inserting them into the db. 	*/
	public Map<SESTObjectTypeEnum,String> createModelsCopy(AssessmentTemplate template, AssessmentProject project){
	Map<SESTObjectTypeEnum,String> newModelsMap = new HashMap<>();
	// create all new models and objects associated to template
	LOG.info("Template with identifier: " + template.getIdentifier() + " and project with identifier: " + project.getIdentifier());
	LOG.info("---------------------------createModelsCopy from Template - begin");
	
	// get risk model associated to template
	String riskModelIdentifier = template.getRiskModel().getIdentifier();
	//copy risk model, insert it into the db and puts it into the result map
	RiskModel rm = copyRisk(riskModelIdentifier);
	newModelsMap.put(SESTObjectTypeEnum.RiskModel, rm.getIdentifier());
		
	// get asset model associated to template
	String assetModelIdentifier = template.getAssetModel().getIdentifier();
	//copy asset, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.AssetModel, copyAsset(assetModelIdentifier));
	
	// get vulnerability model associated to template
	String vulnerabilityModelIdentifier = template.getVulnerabilityModel().getIdentifier();
	//copy vulnerability, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.VulnerabilityModel, copyVulnerability(vulnerabilityModelIdentifier));
	
	// get threat model associated to template
	String threatModelIdentifier = template.getThreatModel().getIdentifier();
	//copy threat, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.ThreatModel, copyThreat(threatModelIdentifier));
	
	// SafeguardModel is created from the current Audit (the SafeguardModel in the template is used during the creation of the AssessmentProject)
	//This is then a different behavior with respect to the other models, since there is a single audit for the project, we create the new SafeguardModel from the current Audit (with the latest updates)
	//Create safeguard model, insert it into the db and puts it into the result map
	SafeguardModel sm = createSafeguard(project);
	newModelsMap.put(SESTObjectTypeEnum.SafeguardModel, sm.getIdentifier());

	//RiskTreatmentModel is created from the current Audit (the SafeguardModel in the template is used during the creation of the AssessmentProject)
	//This is then a different behavior with respect to the other models, since there is a single audit for the project, we create the new SafeguardModel from the current Audit (with the latest updates)
	//Create risk treatment model, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.RiskTreatmentModel, createRiskTreatment(rm, sm));
	
	
	return newModelsMap;
	}
	
	
	/*Create a copy of procedure associated models (AssetModel, ThreatModel, VulnerabilityModel, SafeguardModel, RiskModel, RiskTreatmentModel)
	This method is called when creating a procedure, cloned from a previous procedure
	setting a new SEST Identifier for each of them and inserting them into the db. */
	public Map<SESTObjectTypeEnum,String> createModelsCopy(AssessmentProcedure procedure, AssessmentProject project){
	Map<SESTObjectTypeEnum,String> newModelsMap = new HashMap<>();
	// create all new models and objects associated to procedure

	LOG.info("---------------------------createModelsCopy from Procedure - begin");
	
	// get risk model associated to old procedure
	String riskModelIdentifier = procedure.getRiskModel().getIdentifier();
	//copy risk model, insert it into the db and puts it into the result map
	RiskModel rm = copyRisk(riskModelIdentifier);
	newModelsMap.put(SESTObjectTypeEnum.RiskModel, rm.getIdentifier());
		
	// get asset model associated to old procedure
	String assetModelIdentifier = procedure.getAssetModel().getIdentifier();
	//copy asset, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.AssetModel, copyAsset(assetModelIdentifier));
	
	// get vulnerability model associated to old procedure
	String vulnerabilityModelIdentifier = procedure.getVulnerabilityModel().getIdentifier();
	//copy vulnerability, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.VulnerabilityModel, copyVulnerability(vulnerabilityModelIdentifier));
	
	// get threat model associated to old procedure
	String threatModelIdentifier = procedure.getThreatModel().getIdentifier();
	//copy threat, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.ThreatModel, copyThreat(threatModelIdentifier));
	
	// get safeguard model associated to old procedure
	String safeguardModelIdentifier = procedure.getSafeguardModel().getIdentifier();
	//copy safeguard, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.SafeguardModel, copySafeguard(safeguardModelIdentifier));

	// get risk treatment model associated to old procedure
	String riskTreatmentModelIdentifier = procedure.getRiskTreatmentModel().getIdentifier();
	//copy safeguard, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.RiskTreatmentModel, copyRiskTreatment(riskTreatmentModelIdentifier));
	
	
	return newModelsMap;
	}
	

	/*
     Create a copy of procedure associated models (AssetModel, ThreatModel, VulnerabilityModel, SafeguardModel, RiskModel)
     This method is called when creating a template
     setting a new SEST Identifier for each of them and inserting them into the db. 
	 */
	public Map<SESTObjectTypeEnum,String> createModelsCopy(AssessmentProcedure procedure){
	Map<SESTObjectTypeEnum,String> newModelsMap= new HashMap<>();
	// create all new models and objects associated to template
	LOG.info("---------------------------createModelsCopy from Procedure for a new Template - begin");
	
	// get risk model associated to procedure
    String riskModelIdentifier = procedure.getRiskModel().getIdentifier();
	//copy risk model, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.RiskModel, copyRisk(riskModelIdentifier).getIdentifier());
	
	// get asset model associated to procedure
	String assetModelIdentifier = procedure.getAssetModel().getIdentifier();
	//copy asset, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.AssetModel, copyAsset(assetModelIdentifier));
	
	// get vulnerability model associated to procedure
	String vulnerabilityModelIdentifier = procedure.getVulnerabilityModel().getIdentifier();
	//copy vulnerability, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.VulnerabilityModel, copyVulnerability(vulnerabilityModelIdentifier));
		
	// get threat model associated to procedure
	String threatModelIdentifier = procedure.getThreatModel().getIdentifier();
	//copy threat, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.ThreatModel, copyThreat(threatModelIdentifier));
	
	// get safeguard model associated to procedure
	String safeguardModelIdentifier = procedure.getSafeguardModel().getIdentifier();
	//copy safeguard, insert it into the db and puts it into the result map
	newModelsMap.put(SESTObjectTypeEnum.SafeguardModel, copySafeguard(safeguardModelIdentifier));
	
	return newModelsMap;
	}
	
	 /*
	 Clone the risk model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private RiskModel copyRisk(String riskModelIdentifier) {
		LOG.info("---------------------------copyRisk - begin");
		
		// Risk copy
		String riskModelJson = riskModelService.getByIdentifier(riskModelIdentifier).getRiskModelJson();	

		RiskModelCloner riskModelCloner = new  RiskModelCloner();
	    // clone the json RiskModel (the identifier involved is changed)
		String newRiskModelJson = riskModelCloner.clone(riskModelJson);
		// get the new RiskModel identifier
		String newRiskModelId = riskModelCloner.getIdentifier();
		//insert the new RiskModel into db
		riskModelService.insert(newRiskModelJson, newRiskModelId);
		
		RiskModelSerializerDeserializer rmsr = new RiskModelSerializerDeserializer();
		RiskModel rm = rmsr.getRMFromJSONString(newRiskModelJson);
		
		return rm;
	}
	
	/*
	 Clone the risk treatment model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private String copyRiskTreatment(String riskTreatmentModelIdentifier) {
		LOG.info("---------------------------copyRiskTreatment - begin");
		
		// Risk copy
		String riskTreatmentModelJson = riskTreatmentModelService.getByIdentifier(riskTreatmentModelIdentifier).getRiskTreatmentModelJson();	

		RiskTreatmentModelCloner riskTreatmentModelCloner = new RiskTreatmentModelCloner();
	    // clone the json RiskTreatmentModel (the identifier involved is changed)
		String newRiskTreatmentModelJson = riskTreatmentModelCloner.clone(riskTreatmentModelJson);
		// get the new RiskTreatmentModel identifier
		String newRiskTreatmentModelId = riskTreatmentModelCloner.getIdentifier();
		//insert the new RiskTreatmentModel into db
		riskTreatmentModelService.insert(newRiskTreatmentModelJson, newRiskTreatmentModelId);
		
		return newRiskTreatmentModelId;
	}
	
	/*
	 Clone the asset model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private String copyAsset(String assetModelIdentifier){
		LOG.info("---------------------------copyAssets - begin");
		// Asset copy
		String assetModelJson = assetModelService.getByIdentifier(assetModelIdentifier).getAssetModelJson();	

		AssetModelCloner assetModelCloner = new  AssetModelCloner();
	    // clone the json AssetModel (the identifier involved is changed)
		String newAssetModelJson = assetModelCloner.clone(assetModelJson);
		// get the new assetModel identifier
		String newAssetModelId = assetModelCloner.getIdentifier();
		//insert the new assetModel into db
		assetModelService.insert(newAssetModelJson, newAssetModelId);
		
		return newAssetModelId;
		
	}
	
	/*
	 Clone the vulnerability model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private String copyVulnerability(String vulnerabilityModelIdentifier){
		LOG.info("---------------------------copyVulnerability - begin");

		// Vulnerability copy
		String vulnerabilityModelJson = vulnerabilityModelService.getByIdentifier(vulnerabilityModelIdentifier).getVulnerabilityModelJson();	

		VulnerabilityModelCloner vulnerabilityModelCloner = new  VulnerabilityModelCloner();
	    // clone the json VulnerabilityModel (the identifier involved is changed)
		String newVulnerabilityModelJson = vulnerabilityModelCloner.clone(vulnerabilityModelJson);
		// get the new vulnerabilityModel identifier
		String newVulnModelId = vulnerabilityModelCloner.getIdentifier();
		//insert the new vulnerabilityModel into db
		vulnerabilityModelService.insert(newVulnerabilityModelJson, newVulnModelId);
		
		return newVulnModelId;
		
	}
	
	/*
	 Clone the safeguard model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private String copySafeguard(String safeguardModelIdentifier){
		LOG.info("copySafeguard - begin");

		// Safeguard copy
		String safeguardModelJson = safeguardModelService.getByIdentifier(safeguardModelIdentifier).getSafeguardModelJson();	

		SafeguardModelCloner safeguardModelCloner = new SafeguardModelCloner();
	    // clone the json SafeguardModel (the identifier of the SafeguardModel object is changed)
		String newSafeguardModelJson = safeguardModelCloner.clone(safeguardModelJson);
		// get the new SafeguardModel identifier
		String newSafeguardModelId = safeguardModelCloner.getIdentifier();
		//insert the new SafeguardModel into db
		safeguardModelService.insert(newSafeguardModelJson, newSafeguardModelId);
		
		return newSafeguardModelId;
		
	}
	
	/*
	 Clone the threat model with the identifier in input.
	 Insert the clone into the DB with a new identifier.
	 */
	private String copyThreat(String threatModelIdentifier){
		LOG.info("copyThreat - begin");

		// Threat copy
		String threatModelJson = threatModelService.getByIdentifier(threatModelIdentifier).getThreatModelJson();	

		ThreatModelCloner threatModelCloner = new  ThreatModelCloner();
	    // clone the json ThreatModel (the identifier involved is changed)
		String newThreatModelJson = threatModelCloner.clone(threatModelJson);
		// get the new threatModel identifier
		String newThreatModelId = threatModelCloner.getIdentifier();
		//insert the new threatModel into db
		threatModelService.insert(newThreatModelJson, newThreatModelId);
		
		return newThreatModelId;
		
	}
	
	/*
	 Create the safeguard model with the project identifier.
	 Insert the new SafeguardModel into the DB
	 */
	private SafeguardModel createSafeguard(AssessmentProject project){
		LOG.info("createSafeguard - begin");

		SafeguardModel safeguards = new SafeguardModel();
		
		UUID uuid = UUID.randomUUID();
		safeguards.setIdentifier(uuid.toString());
		safeguards.setObjType(SESTObjectTypeEnum.SafeguardModel);
		DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
		Date now = new Date();
			
		safeguards.setCreationTime(df.format(now));
		safeguards.setUpdateTime(df.format(now));
	
		safeguards = safeguardModelInput.createSafeguardModel(safeguards, project);
		
		//SafeguardModel serialization
		SafeguardModelSerializerDeserializer serializer = new SafeguardModelSerializerDeserializer();
		
		//insert the new SafeguardModel into db
		safeguardModelService.insert(serializer.getJSONStringFromSM(safeguards), safeguards.getIdentifier());
		
		return safeguards;
		
	}
	
	/*
	 Create the risk treatment model with the project identifier.
	 Insert the new SafeguardModel into the DB
	 */
	private String createRiskTreatment(RiskModel risks, SafeguardModel safeguards){
		LOG.info("createRiskTreatment - begin");

		RiskTreatmentModelCloner riskTreatmentModelCloner = new RiskTreatmentModelCloner();
	    // Create the new RiskTreatmentModel 
		String newRiskTreatmentModelJson = riskTreatmentModelCloner.create(risks, safeguards);
		// get the new RiskTreatmentModel identifier
		String newRiskTreatmentModelId = riskTreatmentModelCloner.getIdentifier();
		
		//insert the new RiskTreatmentModel into db
		riskTreatmentModelService.insert(newRiskTreatmentModelJson, newRiskTreatmentModelId);
		
		return newRiskTreatmentModelId;
	}
}
