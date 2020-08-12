/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfileRestServer.java"
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

import org.crmf.core.riskassessment.project.manager.AssessmentProfileInputInterface;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//This class manages the business logic behind the webservices related to the AssessmentProfiles management
public class AssessmentProfileRestServer implements AssessmentProfileRestServerInterface {
  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProfileRestServer.class.getName());

  private AssessmentProfileInputInterface profileInput;

  @Override
  public String createAssessmentProfile(AssessmentProfile profile) throws Exception {
    try {
      LOG.info("createAssessmentProfile, profile with name {}", profile.getName());

      String result = profileInput.createAssessmentProfile(profile);
      if (result == null) {
        throw new Exception("COMMAND_EXCEPTION");
      } else {
        return result;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public void editAssessmentProfile(AssessmentProfile profile) throws Exception {
    try {
      LOG.info("editAssessmentProfile, profile with name {}", profile.getName());

      profileInput.editAssessmentProfile(profile);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public String deleteAssessmentProfile(String identifier) throws Exception {
    try {
      LOG.info("deleteAssessmentProfile, profile with identifier {}", identifier);

      profileInput.deleteAssessmentProfile(identifier);

      return identifier;

    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public List<AssessmentProfile> loadAssessmentProfileList(String token) throws Exception {
    try {
      LOG.info("loadAssessmentProfileList {}", token);
      return profileInput.loadAssessmentProfileList();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  @Override
  public AssessmentProfile loadAssessmentProfile(GenericFilter filter, String token) throws Exception {
    try {
      LOG.info("loadAssessmentProfile with identifier {}", filter.getFilterValue(GenericFilterEnum.IDENTIFIER));

      return profileInput.loadAssessmentProfile(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new Exception("COMMAND_EXCEPTION", ex);
    }
  }

  public AssessmentProfileInputInterface getProfileInput() {
    return profileInput;
  }

  public void setProfileInput(AssessmentProfileInputInterface profileInput) {
    this.profileInput = profileInput;
  }
}
