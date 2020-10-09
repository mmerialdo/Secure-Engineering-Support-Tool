/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateInput.java"
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AssessmentProjectUserInputTest {
  @Mock
  private UserPermissionManagerInput permissionManager;
  @InjectMocks
  private AssessmentProjectUserInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editAssessmentProjectRole() throws Exception {

    AssessmentProject project = new AssessmentProject();

    manager.editAssessmentProjectRole(project);

    verify(permissionManager, times(1)).updatePermissionOnRoleChange(project);
  }
}
