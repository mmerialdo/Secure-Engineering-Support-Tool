/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AsstemplateServiceInterface.java"
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

import org.crmf.model.riskassessment.AssessmentTemplate;

import java.util.List;

public interface AsstemplateServiceInterface {

	String insert(AssessmentTemplate asstemplateDM, String profileIdentifier) throws Exception;

	void update(AssessmentTemplate asstemplateDM);

	void deleteCascade(String identifier);

	AssessmentTemplate getByIdentifier(String identifier);
	
	AssessmentTemplate getByIdentifierFull(String identifier);
	
	Integer getIdByIdentifier(String identifier);

	List<AssessmentTemplate> getByMethodology(String methodology);

	List<AssessmentTemplate> getByProfileIdentifier(String identifier);
	
	List<AssessmentTemplate> getAll();
}