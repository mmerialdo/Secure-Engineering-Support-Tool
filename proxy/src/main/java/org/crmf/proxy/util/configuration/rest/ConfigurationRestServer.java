/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="VulnerabilityModelManagerRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.proxy.util.configuration.rest;

import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationRestServer {

  private String timeout;
  private String timeoutWarning;

  @POST
  @Consumes("text/html")
  @Produces("application/json")
  public List<String> getConfiguration() throws Exception {

    return new ArrayList<>() {
      {add(timeout);add(timeoutWarning);}
    };
  }

  @POST

  public void ping() {
  }

  public String getTimeout() {
    return timeout;
  }

  public String getTimeoutWarning() {
    return timeoutWarning;
  }

  public void setTimeout(String timeout) {
    this.timeout = timeout;
  }

  public void setTimeoutWarning(String timeoutWarning) {
    this.timeoutWarning = timeoutWarning;
  }
}
