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

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.persistency.mapper.project.AssprofileServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentProfileInputTest {
  @Mock
  private AssprofileServiceInterface assprofileService;
  @Mock
  private UserPermissionManagerInput permissionManager;
  @InjectMocks
  private AssessmentProfileInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createAssessmentProfile() throws Exception {

    AssessmentProfile profile = new AssessmentProfile();
    profile.setName("profileName");

    when(assprofileService.insert(profile)).thenReturn("profileIdentifier");

    manager.createAssessmentProfile(profile);

    profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
    profile.setIdentifier("profileIdentifier");
    verify(assprofileService, times(1)).insert(profile);
  }

  @Test
  public void editAssessmentProfile() throws Exception {

    AssessmentProfile profile = new AssessmentProfile();
    profile.setName("profileName");
    profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
    profile.setIdentifier("profileIdentifier");

    manager.editAssessmentProfile(profile);

    verify(assprofileService, times(1)).update(profile);
  }

  @Test
  public void deleteAssessmentProfile() throws Exception {

    String profileIdentifier = "profileIdentifier";

    manager.deleteAssessmentProfile(profileIdentifier);

    verify(assprofileService, times(1)).deleteCascade(profileIdentifier);
  }

  @Test
  public void loadAssessmentProfile() throws Exception {

    String profileIdentifier = "profileIdentifier";

    manager.loadAssessmentProfile(profileIdentifier);

    verify(assprofileService, times(1)).getByIdentifier(profileIdentifier);
  }

  @Test
  public void loadAssessmentProfileList() throws Exception {

    manager.loadAssessmentProfileList();

    verify(assprofileService, times(1)).getAll();
  }
}
