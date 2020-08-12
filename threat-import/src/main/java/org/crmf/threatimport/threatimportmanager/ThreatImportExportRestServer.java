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
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
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
import java.util.List;

public class ThreatImportExportRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(ThreatImportExportRestServer.class.getName());

  private ThreatServiceInterface threatService;
  private ThreatImportManagerInputInterface threatImportManager;

  @POST
  @Path("/export")
  @Produces("application/octet-stream")
  public File exportThreats() throws IOException {
    LOG.info("exportThreats ");
    FileOutputStream fos = null;
    File fileToReturn = new File("exportThreat.temp");
    fileToReturn.deleteOnExit();
    try {
      if (!fileToReturn.exists()) {
        boolean created = fileToReturn.createNewFile();
        if (!created) {
          LOG.error("Unable to create export Threats file");
          return null;
        }
      }
      fos = new FileOutputStream(fileToReturn);
      SestThreatModel model = threatService.getThreatRepository(null);
      if (model != null) {
        String threatModel = model.getThreatModelJson();
        fos.write(threatModel.getBytes());
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
  public String importThreats(MultipartBody body) throws Exception {

    LOG.info("importThreats ");
    List<Attachment> attachments = body.getAllAttachments();
    if (attachments == null || attachments.size() != 2) {
      LOG.error("=========== uploadRequirement error : attachments size is different!!! ");
      throw new Exception("COMMAND_EXCEPTION");
    }
    try {
      LOG.info("=========== import vulnerabilities attachments getContentType : " + attachments.get(0).getContentType()
        + ", " + attachments.get(1).getContentType());
      threatImportManager.importThreatsFromInput(attachments.get(0));

    } catch (Exception e) {
      LOG.error("=========== import vulnerabilities error " + e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
    return "";
  }

  public ThreatServiceInterface getThreatService() {
    return threatService;
  }

  public ThreatImportManagerInputInterface getThreatImportManager() {
    return threatImportManager;
  }

  public void setThreatService(ThreatServiceInterface threatService) {
    this.threatService = threatService;
  }

  public void setThreatImportManager(ThreatImportManagerInputInterface threatImportManager) {
    this.threatImportManager = threatImportManager;
  }
}
