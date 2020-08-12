/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="GASFInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.secreqimport.manager.gasf;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.model.utility.secrequirement.SecurityRequirementSerializerDeserializer;
import org.crmf.persistency.mapper.secrequirement.SecRequirementServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GASFInput implements GASFInputInterface {

  private SecRequirementServiceInterface secrequirementService;
  private SecurityRequirementSerializerDeserializer converter = new SecurityRequirementSerializerDeserializer();

  @Produce(uri = "direct:getRequirement")
  private ProducerTemplate template;

  private static final String filename = ".//gasfSafeguard.csv";
  private static final Logger LOG = LoggerFactory.getLogger(GASFInput.class.getName());

  //This method imports the full list of GASF requirements
  //After the import of GASF requirements, it saves them in the persistency and then it imports the relationships between GASF and MEHARI safeguards
  @Override
  public void importGASFRequirementsFull() {

    LOG.info("importGASFRequirementsFull gasfsecreq");
    Object gasfsecreq = template.requestBody(null);
    if (gasfsecreq != null) {
      ArrayList<SecurityRequirement> gasfRequirements = converter.getSRsFromGASFJSONString(gasfsecreq.toString());
      if (gasfRequirements != null && !gasfRequirements.isEmpty()) {
        secrequirementService.deleteSecRequirement();

        for (SecurityRequirement securityRequirement : gasfRequirements) {
          if (securityRequirement != null)
            secrequirementService.insertSecurityRequirement(securityRequirement);
        }

        for (SecurityRequirement securityRequirement : gasfRequirements) {
          if (securityRequirement != null)
            secrequirementService.insertSecRequirementParent(securityRequirement);
        }

        importGASFSafeguardRelationFromFile(filename);
      }
    }
  }

  //This method imports from a CSV file the relationships between GASF requirements and the MEHARI safeguards and persist them in the repository
  private void importGASFSafeguardRelationFromFile(String filename) {

    try {
      File srSgRelFile = new File(filename);
      byte[] srSgBytes = Files.readAllBytes(srSgRelFile.toPath());
      String srSgS = new String(srSgBytes, "UTF-8");

      LOG.info("loadGASFSafeguardRelationFromFile " + srSgS);

      List<String[]> valuesList = new ArrayList<>();
      String[] lines = srSgS.split("\\r?\\n");
      for (String line : lines) {

        String[] values = line.split(",");

        //We avoid to persist empty rows
        if (values[1].equals("N/A") || values[1].equals("CATEGORY") || values[1].equals("DEFINED")) {
          continue;
        }
        valuesList.add(values);
      }
      ISOControls controls = importISOFromFile();
      secrequirementService.insertSecRequirementSafeguard(valuesList, controls);
    } catch (Exception e) {
      LOG.error("loadGASFSafeguardRelationFromFile " + e.getMessage(), e);
    }
  }


  private ISOControls importISOFromFile() {

    try {
      String isoControlsPath = ".//ISO27002.json";

      File famJson = new File(isoControlsPath);
      byte[] bamJson = Files.readAllBytes(famJson.toPath());
      String amJsonString = new String(bamJson, "UTF-8");

      ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

      return amSerDes.getISOControlsFromJSONString(amJsonString);
    } catch (Exception e) {
      LOG.error("loadGASFSafeguardRelationFromFile " + e.getMessage(), e);
    }
    return null;
  }

  @Override
  public void importCWE() {
    // TODO Auto-generated method stub

  }

  public SecRequirementServiceInterface getSecrequirementService() {
    return secrequirementService;
  }

  public void setSecrequirementService(SecRequirementServiceInterface secrequirementService) {
    this.secrequirementService = secrequirementService;
  }

}
