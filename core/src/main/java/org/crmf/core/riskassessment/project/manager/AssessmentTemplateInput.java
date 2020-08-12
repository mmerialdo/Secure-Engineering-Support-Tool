/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.project.manager;

import org.crmf.core.riskassessment.utility.RiskAssessmentModelsCloner;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AsstemplateServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssessmentTemplates
public class AssessmentTemplateInput implements AssessmentTemplateInputInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentTemplateInput.class.getName());
	private AsstemplateServiceInterface asstemplateService;
	// Procedure service variable of persistency component
	private AssprocedureServiceInterface assprocedureService;
	private AssetServiceInterface assetModelService;
	private VulnerabilityServiceInterface vulnerabilityModelService;
	private ThreatServiceInterface threatModelService;
	private SafeguardServiceInterface safeguardModelService;
	private RiskAssessmentModelsCloner modelsCloner;
	private RiskServiceInterface riskModelService;
	private UserPermissionManagerInputInterface permissionManager;
	private RiskTreatmentServiceInterface riskTreatmentModelService;

	@Override
	public String createAssessmentTemplate(AssessmentTemplate template, String profileIdentifier) throws Exception {

		try{
			LOG.info("createAssessmentTemplate with identifier: {}", template.getIdentifier());
	
			// copy the template: 
			// - a copy of of all models is created
			// - the sest object identifier of each one is changed into a new one
			// - the models with the new identifier are inserted into the db
			AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(template.getIdentifier());
			
			Map<SESTObjectTypeEnum,String> newModelsMap = modelsCloner.createModelsCopy(procedure);
	
			// updateQuestionnaireJSON models'id into the new template
			updateModels(newModelsMap, template);
			
			//set type and identifier accordingly to the sest object just created (template)
			template.setObjType(SESTObjectTypeEnum.AssessmentTemplate);
			template.setIdentifier(template.getIdentifier());
			
			return asstemplateService.insert(template, profileIdentifier);
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public List<AssessmentTemplate> loadAssessmentTemplate(GenericFilter filter) throws Exception {

		LOG.info("loadAssessmentTemplate {}", filter.getFilterMap());
		String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
		if(filter.getFilterValue(GenericFilterEnum.METHODOLOGY) != null){

			LOG.info("loadAssessmentTemplate METHODOLOGY");
			return asstemplateService.getByMethodology(filter.getFilterValue(GenericFilterEnum.METHODOLOGY));
		} else if(filter.getFilterValue(GenericFilterEnum.PROFILE) != null){

			LOG.info("loadAssessmentTemplate PROFILE");
			return asstemplateService.getByProfileIdentifier(filter.getFilterValue(GenericFilterEnum.PROFILE));
		} else {

			List<AssessmentTemplate> templates = new ArrayList<>();
			LOG.info("loadAssessmentTemplate IDENTIFIER"+identifier);
			templates.add(asstemplateService.getByIdentifier(identifier));
			return templates;
		}
	}
	
	@Override
	public AssessmentTemplate loadAssessmentTemplateByIdentifier(String identifier) throws Exception {

		LOG.info("loadAssessmentTemplateByIdentifier: "+ identifier); 
		return asstemplateService.getByIdentifierFull(identifier);
	}

	@Override
	public List<AssessmentTemplate> loadAssessmentTemplateList() throws Exception {

		LOG.info("loadAssessmentTemplateList ");
		return asstemplateService.getAll();
	}
	
	
    //Update the models stored into the Procedure with the models associated to the identifiers in input
	private void updateModels(Map<SESTObjectTypeEnum,String> newModelsMap,AssessmentTemplate template){
		LOG.info("-----------------template updateModels:: begin");
		
		//for each model passed in input
		for(SESTObjectTypeEnum modelType: newModelsMap.keySet()){
			// get the identifier of the current model sest object 
			String identifier = newModelsMap.get(modelType);
			// insert the model into the procedure based on model type
			switch(modelType){
			case AssetModel:
				LOG.info("-----------------template updateModels:: asset id " + identifier);
				template.setAssetModel(assetModelService.getByIdentifier(identifier).convertToModel());
			break;
			case VulnerabilityModel:
				LOG.info("-----------------template updateModels:: vuln id " + identifier);
				template.setVulnerabilityModel(vulnerabilityModelService.getByIdentifier(identifier).convertToModel());
				break;
			case ThreatModel:
				LOG.info("-----------------template updateModels:: threat id " + identifier);
				template.setThreatModel(threatModelService.getByIdentifier(identifier).convertToModel());
				break;
			case SafeguardModel:
				LOG.info("-----------------template updateModels:: safeguard id " + identifier);
				template.setSafeguardModel(safeguardModelService.getByIdentifier(identifier).convertToModel());
				break;
			case RiskModel:
				LOG.info("-----------------template updateModels:: risk id " + identifier);
				template.setRiskModel(riskModelService.getByIdentifier(identifier).convertToModel());
				break;
			case RiskTreatmentModel:
				LOG.info("-----------------template updateModels:: risk treatment id " + identifier);
			/*	template.setRiskTreatmentModel(riskTreatmentModelService.getByIdentifier(identifier).convertToModel());*/
				break;
			default:
				break;
			}		
		}
	}
	

	public AsstemplateServiceInterface getAsstemplateService() {
		return asstemplateService;
	}

	public void setAsstemplateService(AsstemplateServiceInterface asstemplateService) {
		this.asstemplateService = asstemplateService;
	}

	public RiskAssessmentModelsCloner getModelsCloner() {
		return modelsCloner;
	}

	public void setModelsCloner(RiskAssessmentModelsCloner modelsCloner) {
		this.modelsCloner = modelsCloner;
	}
	
	public AssprocedureServiceInterface getAssprocedureService() {
		return assprocedureService;
	}

	public void setAssprocedureService(AssprocedureServiceInterface assprocedureService) {
		this.assprocedureService = assprocedureService;
	}
	
	public AssetServiceInterface getAssetModelService() {
		return assetModelService;
	}

	public void setAssetModelService(AssetServiceInterface assetModelService) {
		this.assetModelService = assetModelService;
	}

	public VulnerabilityServiceInterface getVulnerabilityModelService() {
		return vulnerabilityModelService;
	}

	public void setVulnerabilityModelService(VulnerabilityServiceInterface vulnerabilityModelService) {
		this.vulnerabilityModelService = vulnerabilityModelService;
	}

	public ThreatServiceInterface getThreatModelService() {
		return threatModelService;
	}

	public void setThreatModelService(ThreatServiceInterface threatModelService) {
		this.threatModelService = threatModelService;
	}

	public RiskServiceInterface getRiskModelService() {
		return riskModelService;
	}

	public void setRiskModelService(RiskServiceInterface riskModelService) {
		this.riskModelService = riskModelService;
	}

	public UserPermissionManagerInputInterface getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(UserPermissionManagerInputInterface permissionManager) {
		this.permissionManager = permissionManager;
	}

	public SafeguardServiceInterface getSafeguardModelService() {
		return safeguardModelService;
	}

	public void setSafeguardModelService(SafeguardServiceInterface safeguardModelService) {
		this.safeguardModelService = safeguardModelService;
	}

	public RiskTreatmentServiceInterface getRiskTreatmentModelService() {
		return riskTreatmentModelService;
	}

	public void setRiskTreatmentModelService(RiskTreatmentServiceInterface riskTreatmentModelService) {
		this.riskTreatmentModelService = riskTreatmentModelService;
	}

}
