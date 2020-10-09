/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelManagerRestServer.java"
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
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//This class manages the business logic behind the webservices related to the RiskModels management
@RestController
@RequestMapping(value = "api/scenario")
public class RiskModelManagerRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(RiskModelManagerRestServer.class.getName());
  @Autowired
  private RiskModelManagerInput riskModelInput;

  @PostMapping(path = "riskModel/edit", produces = MediaType.TEXT_PLAIN_VALUE)
  @Permission(value = "RiskModel:Update")
  public String editRiskModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                              @RequestBody ModelObject riskModelObject) {
    LOG.info("RiskModelManagerRestServer editRiskModel:: begin");
    try {
      //retrieve the riskModel in json format
      String riskModelJson = riskModelObject.getJsonModel();
      //retrieve the risk model identifier
      String identifier = riskModelObject.getObjectIdentifier();
      LOG.info("RiskModelManagerRestServer editRiskModel:: identifier = {}", identifier);
      // updateQuestionnaireJSON the risk model
      return riskModelInput.editRiskModel(riskModelJson, identifier);
    } catch (Exception e) {
      LOG.info("RiskModelManagerRestServer editRiskModel:: exception " + e.getMessage(), e);
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("riskScenario/edit")
  @Permission(value = "RiskModel:Update")
  public String editRiskScenario(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                 @RequestBody ModelObject riskModelObject) {
    LOG.info("RiskModelManagerRestServer editRiskScenario:: begin");
    try {
      //retrieve the riskModel in json format
      String riskModelJson = riskModelObject.getJsonModel();
      //retrieve the risk model identifier
      String identifier = riskModelObject.getObjectIdentifier();
      LOG.info("RiskModelManagerRestServer editRiskScenario:: identifier = {}", identifier);
      // updateQuestionnaireJSON the risk model
      return riskModelInput.editRiskScenario(riskModelJson, identifier);
    } catch (Exception e) {
      LOG.info("RiskModelManagerRestServer editRiskScenario:: exception {}", e.getMessage(), e);
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("riskModel/load")
  @Permission(value = "RiskModel:Read")
  public ModelObject loadRiskModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                   @RequestBody GenericFilter filter) {
    LOG.info("RiskModelManagerRestServer loadRiskModel:: begin");
    try {
      //return the risk model in json format that matches the filters in input
      return riskModelInput.loadRiskModel(filter);
    } catch (Exception e) {
      LOG.info("RiskModelManagerRestServer loadRiskModel:: exception {}", e.getMessage(), e);
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("scenarioRepository/update")
  @Permission(value = "Settings:Update")
  public void updateScenarioRepository(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                       @RequestBody String catalogue) {
    LOG.info("updateScenarioRepository:: begin");
    LOG.info("updateScenarioRepository {}", catalogue);

    try {
      if (!catalogue.equals("MEHARI")) {
        throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
      }
      riskModelInput.updateScenarioRepository(catalogue);
    } catch (Exception e) {
      LOG.error("updateScenarioRepository " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("riskScenarioReference/create")
  @Permission(value = "Taxonomy:Update")
  public ResponseMessage createRiskScenarioReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                     @RequestBody RiskScenarioReference riskScenarioReference) {
    try {
      String referenceIdentifier = this.riskModelInput.insertRiskScenarioReference(riskScenarioReference);
      return new ResponseMessage(referenceIdentifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("riskScenarioReference/edit")
  @Permission(value = "Taxonomy:Update")
  public void updateRiskScenarioReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                          @RequestBody RiskScenarioReference riskScenarioReference) {
    try {
      this.riskModelInput.editRiskScenarioReference(riskScenarioReference);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("riskScenarioReference/delete")
  @Permission(value = "Taxonomy:Update")
  public void deleteRiskScenarioReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                          @RequestBody List<String> identifier) {
    try {
      this.riskModelInput.deleteRiskScenarioReference(identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("riskScenarioReference/load")
  @Permission(value = "Taxonomy:Read")
  public List<RiskScenarioReference> loadRiskScenarioReference(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
    try {
      return this.riskModelInput.getRiskScenarioReference();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }
}
