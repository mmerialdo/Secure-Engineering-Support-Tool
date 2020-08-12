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
package org.crmf.riskmodel.manager;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.RiskScenarioReferenceModel;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.riskmodel.RiskScenarioReferenceModelSerializerDeserializer;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RiskScenarioImportExportRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(RiskScenarioImportExportRestServer.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

  private RiskServiceInterface riskDBService;
  private RiskScenariosReferenceImporter riskScenariosImporter;

  @POST
  @Path("/export")
  @Produces("application/octet-stream")
  public File exportRiskScenario() throws IOException {
    LOG.info("exportRiskScenario ");
    FileOutputStream fos = null;
    File fileToReturn = new File("exportRiskScenario.temp");
    fileToReturn.deleteOnExit();
    try {
      if (!fileToReturn.exists()) {
        boolean created = fileToReturn.createNewFile();
        if (!created) {
          LOG.error("Unable to create export RiskScenario file");
          return null;
        }
      }
      fos = new FileOutputStream(fileToReturn);
      List<RiskScenarioReference> scenarios = riskDBService.getRiskScenarioReference();
      if (scenarios != null && !scenarios.isEmpty()) {
        RiskScenarioReferenceModelSerializerDeserializer rmSerDes = new RiskScenarioReferenceModelSerializerDeserializer();
        RiskScenarioReferenceModel rmmodel = new RiskScenarioReferenceModel();
        rmmodel.setScenarios(scenarios);

        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
        rmmodel.setCreationTime(df.format(new Date()));
        rmmodel.setUpdateTime(df.format(new Date()));
        rmmodel.setObjType(SESTObjectTypeEnum.RiskModel);
        rmmodel.setIdentifier("");
        String rmJson = rmSerDes.getJSONStringFromRM(rmmodel);
        fos.write(rmJson.getBytes());
      }
      return fileToReturn;
    } catch (IOException ioe) {
      LOG.error("Unable to create file ", ioe);
    } finally {
      if (fos != null) {
        fos.close();
      }
      fileToReturn.deleteOnExit();
    }
    return null;
  }

  @POST
  @Path("/import")
  @Produces("text/html")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public String importRiskScenario(MultipartBody body) throws Exception {

    LOG.info("importRiskScenario ");
    List<Attachment> attachments = body.getAllAttachments();
    if (attachments == null || attachments.size() != 2) {
      LOG.error("=========== import RiskScenario error : attachments size is different!!! ");
      throw new Exception("COMMAND_EXCEPTION");
    }
    try {
      LOG.info("=========== import RiskScenario attachments getContentType : " + attachments.get(0).getContentType()
        + ", " + attachments.get(1).getContentType());
      riskScenariosImporter.importRiskScenariosFromInput(attachments.get(0));

    } catch (Exception e) {
      LOG.error("=========== import RiskScenario error " + e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
    return "";
  }

  public RiskServiceInterface getRiskDBService() {
    return riskDBService;
  }

  public RiskScenariosReferenceImporter getRiskScenariosImporter() {
    return riskScenariosImporter;
  }

  public void setRiskDBService(RiskServiceInterface riskDBService) {
    this.riskDBService = riskDBService;
  }

  public void setRiskScenariosImporter(RiskScenariosReferenceImporter riskScenariosImporter) {
    this.riskScenariosImporter = riskScenariosImporter;
  }
}
