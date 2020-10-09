/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelManagerRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.risktreatmentmodel.manager.rest;


import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.risktreatmentmodel.manager.RiskTreatmentModelManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//This class manages the business logic behind the webservices related to the RiskTreatmentModel management
@RestController
@RequestMapping(value = "api/riskTreatmentModel")
public class RiskTreatmentModelManagerRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(RiskTreatmentModelManagerRestServer.class.getName());
  @Autowired
  private RiskTreatmentModelManagerInput riskTreatmentModelInput;

  @PostMapping("edit")
  @Permission(value = "RiskTreatmentModel:Update")
  public String editRiskTreatmentModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                       @RequestBody ModelObject riskTreatmentModel) {
    LOG.info("editRiskTreatmentModel:: begin");
    try {
      String riskTreatmentModelJson = riskTreatmentModel.getJsonModel();
      String identifier = riskTreatmentModel.getObjectIdentifier();
      LOG.info("editRiskTreatmentModel:: identifier = " + identifier);
      return riskTreatmentModelInput.editRiskTreatmentModel(riskTreatmentModelJson, identifier);
    } catch (Exception ex) {
      LOG.error("editThreatModel:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("editDetail")
  @Permission(value = "RiskTreatmentModel:Update")
  public String editRiskTreatmentModelDetail(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                             @RequestBody ModelObject riskTreatmentModel) throws Exception {
    LOG.info("editRiskTreatmentModelDetail:: begin");
    try {
      String riskTreatmentModelJson = riskTreatmentModel.getJsonModel();
      String identifier = riskTreatmentModel.getObjectIdentifier();
      LOG.info("editRiskTreatmentModelDetail:: identifier = " + identifier);
      return riskTreatmentModelInput.editRiskTreatmentModelDetail(riskTreatmentModelJson, identifier);
    } catch (Exception ex) {
      LOG.error("editRiskTreatmentModelDetail:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("load")
  @Permission(value = "RiskTreatmentModel:Read")
  public ModelObject loadRiskTreatmentModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                            @RequestBody GenericFilter filter) throws Exception {
    LOG.info("loadRiskTreatmentModel:: begin");
    try {
      return riskTreatmentModelInput.loadRiskTreatmentModel(filter);
    } catch (Exception ex) {
      LOG.error("loadRiskTreatmentModel:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("loadDetail")
  @Permission(value = "RiskTreatmentModel:Read")
  public String loadRiskTreatmentModelDetail(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                             @RequestBody GenericFilter filter) throws Exception {
    LOG.info("loadRiskTreatmentModelDetail:: begin");
    try {
      String result = riskTreatmentModelInput.loadRiskTreatmentModelDetail(filter);
      return result;
    } catch (Exception ex) {
      LOG.error("loadRiskTreatmentModelDetail:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("calculate")
  @Permission(value = "RiskTreatmentModel:Update")
  public String calculateRiskTreatmentModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                            @RequestBody ModelObject riskModel) throws Exception {
    LOG.info("calculateRiskTreatmentModel:: begin");
    try {
      String riskTreatmentModelJson = riskModel.getJsonModel();
      String identifier = riskModel.getObjectIdentifier();
      LOG.info("calculateRiskTreatmentModel:: identifier = " + identifier);
      return riskTreatmentModelInput.calculateRiskTreatmentModel(riskTreatmentModelJson, identifier);
    } catch (Exception ex) {
      LOG.error("calculateRiskTreatmentModel:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("calculateDetail")
  @Permission(value = "RiskTreatmentModel:Update")
  public String calculateRiskTreatmentModelDetail(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                  @RequestBody ModelObject riskModel) {
    LOG.info("calculateRiskTreatmentModelDetail:: begin");
    try {
      String riskTreatmentModelJson = riskModel.getJsonModel();
      String identifier = riskModel.getObjectIdentifier();
      LOG.info("calculateRiskTreatmentModelDetail:: identifier = " + identifier);
      return riskTreatmentModelInput.calculateRiskTreatmentModelDetail(riskTreatmentModelJson, identifier);
    } catch (Exception ex) {
      LOG.error("calculateRiskTreatmentModelDetail:: exception " + ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }
}
