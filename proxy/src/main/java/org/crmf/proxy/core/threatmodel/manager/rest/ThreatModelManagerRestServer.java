/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerRestServer.java"
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

import org.crmf.core.threatmodel.manager.ThreatModelManagerInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.crmf.threatimport.threatimportmanager.ThreatImportManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//This class manages the business logic behind the webservices related to the ThreatModel management
@RestController
@RequestMapping(value = "api/threat")
public class ThreatModelManagerRestServer {
  // the logger of ThreatModelManagerRestServer class
  private static final Logger LOG = LoggerFactory.getLogger(ThreatModelManagerRestServer.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  @Autowired
  private ThreatModelManagerInput threatModelInput;
  @Autowired
  private ThreatImportManagerInput threatImportInput;
  @Autowired
  private RiskModelManagerInput riskModelInput;

  @PostMapping("threatModel/edit")
  @Permission("ThreatModel:Update")
  public void editThreatModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                              @RequestBody ModelObject threatModelObject) {
    LOG.info("ThreatModelManagerRestServer editThreatModel:: begin");
    try {
      //retrieve the threatModel in json format
      String threatModelJson = threatModelObject.getJsonModel();
      LOG.info("ThreatModelManagerRestServer editThreatModel:: threatModelJson = {}", threatModelJson.substring(0, (threatModelJson.length() > 1000 ? 1000 : threatModelJson.length())));
      //retrieve the threat model identifier
      String identifier = threatModelObject.getObjectIdentifier();
      LOG.info("ThreatModelManagerRestServer editThreatModel:: identifier = {}", identifier);

      //ThreatModel data validation and updateQuestionnaireJSON time
      ThreatModelSerializerDeserializer tmsd = new ThreatModelSerializerDeserializer();
      ThreatModel tm = tmsd.getTMFromJSONString(threatModelJson);
      DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
      Date now = new Date();
      tm.setUpdateTime(df.format(now));
      threatModelJson = tmsd.getJSONStringFromTM(tm);

      // updateQuestionnaireJSON the threat model
      threatModelInput.editThreatModel(threatModelJson, identifier);
    } catch (Exception e) {
      LOG.info("ThreatModelManagerRestServer editThreatModel:: exception {}", e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("threatModel/load")
  @Permission("ThreatModel:Read")
  public ModelObject loadThreatModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                     @RequestBody GenericFilter filter) {
    LOG.info("ThreatModelManagerRestServer loadThreatModel:: begin");
    try {
      //return the threat model in json format that matches the filters in input
      return threatModelInput.loadThreatModel(filter);
    } catch (Exception e) {
      LOG.info("ThreatModelManagerRestServer loadThreatModel:: exception {}", e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("threatRepository/load")
  @Permission("ThreatModel:Read")
  public String loadThreatRepository(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                     @RequestBody GenericFilter filter) {
    LOG.info("ThreatModelManagerRestServer loadThreatRepository:: begin");
    LOG.info("loadThreatRepository {}", filter);

    try {
      //return the threat model in json format that matches the filters in input
      String result = threatModelInput.loadThreatRepository(filter);
      return result;
    } catch (Exception e) {
      LOG.error("ThreatModelManagerRestServer loadThreatRepository " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("threatReference/create")
  @Permission("Taxonomy:Update")
  public ResponseMessage createThreatReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                               @RequestBody Threat threat) {

    try {
      String threatIdentifier = threatModelInput.createThreat(threat);
      return new ResponseMessage(threatIdentifier);
    } catch (Exception e) {
      LOG.error("ThreatModelManagerRestServer createThreatRepository " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("threatReference/edit")
  @Permission(value = "Taxonomy:Update")
  public void editThreatReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                  @RequestBody Threat threat) {
    try {
      threatModelInput.editThreat(threat);
    } catch (Exception e) {
      LOG.error("ThreatModelManagerRestServer updateThreatRepository " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("threatReference/delete")
  @Permission(value = "Taxonomy:Update")
  public void deleteThreatReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                    @RequestBody List<String> identifier) {
    try {
      threatModelInput.deleteThreat(identifier);
    } catch (Exception e) {
      LOG.error("ThreatModelManagerRestServer deleteThreatRepository " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
