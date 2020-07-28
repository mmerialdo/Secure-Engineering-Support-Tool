/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureInputInterface.java"
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

import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.utility.GenericFilter;

public interface AssessmentProcedureInputInterface {

	String createAssessmentProcedure(AssessmentProcedure user, String projectIdentifier) throws Exception;

	void editAssessmentProcedure(AssessmentProcedure user) throws Exception;

	void deleteAssessmentProcedure(String identifier) throws Exception;

	List<AssessmentProcedure> loadAssessmentProcedure(GenericFilter filter) throws Exception;
    
    List<AssessmentProcedure> loadAssessmentProcedureList() throws Exception;
}
