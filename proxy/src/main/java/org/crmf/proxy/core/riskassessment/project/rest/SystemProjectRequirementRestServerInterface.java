/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemProjectRequirementRestServerInterface.java"
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

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.crmf.model.requirement.Requirement;
import org.crmf.model.utility.GenericFilter;

public interface SystemProjectRequirementRestServerInterface {

	@POST
	@Produces("application/json")
	List<Requirement> loadProjectRequirement(GenericFilter filter) throws Exception;

	@POST
	@Produces("application/json")
    List<Requirement> loadProjectRequirementByIds(List<String> ids) throws Exception;
}
