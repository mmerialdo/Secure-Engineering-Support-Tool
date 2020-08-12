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

import org.crmf.core.riskassessment.project.manager.AssessmentProjectUserInputInterface;
import org.crmf.model.riskassessment.AssessmentProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the AssessmentProject User Role management
public class AssessmentProjectUserRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProjectUserRestServer.class.getName());

  private AssessmentProjectUserInputInterface projectUserInput;

  public void editAssessmentProjectUserRoles(AssessmentProject project) {

    try {
      projectUserInput.editAssessmentProjectRole(project);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }

  public AssessmentProjectUserInputInterface getProjectUserInput() {
    return projectUserInput;
  }

  public void setProjectUserInput(AssessmentProjectUserInputInterface projectUserInput) {
    this.projectUserInput = projectUserInput;
  }

}
