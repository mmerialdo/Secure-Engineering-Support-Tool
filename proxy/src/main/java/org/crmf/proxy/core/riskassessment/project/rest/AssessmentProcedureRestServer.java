/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureRestServer.java"
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

import org.crmf.core.riskassessment.project.manager.AssessmentProcedureInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
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

//This class manages the business logic behind the webservices related to the AssessmentProcedures management
@RestController
@RequestMapping(value = "api/procedure")
public class AssessmentProcedureRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProcedureRestServer.class.getName());
  @Autowired
  private AssessmentProcedureInput procedureInput;

  @PostMapping("create")
  @Permission(value = "AssessmentProcedure:Update")
  public ResponseMessage createAssessmentProcedure(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                   @RequestBody AssessmentProject procedurePrj) {
    String procedureIdentifier = null;
    try {
      LOG.info("createAssessmentProcedure, procedure with name {}", procedurePrj.getProcedures().get(0),
        procedurePrj.getName());
      procedureIdentifier = procedureInput.createAssessmentProcedure(procedurePrj.getProcedures().get(0),
        procedurePrj.getIdentifier());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
    if (procedureIdentifier == null) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
    } else {
      return new ResponseMessage(procedureIdentifier);
    }
  }

  @PostMapping("edit")
  @Permission(value = "AssessmentProcedure:Update")
  public void editAssessmentProcedure(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                      @RequestBody AssessmentProcedure procedure) {
    try {
      LOG.info("editAssessmentProject, project with name {}", procedure.getName());

      procedureInput.editAssessmentProcedure(procedure);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("delete")
  @Permission(value = "AssessmentProcedure:Update")
  public ResponseMessage deleteAssessmentProcedure(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                   @RequestBody String identifier) {
    try {
      LOG.info("deleteAssessmentProcedure, procedure with identifier {}", identifier);

      procedureInput.deleteAssessmentProcedure(identifier);

      return new ResponseMessage(identifier);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }

  }

  @GetMapping("list")
  @Permission(value = "AssessmentProcedure:Read")
  public List<AssessmentProcedure> loadAssessmentProcedureList(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {

    try {
      return procedureInput.loadAssessmentProcedureList();
    } catch (Exception e) {
      LOG.error("Exception in loadAssessmentProcedureList: " + e.getMessage(), e);
    }
    return null;
  }

  @PostMapping("load")
  @Permission(value = "AssessmentProcedure:Read")
  public List<String> loadAssessmentProcedure(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                              @RequestBody GenericFilter filter) {
    List<String> jsonProcedures = new ArrayList<>();

    LOG.info("loadAssessmentProcedure {}", filter);
    try {
      List<AssessmentProcedure> procedures = procedureInput.loadAssessmentProcedure(filter);
      LOG.info("loadAssessmentProcedure procedures size: {}", procedures.size());

      if (procedures != null) {
        for (AssessmentProcedure p : procedures)
          jsonProcedures.add(p.convertToJson());
      }
      LOG.info("loadAssessmentProcedure procedures after convert");
      return jsonProcedures;
    } catch (Exception e) {
      LOG.error("Exception in loadAssessmentProcedure: " + e.getMessage(), e);
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
