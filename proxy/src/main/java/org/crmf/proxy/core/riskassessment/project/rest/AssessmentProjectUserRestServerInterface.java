/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectUserRestServerInterface.java"
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import org.crmf.model.riskassessment.AssessmentProject;

public interface AssessmentProjectUserRestServerInterface {

    @POST
    @Consumes("application/json") 
	public void editAssessmentProjectUserRoles(AssessmentProject project);
}
