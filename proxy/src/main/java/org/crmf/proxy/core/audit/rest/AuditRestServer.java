/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.audit.rest;

import org.crmf.core.audit.AuditInput;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//This class manages the business logic behind the webservices related to the Audits management

@RestController
@RequestMapping(value = "api/audit")
public class AuditRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AuditRestServer.class.getName());

  @Autowired
  private AuditInput auditInput;

  @PostMapping("edit")
  @Permission(value = "Audit:Update")
  public void editAudit(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                        @RequestBody ModelObject audit) {
    try {
      auditInput.editAudit(audit);
    } catch (Exception e) {
      LOG.error("editAuditList " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("load")
  @Permission(value = "Audit:Read")
  public SestAuditModel loadAudit(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                  @RequestBody GenericFilter filter) {

    try {
      return auditInput.loadAudit(filter.getFilterValue(GenericFilterEnum.IDENTIFIER), AuditTypeEnum.SECURITY, false);
    } catch (Exception e) {
      LOG.error("loadAudit " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("questionnaireSafeguard/load")
  @Permission(value = "Audit:Read")
  public List<Question> loadQuestionnaireSafeguard(
    @RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
    try {
      return auditInput.loadQuestionnaireSafeguard();
    } catch (Exception e) {
      LOG.error("loadQuestionnaire " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("questionnairejson/load")
  @Permission(value = "Audit:Read")
  public ModelObject loadQuestionnaireJson(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                           @RequestBody GenericFilter filter) {
    try {
      return auditInput.loadQuestionnaire(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));
    } catch (Exception e) {
      LOG.error("loadQuestionnaireJson " + e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
