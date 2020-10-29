/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.riskmodel.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.*;
import org.crmf.model.riskassessmentelements.*;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.riskmodel.RiskModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.model.utility.vulnerabilitymodel.VulnerabilityModelSerializerDeserializer;
import org.crmf.persistency.domain.risk.SeriousnessScale;
import org.crmf.persistency.domain.risk.StatusImpactScale;
import org.crmf.persistency.domain.risk.StatusLikelihoodScale;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.riskmodel.utility.SecurityMeasuresInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//The sest-risk-model bundle holds the business logic related to the management of the risk assessment analysis and treatment. It is separated from the sest-core bundle in order to concentrate here
//the risk assessment logic which may depend on the selected risk assessment methodology
@Service
public class RiskModelManagerInput {
  private static final Logger LOG = LoggerFactory.getLogger(RiskModelManagerInput.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  public static final String RISK_MODEL_IDENTIFIER = "riskModelIdentifier";
  public static final String METHOD_RESULT = "methodResult";
  public static final String OTHER_MODELS_STATUS = "otherModelsStatus";
  public static final String SUCCESS = "SUCCESS";
  public static final String UPDATED = "UPDATED";
  public static final String NOT_UPDATED = "NOT UPDATED";

  @Autowired
  @Qualifier("default")
  private AssprocedureServiceInterface assprocedureService;
  @Autowired
  @Qualifier("default")
  private VulnerabilityServiceInterface vulnerabilityModelService;
  @Autowired
  @Qualifier("default")
  private ThreatServiceInterface threatModelService;
  @Autowired
  @Qualifier("default")
  private RiskServiceInterface riskModelService;
  @Autowired
  @Qualifier("default")
  private RiskTreatmentServiceInterface riskTreatmentModelService;
  @Autowired
  @Qualifier("default")
  private SestObjServiceInterface sestObjService;
  @Autowired
  private RiskScenariosReferenceImporter importer;
  @Autowired
  SecurityMeasuresInterpreter interpreter;

  private ArrayList<SeriousnessScale> scales;
  private ArrayList<StatusImpactScale> statusImpactScales;
  private ArrayList<StatusLikelihoodScale> statusLikelihoodScales;

  private boolean modelsVulnThreatUpdated = false;
  private boolean modelsRiskTreatmentUpdated = false;

  public String editRiskModel(String riskModel, String riskModelIdentifier) {
    modelsVulnThreatUpdated = false;

    LOG.info("RiskModelManagerInput about to edit RiskModel: " + riskModel.substring(0, (riskModel.length() > 1000 ? 1000 : riskModel.length())));

    AssessmentProcedure procedure = assprocedureService.getByRiskModelIdentifier(riskModelIdentifier);

    LOG.info("RiskModelManagerInput editRiskModel load assessment procedure with identifier: " + procedure.getIdentifier());

    //If there is a mismatch between the RiskModelIdentifier from the client and the saved RiskModelIdentifier, the server cleans the models and returns an error
    //VulnModel and ThreatModel MAY need to be reloaded, while RiskModel will have to be reloaded anyhow
    if (!procedure.getRiskModel().getIdentifier().equals(riskModelIdentifier)) {
      LOG.error("RiskModelManagerInput editRiskModel mismatch between received RiskModel Identifier = " + riskModelIdentifier + " and saved RiskModel identifier = " + procedure.getRiskModel().getIdentifier());

      harmonizeModels(procedure);

      saveModels(procedure);

      JsonObject jsonObject = new JsonObject();

      if (modelsVulnThreatUpdated) {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, UPDATED);
      } else {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, NOT_UPDATED);
      }
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.serializeNulls();

      Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

      return gson.toJson(jsonObject);
    }

    //The new RiskModel is associated to the procedure
    procedure = updateRiskModel(procedure, riskModel);


    //Since another user may have modified the AssetModel (deleting some Assets) or other models, the server must harmonize it and save the resulting models
    harmonizeModels(procedure);

    saveModels(procedure);

    //If, after harmonization and completion, some models (vulnerabilities an threats) have been modified, the client must retrieve them, so we need to communicate that to the client. If only the risk model has been updated, the client
    //will retrieve only the RiskModel
    JsonObject jsonObject = new JsonObject();


    if (modelsVulnThreatUpdated) {
      jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
      jsonObject.addProperty(METHOD_RESULT, SUCCESS);
      jsonObject.addProperty(OTHER_MODELS_STATUS, UPDATED);
    } else {
      jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
      jsonObject.addProperty(METHOD_RESULT, SUCCESS);
      jsonObject.addProperty(OTHER_MODELS_STATUS, NOT_UPDATED);
    }

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();

    Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

    LOG.info("RiskModelManagerInput about to exit editRiskModel");

    return gson.toJson(jsonObject);
  }

  public String editRiskScenario(String riskModel, String riskModelIdentifier) {
    LOG.info("RiskModelManagerInput about to edit editRiskScenario: " + riskModel);

    AssessmentProcedure procedure = assprocedureService.getByRiskModelIdentifier(riskModelIdentifier);

    LOG.info("RiskModelManagerInput editRiskScenario load assessment procedure = " + procedure);

    //If there is a mismatch between the RiskModelIdentifier from the client and the saved RiskModelIdentifier, the server cleans the models and returns an error
    //VulnModel and ThreatModel MAY need to be reloaded, while RiskModel will have to be reloaded anyhow
    if (!procedure.getRiskModel().getIdentifier().equals(riskModelIdentifier)) {
      LOG.error("RiskModelManagerInput editRiskScenario mismatch between received RiskModel Identifier = " + riskModelIdentifier + " and saved RiskModel identifier = " + procedure.getRiskModel().getIdentifier());


      harmonizeModels(procedure);

      saveModels(procedure);

      JsonObject jsonObject = new JsonObject();


      if (modelsVulnThreatUpdated) {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, UPDATED);
      } else {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, NOT_UPDATED);
      }


      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.serializeNulls();

      Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

      return gson.toJson(jsonObject);
    }

    RiskModelSerializerDeserializer rmsd = new RiskModelSerializerDeserializer();

    RiskModel rm = rmsd.getRMFromJSONString(riskModel);

    //Checking if there is the single Risk Scenario we are expecting
    if (rm.getScenarios().size() != 1) {
      LOG.error("Size of RiskScenarios within the RiskModel is not correct (1). Returning ERROR");


      harmonizeModels(procedure);

      saveModels(procedure);

      JsonObject jsonObject = new JsonObject();


      if (modelsVulnThreatUpdated) {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, UPDATED);
      } else {
        jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
        jsonObject.addProperty(METHOD_RESULT, "ERROR");
        jsonObject.addProperty(OTHER_MODELS_STATUS, NOT_UPDATED);
      }


      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.serializeNulls();

      Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

      return gson.toJson(jsonObject);
    }

    //The new RiskScenario is associated to the RiskModel in the procedure

    updateRiskScenario(procedure, rm);

    //Since another user may have modified the AssetModel (deleting some Assets) the server must harmonize it and save the resulting models

    harmonizeModels(procedure);

    saveModels(procedure);

    //If, after harmonization and completion, some models have been modified, the client must retrieve them, so we need to communicate that to the client. If only the risk model has been updated, the client
    //will retrieve only the RiskModel
    JsonObject jsonObject = new JsonObject();


    if (modelsVulnThreatUpdated) {
      jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
      jsonObject.addProperty(METHOD_RESULT, SUCCESS);
      jsonObject.addProperty(OTHER_MODELS_STATUS, UPDATED);
    } else {
      jsonObject.addProperty(RISK_MODEL_IDENTIFIER, riskModelIdentifier);
      jsonObject.addProperty(METHOD_RESULT, SUCCESS);
      jsonObject.addProperty(OTHER_MODELS_STATUS, NOT_UPDATED);
    }

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();

    Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

    LOG.info("RiskModelManagerInput about to exit editRiskModel: " + gson.toJson(jsonObject));

    return gson.toJson(jsonObject);
  }


  private void updateRiskScenario(AssessmentProcedure procedure, RiskModel riskModel) {
    LOG.info("RiskModelManagerInput updateRiskScenario riskModel; from client: " + riskModel.getIdentifier());

    RiskModel originalRM = procedure.getRiskModel();
    RiskScenario originalRiskScenario = null;

    //We already checked if there is one RiskScenario. It must be unique, since the editRiskScenario method has been called Client side
    RiskScenario updatedRiskScenario = riskModel.getScenarios().get(0);

    for (RiskScenario scenario : originalRM.getScenarios()) {
      if (updatedRiskScenario.getIdentifier().equals(scenario.getIdentifier())) {
        originalRiskScenario = scenario;
        break;
      }
    }

    if (originalRiskScenario == null) {
      //Something went wrong: we can't find the Risk Scenario in the original persisted Risk Model!!!
      LOG.error("Unable to find the Risk Scenario sent from the SEST client!!!");


      return;
    }

    originalRiskScenario.setExpertConfinability(updatedRiskScenario.getExpertConfinability());
    originalRiskScenario.setExpertLikelihood(updatedRiskScenario.getExpertLikelihood());
    originalRiskScenario.setExpertImpact(updatedRiskScenario.getExpertImpact());
    originalRiskScenario.setUserDescription(updatedRiskScenario.getUserDescription());
    originalRiskScenario.setExpertImpactDescription(updatedRiskScenario.getExpertImpactDescription());
    originalRiskScenario.setExpertLikelihoodDescription(updatedRiskScenario.getExpertLikelihoodDescription());
    originalRiskScenario.setScenarioResult(updatedRiskScenario.getScenarioResult());
    originalRiskScenario.setExcluded(updatedRiskScenario.isExcluded());

  }

  private void completeRiskModel(AssetModel assets, VulnerabilityModel vulnerabilities, ThreatModel threats,
                                 RiskModel risks, boolean riskTreatmentSimulation) {
    LOG.info("RiskModelManagerInput completeRiskModel");

    scales = (ArrayList<SeriousnessScale>) riskModelService.getSeriousness(1);
    LOG.info("RiskModelManagerInput completeRiskModel scales " + scales);

    if (scales == null || scales.size() == 0) {
      getDefaultScales();
    }

    statusImpactScales = (ArrayList<StatusImpactScale>) riskModelService.getStatusImpact(1);
    LOG.info("RiskModelManagerInput statusImpactScales scales");

    statusLikelihoodScales = (ArrayList<StatusLikelihoodScale>) riskModelService.getStatusLikelihood(1);
    LOG.info("RiskModelManagerInput statusImpactScales scales");

    //Each RiskScenario is checked in order to compute the correct values

    for (RiskScenario scenario : risks.getScenarios()) {

      LOG.info("RiskModelManagerInput completeRiskModel scenario " + scenario.getIdentifier());

      String assetId = scenario.getAssetId();
      String vulnerabilityId = scenario.getVulnerabilityId();
      String threatId = scenario.getThreatId();

      Asset asset = null;
      for (Asset assetInModel : assets.getAssets()) {
        if (assetId.equals(assetInModel.getIdentifier())) {
          asset = assetInModel;
          break;
        }
      }

      Vulnerability vulnerability = null;
      for (Vulnerability vulnerabilityInModel : vulnerabilities.getVulnerabilities()) {
        if (vulnerabilityId.equals(vulnerabilityInModel.getIdentifier())) {
          vulnerability = vulnerabilityInModel;
          break;
        }
      }

      Threat threat = null;
      for (Threat threatInModel : threats.getThreats()) {
        if (threatId.equals(threatInModel.getIdentifier())) {
          threat = threatInModel;
          break;
        }
      }

      //This should not happen since we harmonized before the models
      if (asset == null) {
        LOG.error("RiskModelManagerInput completeRiskModel critical error: missing asset from the models");
        continue;
      }

      if (vulnerability == null) {
        LOG.info("RiskModelManagerInput completeRiskModel missing vulnerability");
        continue;
      }

      //Scenario intrinsic Impact is given by the corresponding Impact on the Asset
      //We need to keep in mind that if we assigned a vulnerability with not impact on a SecurityImpact scope that has a value for the asset, there is not resulting intrinsic impact for the scenario!
      for (SecurityImpact securityImpact : asset.getSecurityImpacts()) {
        if (securityImpact.getScope().equals(scenario.getImpactScope())) {
          scenario.setIntrinsicImpact(securityImpact.getImpact());
          LOG.info("RiskModelManagerInput completeRiskModel setIntrinsicImpact " + securityImpact.getImpact());
          break;
        } else {
          scenario.setIntrinsicImpact(null);
        }
      }

      if (threat == null) {
        LOG.info("RiskModelManagerInput completeRiskModel missing threat from the models");
        continue;
      }
      if (threat.getScore() == null) {
        LOG.info("RiskModelManagerInput completeRiskModel missing threat score from the models");
        continue;
      }
      if (threat.getScore().getLikelihood() == null) {
        LOG.info("RiskModelManagerInput completeRiskModel missing threat likelihood from the models");
        continue;
      }

      //Set threat class for the scenario from the Threat
      scenario.setThreatClass(threat.getThreatClass());

      //Scenario intrinsic Likelihood is given by the corresponding likelihood of the threat
      scenario.setIntrinsicLikelihood(threat.getScore().getLikelihood());

      if (scenario.getIntrinsicImpact() == null) {
        scenario.setIntrinsicSeriousness(null);
      } else {
        scenario.setIntrinsicSeriousness(calculateSeriousness(scenario.getIntrinsicImpact(), scenario.getIntrinsicLikelihood()));
      }

      //Given the safeguard associated to the scenario and the related values of Dissuasion, Prevention, Confining, Palliation,
      //the calculated values for Impact and Likelihood must be computed (similar to the IP GRIDS of the MEHARI knowledgebase)

      computeCalculatedValues(scenario);

      if (scenario.getCalculatedLikelihood() == null) {
        LOG.error("RiskModelManagerInput completeRiskModel getCalculatedLikelihood == null");
        continue;
      }

      if (scenario.getCalculatedImpact() == null) {
        scenario.setCalculatedSeriousness(null);
      } else {
        scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getCalculatedImpact(), scenario.getCalculatedLikelihood()));
      }

      if (!riskTreatmentSimulation) {
        //If expert values have been inserted, they override any intrinsic or calculated impact/likelihood (UNDEPENDANT OF THE SAFEGUARDS)
        if (scenario.getExpertImpact() != null && scenario.getExpertLikelihood() != null) {
          LOG.info("1 Expert values impact: " + scenario.getExpertImpact().toString() + " likelihood: " + scenario.getExpertLikelihood().toString());
          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getExpertImpact(), scenario.getExpertLikelihood()));
        } else if (scenario.getExpertImpact() != null && scenario.getExpertLikelihood() == null) {
          LOG.info("2 Expert values impact: " + scenario.getExpertImpact().toString());
          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getExpertImpact(), scenario.getCalculatedLikelihood()));
        } else if (scenario.getExpertLikelihood() != null && scenario.getExpertImpact() == null) {
          LOG.info("3 Expert values likelihood: " + scenario.getExpertLikelihood().toString());
          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getCalculatedImpact(), scenario.getExpertLikelihood()));
        }
      } else {
        //If we are simulating the treatment of scenarios and we have expert values, we still want to reduce the seriousness. The expert values hence become
        //the "new" intrinsic values to be reduced
        LOG.info("Risk Treatment simulation");
        if (scenario.getExpertImpact() != null && scenario.getExpertLikelihood() != null) {
          LOG.info("1 Expert values impact: " + scenario.getExpertImpact().toString() + " likelihood: " + scenario.getExpertLikelihood().toString());
          computeCalculatedValuesTreatment(scenario, scenario.getExpertImpact(), scenario.getExpertLikelihood());

          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getCalculatedImpact(), scenario.getCalculatedLikelihood()));
        } else if (scenario.getExpertImpact() != null && scenario.getExpertLikelihood() == null) {
          LOG.info("2 Expert values impact: " + scenario.getExpertImpact().toString());
          computeCalculatedValuesTreatment(scenario, scenario.getExpertImpact(), scenario.getCalculatedLikelihood());

          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getCalculatedImpact(), scenario.getCalculatedLikelihood()));
        } else if (scenario.getExpertLikelihood() != null && scenario.getExpertImpact() == null) {
          LOG.info("3 Expert values likelihood: " + scenario.getExpertLikelihood().toString());
          computeCalculatedValuesTreatment(scenario, scenario.getCalculatedImpact(), scenario.getExpertLikelihood());

          scenario.setCalculatedSeriousness(calculateSeriousness(scenario.getCalculatedImpact(), scenario.getCalculatedLikelihood()));
        }
      }


      scenario.setDescription(computeScenarioDescription(asset, vulnerability, threat));

      //It is always possible for an user to exclude any scenario from the seriousness computation. resulting seriousness is therefore null
      if (scenario.isExcluded()) {
        LOG.info("Scenario with identifier " + scenario.getIdentifier() + " has been excluded and its seriousness is nullified");

        scenario.setCalculatedSeriousness(ImpactEnum.LOW);
      }

      //If Risk is Transferred, Avoided or Accepted, calculated seriousness is null
      if (!scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce)) {
        LOG.info("Scenario with identifier " + scenario.getIdentifier() + " it is not reduced and its seriousness is nullified");

        scenario.setCalculatedSeriousness(ImpactEnum.LOW);
      }

    }

    LOG.info("RiskModelManagerInput completeRiskModel done for riskModel with id: " + risks.getIdentifier());
  }

  private void computeCalculatedValuesTreatment(RiskScenario scenario, ImpactEnum impact, LikelihoodEnum likelihood) {
    LOG.info("RiskModelManagerInput computeCalculatedValuesTreatment for scenario with Id: " + scenario.getIdentifier());

    //Here we are reproducing the computations made using the IP Grids in the MEHARI knowledge base

    //A set of rows have been loaded from the STATUS_IMPACT table.
    //Each row represents a unique combination of values to be matched with the values in the RiskScenario.
    //For example, a RiskScenario with SecurityImpact "Availability", Confining value Low, Palliation value Low and IntrinsicImpact Low,
    //would correspond to one ONLY row in the table giving the correct CalculatedImpact value
    for (StatusImpactScale statusImpact : statusImpactScales) {

      if (scenario.getImpactScope().toString().equals(statusImpact.getSecurityImpact())) {

        SafeguardEffectiveness confining = SafeguardEffectiveness.LOW;
        SafeguardEffectiveness palliation = SafeguardEffectiveness.LOW;
        ImpactEnum intrinsicImpact = ImpactEnum.LOW;
        ImpactEnum calculatedImpact = ImpactEnum.LOW;

        if (statusImpact.getConfining() == 1) {
          confining = SafeguardEffectiveness.LOW;
        }
        if (statusImpact.getConfining() == 2) {
          confining = SafeguardEffectiveness.MEDIUM;
        }
        if (statusImpact.getConfining() == 3) {
          confining = SafeguardEffectiveness.HIGH;
        }
        if (statusImpact.getConfining() == 4) {
          confining = SafeguardEffectiveness.VERY_HIGH;
        }

        if (statusImpact.getPalliation() == 1) {
          palliation = SafeguardEffectiveness.LOW;
        }
        if (statusImpact.getPalliation() == 2) {
          palliation = SafeguardEffectiveness.MEDIUM;
        }
        if (statusImpact.getPalliation() == 3) {
          palliation = SafeguardEffectiveness.HIGH;
        }
        if (statusImpact.getPalliation() == 4) {
          palliation = SafeguardEffectiveness.VERY_HIGH;
        }

        if (statusImpact.getIntrinsincImpact() == 1) {
          intrinsicImpact = ImpactEnum.LOW;
        }
        if (statusImpact.getIntrinsincImpact() == 2) {
          intrinsicImpact = ImpactEnum.MEDIUM;
        }
        if (statusImpact.getIntrinsincImpact() == 3) {
          intrinsicImpact = ImpactEnum.HIGH;
        }
        if (statusImpact.getIntrinsincImpact() == 4) {
          intrinsicImpact = ImpactEnum.CRITICAL;
        }

        if (statusImpact.getCalculatedImpact() == 1) {
          calculatedImpact = ImpactEnum.LOW;
        }
        if (statusImpact.getCalculatedImpact() == 2) {
          calculatedImpact = ImpactEnum.MEDIUM;
        }
        if (statusImpact.getCalculatedImpact() == 3) {
          calculatedImpact = ImpactEnum.HIGH;
        }
        if (statusImpact.getCalculatedImpact() == 4) {
          calculatedImpact = ImpactEnum.CRITICAL;
        }

        if (scenario.getPalliation().equals(palliation) && scenario.getConfining().equals(confining) && impact.equals(intrinsicImpact)) {
          scenario.setCalculatedImpact(calculatedImpact);
        }

      }
    }


    //A set of rows have been loaded from the STATUS_LIKELIHOOD table.
    //Each row represents a unique combination of values to be matched with the values in the RiskScenario.
    //For example, a RiskScenario with Class "Accidental", Dissuasion value Low, Preventions value Low and IntrinsicLikelihood Low,
    //would correspond to one ONLY row in the table giving the correct CalculatedLikelihood value
    for (StatusLikelihoodScale statusLikelihood : statusLikelihoodScales) {
      if (scenario.getThreatClass().toString().equals(statusLikelihood.getThreatClass())) {

        SafeguardEffectiveness dissuasion = SafeguardEffectiveness.LOW;
        SafeguardEffectiveness prevention = SafeguardEffectiveness.LOW;
        LikelihoodEnum intrinsicLikelihood = LikelihoodEnum.LOW;
        LikelihoodEnum calculatedLikelihood = LikelihoodEnum.LOW;

        if (statusLikelihood.getDissiuasion() == 1) {
          dissuasion = SafeguardEffectiveness.LOW;
        }
        if (statusLikelihood.getDissiuasion() == 2) {
          dissuasion = SafeguardEffectiveness.MEDIUM;
        }
        if (statusLikelihood.getDissiuasion() == 3) {
          dissuasion = SafeguardEffectiveness.HIGH;
        }
        if (statusLikelihood.getDissiuasion() == 4) {
          dissuasion = SafeguardEffectiveness.VERY_HIGH;
        }


        if (statusLikelihood.getPrevention() == 1) {
          prevention = SafeguardEffectiveness.LOW;
        }
        if (statusLikelihood.getPrevention() == 2) {
          prevention = SafeguardEffectiveness.MEDIUM;
        }
        if (statusLikelihood.getPrevention() == 3) {
          prevention = SafeguardEffectiveness.HIGH;
        }
        if (statusLikelihood.getPrevention() == 4) {
          prevention = SafeguardEffectiveness.VERY_HIGH;
        }

        if (statusLikelihood.getIntrinsincLikelihood() == 1) {
          intrinsicLikelihood = LikelihoodEnum.LOW;
        }
        if (statusLikelihood.getIntrinsincLikelihood() == 2) {
          intrinsicLikelihood = LikelihoodEnum.MEDIUM;
        }
        if (statusLikelihood.getIntrinsincLikelihood() == 3) {
          intrinsicLikelihood = LikelihoodEnum.HIGH;
        }
        if (statusLikelihood.getIntrinsincLikelihood() == 4) {
          intrinsicLikelihood = LikelihoodEnum.VERY_HIGH;
        }

        if (statusLikelihood.getCalculatedLikelihood() == 1) {
          calculatedLikelihood = LikelihoodEnum.LOW;
        }
        if (statusLikelihood.getCalculatedLikelihood() == 2) {
          calculatedLikelihood = LikelihoodEnum.MEDIUM;
        }
        if (statusLikelihood.getCalculatedLikelihood() == 3) {
          calculatedLikelihood = LikelihoodEnum.HIGH;
        }
        if (statusLikelihood.getCalculatedLikelihood() == 4) {
          calculatedLikelihood = LikelihoodEnum.VERY_HIGH;
        }

        if (scenario.getDissuasion().equals(dissuasion) && scenario.getPrevention().equals(prevention) && likelihood.equals(intrinsicLikelihood)) {
          scenario.setCalculatedLikelihood(calculatedLikelihood);
        }

      }
    }

  }

  private void computeCalculatedValues(RiskScenario scenario) {
    LOG.info("RiskModelManagerInput computeCalculatedValues for scenario with Id: " + scenario.getIdentifier());

    //Here we are reproducing the computations made using the IP Grids in the MEHARI knowledge base
    if (statusImpactScales == null || statusImpactScales.size() == 0 || scenario.getIntrinsicImpact() == null) {
      scenario.setCalculatedImpact(scenario.getIntrinsicImpact());
    } else {
      //A set of rows have been loaded from the STATUS_IMPACT table.
      //Each row represents a unique combination of values to be matched with the values in the RiskScenario.
      //For example, a RiskScenario with SecurityImpact "Availability", Confining value Low, Palliation value Low and IntrinsicImpact Low,
      //would correspond to one ONLY row in the table giving the correct CalculatedImpact value
      for (StatusImpactScale statusImpact : statusImpactScales) {

        if (scenario.getImpactScope().toString().equals(statusImpact.getSecurityImpact())) {

          SafeguardEffectiveness confining = SafeguardEffectiveness.LOW;
          SafeguardEffectiveness palliation = SafeguardEffectiveness.LOW;
          ImpactEnum intrinsicImpact = ImpactEnum.LOW;
          ImpactEnum calculatedImpact = ImpactEnum.LOW;

          if (statusImpact.getConfining() == 1) {
            confining = SafeguardEffectiveness.LOW;
          }
          if (statusImpact.getConfining() == 2) {
            confining = SafeguardEffectiveness.MEDIUM;
          }
          if (statusImpact.getConfining() == 3) {
            confining = SafeguardEffectiveness.HIGH;
          }
          if (statusImpact.getConfining() == 4) {
            confining = SafeguardEffectiveness.VERY_HIGH;
          }


          if (statusImpact.getPalliation() == 1) {
            palliation = SafeguardEffectiveness.LOW;
          }
          if (statusImpact.getPalliation() == 2) {
            palliation = SafeguardEffectiveness.MEDIUM;
          }
          if (statusImpact.getPalliation() == 3) {
            palliation = SafeguardEffectiveness.HIGH;
          }
          if (statusImpact.getPalliation() == 4) {
            palliation = SafeguardEffectiveness.VERY_HIGH;
          }

          if (statusImpact.getIntrinsincImpact() == 1) {
            intrinsicImpact = ImpactEnum.LOW;
          }
          if (statusImpact.getIntrinsincImpact() == 2) {
            intrinsicImpact = ImpactEnum.MEDIUM;
          }
          if (statusImpact.getIntrinsincImpact() == 3) {
            intrinsicImpact = ImpactEnum.HIGH;
          }
          if (statusImpact.getIntrinsincImpact() == 4) {
            intrinsicImpact = ImpactEnum.CRITICAL;
          }

          if (statusImpact.getCalculatedImpact() == 1) {
            calculatedImpact = ImpactEnum.LOW;
          }
          if (statusImpact.getCalculatedImpact() == 2) {
            calculatedImpact = ImpactEnum.MEDIUM;
          }
          if (statusImpact.getCalculatedImpact() == 3) {
            calculatedImpact = ImpactEnum.HIGH;
          }
          if (statusImpact.getCalculatedImpact() == 4) {
            calculatedImpact = ImpactEnum.CRITICAL;
          }

          if (scenario.getPalliation().equals(palliation) && scenario.getConfining().equals(confining) && scenario.getIntrinsicImpact().equals(intrinsicImpact)) {
            scenario.setCalculatedImpact(calculatedImpact);
          }

        }
      }
    }

    if (statusLikelihoodScales == null || statusLikelihoodScales.size() == 0 || scenario.getIntrinsicLikelihood() == null) {
      scenario.setCalculatedLikelihood(scenario.getIntrinsicLikelihood());
    } else {
      //A set of rows have been loaded from the STATUS_LIKELIHOOD table.
      //Each row represents a unique combination of values to be matched with the values in the RiskScenario.
      //For example, a RiskScenario with Class "Accidental", Dissuasion value Low, Preventions value Low and IntrinsicLikelihood Low,
      //would correspond to one ONLY row in the table giving the correct CalculatedLikelihood value
      for (StatusLikelihoodScale statusLikelihood : statusLikelihoodScales) {
        if (scenario.getThreatClass().toString().equals(statusLikelihood.getThreatClass())) {

          SafeguardEffectiveness dissuasion = SafeguardEffectiveness.LOW;
          SafeguardEffectiveness prevention = SafeguardEffectiveness.LOW;
          LikelihoodEnum intrinsicLikelihood = LikelihoodEnum.LOW;
          LikelihoodEnum calculatedLikelihood = LikelihoodEnum.LOW;

          if (statusLikelihood.getDissiuasion() == 1) {
            dissuasion = SafeguardEffectiveness.LOW;
          }
          if (statusLikelihood.getDissiuasion() == 2) {
            dissuasion = SafeguardEffectiveness.MEDIUM;
          }
          if (statusLikelihood.getDissiuasion() == 3) {
            dissuasion = SafeguardEffectiveness.HIGH;
          }
          if (statusLikelihood.getDissiuasion() == 4) {
            dissuasion = SafeguardEffectiveness.VERY_HIGH;
          }


          if (statusLikelihood.getPrevention() == 1) {
            prevention = SafeguardEffectiveness.LOW;
          }
          if (statusLikelihood.getPrevention() == 2) {
            prevention = SafeguardEffectiveness.MEDIUM;
          }
          if (statusLikelihood.getPrevention() == 3) {
            prevention = SafeguardEffectiveness.HIGH;
          }
          if (statusLikelihood.getPrevention() == 4) {
            prevention = SafeguardEffectiveness.VERY_HIGH;
          }

          if (statusLikelihood.getIntrinsincLikelihood() == 1) {
            intrinsicLikelihood = LikelihoodEnum.LOW;
          }
          if (statusLikelihood.getIntrinsincLikelihood() == 2) {
            intrinsicLikelihood = LikelihoodEnum.MEDIUM;
          }
          if (statusLikelihood.getIntrinsincLikelihood() == 3) {
            intrinsicLikelihood = LikelihoodEnum.HIGH;
          }
          if (statusLikelihood.getIntrinsincLikelihood() == 4) {
            intrinsicLikelihood = LikelihoodEnum.VERY_HIGH;
          }

          if (statusLikelihood.getCalculatedLikelihood() == 1) {
            calculatedLikelihood = LikelihoodEnum.LOW;
          }
          if (statusLikelihood.getCalculatedLikelihood() == 2) {
            calculatedLikelihood = LikelihoodEnum.MEDIUM;
          }
          if (statusLikelihood.getCalculatedLikelihood() == 3) {
            calculatedLikelihood = LikelihoodEnum.HIGH;
          }
          if (statusLikelihood.getCalculatedLikelihood() == 4) {
            calculatedLikelihood = LikelihoodEnum.VERY_HIGH;
          }

          if (scenario.getDissuasion().equals(dissuasion) && scenario.getPrevention().equals(prevention) && scenario.getIntrinsicLikelihood().equals(intrinsicLikelihood)) {
            scenario.setCalculatedLikelihood(calculatedLikelihood);
          }

        }
      }

    }


  }

  private String computeScenarioDescription(Asset asset, Vulnerability vulnerability, Threat threat) {
    LOG.info("RiskModelManagerInput computeScenarioDescription");

    String description = "";

    if (vulnerability.getCatalogue().equals(VulnerabilitySourceEnum.CWE)) {
      //TODO
    } else {
      description = vulnerability.getDescription();
    }
    description += ", due to ";


    if (threat.getEvent().getDescription() != null && !threat.getEvent().getDescription().equals("")) {
      description += threat.getEvent().getDescription();
    }

    description += ", of ";
    description += asset.getName();
    description += " (";
    description += asset.getPrimaryCategories().get(0).getValue();
    description += ") ";

    if (threat.getActor().getDescription() != null && !threat.getActor().getDescription().equals("")) {
      description += ", by ";
      description += threat.getActor().getDescription();
    }

    if (threat.getAccess().getDescription() != null && !threat.getAccess().getDescription().equals("")) {
      description += ", ";
      description += threat.getAccess().getDescription();
    }

    if (threat.getPlace().getDescription() != null && !threat.getPlace().getDescription().equals("")) {
      description += ", ";
      description += threat.getPlace().getDescription();
    }

    if (threat.getTime().getDescription() != null && !threat.getTime().getDescription().equals("")) {
      description += ", ";
      description += threat.getTime().getDescription();
    }

    if (threat.getProcess().getDescription() != null && !threat.getProcess().getDescription().equals("")) {
      description += ", ";
      description += threat.getProcess().getDescription();
    }

    LOG.info("RiskModelManagerInput computeScenarioDescription results: " + description);

    return description;
  }

  //This method computes the Seriousness of a scenario using the SeriousnessScales from the DB
  private ImpactEnum calculateSeriousness(ImpactEnum impact, LikelihoodEnum likelihood) {

    LOG.info("RiskModelManagerInput calculateSeriousness impact = " + impact + " likelihood = " + likelihood);

    int impactInt = 1;
    int likelihoodInt = 1;
    if (impact.equals(ImpactEnum.LOW)) {
      impactInt = 1;
    } else if (impact.equals(ImpactEnum.MEDIUM)) {
      impactInt = 2;
    } else if (impact.equals(ImpactEnum.HIGH)) {
      impactInt = 3;
    } else if (impact.equals(ImpactEnum.CRITICAL)) {
      impactInt = 4;
    }

    if (likelihood.equals(LikelihoodEnum.LOW)) {
      likelihoodInt = 1;
    } else if (likelihood.equals(LikelihoodEnum.MEDIUM)) {
      likelihoodInt = 2;
    } else if (likelihood.equals(LikelihoodEnum.HIGH)) {
      likelihoodInt = 3;
    } else if (likelihood.equals(LikelihoodEnum.VERY_HIGH)) {
      likelihoodInt = 4;
    }

    for (SeriousnessScale scale : scales) {
      if (scale.getImpact() == impactInt && scale.getLikelihood() == likelihoodInt) {
        int seriousnessInt = scale.getSeriousness();

        if (seriousnessInt == 1) {
          return ImpactEnum.LOW;
        } else if (seriousnessInt == 2) {
          return ImpactEnum.MEDIUM;
        } else if (seriousnessInt == 3) {
          return ImpactEnum.HIGH;
        } else if (seriousnessInt == 4) {
          return ImpactEnum.CRITICAL;
        }
      }
    }

    LOG.error("RiskModelManagerInput calculateSeriousness critical error: seriousness correspondance with Impact and Likelihood not found!!!!");

    return ImpactEnum.CRITICAL;
  }


  private AssessmentProcedure updateRiskModel(AssessmentProcedure procedure, String riskModel) {
    LOG.info("RiskModelManagerInput updateRiskModel riskModel from client: " + riskModel);

    RiskModelSerializerDeserializer rmsd = new RiskModelSerializerDeserializer();

    RiskModel rm = rmsd.getRMFromJSONString(riskModel);

    procedure.setRiskModel(rm);

    return procedure;
  }

  public ModelObject loadRiskModel(GenericFilter filter) throws Exception {
    // get the procedure identifier passed in input
    String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
    LOG.debug("loadRiskModel:: input procedure filter = " + procedureIdentifier);

    // if the value of procedure identifier is not null
    if (procedureIdentifier != null) {
      // retrieve the assessment procedure associated to the procedure
      // identifier in input
      AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

      // retrieve the threat model identifier associated to the
      // retrieved assessment procedure
      String sestobjId = procedure.getRiskModel().getIdentifier();

      // return the json risk model associated to the
      // risk model identifier retrieved
      ModelObject modelObject = new ModelObject();
      modelObject.setJsonModel(riskModelService.getByIdentifier(sestobjId).getRiskModelJson());
      modelObject.setObjectIdentifier(sestobjId);
      modelObject.setLockedBy(sestObjService.getByIdentifier(sestobjId).getLockedBy());
      return modelObject;
    } else {
      throw new Exception("Incorrect procedure identifier in input");
    }
  }

  //This method is called everytime the client calls the EditAssetModel.
  //It is mandatory since changing the assets may have impact on VulnerabilityModel, ThreatModel and RiskModel
  public void editAssetModel(String assetModelIdentifier) {
    LOG.info("RiskModelManagerInput editAssetModel:: identifier = " + assetModelIdentifier);

    AssessmentProcedure procedure = assprocedureService.getByAssetModelIdentifier(assetModelIdentifier);

    harmonizeModels(procedure);

    saveModels(procedure);

  }

  //This  method is called everytime an Audit is modified. There exists a single audit for each project, which is reflected on SafeguardModels for each procedure
  public void editSafeguardModel(String procedureIdentifier) {
    LOG.info("RiskModelManagerInput editSafeguardModel:: procedure identifier = " + procedureIdentifier);

    AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

    harmonizeModels(procedure);

    saveModels(procedure);
  }

  //This  method is called everytime an Audit is modified. There exists a single audit for each project, which is reflected on SafeguardModels for each procedure
  public void editRiskTreatmentModel(AssessmentProcedure procedure, boolean persist) {
    LOG.info("RiskModelManagerInput editRiskTreatmentModel:: procedure identifier = " + procedure.getIdentifier());

    harmonizeModels(procedure);

    if (persist) {
      saveModels(procedure);
    }
  }

  //This  method is called everytime an AssessmentProcedure is created, in order to eventually updateQuestionnaireJSON the values of the Models
  public void createAssessmentProcedure(AssessmentProcedure procedure) {
    LOG.info("RiskModelManagerInput createAssessmentProcedure:: procedure identifier = " + procedure.getIdentifier());

    harmonizeModels(procedure);

    saveModels(procedure);
  }

  private void saveModels(AssessmentProcedure procedure) {
    LOG.info("RiskModelManagerInput saveModels");
    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
    Date now = new Date();

    procedure.getRiskModel().setUpdateTime(df.format(now));
    RiskModelSerializerDeserializer rmsr = new RiskModelSerializerDeserializer();
    String riskModelJson = rmsr.getJSONStringFromRM(procedure.getRiskModel());


    LOG.info("saveModels about to updateQuestionnaireJSON RiskModel");
    riskModelService.update(riskModelJson, procedure.getRiskModel().getIdentifier());

    if (modelsVulnThreatUpdated) {
      procedure.getVulnerabilityModel().setUpdateTime(df.format(now));
      VulnerabilityModelSerializerDeserializer vmsr = new VulnerabilityModelSerializerDeserializer();
      String vulnerabilityModelJson = vmsr.getJSONStringFromVM(procedure.getVulnerabilityModel());


      LOG.info("saveModels about to updateQuestionnaireJSON VulnerabilityModel");
      vulnerabilityModelService.update(vulnerabilityModelJson, procedure.getVulnerabilityModel().getIdentifier());

      procedure.getThreatModel().setUpdateTime(df.format(now));
      ThreatModelSerializerDeserializer tmsr = new ThreatModelSerializerDeserializer();
      String threatModelJson = tmsr.getJSONStringFromTM(procedure.getThreatModel());


      LOG.info("saveModels about to updateQuestionnaireJSON ThreatModel");
      threatModelService.update(threatModelJson, procedure.getThreatModel().getIdentifier());
    }

    if (modelsRiskTreatmentUpdated) {
      procedure.getRiskTreatmentModel().setUpdateTime(df.format(now));
      RiskTreatmentModelSerializerDeserializer rtmsd = new RiskTreatmentModelSerializerDeserializer();
      String riskTreatmentModelJson = rtmsd.getJSONStringFromRTM(procedure.getRiskTreatmentModel());

      LOG.info("saveModels about to updateQuestionnaireJSON RiskTreatmentModel");
      riskTreatmentModelService.update(riskTreatmentModelJson, procedure.getRiskTreatmentModel().getIdentifier());
    }

    LOG.info("saveModels about to updateQuestionnaireJSON AssessmentProcedure");
    procedure.setUpdateTime(df.format(now));
    assprocedureService.update(procedure);
  }


  private void harmonizeModels(AssessmentProcedure procedure) {
    LOG.info("RiskModelManagerInput harmonizeModels");
    AssetModel assets = procedure.getAssetModel();
    VulnerabilityModel vulnerabilities = procedure.getVulnerabilityModel();
    ThreatModel threats = procedure.getThreatModel();
    SafeguardModel safeguards = procedure.getSafeguardModel();
    RiskModel risks = procedure.getRiskModel();
    RiskTreatmentModel treatments = procedure.getRiskTreatmentModel();

    for (RiskScenario scenario : risks.getScenarios()) {

      LOG.info("harmonizeModels scenario " + scenario.getIdentifier());
      if (scenario.getAssetId() == null) {
        scenario.setAssetId("");
      }
      if (scenario.getVulnerabilityId() == null) {
        scenario.setVulnerabilityId("");
      }
      if (scenario.getThreatId() == null) {
        scenario.setThreatId("");
      }
      if (scenario.getPalliation() == null) {
        scenario.setPalliation(SafeguardEffectiveness.LOW);
      }
      if (scenario.getConfining() == null) {
        scenario.setConfining(SafeguardEffectiveness.LOW);
      }
      if (scenario.getPrevention() == null) {
        scenario.setPrevention(SafeguardEffectiveness.LOW);
      }
      if (scenario.getDissuasion() == null) {
        scenario.setDissuasion(SafeguardEffectiveness.LOW);
      }
      if (scenario.getSafeguardIds() == null) {
        scenario.setSafeguardIds(new ArrayList<String>());
      }
      if (scenario.getScenarioResult() == null) {
        scenario.setScenarioResult(ScenarioResultEnum.Reduce);
      }
    }


    LOG.info("RiskModelManagerInput about to check AssetModel");
    checkAssetModel(assets, risks);

    //Now we have to make Vulnerability and Threat Models consistent
    LOG.info("RiskModelManagerInput about to check VulnerabilityModel");
    checkVulnerabilityModel(vulnerabilities, assets, risks);

    LOG.info("RiskModelManagerInput about to check ThreatModel");
    checkThreatModel(threats, assets, vulnerabilities, risks);

    LOG.info("RiskModelManagerInput about to check SafeguardModel");
    checkSafeguardModel(assets, vulnerabilities, threats, safeguards.getSafeguards(), risks);

    //The RiskModel from the client is missing any computation (impact, seriousness, etc). This method will fill these missing fields
    LOG.info("RiskModelManagerInput about to completeRiskModel");
    completeRiskModel(assets, vulnerabilities, threats, risks, false);

    LOG.info("RiskModelManagerInput about to check RiskTreatmentModel");
    checkRiskTreatmentModel(safeguards, risks, treatments);

    //Risk Analysis is complete. The RiskModel has been updated
    //Now it is time to updateQuestionnaireJSON the RiskTreatmentModel (within it, we have to "simulate" the effect of desired new scores for the Safeguards)

    harmonizeRiskTreatmentModel(procedure);
  }

  private void harmonizeRiskTreatmentModel(AssessmentProcedure procedure) {
    LOG.info("RiskModelManagerInput about to harmonize RiskTreatmentModel");

    //We need to clone the RIskModel in order to simulate the results of the Treatment on the RiskScenarios
    RiskModelSerializerDeserializer rmsd = new RiskModelSerializerDeserializer();
    String rmJson = rmsd.getJSONStringFromRM(procedure.getRiskModel());

    RiskModel risksCloned = rmsd.getRMFromJSONString(rmJson);
    LOG.info("RiskModelManagerInput about to check Safeguards in RiskTreatmentModel");

    checkSafeguardModel(procedure.getAssetModel(), procedure.getVulnerabilityModel(), procedure.getThreatModel(), procedure.getRiskTreatmentModel().getResultingSafeguards(), risksCloned);

    LOG.info("RiskModelManagerInput about to completeRiskModel for RiskTreatmentModel");
    completeRiskModel(procedure.getAssetModel(), procedure.getVulnerabilityModel(), procedure.getThreatModel(), risksCloned, true);

    //Updating the RiskTreatments within the RiskTreatmentModel with the results of the simulation
    HashMap<String, RiskTreatment> riskTreatmentMap = getRiskTreatmentHashMap(procedure.getRiskTreatmentModel().getRiskTreatments());

    for (RiskScenario scenario : risksCloned.getScenarios()) {
      if (riskTreatmentMap.containsKey(scenario.getIdentifier())) {
        RiskTreatment treatment = riskTreatmentMap.get(scenario.getIdentifier());

        if (treatment.getResultingSeriousness().getScore() != scenario.getCalculatedSeriousness().getScore()) {
          modelsRiskTreatmentUpdated = true;
          LOG.info("harmonizeRiskTreatmentModel seriousness changed after treatment for treatment with identifier: " + treatment.getIdentifier());

          treatment.setResultingSeriousness(scenario.getCalculatedSeriousness());
        }

        //This should not happen
        if (treatment.getCurrentSeriousness().getScore() < treatment.getResultingSeriousness().getScore()) {
          LOG.error("harmonizeRiskTreatmentModel RiskTreatment with identifier: {} with CurrentSeriousness < than ResultingSeriousness. This should not happen here", treatment.getIdentifier());
          modelsRiskTreatmentUpdated = true;
          treatment.setResultingSeriousness(treatment.getCurrentSeriousness());
        }
      } else {
        //This should not happen
        if (!scenario.isExcluded() && scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce) && !scenario.getAssetId().equals("") &&
          !scenario.getVulnerabilityId().equals("") && !scenario.getThreatId().equals("")) {

          LOG.error("harmonizeRiskTreatmentModel RiskScenario with identifier: {} not existing in RiskTreatmentModel. This should not happen here ", scenario.getIdentifier());

        }
      }
    }

  }

  private HashMap<String, SecurityRequirement> getSecurityRequirementsHashMap(
    ArrayList<Safeguard> safeguards) {
    // Here we put the SecurityRequirements on an HashMap (faster to be used
    // later)
    HashMap<String, SecurityRequirement> securityRequirementMap = new HashMap<>();

    for (Safeguard safeguard : safeguards) {

      for (SecurityRequirement securityRequirement : safeguard.getRelatedSecurityRequirements()) {

        if (!securityRequirementMap.containsKey(securityRequirement.getIdentifier())) {
          securityRequirementMap.put(securityRequirement.getIdentifier(), securityRequirement);
        }

        getSecurityRequirementsHashMap(securityRequirementMap, securityRequirement);
      }
    }
    LOG.info("resultingSecurityRequirementMap size " + securityRequirementMap.size());
    return securityRequirementMap;
  }

  private void getSecurityRequirementsHashMap(HashMap<String, SecurityRequirement> securityRequirementMap,
                                              SecurityRequirement securityRequirement) {

    if (securityRequirement.getChildren() != null) {
      for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {

        if (!securityRequirementMap.containsKey(innerSecurityRequirement.getIdentifier())) {
          securityRequirementMap.put(innerSecurityRequirement.getIdentifier(),
            innerSecurityRequirement);
        }

        getSecurityRequirementsHashMap(securityRequirementMap,
          innerSecurityRequirement);
      }
      LOG.info("resultingSecurityRequirementMap size {} ", securityRequirementMap.size());
    }
  }

  private void checkSecurityRequirement(SecurityRequirement resultingRequirement,
                                        HashMap<String, SecurityRequirement> securityRequirementMap) {

    for (SecurityRequirement innerRequirement : resultingRequirement.getChildren()) {

      SecurityRequirement requirement = securityRequirementMap.get(innerRequirement.getIdentifier());

      if (requirement == null) {
        LOG.error("Client resulting requirement not existing. SecurityRequirement with identifier: {} ", innerRequirement.getIdentifier());
        continue;
      }

      if (innerRequirement.getScore().getScore() < requirement.getScore().getScore()) {
        modelsRiskTreatmentUpdated = true;
        innerRequirement.setScore(requirement.getScore());
      }

      checkSecurityRequirement(innerRequirement, securityRequirementMap);

    }

  }

  private void checkRiskTreatmentModel(SafeguardModel safeguards, RiskModel risks, RiskTreatmentModel treatments) {
    //We need to check the SafeguardModel (from the Audit) and compare it with the SafeguardModel in the RiskTreatmentModel (the desired new safeguards)
    //In fact, desired new safeguards cannot be lower than original ones

    LOG.info("checkRiskTreatmentModel begin");

    // Here we put the existing Safeguards on
    // an HashMap (faster to be used later)
    HashMap<String, Safeguard> safeguardMap = getSafeguardHashMap(safeguards.getSafeguards());

    // Here we put the existing SecurityRequirements
    // on an HashMap (faster to be used later)
    HashMap<String, SecurityRequirement> securityRequirementMap = getSecurityRequirementsHashMap(safeguards.getSafeguards());

    //We also need to keep the RiskTreatment list coupled with the RiskScenario list (Excluded Scenario or Scenario not Reduced are not in RIskTreatment)
    HashMap<String, RiskTreatment> riskTreatmentMap = getRiskTreatmentHashMap(treatments.getRiskTreatments());

    List<RiskTreatment> treatmentToAdd = new ArrayList<>();
    List<String> safeguardInTreatment = new ArrayList<>();

    // check and syncronize scenario from risk model with scenario from treatment
    for (RiskScenario scenario : risks.getScenarios()) {
      if (riskTreatmentMap.containsKey(scenario.getIdentifier())) {
        RiskTreatment treatment = riskTreatmentMap.get(scenario.getIdentifier());

        treatment.setCurrentSeriousness(scenario.getCalculatedSeriousness());

        if (treatment.getResultingSeriousness() == null) {
          LOG.error("treatment with seriousness null: " + treatment.getIdentifier());
          treatment.setResultingSeriousness(treatment.getCurrentSeriousness());
        }

        if (treatment.getCurrentSeriousness().getScore() < treatment.getResultingSeriousness().getScore()) {
          modelsRiskTreatmentUpdated = true;
          treatment.setResultingSeriousness(treatment.getCurrentSeriousness());
        }
        safeguardInTreatment.addAll(scenario.getSafeguardIds());
      } else {
        if (!scenario.isExcluded() && scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce) && !scenario.getAssetId().equals("") &&
          !scenario.getVulnerabilityId().equals("") && !scenario.getThreatId().equals("")) {

          modelsRiskTreatmentUpdated = true;
          RiskTreatment treatment = new RiskTreatment();
          String treatmentId = UUID.randomUUID().toString();
          treatment.setIdentifier(treatmentId);
          treatment.setObjType(SESTObjectTypeEnum.RiskTreatmentModel);

          treatment.setCurrentSeriousness(scenario.getCalculatedSeriousness());
          treatment.setResultingSeriousness(scenario.getCalculatedSeriousness());
          treatment.setRiskScenarioId(scenario.getIdentifier());

          treatmentToAdd.add(treatment);
          safeguardInTreatment.addAll(scenario.getSafeguardIds());
        }
      }
    }

    // check resulting safeguard in treatment and syncronize if lower with value from audit
    for (Safeguard resultingSafeguard : treatments.getResultingSafeguards()) {

      Safeguard safeguard = safeguardMap.get(resultingSafeguard.getIdentifier());

      if (safeguard == null) {
        LOG.error("Resulting Safeguard with identifier {} not existing in SafeguardModel", resultingSafeguard.getIdentifier());
        continue;
      }

      // if treatment target value is lower then audit value or safeguard is not included in treatment scenarios
      if ((resultingSafeguard.getScore().getScore() < safeguard.getScore().getScore()) ||
        !safeguardInTreatment.contains(resultingSafeguard.getIdentifier())) {
        modelsRiskTreatmentUpdated = true;
        resultingSafeguard.setScore(safeguard.getScore());
      }

      for (SecurityRequirement resultingRequirement : resultingSafeguard.getRelatedSecurityRequirements()) {

        SecurityRequirement requirement = securityRequirementMap.get(resultingRequirement.getIdentifier());

        if (requirement == null) {
          LOG.error("Client resulting requirement not existing. SecurityRequirement with identifier: {} ", resultingRequirement.getIdentifier());
          continue;
        }

        if (resultingRequirement.getScore().getScore() < requirement.getScore().getScore()) {
          modelsRiskTreatmentUpdated = true;
          resultingRequirement.setScore(requirement.getScore());
        }
        checkSecurityRequirement(resultingRequirement, securityRequirementMap);
      }
    }


    treatments.getRiskTreatments().addAll(treatmentToAdd);

    HashMap<String, RiskScenario> scenarioMap = getRiskScenarioHashMap(risks.getScenarios());

    List<RiskTreatment> treatmentToDelete = new ArrayList<>();

    for (RiskTreatment treatment : treatments.getRiskTreatments()) {
      RiskScenario scenario = scenarioMap.get(treatment.getRiskScenarioId());
      if (!scenarioMap.containsKey(treatment.getRiskScenarioId()) ||
        (scenario.isExcluded() || !scenario.getScenarioResult().equals(ScenarioResultEnum.Reduce) || scenario.getAssetId().equals("") ||
          scenario.getVulnerabilityId().equals("") || scenario.getThreatId().equals(""))) {
        modelsRiskTreatmentUpdated = true;
        treatmentToDelete.add(treatment);
      }
    }

    treatments.getRiskTreatments().removeAll(treatmentToDelete);

    LOG.info("checkRiskTreatmentModel end");
  }

  private HashMap<String, RiskScenario> getRiskScenarioHashMap(ArrayList<RiskScenario> riskScenarios) {
    //Here we put the RiskScenario on an HashMap (faster to be used later)
    HashMap<String, RiskScenario> scenarioMap = new HashMap<String, RiskScenario>();

    for (RiskScenario riskScenario : riskScenarios) {
      String scenarioId = riskScenario.getIdentifier();

      scenarioMap.put(scenarioId, riskScenario);
    }

    return scenarioMap;
  }

  private HashMap<String, RiskTreatment> getRiskTreatmentHashMap(ArrayList<RiskTreatment> riskTreatments) {
    //Here we put the RiskTreatment on an HashMap (faster to be used later)
    HashMap<String, RiskTreatment> treatmentMap = new HashMap<String, RiskTreatment>();

    for (RiskTreatment riskTreatment : riskTreatments) {
      String scenarioId = riskTreatment.getRiskScenarioId();

      treatmentMap.put(scenarioId, riskTreatment);
    }

    return treatmentMap;
  }

  private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
    //Here we put the Safeguard on an HashMap (faster to be used later)
    HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

    for (Safeguard safeguard : safeguards) {
      String safeguardId = safeguard.getIdentifier();

      safeguardMap.put(safeguardId, safeguard);
    }

    return safeguardMap;
  }

  private void checkSafeguardModel(AssetModel assets, VulnerabilityModel vulnerabilities, ThreatModel threats, ArrayList<Safeguard> safeguards,
                                   RiskModel risks) {

    LOG.info("checkSafeguardModel begin");

    // Load of all existing risk Scenarios References
    List<RiskScenarioReference> allRsr = riskModelService.getRiskScenarioReference();

    //This method checks and assigns Safeguard Ids to the proper scenario (palliation etc computation will be done in the completeRiskModel method)
    for (RiskScenario scenario : risks.getScenarios()) {
      if (scenario.getAssetId().equals("") || scenario.getVulnerabilityId().equals("") || scenario.getThreatId().equals("")) {
        LOG.info("checkSafeguardModel RiskScenario with identifier {} unable to compute Security Measures values since the RiskScenario is not complete", scenario.getIdentifier());
        continue;
      }

      Asset asset = null;
      for (Asset anAsset : assets.getAssets()) {
        if (anAsset.getIdentifier().equals(scenario.getAssetId())) {
          asset = anAsset;
          break;
        }
      }

      Vulnerability vulnerabilty = null;
      for (Vulnerability aVulnerabilty : vulnerabilities.getVulnerabilities()) {
        if (aVulnerabilty.getIdentifier().equals(scenario.getVulnerabilityId())) {
          vulnerabilty = aVulnerabilty;
          break;
        }
      }

      Threat threat = null;
      for (Threat aThreat : threats.getThreats()) {
        if (aThreat.getIdentifier().equals(scenario.getThreatId())) {
          threat = aThreat;
          break;
        }
      }

      //here we call the interpreter in order to get the current values of Security Measures and associate the Safeguards to the Scenario
      interpreter.manageRiskScenarioMeasures(scenario, safeguards, asset, threat, vulnerabilty, allRsr);

    }

    LOG.info("checkSafeguardModel end");

  }


  private void checkThreatModel(ThreatModel threats, AssetModel assets, VulnerabilityModel vulnerabilities, RiskModel risks) {

    //Here we check if some scenario have references to non existing threats
    ArrayList<RiskScenario> scenariosToDelete = new ArrayList<>();

    for (RiskScenario scenario : risks.getScenarios()) {

      if (scenario.getThreatId().equals("")) {
        continue;
      }
      boolean threatInScenario = false;

      for (Threat threat : threats.getThreats()) {
        if (scenario.getThreatId().equals(threat.getIdentifier())) {
          threatInScenario = true;
          break;
        }
      }
      if (!threatInScenario) {

        LOG.info("checkThreatModel scenarioToDelete due to reference to non existing threat {} ", scenario.getIdentifier());
        scenariosToDelete.add(scenario);
      }
    }

    risks.getScenarios().removeAll(scenariosToDelete);

    //Here we check if the surviving scenario have the proper combinations of Asset-SecondaryAssetCategoryType and Threat-SecondaryAssetCategoryType
    scenariosToDelete = new ArrayList<>();

    for (RiskScenario scenario : risks.getScenarios()) {
      if (scenario.getThreatId().equals("")) {
        continue;
      }

      Threat threat = null;
      for (Threat th : threats.getThreats()) {
        if (scenario.getThreatId().equals(th.getIdentifier())) {
          threat = th;
          break;
        }
      }
      Asset asset = null;
      for (Asset as : assets.getAssets()) {
        if (scenario.getAssetId().equals(as.getIdentifier())) {
          asset = as;
          break;
        }
      }

      boolean categoryExisting = false;
      for (SecondaryAssetCategoryEnum category : threat.getAffectedAssetsCategories()) {
        if (category.equals(asset.getCategory())) {
          categoryExisting = true;
          break;
        }
      }

      if (!categoryExisting) {
        //The asset secondary category is not among the threat-allowed categories. the scenario must be removed
        LOG.info("checkThreatModel scenarioToDelete due to secondary asset category not compliant with threat {} ", scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }

      Vulnerability vulnerability = null;
      for (Vulnerability vuln : vulnerabilities.getVulnerabilities()) {
        if (scenario.getVulnerabilityId().equals(vuln.getIdentifier())) {
          vulnerability = vuln;
          break;
        }
      }

      boolean vulnerabilityExisting = false;
      for (String vulnId : threat.getAssociatedVulnerabilities()) {
        if (vulnId.equals(vulnerability.getCatalogueId())) {
          vulnerabilityExisting = true;
          break;
        }
      }

      if (!vulnerabilityExisting) {
        //Each threat can be only related to specific vulnerabilities. If not, the scenario must be removed
        LOG.info("checkThreatModel scenarioToDelete due to vulnerability not compliant with threat {} ", scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }
    }

    risks.getScenarios().removeAll(scenariosToDelete);

    //Now we check how many threats must be deleted (since some scenario may be deleted)
    ArrayList<Threat> threatsToDelete = new ArrayList<>();
    for (Threat threat : threats.getThreats()) {
      boolean threatInScenario = false;
      for (RiskScenario scenario : risks.getScenarios()) {

        if (scenario.getThreatId().equals(threat.getIdentifier())) {
          threatInScenario = true;
          break;
        }
      }

      if (!threatInScenario) {
        LOG.info("checkThreatModel threatToDelete {} ", threat.getIdentifier());
        threatsToDelete.add(threat);
      }
    }

    if (threats.getThreats().removeAll(threatsToDelete)) {
      LOG.info("checkThreatModel modelsVulnThreatUpdated = true");
      modelsVulnThreatUpdated = true;
    }
  }

  private void checkVulnerabilityModel(VulnerabilityModel vulnerabilities, AssetModel assets, RiskModel risks) {


    //Here we check if some scenario have references to non existing vulnerabilities
    ArrayList<RiskScenario> scenariosToDelete = new ArrayList<>();

    for (RiskScenario scenario : risks.getScenarios()) {
      if (scenario.getVulnerabilityId().equals("")) {
        scenariosToDelete.add(scenario);
        continue;
      }

      boolean vulnerabilityInScenario = false;

      for (Vulnerability vulnerability : vulnerabilities.getVulnerabilities()) {
        if (scenario.getVulnerabilityId().equals(vulnerability.getIdentifier())) {
          vulnerabilityInScenario = true;
          break;
        }
      }
      if (!vulnerabilityInScenario) {

        LOG.info("checkVulnerabilityModel scenarioToDelete due to reference to non existing vulnerability " + scenario.getIdentifier());
        scenariosToDelete.add(scenario);
      }
    }

    risks.getScenarios().removeAll(scenariosToDelete);

    //Here we check if the surviving scenario have the proper combinations of Asset-SecondaryAssetCategoryType and Vulnerability-SecondaryAssetCategoryType, and Asset-SecurityImpact and VUlnerability-SecurityImpact
    scenariosToDelete = new ArrayList<>();

    for (RiskScenario scenario : risks.getScenarios()) {
      Vulnerability vulnerability = null;
      for (Vulnerability vuln : vulnerabilities.getVulnerabilities()) {
        if (scenario.getVulnerabilityId().equals(vuln.getIdentifier())) {
          vulnerability = vuln;
          break;
        }
      }
      Asset asset = null;
      for (Asset as : assets.getAssets()) {
        if (scenario.getAssetId().equals(as.getIdentifier())) {
          asset = as;
          break;
        }
      }

      boolean categoryExisting = false;
      if (vulnerability != null && asset != null) {
        for (SecondaryAssetCategoryEnum category : vulnerability.getAffectedAssetsCategories()) {
          if (category.equals(asset.getCategory())) {
            categoryExisting = true;
            break;
          }
        }
      }

      if (!categoryExisting) {
        //The asset secondary category is not among the vulnerability-allowed categories. the scenario must be removed
        LOG.info("checkVulnerabilityModel scenarioToDelete due to secondary asset category not compliant with vulnerability " + scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }

      boolean securityImpactMatched = false;
      for (Consequence consequence : (vulnerability.getScore()).getConsequences()) {

        for (SecurityImpact scopeVuln : consequence.getSecurityImpacts()) {
          if (scenario.getImpactScope().equals(scopeVuln.getScope())) {
            securityImpactMatched = true;
            break;
          }
        }

      }

      if (!securityImpactMatched) {
        //The asset securityimpactscope is not among the vulnerability secuirty impact scope
        LOG.info("checkVulnerabilityModel scenarioToDelete due to an asset securityscope not compliant with vulnerability " + scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }

    }

    risks.getScenarios().removeAll(scenariosToDelete);


    //Now we check how many vulnerabilities must be deleted (since some assets may have been deleted)
    ArrayList<Vulnerability> vulnerabilitiesToDelete = new ArrayList<>();
    for (Vulnerability vulnerability : vulnerabilities.getVulnerabilities()) {
      boolean vulnerabilityInScenario = false;
      for (RiskScenario scenario : risks.getScenarios()) {
        if (scenario.getVulnerabilityId().equals(vulnerability.getIdentifier())) {
          vulnerabilityInScenario = true;
          break;
        }
      }
      if (!vulnerabilityInScenario) {
        LOG.info("checkVulnerabilityModel vulnerabilityToDelete " + vulnerability.getIdentifier());
        vulnerabilitiesToDelete.add(vulnerability);
      }

    }

    if (vulnerabilities.getVulnerabilities().removeAll(vulnerabilitiesToDelete)) {
      LOG.info("checkVulnerabilityModel modelsVulnThreatUpdated = true");
      modelsVulnThreatUpdated = true;
    }

  }


  private void checkAssetModel(AssetModel assets, RiskModel risks) {

    //Here we check how many assets have been deleted or they don't have a proper assetId
    ArrayList<RiskScenario> scenariosToDelete = new ArrayList<>();

    for (RiskScenario scenario : risks.getScenarios()) {
      boolean assetInScenario = false;

      if (scenario.getAssetId().equals("")) {
        scenariosToDelete.add(scenario);
        continue;
      }

      Asset asset = null;
      for (Asset scenarioAsset : assets.getAssets()) {
        if (scenario.getAssetId().equals(scenarioAsset.getIdentifier())) {
          asset = scenarioAsset;
          assetInScenario = true;
          break;
        }
      }

      if (!assetInScenario) {
        LOG.info("checkAssetModel scenarioToDelete since asset in scenario does not exist in AssetModel " + scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }


      boolean securityImpactMatched = false;
      for (SecurityImpact scopeAsset : asset.getSecurityImpacts()) {

        if (scenario.getImpactScope().equals(scopeAsset.getScope())) {
          securityImpactMatched = true;
          break;
        }
      }

      if (!securityImpactMatched) {
        LOG.info("checkAssetModel scenarioToDelete since scenario SecurityImpactScope is not compatible with SecurityImpactScope of the Asset " + scenario.getIdentifier());
        scenariosToDelete.add(scenario);
        continue;
      }

    }

    //Here we remove the scenario related to deleted assets
    risks.getScenarios().removeAll(scenariosToDelete);


  }

  //If it is impossible to get the Seriousness Values from the DB, we use a set of default values.
  private void getDefaultScales() {
    scales = new ArrayList<SeriousnessScale>();

    scales.add(new SeriousnessScale(1, 1, 1));
    scales.add(new SeriousnessScale(2, 1, 1));
    scales.add(new SeriousnessScale(3, 1, 2));
    scales.add(new SeriousnessScale(4, 1, 2));
    scales.add(new SeriousnessScale(1, 2, 1));
    scales.add(new SeriousnessScale(2, 2, 2));
    scales.add(new SeriousnessScale(3, 2, 3));
    scales.add(new SeriousnessScale(4, 2, 3));
    scales.add(new SeriousnessScale(1, 3, 1));
    scales.add(new SeriousnessScale(2, 3, 2));
    scales.add(new SeriousnessScale(3, 3, 3));
    scales.add(new SeriousnessScale(4, 3, 4));
    scales.add(new SeriousnessScale(1, 4, 2));
    scales.add(new SeriousnessScale(2, 4, 3));
    scales.add(new SeriousnessScale(3, 4, 4));
    scales.add(new SeriousnessScale(4, 4, 4));
  }

  public void updateScenarioRepository(String catalogue) throws Exception {

    importer.init();
  }

  //not used for the moment
  private RiskScenario createRiskScenario(Asset asset) {

    RiskScenario scenario = new RiskScenario();

    UUID uuid = UUID.randomUUID();
    scenario.setIdentifier(uuid.toString());

    scenario.setAssetId(asset.getIdentifier());

    scenario.setObjType(SESTObjectTypeEnum.RiskModel);
    scenario.setDescription(asset.getName());

    return scenario;
  }

  public String insertRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    return this.riskModelService.insertRiskScenarioReference(riskScenarioReference);
  }

  public void deleteRiskScenarioReference(List<String> identifier) throws Exception {
    this.riskModelService.deleteRiskScenarioReference(identifier);
  }

  public void editRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    this.riskModelService.editRiskScenarioReference(riskScenarioReference);
  }

  public List<RiskScenarioReference> getRiskScenarioReference() {
    return riskModelService.getRiskScenarioReference();
  }

  public boolean updateScenarioRepository(List<RiskScenarioReference> rsr) {
    return riskModelService.updateScenarioRepository(rsr);
  }
}
