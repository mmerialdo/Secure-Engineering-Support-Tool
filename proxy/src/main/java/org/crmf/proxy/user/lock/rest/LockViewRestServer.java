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
import org.crmf.user.manager.lock.LockViewInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class LockViewRestServer {

  @Autowired
  private LockViewInput lockInput;

  @PostMapping("lock")
  public String lock(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                     @RequestBody GenericFilter filter) throws Exception {
    return lockInput.lock(filter);
  }

  @PostMapping("unlock")
  public String unlock(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                       @RequestBody GenericFilter filter) throws Exception {
    return lockInput.unlock(filter);
  }

  @PostMapping(value = "getlock")
  public String getlock(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                 @RequestBody GenericFilter filter) throws Exception {
    return lockInput.getlock(filter);
  }
}
