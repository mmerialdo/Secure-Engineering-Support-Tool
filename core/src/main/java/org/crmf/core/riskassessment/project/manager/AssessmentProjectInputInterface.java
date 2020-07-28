/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectInputInterface.java"
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

import org.crmf.model.riskassessment.AssessmentProject;

public interface AssessmentProjectInputInterface {

	String createAssessmentProject(AssessmentProject user) throws Exception;

	void editAssessmentProject(AssessmentProject user) throws Exception;

	void deleteAssessmentProject(String identifier) throws Exception;

	AssessmentProject loadAssessmentProject(String identifier) throws Exception;
    
    List<AssessmentProject> loadAssessmentProjectList(String token, String permission) throws Exception;
}
