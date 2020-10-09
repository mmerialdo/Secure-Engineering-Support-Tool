/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ISOControlInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.secreqimport.manager.iso;

import org.crmf.model.audit.ISOControls;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.persistency.mapper.audit.ISOControlServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Files;

public class ISOControlInput {

  private static final Logger LOG = LoggerFactory.getLogger(ISOControlInput.class.getName());
  @Autowired
  private ISOControlServiceInterface isoControlService;

  private void importISOFromFile(String filename) {

    try {
      String isoControlsPath = ".//ISO27002.json";

      File famJson = new File(isoControlsPath);
      byte[] bamJson = Files.readAllBytes(famJson.toPath());
      String amJsonString = new String(bamJson, "UTF-8");

      ISOControlsSerializerDeserializer amSerDes = new ISOControlsSerializerDeserializer();

      ISOControls controls = amSerDes.getISOControlsFromJSONString(amJsonString);

      isoControlService.createDefaultQuestionnaireWithISO(controls);
    } catch (Exception e) {
      LOG.error("loadGASFSafeguardRelationFromFile " + e.getMessage(), e);
    }
  }

  public ISOControlServiceInterface getIsoControlService() {
    return isoControlService;
  }

  public void setIsoControlService(ISOControlServiceInterface isoControlService) {
    this.isoControlService = isoControlService;
  }
}
