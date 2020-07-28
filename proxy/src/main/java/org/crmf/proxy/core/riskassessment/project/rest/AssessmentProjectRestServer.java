/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectRestServer.java"
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;
import org.apache.camel.component.shiro.security.ShiroSecurityToken;
import org.crmf.core.riskassessment.project.manager.AssessmentProjectInputInterface;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the AssessmentProject management
public class AssessmentProjectRestServer implements AssessmentProjectRestServerInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AssessmentProjectRestServer.class.getName());

	private AssessmentProjectInputInterface projectInput;

	@Override
	public String createAssessmentProject(AssessmentProject project) throws Exception {
		try {
			LOG.info("createAssessmentProject, project with name " + project.getName());
		
			String result = projectInput.createAssessmentProject(project);
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
	public void editAssessmentProject(AssessmentProject project) throws Exception {
		try {
			LOG.info("editAssessmentProject, project with name " + project.getName());
		
			projectInput.editAssessmentProject(project);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public String deleteAssessmentProject(String identifier) throws Exception {
		try {
			LOG.info("deleteAssessmentProject, project with identifier " + identifier);
		
			projectInput.deleteAssessmentProject(identifier);
			
			return identifier;
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public List<AssessmentProject> loadAssessmentProjectList(String token) throws Exception {
		try{
		LOG.info("loadAssessmentProjectList token " + token);
			Gson gson = new Gson();
			String decryptedtoken = new String(
				Base64.getDecoder().decode(token.getBytes()),
				StandardCharsets.UTF_8);
			ShiroSecurityToken securityToken = gson.fromJson(decryptedtoken, ShiroSecurityToken.class);
			LOG.info("loadAssessmentProjectList securityToken " + securityToken);
			LOG.info("loadAssessmentProjectList securityToken " + securityToken.getUsername());
			return projectInput.loadAssessmentProjectList(securityToken.getUsername(), PermissionTypeEnum.Read.name());
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			throw new Exception("COMMAND_EXCEPTION", ex);
		}
	}

	@Override
	public AssessmentProject loadAssessmentProject(GenericFilter filter) throws Exception {
		try {
			LOG.info("loadAssessmentProject with identifier " + filter.getFilterValue(GenericFilterEnum.PROJECT));
		
			AssessmentProject prj = projectInput
					.loadAssessmentProject(filter.getFilterValue(GenericFilterEnum.PROJECT));
			return prj;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			throw new Exception("COMMAND_EXCEPTION", ex);
		}
	}

	public AssessmentProjectInputInterface getProjectInput() {
		return projectInput;
	}

	public void setProjectInput(AssessmentProjectInputInterface projectInput) {
		this.projectInput = projectInput;
	}
}
