/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprojectServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.project;

import org.crmf.model.riskassessment.AssessmentProject;

import java.util.List;

public interface AssprojectServiceInterface {

	String insert(AssessmentProject assprojectDM) throws Exception;
	
	void update(AssessmentProject assprojectDM);

	void deleteCascade(String identifier);

	AssessmentProject getByIdentifier(String identifier);

	AssessmentProject getByIdentifierFull(String identifier);
	
	Integer getIdByIdentifier(String identifier);

	AssessmentProject getById(int id);
	
	List<AssessmentProject> getAll();

}