/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelManagerRestServerInterface.java"
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.List;

public interface RiskModelManagerRestServerInterface {

  @POST
  @Consumes("application/json")
  @Produces("application/json")
  String editRiskModel(ModelObject riskModel) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  ModelObject loadRiskModel(GenericFilter filter) throws Exception;

  @POST
  @Consumes("application/json")
  @Produces("application/json")
  String editRiskScenario(ModelObject riskModel) throws Exception;

  @POST
  @Consumes("text/html")
  void updateScenarioRepository(String catalogue) throws Exception;

  @POST
  @Consumes("application/json")
  @Produces("text/html")
  String createRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;

  @POST
  @Consumes("application/json")
  void deleteRiskScenarioReference(List<String> identifier) throws Exception;

  @POST
  @Consumes("application/json")
  void updateRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;

  @POST
  @Consumes("application/json")
  @Produces("application/json")
  List<RiskScenarioReference> loadRiskScenarioReference();
}
