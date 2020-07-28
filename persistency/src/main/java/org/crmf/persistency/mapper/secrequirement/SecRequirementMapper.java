/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.secrequirement;

import java.util.List;

import org.crmf.persistency.domain.secrequirement.SecRequirement;
import org.crmf.persistency.domain.secrequirement.SecRequirementSafeguard;

//This interface allows the bundle to invoke the SQL methods within the SecRequirementMapper.xml (via the ibatis API)
public interface SecRequirementMapper {
	
	void insert(SecRequirement requirement);

	void insertSafeguardAssoc(SecRequirementSafeguard requirementSafeguard);

	List<SecRequirementSafeguard> getRequirementsAssocBySafeguard(int safeguardId);

	SecRequirement getSecRequirementById(Integer id);

	SecRequirement getSecRequirementByReqId(String gasfId);
	
	SecRequirement getSecRequirementByIdentifier(String identifier);

	void deleteSecRequirementAssoc();

	void deleteSecRequirement();
}
