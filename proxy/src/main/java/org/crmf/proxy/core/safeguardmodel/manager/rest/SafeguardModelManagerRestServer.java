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

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.utility.GenericFilter;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//This class manages the business logic behind the webservices related to the SafeguardModel management
@RestController
@RequestMapping(value = "api/safeguardModel")
public class SafeguardModelManagerRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(SafeguardModelManagerRestServer.class.getName());
  private SafeguardModelManagerInput safeguardModelInput;

  @PostMapping("load")
  @Permission(value = "SafeguardModel:Read")
  public String loadSafeguardModel(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                   @RequestBody GenericFilter filter) {
    LOG.info("SafeguardModelManagerRestServer loadSafeguardModel:: begin");
    try {
      //return the threat model in json format that matches the filters in input
      String result = safeguardModelInput.loadSafeguardModel(filter);
      return result;
    } catch (Exception e) {
      LOG.info("SafeguardModelManagerRestServer loadSafeguardModel:: exception " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }

  }
}
