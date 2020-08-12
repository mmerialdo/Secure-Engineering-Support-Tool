/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemProjectRequirementRestServer.java"
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

import org.crmf.core.riskassessment.project.requirement.SystemProjectRequirementInputInterface;
import org.crmf.model.requirement.Requirement;
import org.crmf.model.utility.GenericFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//This class manages the business logic behind the webservices related to the SystemProjectRequirements management
public class SystemProjectRequirementRestServer implements SystemProjectRequirementRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SystemProjectRequirementRestServer.class.getName());
  private SystemProjectRequirementInputInterface requirementInput;


  @Override
  public List<Requirement> loadProjectRequirement(GenericFilter filter) throws Exception {

    LOG.info("loadProjectRequirement Filter : {}", filter);
    try {
      return requirementInput.loadProjectRequirement(filter);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public List<Requirement> loadProjectRequirementByIds(List<String> ids) throws Exception {

    LOG.info("loadProjectRequirementByIds ids : {}", ids);
    try {
      return requirementInput.loadProjectRequirementByIds(ids);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  public SystemProjectRequirementInputInterface getRequirementInput() {
    return requirementInput;
  }

  public void setRequirementInput(SystemProjectRequirementInputInterface requirementInput) {
    this.requirementInput = requirementInput;
  }

}
