/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.project.requirement;

import java.util.List;

import org.crmf.core.riskassessment.project.manager.AssessmentTemplateInput;
import org.crmf.model.requirement.Requirement;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.requirement.RequirementServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the ProjectRequirements
public class SystemProjectRequirementInput implements SystemProjectRequirementInputInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentTemplateInput.class.getName());
	private RequirementServiceInterface requirementService;
	
	@Override
	public List<Requirement> loadProjectRequirement(GenericFilter filter) throws Exception {
		//system project identifier
		String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
		LOG.info("system project identifier "+identifier);
		return requirementService.getBySysProject(identifier);
	}

	@Override
	public List<Requirement> loadProjectRequirementByIds(List<String> ids) throws Exception {

		return requirementService.getByIds(ids);
	}

	public RequirementServiceInterface getRequirementService() {
		return requirementService;
	}

	public void setRequirementService(RequirementServiceInterface requirementService) {
		this.requirementService = requirementService;
	}

}
