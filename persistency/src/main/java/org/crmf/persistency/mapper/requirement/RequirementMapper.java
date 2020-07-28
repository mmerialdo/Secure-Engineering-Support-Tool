/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementMapper.java"
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

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.requirement.Requirement;

//This interface allows the bundle to invoke the SQL methods within the RequirementMapper.xml (via the ibatis API)
public interface RequirementMapper {
	
	public void insert(Requirement requirement);

	public List<Requirement> getByIds(List<String> ids);

	public List<Requirement> getBySysProject(@Param("sysprojectId")Integer sysProjectId);
	
	public List<Requirement> getBySysProjectAndFile(@Param("sysprojectIdentifier") String sysProjectId, @Param("filename") String filename);
	
	public List<String> getFilenameByProject(String sysProjectId);
}
