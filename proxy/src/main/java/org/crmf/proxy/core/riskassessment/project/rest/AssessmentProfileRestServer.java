/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfileRestServer.java"
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

import org.crmf.core.riskassessment.project.manager.AssessmentProfileInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//This class manages the business logic behind the webservices related to the AssessmentProfiles management
@RestController
@RequestMapping(value = "api/profile")
public class AssessmentProfileRestServer {
  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProfileRestServer.class.getName());
  @Autowired
  private AssessmentProfileInput profileInput;

  @PostMapping("create")
  @Permission(value = "AssessmentProfile:Update")
  public ResponseMessage createAssessmentProfile(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody AssessmentProfile profile) {
    String profileIdentifier = null;
    try {
      LOG.info("createAssessmentProfile, profile with name {}", profile.getName());

      profileIdentifier = profileInput.createAssessmentProfile(profile);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
    if (profileIdentifier == null) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
    } else {
      return new ResponseMessage(profileIdentifier);
    }
  }

  @PostMapping("edit")
  @Permission(value = "AssessmentProfile:Update")
  public void editAssessmentProfile(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                    @RequestBody AssessmentProfile profile) {
    try {
      LOG.info("editAssessmentProfile, profile with name {}", profile.getName());

      profileInput.editAssessmentProfile(profile);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("delete")
  @Permission(value = "AssessmentProfile:Update")
  public ResponseMessage deleteAssessmentProfile(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody String identifier) {
    try {
      LOG.info("deleteAssessmentProfile, profile with identifier {}", identifier);

      profileInput.deleteAssessmentProfile(identifier);

      return new ResponseMessage(identifier);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @GetMapping("list")
  @Permission(value = "AssessmentProfile:Read")
  public List<AssessmentProfile> loadAssessmentProfileList(
    @RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
    try {
      LOG.info("loadAssessmentProfileList {}", token);
      return profileInput.loadAssessmentProfileList();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("load")
  @Permission(value = "AssessmentProfile:Read")
  public AssessmentProfile loadAssessmentProfile(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody GenericFilter filter) {
    try {
      LOG.info("loadAssessmentProfile with identifier {}", filter.getFilterValue(GenericFilterEnum.IDENTIFIER));

      return profileInput.loadAssessmentProfile(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }
}
