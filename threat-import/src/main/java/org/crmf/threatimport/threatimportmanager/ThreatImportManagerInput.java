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

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ThreatImportManagerInput implements ThreatImportManagerInputInterface {

  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  // Threat service variable of persistency component
  private ThreatServiceInterface threatService;

  private static final Logger LOG = LoggerFactory.getLogger(ThreatImportManagerInput.class.getName());
  private String fileName;
  private String url;
  private ThreatModel tmToAdd;
  private ThreatModel tmToUpdate;

  //Actually, the only implemented threat taxonomy source is a JSON file
  //The method could be easily extended in order to import taxonomies also from other sources
  @Override
  public void importThreats(ThreatSourceEnum source) throws Exception {

    LOG.info("importThreats -  source: " + source.toString());

    fileName = "";
    url = "";

    loadConfiguredThreatCatalogues(source.toString());

    ThreatModel tm = new ThreatModel();
    if (fileName != null && !fileName.equals("")) {
      tm = loadThreatsFromFile(source);
    } else if (url != null && !url.equals("")) {
      //TODO
    } else {
      throw new RemoteComponentException("Threat Catalogue not supported");
    }

    importThreatsWithUpdate(source, tm);
  }

  private void importThreatsWithUpdate(ThreatSourceEnum source, ThreatModel tm) throws Exception {
    if (tm == null || tm.getThreats() == null || tm.getThreats().size() == 0) {
      LOG.info("Threat Catalogue empty");
      throw new RemoteComponentException("Threat Catalogue empty");
    }

    //In this moment I have a ThreatModel with a set of threat just imported
    //Now I need to updateQuestionnaireJSON the database.
    //At first i load all existing threats for the selected source
    ThreatModel savedTm = threatService.getThreatRepository(source != null ? source.toString() : null).convertToModel();

    //Here I create 2 ThreatModels: the first will collect all threats I have to add, the other all already existing threats which I have to updateQuestionnaireJSON
    tmToAdd = new ThreatModel();
    tmToUpdate = new ThreatModel();

    compareThreatRepositories(tm, savedTm);

    if (threatService.updateThreatRepository(tmToAdd, tmToUpdate)) {
      LOG.info("importThreats successful");
    } else {
      LOG.info("importThreats failed");
      throw new Exception("COMMAND_EXCEPTION");
    }
  }

  public void importThreatsFromInput(Attachment attachmentFile) throws Exception {
    ThreatModel model = loadMehariThreatsFromInput(attachmentFile.getObject(InputStream.class));
    importThreatsWithUpdate(null, model);
  }

  private ThreatModel loadMehariThreatsFromInput(InputStream is) throws IOException {
    try {
      byte[] bamJson = new byte[is.available()];
      Integer bytesNumber = is.read(bamJson);
      if (bytesNumber > 0) {
        String tmJsonString = new String(bamJson, "UTF-8");
        LOG.info("loadThreatsFromInput " + tmJsonString);
        ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();
        return tmSerDes.getTMFromJSONString(tmJsonString);
      }
    } catch (Exception e) {
      LOG.error("loadThreatsFromInput " + e.getMessage());
    }
    return null;
  }

  private void compareThreatRepositories(ThreatModel tm, ThreatModel savedTm) {

    LOG.info("compareThreatRepositories " + tm.getThreats().size());
    LOG.info("compareThreatRepositories " + savedTm.getThreats().size());

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
        LOG.info("compareThreatRepositories add " + newThreat.getCatalogueId());
        tmToAdd.getThreats().add(newThreat);
      }
      if (toBeUpdated) {
        LOG.info("compareThreatRepositories update " + newThreat.getCatalogueId());
        tmToUpdate.getThreats().add(newThreat);
      }
    }

  }


  private boolean compareThreats(Threat oldThreat, Threat newThreat) {

    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
    try {
      Date oldThreatDate = df.parse(oldThreat.getLastUpdate());
      Date newThreatDate = df.parse(newThreat.getLastUpdate());

      if (newThreatDate.compareTo(oldThreatDate) > 0) {
        return true;
      } else {
        return false;
      }

    } catch (ParseException e) {
      LOG.error("loadThreats " + e.getMessage());

      return true;
    }

  }

  private ThreatModel loadThreatsFromFile(ThreatSourceEnum source) throws IOException {
    switch (source) {
      case MEHARI:
        return loadMehariThreatsFromFile();
      case CUSTOM:
        return null;
      default:
        return null;
    }

  }

  private ThreatModel loadMehariThreatsFromFile() throws IOException {
    try {
      File ftmJson = new File(fileName);
      byte[] bamJson = Files.readAllBytes(ftmJson.toPath());
      String tmJsonString = new String(bamJson, "UTF-8");

      LOG.info("loadThreatsFromFile " + tmJsonString);

      ThreatModelSerializerDeserializer tmSerDes = new ThreatModelSerializerDeserializer();

      ThreatModel vm = tmSerDes.getTMFromJSONString(tmJsonString);

      return vm;

    } catch (Exception e) {
      LOG.error("loadThreats " + e.getMessage());
      return null;
    }
  }


  public void loadConfiguredThreatCatalogues(String catalogue) {
    Wini iniVuln = null;

    try {
      iniVuln = new Wini(new File("threatCatalogue.ini"));

      fileName = iniVuln.get(catalogue, "fileName");
      url = iniVuln.get(catalogue, "url");


    } catch (IOException ex) {
      LOG.error("loadConfiguredThreatCatalogues " + ex.getMessage());
    }
  }


  public ThreatServiceInterface getThreatService() {
    return threatService;
  }


  public void setThreatService(ThreatServiceInterface threatService) {
    this.threatService = threatService;
  }


}
