/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.risktreatmentmodel.manager;

import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

//The sest-risk-model bundle holds the business logic related to the management of the risk assessment analysis and treatment. It is separated from the sest-core bundle in order to concentrate here
//the risk assessment logic which may depend on the selected risk assessment methodology
public class RiskTreatmentModelManagerInput implements RiskTreatmentModelManagerInputInterface {

  private AssprocedureServiceInterface assprocedureService;
  private RiskTreatmentServiceInterface riskTreatmentModelService;
  private RiskModelManagerInputInterface riskModelInput;
  private SestObjServiceInterface sestObjService;

  private static final Logger LOG = LoggerFactory.getLogger(RiskTreatmentModelManagerInput.class.getName());

  @Override
  public String editRiskTreatmentModel(String riskTreatmentModel, String riskTreatmentModelIdentifier) {
    LOG.info("editRiskTreatmentModel about to edit RiskTreatmentModel: " + riskTreatmentModel + " with identifier: " + riskTreatmentModelIdentifier);

    AssessmentProcedure procedure = assprocedureService.getByRiskTreatmentModelIdentifier(riskTreatmentModelIdentifier);

    LOG.info("editRiskTreatmentModel load assessment procedure with identifier: " + procedure.getIdentifier());

    RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
    RiskTreatmentModel clientRtm = rtmsr.getRTMFromClientJSONString(riskTreatmentModel);

    harmonizeModels(procedure, clientRtm, true);

    RiskTreatmentModelSerializerDeserializer riskTreatmentModelDeserializer = new RiskTreatmentModelSerializerDeserializer();
    String riskTreatmentModelClientFull = riskTreatmentModelDeserializer.getClientFullJSONStringFromRTM(procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel());

    return riskTreatmentModelClientFull;
  }

  @Override
  public String calculateRiskTreatmentModel(String riskTreatmentModel, String riskTreatmentModelIdentifier) {
    LOG.info("calculateRiskTreatmentModel about to calculate RiskTreatmentModel: " + riskTreatmentModel + " with identifier: " + riskTreatmentModelIdentifier);

    AssessmentProcedure procedure = assprocedureService.getByRiskTreatmentModelIdentifier(riskTreatmentModelIdentifier);

    LOG.info("calculateRiskTreatmentModel load assessment procedure with identifier: " + procedure.getIdentifier());

    RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
    RiskTreatmentModel clientRtm = rtmsr.getRTMFromClientJSONString(riskTreatmentModel);

    if (clientRtm != null) {
      harmonizeModels(procedure, clientRtm, false);
    }

    RiskTreatmentModelSerializerDeserializer riskTreatmentModelDeserializer = new RiskTreatmentModelSerializerDeserializer();
    String riskTreatmentModelClientFull = riskTreatmentModelDeserializer.getClientFullJSONStringFromRTM(procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel());

    return riskTreatmentModelClientFull;
  }

  @Override
  public String editRiskTreatmentModelDetail(String riskTreatmentModel, String riskTreatmentModelIdentifier) {
    LOG.info("editRiskTreatmentModelDetail about to edit RiskTreatmentModel: " + riskTreatmentModel + " with identifier: " + riskTreatmentModelIdentifier);

    RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
    String assetPrimaryCategory = rtmsr.getAssetCategoryFromClientJSONString(riskTreatmentModel);

    if (assetPrimaryCategory == null) {
      LOG.error("editRiskTreatmentModelDetail error, assetPrimaryCategory is null");
      return null;
    }

    AssessmentProcedure procedure = assprocedureService.getByRiskTreatmentModelIdentifier(riskTreatmentModelIdentifier);

    LOG.info("editRiskTreatmentModelDetail load assessment procedure with identifier: " + procedure.getIdentifier());

    RiskTreatmentModel clientRtm = rtmsr.getRTMFromClientDetailJSONString(riskTreatmentModel);

    if (clientRtm == null) {
      LOG.error("editRiskTreatmentModelDetail error, clientRtm is null");
      return null;
    }

    harmonizeModels(procedure, clientRtm, true);

    String riskTreatmentModelClientFull = rtmsr.getClientFullJSONStringFromRTM(procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel());

    return riskTreatmentModelClientFull;

  }

  @Override
  public String calculateRiskTreatmentModelDetail(String riskTreatmentModel, String riskTreatmentModelIdentifier) {
    LOG.info("calculateRiskTreatmentModelDetail about to calculate RiskTreatmentModel: " + riskTreatmentModel + " with identifier: " + riskTreatmentModelIdentifier);

    RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
    String assetPrimaryCategory = rtmsr.getAssetCategoryFromClientJSONString(riskTreatmentModel);

    if (assetPrimaryCategory == null) {
      LOG.error("calculateRiskTreatmentModelDetail error, assetPrimaryCategory is null");
      return null;
    }

    AssessmentProcedure procedure = assprocedureService.getByRiskTreatmentModelIdentifier(riskTreatmentModelIdentifier);

    LOG.info("calculateRiskTreatmentModelDetail load assessment procedure with identifier: " + procedure.getIdentifier());

    RiskTreatmentModel clientRtm = rtmsr.getRTMFromClientDetailJSONString(riskTreatmentModel);

    if (clientRtm == null) {
      LOG.error("calculateRiskTreatmentModelDetail error, clientRtm is null");
      return null;
    }

    harmonizeModels(procedure, clientRtm, false);

    String riskTreatmentModelClientFull = rtmsr.getClientFullJSONStringFromRTM(procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel());

    return riskTreatmentModelClientFull;
  }

  //This method loads the RiskTreatmodel from persistency and converts it in the JSON format the SEST client can manage for RiskTreatment View 1 (the grouped view)
  @Override
  public ModelObject loadRiskTreatmentModel(GenericFilter filter) throws Exception {

    String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
    LOG.info("loadRiskTreatmentModel:: input procedure filter = " + procedureIdentifier);

    if (procedureIdentifier != null) {
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

      RiskTreatmentModelSerializerDeserializer riskTreatmentModelDeserializer = new RiskTreatmentModelSerializerDeserializer();

      String riskTreatmentModelClientFull = riskTreatmentModelDeserializer.getClientFullJSONStringFromRTM(
        procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel());
      String identifier = procedure.getRiskTreatmentModel().getIdentifier();
      LOG.info("loadRiskTreatmentModel:: identifier = " + identifier);

      ModelObject modelObject = new ModelObject();
      modelObject.setJsonModel(riskTreatmentModelClientFull);
      modelObject.setObjectIdentifier(identifier);
      modelObject.setLockedBy(sestObjService.getByIdentifier(identifier).getLockedBy());
      return modelObject;
    } else {
      LOG.error("Null procedure identifier in input");
      return null;
    }
  }

  @Override
  public String loadRiskTreatmentModelDetail(GenericFilter filter) throws Exception {

    String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
    String assetPrimaryCategory = filter.getFilterValue(GenericFilterEnum.ASSET_CATEGORY);
    LOG.info("loadRiskTreatmentModelDetail:: input procedure filter = {} assetPrimaryCategory = {} ", procedureIdentifier, assetPrimaryCategory);

    if (procedureIdentifier != null && assetPrimaryCategory != null) {
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

      RiskTreatmentModelSerializerDeserializer riskTreatmentModelDeserializer = new RiskTreatmentModelSerializerDeserializer();

      String riskTreatmentModelClientDetail = riskTreatmentModelDeserializer.getClientDetailJSONStringFromRTM(procedure.getRiskModel(), procedure.getAssetModel(), procedure.getSafeguardModel(), procedure.getRiskTreatmentModel(), assetPrimaryCategory);

      return riskTreatmentModelClientDetail;
    } else {
      LOG.error("Null procedure identifier in input");
      return null;
    }
  }

  private void harmonizeModels(AssessmentProcedure procedure, RiskTreatmentModel clientRtm, boolean persist) {
    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier());

    //We need to updateQuestionnaireJSON the original (persisted RiskTreatmentModel) with the updates from the client
    RiskTreatmentModel treatments = procedure.getRiskTreatmentModel();

    // Here we put the resulting Safeguards (from the RiskTreatment) on
    // an HashMap (faster to be used later)
    HashMap<String, Safeguard> clientResultingSafeguardMap = getSafeguardHashMap(clientRtm.getResultingSafeguards());

    HashMap<String, Safeguard> originalSafeguardMap = getSafeguardHashMap(procedure.getSafeguardModel().getSafeguards());
    // Here we put the resulting SecurityRequirements (from the
    // RiskTreatment) on an HashMap (faster to be used later)
    HashMap<String, SecurityRequirement> clientResultingSecurityRequirementMap = getSecurityRequirementsHashMap(clientRtm.getResultingSafeguards());
    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " clientResultingSafeguardMap1 " + clientResultingSafeguardMap.size());
    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " clientResultingSafeguardMap2 " + originalSafeguardMap.size());
    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " clientResultingSafeguardMap3 " + clientResultingSecurityRequirementMap.size());
    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " clientResultingSafeguardMap4 " + treatments.getResultingSafeguards().size());

    for (Safeguard safeguard : treatments.getResultingSafeguards()) {
      Safeguard resultingSafeguard = clientResultingSafeguardMap.get(safeguard.getIdentifier());

      if (resultingSafeguard == null) {
        //This may happen in case of a detailed risk treatment received (which does not contain all safeguards) or
        //it is possible that there is inconsistency between the safeguards sent by the client and the safeguards saved by the server (this happens
        //sometimes when RiskScenarioReferences changed and some safeguards are not useful anymore, but they have been previously added)

        continue;
      }
      LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " safeguard set score " + resultingSafeguard.getScore());
      //Here we assign the new score value from the client to the Safeguard
      safeguard.setScore(resultingSafeguard.getScore());


      for (SecurityRequirement requirement : safeguard.getRelatedSecurityRequirements()) {

        SecurityRequirement resultingRequirement = clientResultingSecurityRequirementMap.get(requirement.getIdentifier());

        if (resultingRequirement == null) {
          LOG.error("Client resulting requirement not existing. SecurityRequirement with identifier: " + requirement.getIdentifier());
          continue;
        }

        //Here we assign the new score value from the client to the SecurityRequirement
        requirement.setScore(resultingRequirement.getScore());

        harmonizeSecurityRequirement(requirement, clientResultingSecurityRequirementMap);
      }

    }

    LOG.info("harmonizeModels for procedure with identifier: " + procedure.getIdentifier() + " Safeguards harmonization concluded");

    riskModelInput.editRiskTreatmentModel(procedure, persist);
  }

  private void harmonizeSecurityRequirement(SecurityRequirement requirement,
                                            HashMap<String, SecurityRequirement> clientResultingSecurityRequirementMap) {

    for (SecurityRequirement innerRequirement : requirement.getChildren()) {

      SecurityRequirement resultingRequirement = clientResultingSecurityRequirementMap.get(innerRequirement.getIdentifier());

      if (resultingRequirement == null) {
        LOG.error("Client resulting requirement not existing. SecurityRequirement with identifier: " + requirement.getIdentifier());
        continue;
      }

      //Here we assign the new score value from the client to the SecurityRequirement
      innerRequirement.setScore(resultingRequirement.getScore());

      harmonizeSecurityRequirement(innerRequirement, clientResultingSecurityRequirementMap);

    }

  }

  private HashMap<String, SecurityRequirement> getSecurityRequirementsHashMap(
    ArrayList<Safeguard> resultingSafeguards) {
    // Here we put the SecurityRequirements on an HashMap (faster to be used
    // later)
    HashMap<String, SecurityRequirement> resultingSecurityRequirementMap = new HashMap<>();

    for (Safeguard safeguard : resultingSafeguards) {

      for (SecurityRequirement securityRequirement : safeguard.getRelatedSecurityRequirements()) {

        if (!resultingSecurityRequirementMap.containsKey(securityRequirement.getIdentifier())) {
          resultingSecurityRequirementMap.put(securityRequirement.getIdentifier(), securityRequirement);
        }

        getSecurityRequirementsHashMap(resultingSecurityRequirementMap, securityRequirement);
      }
    }
    LOG.info("resultingSecurityRequirementMap size " + resultingSecurityRequirementMap.size());
    return resultingSecurityRequirementMap;
  }

  private void getSecurityRequirementsHashMap(HashMap<String, SecurityRequirement> resultingSecurityRequirementMap,
                                              SecurityRequirement securityRequirement) {

    if (securityRequirement.getChildren() != null) {
      for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {

        if (innerSecurityRequirement instanceof SecurityRequirement) {

          if (!resultingSecurityRequirementMap.containsKey(innerSecurityRequirement.getIdentifier())) {
            resultingSecurityRequirementMap.put(innerSecurityRequirement.getIdentifier(),
              (SecurityRequirement) innerSecurityRequirement);
          }

          getSecurityRequirementsHashMap(resultingSecurityRequirementMap,
            (SecurityRequirement) innerSecurityRequirement);
        } else {
          LOG.error("innerSecurityRequirement not instance of SecurityRequirement " + innerSecurityRequirement.getId());
        }
      }
      LOG.info("resultingSecurityRequirementMap size " + resultingSecurityRequirementMap.size());
    }
  }

  private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
    // Here we put the Safeguard on an HashMap (faster to be used later)
    HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

    for (Safeguard safeguard : safeguards) {
      String safeguardId = safeguard.getIdentifier();

      if (safeguard.getScore() == null) {
        LOG.error("Safeguard score is null! Identifier: " + safeguard.getIdentifier());
      }

      safeguardMap.put(safeguardId, safeguard);

    }
    LOG.info("safeguardMap size " + safeguardMap.size());
    return safeguardMap;
  }

  public RiskTreatmentServiceInterface getRiskTreatmentModelService() {
    return riskTreatmentModelService;
  }

  public void setRiskTreatmentModelService(RiskTreatmentServiceInterface riskTreatmentModelService) {
    this.riskTreatmentModelService = riskTreatmentModelService;
  }

  public AssprocedureServiceInterface getAssprocedureService() {
    return assprocedureService;
  }

  public void setAssprocedureService(AssprocedureServiceInterface assprocedureService) {
    this.assprocedureService = assprocedureService;
  }

  public RiskModelManagerInputInterface getRiskModelInput() {
    return riskModelInput;
  }

  public void setRiskModelInput(RiskModelManagerInputInterface riskModelInput) {
    this.riskModelInput = riskModelInput;
  }

  public SestObjServiceInterface getSestObjService() {
    return sestObjService;
  }

  public void setSestObjService(SestObjServiceInterface sestObjService) {
    this.sestObjService = sestObjService;
  }
}
