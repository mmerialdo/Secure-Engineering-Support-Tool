/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprocedureServiceInterface.java"
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

import java.util.List;

import org.crmf.model.riskassessment.AssessmentProcedure;

public interface AssprocedureServiceInterface {

	AssessmentProcedure insert(AssessmentProcedure assprocedureDM, String projectIdentifier) throws Exception;

	void update(AssessmentProcedure assprocedureDM);
	
	void deleteCascade(String identifier);

	AssessmentProcedure getByIdentifier(String identifier);
	
	AssessmentProcedure getByIdentifierFull(String identifier);
	
	AssessmentProcedure getByAssetModelIdentifier(String identifier);
	
	AssessmentProcedure getByVulnerabilityModelIdentifier(String identifier);
	
	AssessmentProcedure getByThreatModelIdentifier(String identifier);
	
	AssessmentProcedure getByRiskModelIdentifier(String identifier);
	
	AssessmentProcedure getByRiskTreatmentModelIdentifier(String riskTreatmentModelIdentifier);

	AssessmentProcedure getBySafeguardModelIdentifier(String identifier);
	
	List<AssessmentProcedure> getByProjectIdentifier(String projectIdentifier);

	int getProjectdIdByIdentifier(String identifier);

	List<AssessmentProcedure> getAll();

}