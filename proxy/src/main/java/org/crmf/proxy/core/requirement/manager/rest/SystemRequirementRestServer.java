/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirementRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.requirement.manager.rest;

import org.crmf.core.riskassessment.project.requirement.SystemProjectRequirementInput;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.requirement.Requirement;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.requirementimport.processor.SystemRequirementExcelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

//This class manages the webservices invoked by the SEST Client
@RestController
@RequestMapping(value = "api/sysrequirement")
public class SystemRequirementRestServer {

  @Autowired
  private SystemProjectRequirementInput requirementInput;
  @Autowired
  private SystemRequirementExcelProcessor processor;
  private static final Logger LOG = LoggerFactory.getLogger(SystemRequirementRestServer.class.getName());

  @PostMapping("filename/list")
  @Permission("AssessmentProject:Update")
  public List<String> listRequirementLoadedFile(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                @RequestBody GenericFilter filter) throws Exception {

    LOG.info("Loading filenames " + filter.getFilterValue(GenericFilterEnum.SYS_PROJECT));
    List<String> filenames = processor.listRequirementLoadedFile(filter);
    // wrap filenames with "" because of gson which didn't convert it otherwise
    List<String> filenamesToJSon = new ArrayList<>();
    for (String string : filenames) {
      filenamesToJSon.add("\"".concat(string).concat("\""));
    }
    LOG.info("Loading filenames " + filenamesToJSon.size());
    return filenamesToJSon;
  }

  @PostMapping("upload")
  @Permission("AssessmentProject:Update")
  public String uploadRequirement(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                           @RequestParam("file") MultipartFile multipartFile,
                                           @RequestParam("filename") String filename,
                                           @RequestParam("sysprojectIdentifier") String sysprojectIdentifier) {
    try {
      String itemNumber = processor.process(multipartFile.getInputStream(), sysprojectIdentifier, filename);
      return itemNumber;
    } catch (Exception e) {
      LOG.error("=========== uploadRequirement error " + e.getMessage());
    }
    return null;
  }

  @PostMapping("load")
  @Permission(value = "AssessmentProject:Read")
  public List<Requirement> loadProjectRequirement(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                                  @RequestBody GenericFilter filter) {

    LOG.info("loadProjectRequirement Filter : {}", filter);
    try {
      return requirementInput.loadProjectRequirement(filter);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, e);
    }
  }
}
