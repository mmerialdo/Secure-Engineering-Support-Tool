/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelClientDetailInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/


package org.crmf.model.utility.risktreatmentmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.requirement.Requirement;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.RiskTreatment;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//The json structure to be returned to the client is quite different
// from the Java RiskTreatmentModel structure
// we then need to serialize/deserialize accordingly
public class RiskTreatmentModelClientDetailInstanceCreator
  implements JsonDeserializer<RiskTreatmentModel[]>, JsonSerializer<RiskTreatmentModel> {

  private static final Logger LOG = LoggerFactory
    .getLogger(RiskTreatmentModelClientDetailInstanceCreator.class.getName());
  public static final String DATA = "data";
  public static final String RISK_SCENARIO_IDENTIFIER = "riskScenarioIdentifier";
  public static final String RISK_TREATMENT_IDENTIFIER = "riskTreatmentIdentifier";
  public static final String CHILDREN = "children";
  public static final String SAFEGUARD_IDENTIFIER = "safeguardIdentifier";
  public static final String TARGET_VALUE = "targetValue";
  public static final String SECURITY_REQUIREMENT_IDENTIFIER = "securityRequirementIdentifier";
  public static final String TYPE = "type";
  public static final String PRIMARY_ASSET_CATEGORY = "primaryAssetCategory";
  public static final String SECONDARY_ASSET_CATEGORY = "secondaryAssetCategory";
  public static final String DESCRIPTION = "description";
  public static final String VALUE = "value";
  public static final String ASSET_IDENTIFIER = "assetIdentifier";
  public static final String CURRENT_SERIOUSNESS = "currentSeriousness";
  public static final String RESULTING_SERIOUSNESS = "resultingSeriousness";
  public static final String SAFEGUARD_CATALOGUE_ID = "safeguardCatalogueId";
  public static final String USER_DESCRIPTION = "userDescription";
  public static final String NAME = "name";
  public static final String SAFEGUARD = "Safeguard";
  public static final String CURRENT_VALUE = "currentValue";
  public static final String SECURITY_REQUIREMENT_CATALOGUE_ID = "securityRequirementCatalogueId";
  public static final String SECURITY_REQUIREMENT = "SecurityRequirement";

  private RiskModel rm;
  private AssetModel am;
  private SafeguardModel sm;

  private HashMap<String, RiskScenario> scenarioMap;
  private HashMap<String, Safeguard> safeguardMap;
  private HashMap<String, Safeguard> resultingSafeguardMap;
  private HashMap<String, SecurityRequirement> resultingSecurityRequirementMap;

  private PrimaryAssetCategoryEnum assetCategory;

  public RiskTreatmentModelClientDetailInstanceCreator(RiskModel rm, AssetModel am, SafeguardModel sm,
                                                       String assetPrimaryCategory) {
    this.rm = rm;
    this.am = am;
    this.sm = sm;
    this.assetCategory = PrimaryAssetCategoryEnum.valueOf(assetPrimaryCategory);
  }

  public RiskTreatmentModelClientDetailInstanceCreator() {

  }

  // The json structure to be returned to the client is quite different
  // from the Java RiskTreatmentModel structure
  // we then need to serialize accordingly
  @Override
  public JsonElement serialize(RiskTreatmentModel rtm, Type typeOfSrc, JsonSerializationContext context) {

    JsonArray assetJsonArray = new JsonArray();

    if (rm != null && am != null && sm != null && rtm != null) {
      // Here we put the RiskScenario on an HashMap (faster to be used
      // later)
      scenarioMap = getRiskScenarioHashMap(rm.getScenarios());
      // Here we put the existing Safeguards on an HashMap (faster to be
      // used later)
      safeguardMap = getSafeguardHashMap(sm.getSafeguards());
      // Here we put the resulting Safeguards (from the RiskTreatment) on
      // an HashMap (faster to be used later)
      resultingSafeguardMap = getSafeguardHashMap(rtm.getResultingSafeguards());
      // Here we put the resulting SecurityRequirements (from the
      // RiskTreatment) on an HashMap (faster to be used later)
      resultingSecurityRequirementMap = getSecurityRequirementsHashMap(rtm.getResultingSafeguards());

      LOG.info("RiskTreatmentModelClientDetailInstanceCreator assetCategory " + assetCategory);
      List<Asset> assets = am.getAssets();
      for (Asset asset : assets) {

        // serialize all assets belonging to the primaryAssetCategory
        if (asset.getPrimaryCategories().contains(assetCategory)) {

          LOG.info("RiskTreatmentModelClientDetailInstanceCreator serialize asset " + asset.getName());
          JsonObject assetJson = new JsonObject();
          assetJson = serializeAsset(asset, rtm);
          // adds asset to the array to be returned
          assetJsonArray.add(assetJson);
        }
      }
    }

    return assetJsonArray;
  }

  // The json structure sent by the client is quite different from the
  // Java RiskTreatmentModel structure
  // we then need to deserialize accordingly
  @Override
  public RiskTreatmentModel[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {

    RiskTreatmentModel[] rtms = new RiskTreatmentModel[1];
    RiskTreatmentModel newRtm = new RiskTreatmentModel();
    rtms[0] = newRtm;

    JsonArray fullRiskTreatmentDetailJsonArray = json.getAsJsonArray();

    for (JsonElement item : fullRiskTreatmentDetailJsonArray) {
      //This is a single Asset
      JsonObject assetJsonObject = item.getAsJsonObject();

      if (assetJsonObject.has(CHILDREN)) {

        //Asset children are riskTreatments
        JsonArray assetChildrenJsonArray = assetJsonObject.getAsJsonArray(CHILDREN);

        for (JsonElement riskTreatment : assetChildrenJsonArray) {

          JsonObject riskTreatmentJsonObject = riskTreatment.getAsJsonObject();

          if (riskTreatmentJsonObject.has(DATA)) {

            JsonObject innerRiskTreatmentJsonObject = riskTreatmentJsonObject.getAsJsonObject(DATA);

            if (innerRiskTreatmentJsonObject.has(RISK_SCENARIO_IDENTIFIER) && innerRiskTreatmentJsonObject.has(RISK_TREATMENT_IDENTIFIER)) {
              RiskTreatment rt = new RiskTreatment();

              rt.setIdentifier(innerRiskTreatmentJsonObject.get(RISK_TREATMENT_IDENTIFIER).getAsString());
              rt.setRiskScenarioId(innerRiskTreatmentJsonObject.get(RISK_SCENARIO_IDENTIFIER).getAsString());

              newRtm.getRiskTreatments().add(rt);
            }
          }

          if (riskTreatmentJsonObject.has(CHILDREN)) {

            JsonArray safeguardChildrenJsonArray = riskTreatmentJsonObject.getAsJsonArray(CHILDREN);

            for (JsonElement safeguardItem : safeguardChildrenJsonArray) {
              JsonObject safeguardJsonObject = safeguardItem.getAsJsonObject();

              if (safeguardJsonObject.has(DATA)) {
                JsonObject innerSafeguardJsonObject = safeguardJsonObject.getAsJsonObject(DATA);
                String safeguardIdentifier = innerSafeguardJsonObject.get(SAFEGUARD_IDENTIFIER)
                  .getAsString();

                Safeguard resultingSafeguard = null;
                boolean alreadyAdded = false;
                for (Safeguard safeguard : newRtm.getResultingSafeguards()) {
                  if (safeguard.getIdentifier().equals(safeguardIdentifier)) {
                    resultingSafeguard = safeguard;
                    alreadyAdded = true;
                    break;
                  }
                }

                if (!alreadyAdded) {
                  String targetValue = innerSafeguardJsonObject.get(TARGET_VALUE).getAsString();
                  if (targetValue == null) {
                    targetValue = "";
                  }

                  resultingSafeguard = new Safeguard();
                  resultingSafeguard.setIdentifier(safeguardIdentifier);

                  switch (targetValue) {
                    case "":
                      resultingSafeguard.setScore(SafeguardScoreEnum.NONE);
                      break;
                    case "1":
                      resultingSafeguard.setScore(SafeguardScoreEnum.LOW);
                      break;
                    case "2":
                      resultingSafeguard.setScore(SafeguardScoreEnum.MEDIUM);
                      break;
                    case "3":
                      resultingSafeguard.setScore(SafeguardScoreEnum.HIGH);
                      break;
                    case "4":
                      resultingSafeguard.setScore(SafeguardScoreEnum.VERY_HIGH);
                      break;
                  }

                  newRtm.getResultingSafeguards().add(resultingSafeguard);

                }

                if (safeguardJsonObject.has(CHILDREN)) {
                  JsonArray safeguardInnerChildrenJsonArray = safeguardJsonObject
                    .getAsJsonArray(CHILDREN);

                  deserializeSecurityRequirements(resultingSafeguard,
                    safeguardInnerChildrenJsonArray);

                }

              }

            }

          }
        }

      }
    }

    return rtms;

  }

  private void deserializeSecurityRequirements(Safeguard resultingSafeguard, JsonArray safeguardChildrenJsonArray) {

    for (JsonElement securityRequirementItem : safeguardChildrenJsonArray) {
      JsonObject securityRequirementJsonObject = securityRequirementItem.getAsJsonObject();

      if (securityRequirementJsonObject.has(DATA)) {
        JsonObject innerSecurityRequirementJsonObject = securityRequirementJsonObject.getAsJsonObject(DATA);

        String securityRequirementIdentifier = innerSecurityRequirementJsonObject
          .get(SECURITY_REQUIREMENT_IDENTIFIER).getAsString();

        SecurityRequirement resultingSecurityRequirement = null;
        boolean alreadyAdded = false;
        for (SecurityRequirement requirement : resultingSafeguard.getRelatedSecurityRequirements()) {
          if (requirement.getIdentifier().equals(securityRequirementIdentifier)) {
            resultingSecurityRequirement = requirement;
            alreadyAdded = true;
            break;
          }
        }

        if (!alreadyAdded) {
          String targetValue = innerSecurityRequirementJsonObject.get(TARGET_VALUE).getAsString();
          if (targetValue == null) {
            targetValue = "";
          }

          resultingSecurityRequirement = new SecurityRequirement();
          resultingSecurityRequirement.setIdentifier(securityRequirementIdentifier);

          switch (targetValue) {
            case "":
              resultingSecurityRequirement.setScore(SafeguardScoreEnum.NONE);
              break;
            case "1":
              resultingSecurityRequirement.setScore(SafeguardScoreEnum.LOW);
              break;
          }

          resultingSafeguard.getRelatedSecurityRequirements().add(resultingSecurityRequirement);

        }

        if (securityRequirementJsonObject.has(CHILDREN)) {
          JsonArray securityRequirementChildrenJsonArray = securityRequirementJsonObject
            .getAsJsonArray(CHILDREN);

          deserializeSecurityRequirements(resultingSecurityRequirement, securityRequirementChildrenJsonArray);

        }

      }

    }

  }

  private void deserializeSecurityRequirements(SecurityRequirement securityRequirement,
                                               JsonArray securityRequirementChildrenJsonArray) {
    for (JsonElement securityRequirementItem : securityRequirementChildrenJsonArray) {
      JsonObject securityRequirementJsonObject = securityRequirementItem.getAsJsonObject();

      if (securityRequirementJsonObject.has(DATA)) {
        JsonObject innerSecurityRequirementJsonObject = securityRequirementJsonObject.getAsJsonObject(DATA);

        String securityRequirementIdentifier = innerSecurityRequirementJsonObject
          .get(SECURITY_REQUIREMENT_IDENTIFIER).getAsString();

        SecurityRequirement resultingSecurityRequirement = null;
        boolean alreadyAdded = false;
        for (SecurityRequirement requirement : securityRequirement.getChildren()) {
          if (requirement.getIdentifier().equals(securityRequirementIdentifier)) {
            resultingSecurityRequirement = requirement;
            alreadyAdded = true;
            break;
          }
        }

        if (!alreadyAdded) {
          String targetValue = innerSecurityRequirementJsonObject.get(TARGET_VALUE).getAsString();
          if (targetValue == null) {
            targetValue = "";
          }

          resultingSecurityRequirement = new SecurityRequirement();
          resultingSecurityRequirement.setIdentifier(securityRequirementIdentifier);

          switch (targetValue) {
            case "":
              resultingSecurityRequirement.setScore(SafeguardScoreEnum.NONE);
              break;
            case "1":
              resultingSecurityRequirement.setScore(SafeguardScoreEnum.LOW);
              break;
          }

          securityRequirement.getChildren().add(resultingSecurityRequirement);

        }

        if (securityRequirementJsonObject.has(CHILDREN)) {
          JsonArray securityRequirementInnerChildrenJsonArray = securityRequirementJsonObject
            .getAsJsonArray(CHILDREN);

          deserializeSecurityRequirements(resultingSecurityRequirement,
            securityRequirementInnerChildrenJsonArray);

        }

      }

    }
  }

  //Serialize asset, related scenarios and safeguard, with resulting score
  //from risk treatment.
  private JsonObject serializeAsset(Asset asset, RiskTreatmentModel rtm) {

    JsonObject assetData = new JsonObject();

    // creates the asset data object
    assetData.addProperty(TYPE, "Asset");
    assetData.addProperty(PRIMARY_ASSET_CATEGORY, assetCategory.name());
    assetData.addProperty(SECONDARY_ASSET_CATEGORY, asset.getCategory().name());
    assetData.addProperty(DESCRIPTION, asset.getDescription());
    assetData.addProperty(VALUE, asset.getName());
    assetData.addProperty(ASSET_IDENTIFIER, asset.getIdentifier());

    // creates the array of scenarios
    JsonArray scenarioJsonArray = new JsonArray();

    for (RiskTreatment riskTreatment : rtm.getRiskTreatments()) {
      RiskScenario scenario = scenarioMap.get(riskTreatment.getRiskScenarioId());

      if (scenario == null) {
        LOG.error(
          "serialize: There is a mismatch between RiskModel and RiskTreatmentModel: RiskScenario with identifier: "
            + riskTreatment.getRiskScenarioId() + " not present in RiskModel");
        continue;
      }
      if (!scenario.getAssetId().equals(asset.getIdentifier())) {
        continue;
      }
      LOG.info("serialize analysing RiskScenario with identifier: " + scenario.getIdentifier());

      JsonObject scenarioJsonObject = serializeScenario(scenario, rtm);
      scenarioJsonArray.add(scenarioJsonObject);
    }

    // creates asset object to be returned
    JsonObject assetObject = new JsonObject();
    assetObject.add(DATA, assetData);
    assetObject.add(CHILDREN, scenarioJsonArray);
    return assetObject;
  }

  //Serialize scenario and safeguard, with resulting score from risk
  //treatment.
  private JsonObject serializeScenario(RiskScenario scenario, RiskTreatmentModel rtm) {

    // creates the scenario data object
    JsonObject scenarioDataJsonObject = new JsonObject();
    scenarioDataJsonObject.addProperty("scenarioClass", scenario.getThreatClass().toString());
    scenarioDataJsonObject.addProperty(RISK_SCENARIO_IDENTIFIER, scenario.getIdentifier());
    scenarioDataJsonObject.addProperty(DESCRIPTION, scenario.getUserDescription());
    scenarioDataJsonObject.addProperty(VALUE, scenario.getDescription());
    scenarioDataJsonObject.addProperty(TYPE, "RiskScenario");

    ArrayList<RiskTreatment> riskTreatments = rtm.getRiskTreatments();
    for (RiskTreatment riskTreatment : riskTreatments) {

      if (riskTreatment.getRiskScenarioId().equals(scenario.getIdentifier())) {
        scenarioDataJsonObject.addProperty(RISK_TREATMENT_IDENTIFIER, riskTreatment.getIdentifier());
        scenarioDataJsonObject.addProperty(CURRENT_SERIOUSNESS,
          String.valueOf(getValueFromImpact(riskTreatment.getCurrentSeriousness())));
        scenarioDataJsonObject.addProperty(RESULTING_SERIOUSNESS,
          String.valueOf(getValueFromImpact(riskTreatment.getResultingSeriousness())));
      }
    }

    // creates the array of safeguard
    JsonArray safeguardJsonArray = new JsonArray();
    ArrayList<String> safeguardIds = scenario.getSafeguardIds();
    for (String safeguardId : safeguardIds) {

      // serialize each safeguard related to the given scenario
      Safeguard safeguard = safeguardMap.get(safeguardId);
      LOG.info("RiskTreatmentModelClientDetailInstanceCreator serialize safeguard " + safeguard.getCatalogueId());

      JsonObject safeguardJsonObject = serializeSafeguardJsonObject(safeguard);
      safeguardJsonArray.add(safeguardJsonObject);
    }

    // creates cenario object to be returned
    JsonObject scenarioJsonObject = new JsonObject();
    scenarioJsonObject.add(DATA, scenarioDataJsonObject);
    scenarioJsonObject.add(CHILDREN, safeguardJsonArray);
    return scenarioJsonObject;
  }

  //Serialize safeguard with resulting score from risk treatment. (same logic
  //as FullInstanceCreator)
  private JsonObject serializeSafeguardJsonObject(Safeguard safeguard) {
    LOG.info("createSafeguardJsonObject for safeguard with id: " + safeguard.getIdentifier());
    // This jsonobjects represents a Safeguard
    JsonObject safeguardJsonObject = new JsonObject();
    JsonObject innerSafeguardData = new JsonObject();

    innerSafeguardData.addProperty(SAFEGUARD_CATALOGUE_ID, safeguard.getCatalogueId());
    innerSafeguardData.addProperty(SAFEGUARD_IDENTIFIER, safeguard.getIdentifier());
    innerSafeguardData.addProperty(USER_DESCRIPTION, safeguard.getUserDescription());
    innerSafeguardData.addProperty(NAME, safeguard.getName());
    innerSafeguardData.addProperty(VALUE, "(" + safeguard.getCatalogueId() + ") " + safeguard.getName());
    innerSafeguardData.addProperty(DESCRIPTION, safeguard.getDescription());
    innerSafeguardData.addProperty(TYPE, SAFEGUARD);
    innerSafeguardData.addProperty(CURRENT_VALUE, getValueFromScoreSafeguard(safeguard.getScore()));

    if (resultingSafeguardMap.containsKey(safeguard.getIdentifier())) {

      Safeguard resultingSafeguard = resultingSafeguardMap.get(safeguard.getIdentifier());
      innerSafeguardData.addProperty(TARGET_VALUE, getValueFromScoreSafeguard(resultingSafeguard.getScore()));

    } else {
      // This should not happen (all safeguard should be in the
      // "resulting" SafeguardModel!)
      LOG.error("createSafeguardJsonObject unable to find safeguard with id " + safeguard.getIdentifier()
        + " in resultingSafeguardMap");
      innerSafeguardData.addProperty(TARGET_VALUE, getValueFromScoreSafeguard(safeguard.getScore()));
    }

    safeguardJsonObject.add(DATA, innerSafeguardData);

    // Now we have to add all children to the Json (GASF requirements)
    JsonArray securityRequirementChildrenJsonArray = new JsonArray();

    // For each SecurityRequirement related to this Safeguard, we have to
    // create the related JsonObjects
    for (SecurityRequirement securityRequirement : safeguard.getRelatedSecurityRequirements()) {

      createSecurityRequirementJsonObject(securityRequirement, securityRequirementChildrenJsonArray);
    }

    safeguardJsonObject.add(CHILDREN, securityRequirementChildrenJsonArray);

    return safeguardJsonObject;
  }

  private void createSecurityRequirementJsonObject(SecurityRequirement securityRequirement,
                                                   JsonArray securityRequirementChildrenJsonArray) {
    LOG.info("createSecurityRequirementJsonObject for securityRequirement with id: "
      + securityRequirement.getIdentifier());
    // This jsonobjects represents a SecurityRequirement
    JsonObject securityRequirementJsonObject = new JsonObject();
    JsonObject securityRequirementInnerSafeguardData = new JsonObject();

    securityRequirementInnerSafeguardData.addProperty(SECURITY_REQUIREMENT_CATALOGUE_ID,
      securityRequirement.getId());
    securityRequirementInnerSafeguardData.addProperty(SECURITY_REQUIREMENT_IDENTIFIER,
      securityRequirement.getIdentifier());
    securityRequirementInnerSafeguardData.addProperty(NAME, securityRequirement.getTitle());
    securityRequirementInnerSafeguardData.addProperty(USER_DESCRIPTION, securityRequirement.getUserDescription());
    securityRequirementInnerSafeguardData.addProperty(VALUE, "(" + securityRequirement.getId() + ") " + securityRequirement.getTitle());
    securityRequirementInnerSafeguardData.addProperty(DESCRIPTION, securityRequirement.getDescription());
    securityRequirementInnerSafeguardData.addProperty(TYPE, SECURITY_REQUIREMENT);
    securityRequirementInnerSafeguardData.addProperty(CURRENT_VALUE,
      String.valueOf(getValueFromScoreSecurityRequirement(securityRequirement.getScore())));

    SecurityRequirement resultingSecurityRequirement = resultingSecurityRequirementMap
      .get(securityRequirement.getIdentifier());

    if (resultingSecurityRequirement == null) {
      // This should not happen (all safeguard should be in the
      // resultingSecurityRequirement!)
      LOG.error("createSecurityRequirementJsonObject unable to find securityRequirement with id {} in resulting Safeguard",
        securityRequirement.getIdentifier());
      securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE,
        getValueFromScoreSecurityRequirement(securityRequirement.getScore()));
    } else {

      securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE,
        getValueFromScoreSecurityRequirement(resultingSecurityRequirement.getScore()));
    }

    securityRequirementJsonObject.add(DATA, securityRequirementInnerSafeguardData);

    // Now we have to add all children to the Json (GASF requirements)
    JsonArray securityRequirementInnerChildrenJsonArray = new JsonArray();

    // For each SecurityRequirement related to this Safeguard, we have to
    // create the related JsonObjects
    for (Requirement innerSecurityRequirement : securityRequirement.getChildren()) {

      createSecurityRequirementJsonObject((SecurityRequirement) innerSecurityRequirement,
        securityRequirementInnerChildrenJsonArray);
    }

    securityRequirementJsonObject.add(CHILDREN, securityRequirementInnerChildrenJsonArray);

    securityRequirementChildrenJsonArray.add(securityRequirementJsonObject);

  }

  private String getValueFromScoreSecurityRequirement(SafeguardScoreEnum score) {
    if (score != null) {
      switch (score) {
        case NONE:
          return "";
        case LOW:
          return "1";
        default:
          return "";
      }
    } else {
      LOG.info("Safeguard secreq score is null.");
      return "";
    }
  }

  private String getValueFromScoreSafeguard(SafeguardScoreEnum score) {
    if (score != null) {
      switch (score) {
        case NONE:
          return "";
        case LOW:
          return "1";
        case MEDIUM:
          return "2";
        case HIGH:
          return "3";
        case VERY_HIGH:
          return "4";
        default:
          return "";
      }
    } else {
      LOG.info("Safeguard secreq score is null.");
      return "";
    }
  }

  private int getValueFromImpact(ImpactEnum score) {
    if (score != null) {
      switch (score) {
        case LOW:
          return 1;
        case MEDIUM:
          return 2;
        case HIGH:
          return 3;
        case CRITICAL:
          return 4;
        default:
          return 1;
      }
    } else {
      LOG.info("Safeguard impact score is null.");
      return 0;
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

    return resultingSecurityRequirementMap;
  }

  private void getSecurityRequirementsHashMap(HashMap<String, SecurityRequirement> resultingSecurityRequirementMap2,
                                              SecurityRequirement securityRequirement) {

    for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {

      if (innerSecurityRequirement instanceof SecurityRequirement) {
        if (!resultingSecurityRequirementMap2.containsKey(innerSecurityRequirement.getIdentifier())) {

          resultingSecurityRequirementMap2.put(innerSecurityRequirement.getIdentifier(), innerSecurityRequirement);

        }

        getSecurityRequirementsHashMap(resultingSecurityRequirementMap2, innerSecurityRequirement);
      } else {
        LOG.error("innerSecurityRequirement not instance of SecurityRequirement {}", innerSecurityRequirement.getId());
      }
    }

  }

  private HashMap<String, RiskScenario> getRiskScenarioHashMap(ArrayList<RiskScenario> riskScenarios) {
    // Here we put the RiskScenario on an HashMap (faster to be used later)
    HashMap<String, RiskScenario> scenarioMap = new HashMap<String, RiskScenario>();

    for (RiskScenario riskScenario : riskScenarios) {
      String scenarioId = riskScenario.getIdentifier();

      scenarioMap.put(scenarioId, riskScenario);
    }
    LOG.info("scenarioMap size {}", scenarioMap.size());

    return scenarioMap;
  }

  private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
    // Here we put the Safeguard on an HashMap (faster to be used later)
    HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

    for (Safeguard safeguard : safeguards) {
      String safeguardId = safeguard.getIdentifier();

      safeguardMap.put(safeguardId, safeguard);
    }

    return safeguardMap;
  }

  public PrimaryAssetCategoryEnum getAssetCategory() {
    return assetCategory;
  }

  public void setAssetCategory(PrimaryAssetCategoryEnum assetCategory) {
    this.assetCategory = assetCategory;
  }
}
