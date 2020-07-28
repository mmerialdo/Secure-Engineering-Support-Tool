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
package org.crmf.proxy.user.lock.rest;

import org.crmf.model.utility.GenericFilter;
import org.crmf.user.manager.lock.LockViewInputInterface;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

public class LockViewRestServer {

  private LockViewInputInterface lockInput;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  public String lock(GenericFilter filter) throws Exception {

    return lockInput.lock(filter);
  }

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  public String unlock(GenericFilter filter) throws Exception {

    return lockInput.unlock(filter);
  }

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  public String getlock(GenericFilter filter) throws Exception {

    return lockInput.getlock(filter);
  }

  public LockViewInputInterface getLockInput() {
    return lockInput;
  }

  public void setLockInput(LockViewInputInterface lockInput) {
    this.lockInput = lockInput;
  }
}
