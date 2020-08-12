/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelManagerRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.safeguardmodel.manager.rest;

import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

public interface SafeguardModelManagerRestServerInterface {
  @POST
  @Produces("application/json")
  @Consumes("text/html")
  String loadSafeguardModel(GenericFilter filter) throws Exception;
}
