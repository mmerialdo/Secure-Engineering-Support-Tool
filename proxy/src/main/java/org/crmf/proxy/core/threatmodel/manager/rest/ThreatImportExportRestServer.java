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

package org.crmf.proxy.core.threatmodel.manager.rest;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.threatimport.threatimportmanager.ThreatImportManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "api/threat")
public class ThreatImportExportRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(ThreatImportExportRestServer.class.getName());

  @Autowired
  private ThreatService threatService;
  @Autowired
  private ThreatImportManagerInput threatImportManager;

  @GetMapping("export")
  @Permission("Taxonomy:Read")
  public InputStreamResource exportThreats(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) throws IOException {
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
      return new InputStreamResource(new FileInputStream(fileToReturn));
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

  @PostMapping("import")
  @Permission("Taxonomy:Read")
  public String importThreats(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                       @RequestParam("file") MultipartFile multipartFile) {
    try {
      threatImportManager.importThreatsFromInput(multipartFile.getInputStream());
    } catch (Exception e) {
      LOG.error("=========== import vulnerabilities error " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
    return "";
  }
}
