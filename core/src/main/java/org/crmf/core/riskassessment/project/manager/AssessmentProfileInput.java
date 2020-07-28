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

import java.util.List;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.persistency.mapper.project.AssprofileServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssessmentProfiles
public class AssessmentProfileInput implements AssessmentProfileInputInterface {
	
	private static final Logger LOG = LoggerFactory.getLogger(AssessmentProfileInput.class.getName());
	private AssprofileServiceInterface assprofileService;
	private UserPermissionManagerInputInterface permissionManager;
	
	
	@Override
	public String createAssessmentProfile(AssessmentProfile profile) throws Exception {
		
		LOG.info("createAssessmentProfile with identifier: " + profile.getIdentifier());
		String profileId = assprofileService.insert(profile);
		
		//set type and identifier accordingly to the sest object just created (user)
		profile.setObjType(SESTObjectTypeEnum.AssessmentProfile);
		profile.setIdentifier(profileId);

		return profileId;
	}

	@Override
	public void editAssessmentProfile(AssessmentProfile profile) throws Exception {

		LOG.info("editAssessmentProfile with identifier: " + profile.getIdentifier());
		assprofileService.update(profile);
	}

	@Override
	public void deleteAssessmentProfile(String identifier) throws Exception {

		LOG.info("deleteAssessmentProfile with identifier: " + identifier);
		assprofileService.deleteCascade(identifier);
	}

	@Override
	public AssessmentProfile loadAssessmentProfile(String identifier) throws Exception {

		LOG.info("loadAssessmentProfile with identifier: "+identifier);
		return assprofileService.getByIdentifier(identifier);
	}

	@Override
	public List<AssessmentProfile> loadAssessmentProfileList() throws Exception {

		LOG.info("loadAssessmentProfileList ");
		return assprofileService.getAll();
	}

	public AssprofileServiceInterface getAssprofileService() {
		return assprofileService;
	}

	public void setAssprofileService(AssprofileServiceInterface assprofileService) {
		this.assprofileService = assprofileService;
	}
	
	public UserPermissionManagerInputInterface getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(UserPermissionManagerInputInterface permissionManager) {
		this.permissionManager = permissionManager;
	}
	
}
