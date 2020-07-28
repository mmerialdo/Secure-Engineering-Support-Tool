/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateRestServer.java"
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

import java.util.ArrayList;
import java.util.List;

import org.crmf.core.riskassessment.project.manager.AssessmentTemplateInputInterface;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the AssessmentTemplate management
public class AssessmentTemplateRestServer implements AssessmentTemplateRestServerInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentTemplateRestServer.class.getName());
	private AssessmentTemplateInputInterface templateInput;

	@Override
	public String createAssessmentTemplate(AssessmentProfile profile) throws Exception {
		try {
			LOG.info("createAssessmentTemplate, template with name " + profile.getTemplates().get(0).getName());
		
			String result = templateInput.createAssessmentTemplate(profile.getTemplates().get(0), profile.getIdentifier());
			if(result == null){
				throw new Exception("COMMAND_EXCEPTION");
			}
			else{
				return result;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public List<AssessmentTemplate> loadAssessmentTemplateList(String token) throws Exception {

		try {
			return templateInput.loadAssessmentTemplateList();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public List<String> loadAssessmentTemplate(GenericFilter filter, String token) throws Exception {
		List<String> jsonTemplates = new ArrayList<String>();

		LOG.info("loadAssessmentTemplate "+filter);
		try {
			List<AssessmentTemplate> templates = templateInput.loadAssessmentTemplate(filter);
			LOG.info("loadAssessmentTemplate templates size: "+templates.size());

			for (AssessmentTemplate t : templates) {
				jsonTemplates.add(t.convertToJson());
			}
			LOG.info("loadAssessmentTemplate templates after convert");
			return jsonTemplates;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	public AssessmentTemplateInputInterface getTemplateInput() {
		return templateInput;
	}

	public void setTemplateInput(AssessmentTemplateInputInterface TemplateInput) {
		this.templateInput = TemplateInput;
	}
}
