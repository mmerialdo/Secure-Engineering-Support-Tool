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
package org.crmf.proxy.core.riskmodel.manager.rest;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.RiskScenarioReferenceModel;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.riskmodel.RiskScenarioReferenceModelSerializerDeserializer;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.riskmodel.manager.RiskScenariosReferenceImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "api/riskScenario")
public class RiskScenarioImportExportRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(RiskScenarioImportExportRestServer.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

  @Autowired
  @Qualifier("default")
  private RiskServiceInterface riskDBService;
  @Autowired
  private RiskScenariosReferenceImporter riskScenariosImporter;

  @GetMapping("export")
  @Permission("Taxonomy:Read")
  public InputStreamResource exportRiskScenario(
    @RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) throws IOException {
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
      return new InputStreamResource(new FileInputStream(fileToReturn));
    } catch (IOException ioe) {
      LOG.error("Unable to create file ", ioe);
    } finally {
      if (fos != null) {
        fos.close();
      }
      fileToReturn.delete();
    }
    return null;
  }

  @PostMapping("import")
  @Permission("Taxonomy:Read")
  public String importRiskScenario(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                            @RequestParam("file") MultipartFile multipartFile) {

    LOG.info("importRiskScenario ");
    try {
      riskScenariosImporter.importRiskScenariosFromInput(multipartFile.getInputStream());
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
    return "";
  }
}
