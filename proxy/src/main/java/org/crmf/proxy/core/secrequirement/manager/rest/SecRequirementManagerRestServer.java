/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementManagerRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.secrequirement.manager.rest;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.secreqimport.manager.gasf.GASFInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//This class manages the business logic behind the webservices related to the SecurityRequirements management
@RestController
@RequestMapping(value = "api/secrequirement")
public class SecRequirementManagerRestServer {
  private static final Logger LOG = LoggerFactory.getLogger(SecRequirementManagerRestServer.class.getName());

  @Autowired
  private GASFInput secrequirementInput;
  @Value("${IP_GASF}")
  private String ipgasf;

  @PostMapping("import")
  @Permission(value = "Settings:Update")
  public void importGasfRequirement(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
    try {
      LOG.info("importGasfRequirement");
      final String uri = "http://" + ipgasf + "/gasf/getSecurityRequirements?full_data=true";

      RestTemplate restTemplate = new RestTemplate();
      String result = restTemplate.getForObject(uri, String.class);

      secrequirementInput.importGASFRequirementsFull(result);
    } catch (Exception e) {
      LOG.info("importGasfRequirement:: exception " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
