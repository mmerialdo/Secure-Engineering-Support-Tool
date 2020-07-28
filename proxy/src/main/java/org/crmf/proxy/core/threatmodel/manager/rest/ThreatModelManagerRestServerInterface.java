/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerRestServerInterface.java"
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

import java.util.List;

public interface ThreatModelManagerRestServerInterface {

  @POST
  @Consumes("application/json")
  void editThreatModel(ModelObject threatModel) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  ModelObject loadThreatModel(GenericFilter filter) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  String loadThreatRepository(GenericFilter filter) throws Exception;

  @POST
  @Consumes("text/html")
  void updateThreatRepository(String catalogue) throws Exception;

  @POST
  @Consumes("application/json")
  @Produces("text/html")
  String createThreatReference(Threat threat) throws Exception;

  @POST
  @Consumes("application/json")
  void editThreatReference(Threat threat) throws Exception;

  @POST
  @Consumes("application/json")
  void deleteThreatReference(List<String> identifier) throws Exception;
}
