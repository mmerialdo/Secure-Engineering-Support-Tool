/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfileInputInterface.java"
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

import org.crmf.model.riskassessment.AssessmentProfile;

public interface AssessmentProfileInputInterface {

	String createAssessmentProfile(AssessmentProfile user) throws Exception;

	void editAssessmentProfile(AssessmentProfile user) throws Exception;

	void deleteAssessmentProfile(String identifier) throws Exception;

	AssessmentProfile loadAssessmentProfile(String identifier) throws Exception;
    
    List<AssessmentProfile> loadAssessmentProfileList() throws Exception;
}
