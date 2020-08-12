/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityMeasuresInterpreter.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.riskmodel.utility;

import org.crmf.model.riskassessmentelements.Asset;
import org.crmf.model.riskassessmentelements.RiskScenario;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.RiskScenarioReference.SecurityMeasureEnum;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardEffectiveness;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.Vulnerability;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//Calculate of risk scenario security measures and updateQuestionnaireJSON risk scenario associated safeguards field
public class SecurityMeasuresInterpreter {
  private static final Logger LOG = LoggerFactory.getLogger(SecurityMeasuresInterpreter.class.getName());
  private RiskServiceInterface riskModelService;

  /*
   Calculate of risk scenario security measures and updateQuestionnaireJSON risk scenario associated safeguards field
   */
  public void manageRiskScenarioMeasures(RiskScenario rs, ArrayList<Safeguard> safeguards, Asset asset, Threat threat, Vulnerability vulnerability, ArrayList<RiskScenarioReference> allRsr) {
    // FIRST STEP: calculation of risk scenario security measures

    //find the Reference Scenario matching the input one
    RiskScenarioReference rsr = findMatchReferenceScenario(rs, asset, threat, vulnerability, allRsr);

    if (null == rsr) {
      LOG.info("manageRiskScenarioMeasures: no Reference Scenario found for risk scenario: " + rs.getIdentifier());
      rs.setSafeguardIds(new ArrayList<>());
      return;
    }
    LOG.info("manageRiskScenarioMeasures: RiskScenario with identifier " + rs.getIdentifier() + " has been matched");

    //create a map with all the risk scenario reference security measures and formulas associated
    Map<SecurityMeasureEnum, String> securityMeasuresMap = rsr.createSecurityMeasureMap();

    //for each security measure, calculate the effectiveness value and updateQuestionnaireJSON it into the risk scenario
    for (SecurityMeasureEnum sme : securityMeasuresMap.keySet()) {
      if (securityMeasuresMap.get(sme) == null || securityMeasuresMap.get(sme).equals("")) {
        continue;
      }
      int measureValue = calculateSecurityMeasure(securityMeasuresMap.get(sme), safeguards);
      SafeguardEffectiveness effectiveness = SafeguardEffectiveness.valueOf(measureValue);

      switch (sme) {
        case DISSUASION:
          rs.setDissuasion(effectiveness);
          break;
        case CONFINING:
          rs.setConfining(effectiveness);
          break;
        case PREVENTION:
          rs.setPrevention(effectiveness);
          break;
        case PALLIATION:
          rs.setPalliation(effectiveness);
          break;
        default:
          break;
      }

    }

    // SECOND STEP: updateQuestionnaireJSON of risk scenario associated safeguards field

    // extract safeguards catalogue_id from all formulas associated to risk scenario
    // and retrieve the associated Safeguards from safeguard model
    ArrayList<Safeguard> associatedSafeguards = (ArrayList<Safeguard>) findSafeguardsAssociatedToScenario(securityMeasuresMap, safeguards);

    // updateQuestionnaireJSON Risk Scenario safeguard List
    ArrayList<String> associatedSafeguardIdentifiers = new ArrayList<>();
    for (Safeguard sg : associatedSafeguards) {
      LOG.info("manageRiskScenarioMeasures: associatedSafeguards " + sg.getIdentifier());
      associatedSafeguardIdentifiers.add(sg.getIdentifier());
    }

    rs.setSafeguardIds(associatedSafeguardIdentifiers);
  }


  /*
   find the reference scenario that matches the input scenario and models
   */
  private RiskScenarioReference findMatchReferenceScenario(RiskScenario rs, Asset asset, Threat threat,
                                                           Vulnerability vulnerability, ArrayList<RiskScenarioReference> allRsr) {

    LOG.info("findMatchReferenceScenario - RiskScenario with identifier: " + rs.getIdentifier());
    for (RiskScenarioReference rsr : allRsr) {
      if (rsr.compareRiskScenarioReference(rs, asset, threat, vulnerability))
        return rsr;
    }

    return null;
  }


  /*
   Calculate security measure formula
   */
  private int calculateSecurityMeasure(String smFormula, ArrayList<Safeguard> sm) {
    LOG.info("calculateSecurityMeasure - begin");

    LOG.info("calculateSecurityMeasure smFormula before: " + smFormula);
    //Map<String, Integer> safeguardsScores = new HashMap<String, Integer>();

    //create a map of all safeguards into the input safeguard Model to retrieve directly the associated score
    for (Safeguard safeguard : sm) {
      //safeguardsScores.put(safeguard.getCatalogueId(), safeguard.getScore().getScore());
      smFormula = smFormula.replace(safeguard.getCatalogueId(), Integer.toString(safeguard.getScore().getScore()));
    }
    LOG.info("calculateSecurityMeasure smFormula after: " + smFormula);

    // if the formula is "simple" (only a safeguard involved), return the score value associated
    try {
      if (!smFormula.contains("(") && !smFormula.contains(")")) {
        return Integer.parseInt(smFormula);
      }
    } catch (Exception e) {
      LOG.error("calculateSecurityMeasure - exception: " + e.getMessage(), e);
      return 1;
    }
    smFormula = smFormula.replaceAll("(?i)max", "Math.max");
    smFormula = smFormula.replaceAll("(?i)min", "Math.min");
    smFormula = smFormula.replaceAll(";", ",");

    LOG.info("calculateSecurityMeasure smFormula after replace: " + smFormula);
    String result = "1";

    //TODO Extract this creation from here to upper level, since it is time consuming
    ScriptEngineManager manager = new ScriptEngineManager(null);
    ScriptEngine engine = manager.getEngineByName("JavaScript");
    LOG.info("engine1 " + engine);
    try {
      result = String.valueOf(engine.eval(smFormula));
      LOG.info("engine smFormula {} result {} ", smFormula, result);
    } catch (Exception e) {
      LOG.error("calculateSecurityMeasure - exception: " + e.getMessage(), e);
      return 1;
    }

    Double resultDouble = Double.parseDouble(result);
    return resultDouble.intValue();
  }

  /*
   Retrieve the Safeguards associated to a Risk senario
   (all the Safeguard involved into dissuasion, prevention, confining and palliative measures)
   */
  private List<Safeguard> findSafeguardsAssociatedToScenario(Map<SecurityMeasureEnum, String> securityMeasuresMap, ArrayList<Safeguard> sm) {
    List<Safeguard> resultList = new ArrayList<>();
    List<String> safeguardsFromFormula = new ArrayList<>();

    // for each security measure
    for (SecurityMeasureEnum sme : securityMeasuresMap.keySet()) {
      // get the formula associated to the security measure
      String smFormula = securityMeasuresMap.get(sme);
      if (smFormula == null || smFormula.equals("")) {
        continue;
      }

      // removes all the formula symbols
      // in order to have a list of safeguard names divided by ;
      smFormula = smFormula.replace("max", "");
      smFormula = smFormula.replace("min", "");
      smFormula = smFormula.replace("(", "");
      smFormula = smFormula.replace(")", "");

      // add the safeguard names found to the list of all risk scenario safeguards
      for (String safeguardName : Arrays.asList(smFormula.split(";")))
        safeguardsFromFormula.add(safeguardName);
    }

    // retrieve the Safeguards
    // whose catalogue_id matches the ones extracted form formulas
    for (Safeguard sg : sm) {
      for (String catalogue_id : safeguardsFromFormula)
        if (catalogue_id.equals(sg.getCatalogueId())) {
          resultList.add(sg);
          break;
        }
    }

    return resultList;
  }

  public RiskServiceInterface getRiskModelService() {
    return riskModelService;
  }


  public void setRiskModelService(RiskServiceInterface riskModelService) {
    this.riskModelService = riskModelService;
  }


}
