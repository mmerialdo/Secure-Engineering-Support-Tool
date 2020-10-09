/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectUserRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.riskassessment.project.rest;

import org.crmf.core.riskassessment.project.manager.AssessmentProjectUserInput;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.proxy.authnauthz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//This class manages the business logic behind the webservices related to the AssessmentProject User Role management
@RestController
@RequestMapping(value = "api/project/users")
public class AssessmentProjectUserRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProjectUserRestServer.class.getName());
  @Autowired
  private AssessmentProjectUserInput projectUserInput;

  @PostMapping("edit")
  @Permission(value = "AssessmentProject:Update")
  public void editAssessmentProjectUserRoles(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                             @RequestBody AssessmentProject project) {

    try {
      projectUserInput.editAssessmentProjectRole(project);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }
}
