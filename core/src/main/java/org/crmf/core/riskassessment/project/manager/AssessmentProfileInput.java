/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfileInput.java"
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssessmentProfiles
@Service
public class AssessmentProfileInput {
	
	private static final Logger LOG = LoggerFactory.getLogger(AssessmentProfileInput.class.getName());
	@Autowired
	@Qualifier("default")
	private AssprofileServiceInterface assprofileService;
	@Autowired
	private UserPermissionManagerInput permissionManager;
	
	public String createAssessmentProfile(AssessmentProfile profile) throws Exception {
		
		LOG.info("createAssessmentProfile with identifier: {}", profile.getIdentifier());
		String profileId = assprofileService.insert(profile);
		
		//set type and identifier accordingly to the sest object just created (user)
		profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
		profile.setIdentifier(profileId);

		return profileId;
	}

	public void editAssessmentProfile(AssessmentProfile profile) {

		LOG.info("editAssessmentProfile with identifier: {}", profile.getIdentifier());
		assprofileService.update(profile);
	}

	public void deleteAssessmentProfile(String identifier) {

		LOG.info("deleteAssessmentProfile with identifier: {}", identifier);
		assprofileService.deleteCascade(identifier);
	}

	public AssessmentProfile loadAssessmentProfile(String identifier) {

		LOG.info("loadAssessmentProfile with identifier: {}", identifier);
		return assprofileService.getByIdentifier(identifier);
	}

	public List<AssessmentProfile> loadAssessmentProfileList() {

		LOG.info("loadAssessmentProfileList ");
		return assprofileService.getAll();
	}
}
