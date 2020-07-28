/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskScenariosReferenceImporter.java"
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.crmf.model.riskassessment.RiskScenarioReferenceModel;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.crmf.model.utility.riskmodel.RiskScenarioReferenceModelSerializerDeserializer;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.riskmodel.utility.SestStandardConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the import of risk scenarios from a CSV file, deserializing them and persisting them in the database
public class RiskScenariosReferenceImporter {
  private static final Logger LOG = LoggerFactory.getLogger(RiskScenariosReferenceImporter.class.getName());
  private RiskServiceInterface riskModelService;

  public void init() throws Exception {
    importRiskScenariosFromCsv();
  }

  /*
   Import the risk reference scenarios from configuration file
   and store them into sest DB
   */
  public void importRiskScenariosFromCsv() {
    // Load a set of risk scenario references importing them from file
    Set<RiskScenarioReference> riskScenarioReference =  loadReferenceScenariosFromCsv();
    importRiskScenarios(riskScenarioReference);
  }

  public void importRiskScenariosFromInput(Attachment attachmentFile) throws Exception{
		Set<RiskScenarioReference> riskScenarioReference = loadRiskScenariosFromInput(attachmentFile.getObject(InputStream.class));
    importRiskScenarios(riskScenarioReference);
  }

  private Set<RiskScenarioReference> loadRiskScenariosFromInput(InputStream is) throws IOException {
    try {
			Set<RiskScenarioReference> riskScenarioReference = new HashSet<>();
      byte[] bamJson = new byte[is.available()];
      is.read(bamJson);
      String rsJsonString = new String(bamJson, "UTF-8");
      LOG.info("loadRiskScenarioFromInput " + rsJsonString);
      RiskScenarioReferenceModelSerializerDeserializer rmSerDes = new RiskScenarioReferenceModelSerializerDeserializer();
      RiskScenarioReferenceModel rm = rmSerDes.getRMFromJSONString(rsJsonString);
      if(rm == null) {
        LOG.error("Unable to unmarshall riskScenraioReferenceModel.");
        return null;
      }

      return new HashSet<RiskScenarioReference>(rm.getScenarios());
    } catch (Exception e) {
      LOG.error("loadRiskScenarioFromInput " + e.getMessage());
      return null;
    }
  }

  private void importRiskScenarios(Set<RiskScenarioReference> riskScenarioReference) {
    LOG.info("number of reference risk scenarios imported: " + riskScenarioReference.size());

    if (riskScenarioReference.size() == 0) {
      LOG.info("New Risk Scenarios Reference is empty");
      return;
    }

    // Update of database table associated to Risk Scenario Reference
    // Load of all existing risk Scenarios References
    ArrayList<RiskScenarioReference> rsr = riskModelService.getRiskScenarioReference();

    //merge the new Reference Scenario with the current one (stored on DB)
    mergeRiskScenarioRepositories(riskScenarioReference, rsr);

    // if the updateQuestionnaireJSON of the Risk Scenario Reference table on db is successful, log it
    if (riskModelService.updateScenarioRepository(rsr)) {
      LOG.info("Import Risk Scenario Reference successful");
    } else
      LOG.info("Import Risk Scenario Reference not successfull");
  }

  /*
   Compare the current scenario references stored into the db with the new imported ones.
     If the new risk scenario references exist, add them.
     If an already existing risk scenario reference has
     different dissuasion/prevention/confining/palliative values, updateQuestionnaireJSON them.
   */
  private void mergeRiskScenarioRepositories(Set<RiskScenarioReference> riskScenarioReference, ArrayList<RiskScenarioReference> rsr) {
    // for each Scenario into the new Reference
    for (RiskScenarioReference currentScenario : riskScenarioReference) {
      boolean found = false;
      // for each Scenario already stored into db
      for (RiskScenarioReference dbRiskScenario : rsr) {
        // if the two Scenarios are equal
        // (same assetType/securityScope/vulnerability fields/Threat fields combination)
        // check and updateQuestionnaireJSON dissuasion/prevention/confining/palliative values if needed
        if (currentScenario.equals(dbRiskScenario)) {
          // signals that the scenario is a duplicate
          //we updateQuestionnaireJSON the Security measures values (which may have been updated by the Users)
          found = true;
          dbRiskScenario.setDissuasion(currentScenario.getDissuasion());
          dbRiskScenario.setPrevention(currentScenario.getPrevention());
          dbRiskScenario.setConfining(currentScenario.getConfining());
          dbRiskScenario.setPalliative(currentScenario.getPalliative());

          break;
        }
      }
      if (found == false) {
        rsr.add(currentScenario);
      }

    }

  }


  /*
   Load a set of risk scenario references importing them from file
   */
  private Set<RiskScenarioReference> loadReferenceScenariosFromCsv() {
    LOG.info("loadReferenceScenariosFromCsv - importing data...");
    String csvFile = ".//RiskScenarioReference.csv";
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";
		Set<RiskScenarioReference> riskScenarioReference = new HashSet<>();

    try {

      // read the csv file
      br = new BufferedReader(new FileReader(csvFile));

      // for each line of csv file (corresponding to one or more reference scenarios)
      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] scenarioString = line.split(cvsSplitBy);

        // fill the empty values at the end of the array
        // to have exact number of fields required (16)
        String[] newArray = new String[16];
        System.arraycopy(scenarioString, 0, newArray, 0, scenarioString.length);
        int size = scenarioString.length;
        while (size <= 15) {
          newArray[size] = "";
          size++;
        }
        scenarioString = newArray;

        // asset fields
        PrimaryAssetCategoryEnum assetType = SestStandardConverter.checkPrimaryAssetCategory(scenarioString[0]);

        if (assetType == null) {
          continue;
        }

        //vulnerability fields
        //scope/aice field
        SecurityImpactScopeEnum aice = SestStandardConverter.translateScope(scenarioString[1]);

        //if the scope value is not into the possible range go to next line
        if (null == aice) continue;
        //translate the Vulnerability supporting Asset into our ENUM standard
        List<SecondaryAssetCategoryEnum> supportingAssets = SestStandardConverter.checkSecondaryAssetCategory(scenarioString[2]);
        if (supportingAssets == null) {
          continue;
        }
        //String damage = scenarioString[3].toLowerCase();
        String vulnerabilityCode = scenarioString[4];
        if (vulnerabilityCode == null || vulnerabilityCode.equals("")) {
          continue;
        }

        //threat fields
        String eventType = scenarioString[5];
        if (eventType == null || eventType.equals("")) {
          continue;
        }
        String eventSubType = scenarioString[6];
        if (eventSubType == null || eventSubType.equals("")) {
          continue;
        }
        String place = scenarioString[7];
        if (place == null) {
          place = "";
        }
        String time = scenarioString[8];
        if (time == null) {
          time = "";
        }
        String access = scenarioString[9];
        if (access == null) {
          access = "";
        }
        String process = scenarioString[10];
        if (process == null) {
          process = "";
        }
        String actor = scenarioString[11];
        if (actor == null) {
          actor = "";
        }

        //security measures fields
        String dissuasion = scenarioString[12];
        if (dissuasion == null) {
          dissuasion = "";
        }
        String prevention = scenarioString[13];
        if (prevention == null) {
          prevention = "";
        }
        String confining = scenarioString[14];
        if (confining == null) {
          confining = "";
        }
        String palliative = scenarioString[15];
        if (palliative == null) {
          palliative = "";
        }

        for (SecondaryAssetCategoryEnum supportingAsset : supportingAssets) {
          RiskScenarioReference currentLineScenario = new RiskScenarioReference(assetType, aice, supportingAsset, vulnerabilityCode,
            eventType, eventSubType, place, time, access, process, actor, dissuasion, prevention, confining, palliative);
          riskScenarioReference.add(currentLineScenario);
        }
      }

    } catch (FileNotFoundException e) {
      LOG.error(e.getMessage());
    } catch (IOException e) {
      LOG.error(e.getMessage());
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          LOG.error(e.getMessage());
        }
      }
    }
    return riskScenarioReference;
  }

  public RiskServiceInterface getRiskModelService() {
    return riskModelService;
  }

  public void setRiskModelService(RiskServiceInterface riskModelService) {
    this.riskModelService = riskModelService;
  }
}
