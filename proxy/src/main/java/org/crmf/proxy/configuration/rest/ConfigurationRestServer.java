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
package org.crmf.proxy.configuration.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api")
public class ConfigurationRestServer {

  @Value("${session.timeout}")
  private String timeout;
  @Value("${session.timeout.warning}")
  private String timeoutWarning;
  @Value("${session.validation.interval}")
  private String timeoutValidationInterval;

  @PostMapping("configuration")
  public List<String> getConfiguration() {

    List<String> configuration = new ArrayList<>();
    configuration.add(timeout);
    configuration.add(timeoutWarning);
    configuration.add(timeoutValidationInterval);
    return configuration;
  }

  @PostMapping("ping")
  public void ping() {
  }
}
