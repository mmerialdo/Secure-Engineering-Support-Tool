/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelClientFullInstanceCreator.java"
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
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.RiskTreatment;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScopeEnum;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//The json structure to be returned to the client is quite different
// from the Java RiskTreatmentModel structure
// we then need to serialize/deserialize accordingly
public class RiskTreatmentModelClientFullInstanceCreator
		implements JsonDeserializer<RiskTreatmentModel>, JsonSerializer<RiskTreatmentModel> {

	private static final Logger LOG = LoggerFactory
			.getLogger(RiskTreatmentModelClientFullInstanceCreator.class.getName());
	public static final String TYPE = "type";
	public static final String PRIMARY_ASSET_CATEGORY = "primaryAssetCategory";
	public static final String DESCRIPTION = "description";
	public static final String VALUE = "value";
	public static final String SECURITY_IMPACT = "securityImpact";
	public static final String TOT = "tot";
	public static final String SAFEGUARD_SCOPE = "safeguardScope";
	public static final String COVERED_SCENARIO = "coveredScenario";
	public static final String DATA = "data";
	public static final String SAFEGUARD_CATALOGUE_ID = "safeguardCatalogueId";
	public static final String SAFEGUARD_IDENTIFIER = "safeguardIdentifier";
	public static final String USER_DESCRIPTION = "userDescription";
	public static final String NAME = "name";
	public static final String CURRENT_VALUE = "currentValue";
	public static final String TARGET_VALUE = "targetValue";
	public static final String CHILDREN = "children";
	public static final String SECURITY_REQUIREMENT_CATALOGUE_ID = "securityRequirementCatalogueId";
	public static final String SECURITY_REQUIREMENT_IDENTIFIER = "securityRequirementIdentifier";

	private RiskModel rm;
	private AssetModel am;
	private SafeguardModel sm;
	private HashMap<String, Asset> assetMap;
	private HashMap<String, RiskScenario> scenarioMap;
	private HashMap<String, Safeguard> safeguardMap;

	// This RiskTreatmentModel is the RTM persisted (which needs to be updated
	// due to the edit from the SEST client)
	private HashMap<String, Safeguard> resultingSafeguardMap;
	private HashMap<String, SecurityRequirement> resultingSecurityRequirementMap;

	public RiskTreatmentModelClientFullInstanceCreator(RiskModel rm, AssetModel am, SafeguardModel sm) {
		this.rm = rm;
		this.am = am;
		this.sm = sm;
	}

	public RiskTreatmentModelClientFullInstanceCreator() {

	}

	// The json structure to be returned to the client is quite different
	// from the Java RiskTreatmentModel structure
    // we then need to serialize accordingly

	@Override
	public JsonElement serialize(RiskTreatmentModel rtm, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject fullRiskTreatmentJson = new JsonObject();
		JsonArray fullRiskTreatmentJsonArray = new JsonArray();

		if (rm != null && am != null && sm != null && rtm != null) {

			// Here we put the Asset on an HashMap (faster to be used later)
			assetMap = getAssetHashMap(am.getAssets());
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

			// Here we divide the scenario within the RiskTreatmentModel in
			// groups using PrimaryAssetCategoryEnum and SecurityImpactScopeEnum
			HashMap<PrimaryAssetCategoryEnum, HashMap<SecurityImpactScopeEnum, ArrayList<RiskTreatment>>> primaryGroupingHashMap = new HashMap<>();

			for (RiskTreatment riskTreatment : rtm.getRiskTreatments()) {
				RiskScenario scenario = scenarioMap.get(riskTreatment.getRiskScenarioId());

				if (scenario == null) {
					LOG.error(
							"serialize: There is a mismatch between RiskModel and RiskTreatmentModel: RiskScenario with identifier: "
									+ riskTreatment.getRiskScenarioId() + " not present in RiskModel");
					continue;
				}
				LOG.info("serialize analysing RiskScenario with identifier: " + scenario.getIdentifier());

				// If the RiskScenario is not complete we can't include it in
				// the risk treatment for the client (but this should not happen
				// since in
				// RiskScenarioManagerInput we harmonize the models)
				if (scenario.getAssetId() == null) {
					continue;
				}
				if (scenario.getVulnerabilityId() == null) {
					continue;
				}
				if (scenario.getThreatId() == null) {
					continue;
				}

				if (scenario.getAssetId().equals("")) {
					continue;
				}
				if (scenario.getVulnerabilityId().equals("")) {
					continue;
				}
				if (scenario.getThreatId().equals("")) {
					continue;
				}

				Asset asset = assetMap.get(scenario.getAssetId());

				if (asset == null) {
					LOG.error("serialize: There is a mismatch between AssetModel and RiskModel: Asset with identifier: "
							+ scenario.getAssetId() + " not present in AssetModel");
					continue;
				}

				if (asset.getPrimaryCategories() == null || asset.getPrimaryCategories().size() <= 0) {

					LOG.error("serialize: Asset with identifier: " + asset.getIdentifier()
							+ " not primary categories found");
					continue;
				}
				PrimaryAssetCategoryEnum category = asset.getPrimaryCategories().get(0);
				SecurityImpactScopeEnum scope = scenario.getImpactScope();

				if (primaryGroupingHashMap.get(category) != null) {
					HashMap<SecurityImpactScopeEnum, ArrayList<RiskTreatment>> securityImpactHashMap = primaryGroupingHashMap
							.get(category);

					if (securityImpactHashMap.get(scope) != null) {

						securityImpactHashMap.get(scope).add(riskTreatment);
					} else {
						ArrayList<RiskTreatment> riskTreatments = new ArrayList<>();
						riskTreatments.add(riskTreatment);

						securityImpactHashMap.put(scope, riskTreatments);
					}
				} else {
					HashMap<SecurityImpactScopeEnum, ArrayList<RiskTreatment>> securityImpactHashMap = new HashMap<>();
					ArrayList<RiskTreatment> riskTreatments = new ArrayList<>();
					riskTreatments.add(riskTreatment);

					securityImpactHashMap.put(scope, riskTreatments);

					primaryGroupingHashMap.put(category, securityImpactHashMap);

				}

			}

			// After the preliminary division it is then possible to create the
			// Json data
			// each grouping must be composed by a PrimaryCategory and a
			// SecurityImpact
			Set<PrimaryAssetCategoryEnum> primaryKeys = primaryGroupingHashMap.keySet();

			for (PrimaryAssetCategoryEnum category : primaryKeys) {
				HashMap<SecurityImpactScopeEnum, ArrayList<RiskTreatment>> securityImpactHashMap = primaryGroupingHashMap
						.get(category);

				Set<SecurityImpactScopeEnum> secondaryKeys = securityImpactHashMap.keySet();

				for (SecurityImpactScopeEnum scope : secondaryKeys) {

					// This is the main jsonobject to be put within the
					// fullRiskTreatmentJsonData JsonArray
					JsonObject categoryScopeJsonObject = new JsonObject();

					ArrayList<RiskTreatment> riskTreatments = securityImpactHashMap.get(scope);

					// This is the jsonobject to be put inside the previous
					// jsonobject
					JsonObject categoryData = new JsonObject();

					categoryData.addProperty(TYPE, "PrimaryAssetCategory");
					categoryData.addProperty(PRIMARY_ASSET_CATEGORY, category.toString());
					categoryData.addProperty(DESCRIPTION, "Risk Scenarios affecting assets with Primary Category '"
							+ category.getValue() + "' and Security Impact Scope " + scope.toString());
					categoryData.addProperty(VALUE, category.getValue());
					categoryData.addProperty(SECURITY_IMPACT, scope.toString());
					categoryData.addProperty(TOT, riskTreatments.size());

					// We need to count, for each possible Seriousness value,
					// all related RiskScenario
					int s1 = 0;
					int s2 = 0;
					int s3 = 0;
					int s4 = 0;

					for (RiskTreatment riskTreatment : riskTreatments) {
						// We compute how many plans are to be treated for each level of Seriousness
						if (riskTreatment.getResultingSeriousness() == null) {
							LOG.error("CurrentSeriousness is null for RiskTreatment with identifier: {}", riskTreatment.getIdentifier());
							continue;
						}
						else{
							switch (riskTreatment.getResultingSeriousness()) {
							case LOW:
								s1++;
								break;
							case MEDIUM:
								s2++;
								break;
							case HIGH:
								s3++;
								break;
							case CRITICAL:
								s4++;
								break;
							}
						}
					}
					categoryData.addProperty("s1", String.valueOf(s1));
					categoryData.addProperty("s2", String.valueOf(s2));
					categoryData.addProperty("s3", String.valueOf(s3));
					categoryData.addProperty("s4", String.valueOf(s4));
					
					int tot = s1 + s2 + s3 + s4;
					categoryData.addProperty(TOT, String.valueOf(tot));
					

					categoryScopeJsonObject.add(DATA, categoryData);

					// we organize the Safeguards for these RiskScenario
					// grouping them with their scope
					HashMap<SafeguardScopeEnum, ArrayList<Safeguard>> safeguards = new HashMap<>();

					for (RiskTreatment riskTreatment : riskTreatments) {
						RiskScenario scenario = scenarioMap.get(riskTreatment.getRiskScenarioId());

						for (String safeguardId : scenario.getSafeguardIds()) {
							Safeguard safeguard = safeguardMap.get(safeguardId);

							if (safeguard == null) {
								LOG.error("Safeguard in RiskScenario not existing in Safeguard model: {}", safeguardId);
								continue;
							}

							if (safeguard.getScope() == null) {

								LOG.error("Safeguard in RiskScenario has null scope : {}", safeguardId);
								continue;
							}

							if (safeguards.containsKey(safeguard.getScope())) {
								ArrayList<Safeguard> safeguardInternalList = safeguards.get(safeguard.getScope());
								safeguardInternalList.add(safeguard);
								safeguards.put(safeguard.getScope(), safeguardInternalList);

							} else {
								ArrayList<Safeguard> safeguardInternalList = new ArrayList<>();
								safeguardInternalList.add(safeguard);
								safeguards.put(safeguard.getScope(), safeguardInternalList);
							}

						}
					}

					// Now we have to add all children to the Json (grouping
					// safeguards within a scope)
					JsonArray categoryChildrenJsonArray = new JsonArray();

					Set<SafeguardScopeEnum> safeguardKeys = safeguards.keySet();

					for (SafeguardScopeEnum safeguardScope : safeguardKeys) {

						// This jsonobjects collects all safeguards within a
						// specific SafeguardScope
						JsonObject safeguardScopeJsonObject = new JsonObject();
						JsonObject innerSafeguardScopeData = new JsonObject();

						innerSafeguardScopeData.addProperty(SAFEGUARD_SCOPE, safeguardScope.toString());
						innerSafeguardScopeData.addProperty(DESCRIPTION, "");
						innerSafeguardScopeData.addProperty(VALUE, safeguardScope.toString());
						innerSafeguardScopeData.addProperty(TYPE, "SafeguardScope");

						int coveredScenario = 0;

						for (RiskTreatment riskTreatment : riskTreatments) {
							RiskScenario scenario = scenarioMap.get(riskTreatment.getRiskScenarioId());

							// We check all safeguards of all scenarios in order
							// to count how many scenario could be mitigated by
							// using one or more safeguards
							for (String safeguardId : scenario.getSafeguardIds()) {
								Safeguard safeguard = safeguardMap.get(safeguardId);

								if (safeguard == null) {
									LOG.error("Safeguard in RiskScenario not existing in Safeguard model: " + safeguardId);
									continue;
								}
								
								if (safeguard.getScope() != null && safeguardScope != null
										&& safeguard.getScope().equals(safeguardScope)) {
									coveredScenario++;
									break;
								}
							}

						}
						innerSafeguardScopeData.addProperty(COVERED_SCENARIO, coveredScenario);

						safeguardScopeJsonObject.add(DATA, innerSafeguardScopeData);

						// Now we have to add all children to the Json
						// (safeguards)
						JsonArray safeguardChildrenJsonArray = new JsonArray();

						// we create an hashmap of safeguards involved in these
						// scenarios (they don't have to be repeated, so we use
						// an HashMap)
						ArrayList<String> safeguardChildrenIds = new ArrayList<>();

						for (RiskTreatment riskTreatment : riskTreatments) {
							RiskScenario scenario = scenarioMap.get(riskTreatment.getRiskScenarioId());

							// We check all safeguards of all scenarios
							for (String safeguardId : scenario.getSafeguardIds()) {
								Safeguard safeguard = safeguardMap.get(safeguardId);

								if (safeguard == null) {
									LOG.error("Safeguard in RiskScenario not existing in Safeguard model: {}", safeguardId);
									continue;
								}
								if (safeguard.getScope().equals(safeguardScope)) {

									if (!safeguardChildrenIds.contains(safeguard.getIdentifier())) {
										safeguardChildrenIds.add(safeguardId);
									}
								}
							}

						}

						// For each Safeguard involved in this group of
						// Scenarios, we have to create the related JsonObjects
						for (String safeguardKey : safeguardChildrenIds) {
							Safeguard safeguard = safeguardMap.get(safeguardKey);

							if (safeguard == null) {
								LOG.error("Safeguard in RiskScenario not existing in Safeguard model: {}", safeguardKey);
								continue;
							}
							createSafeguardJsonObject(safeguard, safeguardChildrenJsonArray);
						}

						safeguardScopeJsonObject.add(CHILDREN, safeguardChildrenJsonArray);

						categoryChildrenJsonArray.add(safeguardScopeJsonObject);

					}

					categoryScopeJsonObject.add(CHILDREN, categoryChildrenJsonArray);

					fullRiskTreatmentJsonArray.add(categoryScopeJsonObject);
				}
			}

		} else {
			LOG.error("serialize: AssetModel/RiskModel/SafeguardModel/RiskTreatmentModel null. Unable to serialize");
		}

		fullRiskTreatmentJson.add(DATA, fullRiskTreatmentJsonArray);
		return fullRiskTreatmentJson;

	}

	private void createSafeguardJsonObject(Safeguard safeguard, JsonArray safeguardChildren) {
		LOG.info("createSafeguardJsonObject for safeguard with id: {}", safeguard.getIdentifier());
		// This jsonobjects represents a Safeguard
		JsonObject safeguardJsonObject = new JsonObject();
		JsonObject innerSafeguardData = new JsonObject();

		innerSafeguardData.addProperty(SAFEGUARD_CATALOGUE_ID, safeguard.getCatalogueId());
		innerSafeguardData.addProperty(SAFEGUARD_IDENTIFIER, safeguard.getIdentifier());
		innerSafeguardData.addProperty(USER_DESCRIPTION, safeguard.getUserDescription());
		innerSafeguardData.addProperty(NAME, safeguard.getName());
		innerSafeguardData.addProperty(VALUE, "(" + safeguard.getCatalogueId() + ") " + safeguard.getName());
		innerSafeguardData.addProperty(DESCRIPTION, safeguard.getDescription());
		innerSafeguardData.addProperty(TYPE, "Safeguard");

		switch (safeguard.getScore()) {
		case NONE:
			innerSafeguardData.addProperty(CURRENT_VALUE, "");
			break;
		case LOW:
			innerSafeguardData.addProperty(CURRENT_VALUE, "1");
			break;
		case MEDIUM:
			innerSafeguardData.addProperty(CURRENT_VALUE, "2");
			break;
		case HIGH:
			innerSafeguardData.addProperty(CURRENT_VALUE, "3");
			break;
		case VERY_HIGH:
			innerSafeguardData.addProperty(CURRENT_VALUE, "4");
			break;
		default:
			innerSafeguardData.addProperty(CURRENT_VALUE, "");
			break;
		}

		if (resultingSafeguardMap.containsKey(safeguard.getIdentifier())) {
			Safeguard resultingSafeguard = resultingSafeguardMap.get(safeguard.getIdentifier());

			switch (resultingSafeguard.getScore()) {
			case NONE:
				innerSafeguardData.addProperty(TARGET_VALUE, "");
				break;
			case LOW:
				innerSafeguardData.addProperty(TARGET_VALUE, "1");
				break;
			case MEDIUM:
				innerSafeguardData.addProperty(TARGET_VALUE, "2");
				break;
			case HIGH:
				innerSafeguardData.addProperty(TARGET_VALUE, "3");
				break;
			case VERY_HIGH:
				innerSafeguardData.addProperty(TARGET_VALUE, "4");
				break;
			default:
				innerSafeguardData.addProperty(TARGET_VALUE, "");
				break;
			}
		} else {
			// This should not happen (all safeguard should be in the
			// "resulting" SafeguardModel!)
			LOG.error("createSafeguardJsonObject unable to find safeguard with id " + safeguard.getIdentifier()
					+ " in resultingSafeguardMap");
			innerSafeguardData.addProperty(TARGET_VALUE, innerSafeguardData.get(CURRENT_VALUE).getAsNumber());
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

		safeguardChildren.add(safeguardJsonObject);
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
		securityRequirementInnerSafeguardData.addProperty(VALUE,  "(" + securityRequirement.getId() + ") " + securityRequirement.getTitle());
		securityRequirementInnerSafeguardData.addProperty(DESCRIPTION, securityRequirement.getDescription());
		securityRequirementInnerSafeguardData.addProperty(TYPE, "SecurityRequirement");

		if(securityRequirement.getScore() != null) {
			switch (securityRequirement.getScore()) {
			case NONE:
				securityRequirementInnerSafeguardData.addProperty(CURRENT_VALUE, "");
				break;
			case LOW:
				securityRequirementInnerSafeguardData.addProperty(CURRENT_VALUE, "1");
				break;
			default:
				securityRequirementInnerSafeguardData.addProperty(CURRENT_VALUE, "");
				break;
			} 
		}else {
			securityRequirementInnerSafeguardData.addProperty(CURRENT_VALUE, "");
		}

		SecurityRequirement resultingSecurityRequirement = resultingSecurityRequirementMap
				.get(securityRequirement.getIdentifier());

		if (resultingSecurityRequirement == null) {
			// This should not happen (all safeguard should be in the
			// resultingSecurityRequirement!)
			LOG.error("createSecurityRequirementJsonObject unable to find securityRequirement with id "
					+ securityRequirement.getIdentifier() + " in resulting Safeguard");
			securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE,
					securityRequirementInnerSafeguardData.get(CURRENT_VALUE).getAsNumber());
		}
		else if(resultingSecurityRequirement.getScore() != null){
			switch (resultingSecurityRequirement.getScore()) {
			case NONE:
				securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE, "");
				break;
			case LOW:
				securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE, "1");
				break;
			default:
				securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE, "");
				break;
			}
		} else {
			securityRequirementInnerSafeguardData.addProperty(TARGET_VALUE, "");
		}

		securityRequirementJsonObject.add(DATA, securityRequirementInnerSafeguardData);

		// Now we have to add all children to the Json (GASF requirements)
		JsonArray securityRequirementInnerChildrenJsonArray = new JsonArray();

		// For each SecurityRequirement related to this Safeguard, we have to
		// create the related JsonObjects
		for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {

			createSecurityRequirementJsonObject(innerSecurityRequirement,
					securityRequirementInnerChildrenJsonArray);
		}

		securityRequirementJsonObject.add(CHILDREN, securityRequirementInnerChildrenJsonArray);

		securityRequirementChildrenJsonArray.add(securityRequirementJsonObject);

	}

	@Override
	public RiskTreatmentModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject jsonObject = json.getAsJsonObject();
		// The json structure sent by the client is quite different from the
		// Java RiskTreatmentModel structure
		// we then need to deserialize accordingly

		if (!jsonObject.has(DATA)) {
			LOG.error("deserialize RiskTreatmentModel json from client has no data field: {}", json.getAsString());
			return null;
		}

		RiskTreatmentModel newRtm = new RiskTreatmentModel();

		JsonArray fullRiskTreatmentJsonArray = jsonObject.get(DATA).getAsJsonArray();

		for (JsonElement item : fullRiskTreatmentJsonArray) {
			JsonObject categoryScopeJsonObject = item.getAsJsonObject();

			if (categoryScopeJsonObject.has(CHILDREN)) {

				JsonArray categoryChildrenJsonArray = categoryScopeJsonObject.getAsJsonArray(CHILDREN);

				for (JsonElement innerItem : categoryChildrenJsonArray) {

					JsonObject safeguardScopeJsonObject = innerItem.getAsJsonObject();

					if (safeguardScopeJsonObject.has(CHILDREN)) {

						JsonArray safeguardChildrenJsonArray = safeguardScopeJsonObject.getAsJsonArray(CHILDREN);

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

		return newRtm;

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

		if(securityRequirement.getChildren() != null) {
			for (SecurityRequirement innerSecurityRequirement : securityRequirement.getChildren()) {
	
				if(innerSecurityRequirement instanceof SecurityRequirement) {
					
					if (!resultingSecurityRequirementMap.containsKey(innerSecurityRequirement.getIdentifier())) {
						resultingSecurityRequirementMap.put(innerSecurityRequirement.getIdentifier(),
								(SecurityRequirement) innerSecurityRequirement);
					}
		
					getSecurityRequirementsHashMap(resultingSecurityRequirementMap,
							(SecurityRequirement) innerSecurityRequirement);
				} else {
					LOG.error("innerSecurityRequirement not instance of SecurityRequirement "+innerSecurityRequirement.getId());
				}
			}
			LOG.info("resultingSecurityRequirementMap size " + resultingSecurityRequirementMap.size());
		}
	}

	private HashMap<String, Asset> getAssetHashMap(List<Asset> assets) {
		// Here we put the Asset on an HashMap (faster to be used later)
		HashMap<String, Asset> assetMap = new HashMap<String, Asset>();

		for (Asset asset : assets) {
			String assetId = asset.getIdentifier();

			assetMap.put(assetId, asset);
		}
		LOG.info("assetMap size " + assetMap.size());
		return assetMap;
	}

	private HashMap<String, RiskScenario> getRiskScenarioHashMap(ArrayList<RiskScenario> riskScenarios) {
		// Here we put the RiskScenario on an HashMap (faster to be used later)
		HashMap<String, RiskScenario> scenarioMap = new HashMap<String, RiskScenario>();

		for (RiskScenario riskScenario : riskScenarios) {
			String scenarioId = riskScenario.getIdentifier();

			scenarioMap.put(scenarioId, riskScenario);
		}
		LOG.info("scenarioMap size " + scenarioMap.size());

		return scenarioMap;
	}

	private HashMap<String, Safeguard> getSafeguardHashMap(ArrayList<Safeguard> safeguards) {
		// Here we put the Safeguard on an HashMap (faster to be used later)
		HashMap<String, Safeguard> safeguardMap = new HashMap<String, Safeguard>();

		for (Safeguard safeguard : safeguards) {
			String safeguardId = safeguard.getIdentifier();

			if (safeguard.getScope() != null) {

				safeguardMap.put(safeguardId, safeguard);
			}
		}
		LOG.info("safeguardMap size " + safeguardMap.size());
		return safeguardMap;
	}

}
