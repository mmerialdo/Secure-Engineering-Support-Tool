/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelManagerRestServer.java"
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

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInputInterface;
import org.crmf.model.utility.GenericFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the SafeguardModel management
public class SafeguardModelManagerRestServer implements SafeguardModelManagerRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SafeguardModelManagerRestServer.class.getName());
  private SafeguardModelManagerInputInterface safeguardModelInput;

  @Override
  public String loadSafeguardModel(GenericFilter filter) throws Exception {
    LOG.info("SafeguardModelManagerRestServer loadSafeguardModel:: begin");
    try {
      //return the threat model in json format that matches the filters in input
      String result = safeguardModelInput.loadSafeguardModel(filter);
      return result;
    } catch (Exception e) {
      LOG.info("SafeguardModelManagerRestServer loadSafeguardModel:: exception " + e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  public SafeguardModelManagerInputInterface getSafeguardModelInput() {
    return safeguardModelInput;
  }

  public void setSafeguardModelInput(SafeguardModelManagerInputInterface safeguardModelInput) {
    this.safeguardModelInput = safeguardModelInput;
  }

}
