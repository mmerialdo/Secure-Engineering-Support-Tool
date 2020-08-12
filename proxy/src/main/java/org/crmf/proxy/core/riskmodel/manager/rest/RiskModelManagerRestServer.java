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

import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.riskmodel.manager.RiskModelManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//This class manages the business logic behind the webservices related to the RiskModels management
public class RiskModelManagerRestServer implements RiskModelManagerRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RiskModelManagerRestServerInterface.class.getName());
  private RiskModelManagerInputInterface riskModelInput;

  @Override
  public String editRiskModel(ModelObject riskModelObject) throws Exception {
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
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public String editRiskScenario(ModelObject riskModelObject) throws Exception {
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
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public ModelObject loadRiskModel(GenericFilter filter) throws Exception {
    LOG.info("RiskModelManagerRestServer loadRiskModel:: begin");
    try {
      //return the risk model in json format that matches the filters in input
      return riskModelInput.loadRiskModel(filter);
    } catch (Exception e) {
      LOG.info("RiskModelManagerRestServer loadRiskModel:: exception {}", e.getMessage(), e);
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public void updateScenarioRepository(String catalogue) throws Exception {
    LOG.info("updateScenarioRepository:: begin");
    LOG.info("updateScenarioRepository {}", catalogue);

    try {
      if (!catalogue.equals("MEHARI")) {
        throw new Exception("COMMAND_EXCEPTION");
      }
      riskModelInput.updateScenarioRepository(catalogue);
    } catch (Exception e) {
      LOG.error("updateScenarioRepository " + e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public String createRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    return this.riskModelInput.insertRiskScenarioReference(riskScenarioReference);
  }

  @Override
  public void deleteRiskScenarioReference(List<String> identifier) throws Exception {
    this.riskModelInput.deleteRiskScenarioReference(identifier);
  }

  @Override
  public void updateRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    this.riskModelInput.editRiskScenarioReference(riskScenarioReference);
  }

  @Override
  public List<RiskScenarioReference> loadRiskScenarioReference() {
    return this.riskModelInput.getRiskScenarioReference();
  }

  public RiskModelManagerInputInterface getRiskModelInput() {
    return riskModelInput;
  }

  public void setRiskModelInput(RiskModelManagerInputInterface riskModelInput) {
    this.riskModelInput = riskModelInput;
  }
}
