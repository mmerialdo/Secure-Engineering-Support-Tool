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

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.requirement.Requirement;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the RequirementMapper.xml (via the ibatis API)
public interface RequirementMapper {

  void insert(Requirement requirement);

  List<Requirement> getByIds(List<String> ids);

  List<Requirement> getBySysProject(@Param("sysprojectId") Integer sysProjectId);

  List<Requirement> getBySysProjectAndFile(@Param("sysprojectIdentifier") String sysProjectId, @Param("filename") String filename);

  List<String> getFilenameByProject(String sysProjectId);
}
