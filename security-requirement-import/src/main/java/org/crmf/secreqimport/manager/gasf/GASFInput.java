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

import org.crmf.model.audit.ISOControls;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.model.utility.secrequirement.SecurityRequirementSerializerDeserializer;
import org.crmf.persistency.mapper.secrequirement.SecRequirementServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GASFInput {

  @Autowired
  private SecRequirementServiceInterface secrequirementService;

  private static final String filename = "gasfSafeguard.csv";
  private static final Logger LOG = LoggerFactory.getLogger(GASFInput.class.getName());

  //This method imports the full list of GASF requirements
  //After the import of GASF requirements, it saves them in the persistency and then it imports the relationships between GASF and MEHARI safeguards
  public void importGASFRequirementsFull(String gasfsecreq) {

    LOG.info("importGASFRequirementsFull gasfsecreq");
    if (gasfsecreq != null) {
      SecurityRequirementSerializerDeserializer converter = new SecurityRequirementSerializerDeserializer();
      ArrayList<SecurityRequirement> gasfRequirements = converter.getSRsFromGASFJSONString(gasfsecreq);
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
      InputStream resource = new ClassPathResource(filename).getInputStream();
      byte[] srSgBytes = resource.readAllBytes();
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
      String isoControlsPath = "ISO27002.json";

      InputStream resource = new ClassPathResource(isoControlsPath).getInputStream();
      byte[] bamJson = resource.readAllBytes();
      String amJsonString = new String(bamJson, "UTF-8");

      ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

      return amSerDes.getISOControlsFromJSONString(amJsonString);
    } catch (Exception e) {
      LOG.error("loadGASFSafeguardRelationFromFile " + e.getMessage(), e);
    }
    return null;
  }

  public void importCWE() {
    // TODO Auto-generated method stub
  }
}
