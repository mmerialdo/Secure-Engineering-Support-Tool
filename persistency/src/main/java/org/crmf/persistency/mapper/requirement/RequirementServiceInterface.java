/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.requirement;

import java.util.List;

import org.crmf.model.requirement.ProjectRequirement;
import org.crmf.model.requirement.Requirement;

public interface RequirementServiceInterface {

	public void insertSysRequirement(ProjectRequirement requirement, String sysprojectIdentifier);

	public void deleteSysRequirement(Requirement requirement);
	
	public List<Requirement> getByIds(List<String> ids);

	public List<Requirement> getBySysProject(String sysprojectIdentifier);

	public List<Requirement> getBySysProjectAndFile(String sysprojectIdentifier, String filename);
	
	public List<String> getFilenameByProject(String sysprojectIdentifier);
}
