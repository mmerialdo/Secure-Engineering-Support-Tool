/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateRestServer.java"
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

import org.crmf.core.riskassessment.project.manager.AssessmentTemplateInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;
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

import java.util.ArrayList;
import java.util.List;

//This class manages the business logic behind the webservices related to the AssessmentTemplate management
@RestController
@RequestMapping(value = "api/template")
public class AssessmentTemplateRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentTemplateRestServer.class.getName());
  @Autowired
  private AssessmentTemplateInput templateInput;

  @PostMapping("create")
  @Permission(value = "AssessmentTemplate:Update")
  public ResponseMessage createAssessmentTemplate(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                  @RequestBody AssessmentProfile profile) {
    String identifier = null;
    try {
      LOG.info("createAssessmentTemplate, template with name {}", profile.getTemplates().get(0).getName());

      identifier = templateInput.createAssessmentTemplate(profile.getTemplates().get(0), profile.getIdentifier());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
    if (identifier == null) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
    } else {
      return new ResponseMessage(identifier);
    }
  }

  @GetMapping("list")
  @Permission(value = "AssessmentTemplate:Read")
  public List<AssessmentTemplate> loadAssessmentTemplateList(
    @RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {

    try {
      return templateInput.loadAssessmentTemplateList();
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("load")
  @Permission(value = "AssessmentTemplate:Read")
  public List<String> loadAssessmentTemplate(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                             @RequestBody GenericFilter filter) {
    List<String> jsonTemplates = new ArrayList<>();

    LOG.info("loadAssessmentTemplate {}", filter);
    try {
      List<AssessmentTemplate> templates = templateInput.loadAssessmentTemplate(filter);
      LOG.info("loadAssessmentTemplate templates size: {}", templates.size());

      for (AssessmentTemplate t : templates) {
        jsonTemplates.add(t.convertToJson());
      }
      LOG.info("loadAssessmentTemplate templates after convert");
      return jsonTemplates;
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
