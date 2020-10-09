/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ReportGeneratorRestServer.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.proxy.core.report.manager.rest;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.crmf.reportgenerator.manager.ReportGeneratorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/report")
public class ReportGeneratorRestServer {

  @Autowired
  private ReportGeneratorInput reportgeneratorInput;
  private static final Logger LOG = LoggerFactory.getLogger(ReportGeneratorRestServer.class.getName());

  @PostMapping("edit")
  @Permission("AssessmentProcedure:Read")
  public ResponseMessage editReport(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                    @RequestBody GenericFilter filter) {
    LOG.info("editReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE));
    try {
      String filename = reportgeneratorInput.editReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE));
      return new ResponseMessage(filename);
    } catch (Exception e) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("editLight")
  @Permission("AssessmentProcedure:Read")
  public ResponseMessage editLightReport(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                @RequestBody GenericFilter filter) {
    LOG.info("editLightReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE) + " " + ImpactEnum.valueOf(filter.getFilterValue(GenericFilterEnum.IMPACT)));
    try {
      String filename =  reportgeneratorInput.editLightReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE),
        ImpactEnum.valueOf(filter.getFilterValue(GenericFilterEnum.IMPACT)));
      return new ResponseMessage(filename);
    } catch (Exception e) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("editISO")
  @Permission("AssessmentProcedure:Read")
  public ResponseMessage editISOReport(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                              @RequestBody GenericFilter filter) {
    LOG.info("editISOReport " + filter.getFilterValue(GenericFilterEnum.PROCEDURE));
    try {
      String filename =  reportgeneratorInput.editISOReport(filter.getFilterValue(GenericFilterEnum.PROCEDURE));
      return new ResponseMessage(filename);
    } catch (Exception e) {
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }

  @PostMapping("load")
  @Permission("AssessmentProcedure:Read")
  public InputStreamResource downloadReport(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                           @RequestBody GenericFilter filter) throws Exception {
    return reportgeneratorInput.download(filter);
  }
}
