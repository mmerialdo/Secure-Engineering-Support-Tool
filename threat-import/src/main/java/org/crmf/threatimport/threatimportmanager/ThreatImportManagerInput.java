/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatImportManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.threatimport.threatimportmanager;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ThreatImportManagerInput {

  private static final Logger LOG = LoggerFactory.getLogger(ThreatImportManagerInput.class.getName());

  // Threat service variable of persistency component
  @Autowired
  @Qualifier("default")
  private ThreatServiceInterface threatService;

  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

  private void importThreatsWithUpdate(ThreatModel tm) throws Exception {
    if (tm == null || tm.getThreats() == null || tm.getThreats().isEmpty()) {
      LOG.info("Threat Catalogue empty");
      throw new RemoteComponentException("Threat Catalogue empty");
    }

    //In this moment I have a ThreatModel with a set of threat just imported
    //Now I need to updateQuestionnaireJSON the database.
    //At first i load all existing threats for the selected source
    ThreatModel savedTm = threatService.getThreatRepository(null).convertToModel();

    //Here I create 2 ThreatModels: the first will collect all threats I have to add, the other all already existing threats which I have to updateQuestionnaireJSON
    ThreatModel tmToAdd = new ThreatModel();
    ThreatModel tmToUpdate = new ThreatModel();

    compareThreatRepositories(tmToAdd, tmToUpdate, tm, savedTm);

    if (threatService.updateThreatRepository(tmToAdd, tmToUpdate)) {
      LOG.info("importThreats successful");
    } else {
      LOG.info("importThreats failed");
      throw new Exception("unable to updateThreatRepository");
    }
  }

  public void importThreatsFromInput(InputStream file) throws Exception {
    ThreatModel model = loadMehariThreatsFromInput(file);
    importThreatsWithUpdate(model);
  }

  private ThreatModel loadMehariThreatsFromInput(InputStream is) throws IOException {
    try {
      byte[] bamJson = new byte[is.available()];
      Integer bytesNumber = is.read(bamJson);
      if (bytesNumber > 0) {
        String tmJsonString = new String(bamJson, StandardCharsets.UTF_8);
        ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();
        return tmSerDes.getTMFromJSONString(tmJsonString);
      }
    } catch (Exception e) {
      LOG.error("loadThreatsFromInput {} ", e.getMessage());
    }
    return null;
  }

  private void compareThreatRepositories(ThreatModel tmToAdd, ThreatModel tmToUpdate, ThreatModel tm, ThreatModel savedTm) {

    LOG.info("compareThreatRepositories {} ", tm.getThreats().size());
    LOG.info("compareThreatRepositories {} ", savedTm.getThreats().size());

    for (Threat newThreat : tm.getThreats()) {

      boolean alreadyExisting = false;
      boolean toBeUpdated = false;
      for (Threat oldThreat : savedTm.getThreats()) {

        if (newThreat.getCatalogueId().equals(oldThreat.getCatalogueId())) {
          alreadyExisting = true;

          if (compareThreats(oldThreat, newThreat)) {
            newThreat.setIdentifier(oldThreat.getIdentifier());
            toBeUpdated = true;
          }
          break;
        }
      }

      if (!alreadyExisting) {
        LOG.info("compareThreatRepositories add {} ", newThreat.getCatalogueId());
        tmToAdd.getThreats().add(newThreat);
      }
      if (toBeUpdated) {
        LOG.info("compareThreatRepositories update {} ", newThreat.getCatalogueId());
        tmToUpdate.getThreats().add(newThreat);
      }
    }
  }


  private boolean compareThreats(Threat oldThreat, Threat newThreat) {

    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
    try {
      Date oldThreatDate = df.parse(oldThreat.getLastUpdate());
      Date newThreatDate = df.parse(newThreat.getLastUpdate());

      return (newThreatDate.compareTo(oldThreatDate) > 0) ? true : false;
    } catch (ParseException e) {
      LOG.error("loadThreats {} ", e.getMessage());

      return true;
    }
  }
}
