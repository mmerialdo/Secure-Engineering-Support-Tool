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

package org.crmf.core.riskassessment.project.manager;

import java.util.List;

import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;

public interface AssessmentTemplateInputInterface {

	String createAssessmentTemplate(AssessmentTemplate user, String profileIdentifier) throws Exception;

//	void editAssessmentTemplate(GenericFilter filter) throws Exception;
//
//	void deleteAssessmentTemplate(String identifier) throws Exception;

	List<AssessmentTemplate> loadAssessmentTemplate(GenericFilter filter) throws Exception;
    
    List<AssessmentTemplate> loadAssessmentTemplateList() throws Exception;
    
    AssessmentTemplate loadAssessmentTemplateByIdentifier(String identifier) throws Exception;
}
