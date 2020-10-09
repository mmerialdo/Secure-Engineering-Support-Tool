/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectUserInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.project.manager;

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the Users
@Service
public class AssessmentProjectUserInput {

  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProjectUserInput.class.getName());
  @Autowired
  private UserPermissionManagerInput permissionManager;

  public void editAssessmentProjectRole(AssessmentProject project) throws Exception {

    LOG.info("saveAssessmentProjectRole project identifier: {}", project.getIdentifier());
    permissionManager.updatePermissionOnRoleChange(project);
  }
}

