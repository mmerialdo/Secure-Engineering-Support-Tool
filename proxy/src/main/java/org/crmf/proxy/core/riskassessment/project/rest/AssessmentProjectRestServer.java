/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectRestServer.java"
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

import com.google.gson.Gson;
import org.crmf.core.riskassessment.project.manager.AssessmentProjectInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.AuthToken;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

//This class manages the business logic behind the webservices related to the AssessmentProject management
@RestController
@RequestMapping(value = "api/project")
public class AssessmentProjectRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProjectRestServer.class.getName());
  @Autowired
  private AssessmentProjectInput projectInput;

  @PostMapping("create")
  @Permission(value = "AssessmentProject:Update")
  public ResponseMessage createAssessmentProject(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody AssessmentProject project) {
    String result = null;
    try {
      LOG.info("createAssessmentProject, project with name {}", project.getName());

      result = projectInput.createAssessmentProject(project);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
    if (result == null) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
    } else {
      return new ResponseMessage(result);
    }
  }

  @PostMapping("edit")
  @Permission(value = "AssessmentProject:Update")
  public void editAssessmentProject(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                    @RequestBody AssessmentProject project) {
    try {
      LOG.info("editAssessmentProject, project with name {}", project.getName());

      projectInput.editAssessmentProject(project);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("delete")
  @Permission(value = "AssessmentProject:Update")
  public ResponseMessage deleteAssessmentProject(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody String identifier) {
    try {
      LOG.info("deleteAssessmentProject, project with identifier {}", identifier);

      projectInput.deleteAssessmentProject(identifier);

      return new ResponseMessage(identifier);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @GetMapping("list")
  public List<AssessmentProject> loadAssessmentProjectList(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
    try {
      LOG.info("loadAssessmentProjectList token {}", token);
      Gson gson = new Gson();
      String decryptedtoken = new String(
        Base64.getDecoder().decode(token.getBytes()),
        StandardCharsets.UTF_8);
      AuthToken securityToken = gson.fromJson(decryptedtoken, AuthToken.class);
      LOG.info("loadAssessmentProjectList securityToken {}", securityToken);
      LOG.info("loadAssessmentProjectList securityToken {}", securityToken.getUsername());
      return projectInput.loadAssessmentProjectList(securityToken.getUsername(), PermissionTypeEnum.Read.name());
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }

  @PostMapping("load")
  @Permission(value = "AssessmentProject:Read")
  public AssessmentProject loadAssessmentProject(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                 @RequestBody GenericFilter filter) {
    try {
      LOG.info("loadAssessmentProject with identifier {}", filter.getFilterValue(GenericFilterEnum.PROJECT));

      AssessmentProject prj = projectInput
        .loadAssessmentProject(filter.getFilterValue(GenericFilterEnum.PROJECT));
      return prj;
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
    }
  }
}
