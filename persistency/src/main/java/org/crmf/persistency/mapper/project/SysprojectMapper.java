/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysprojectMapper.java"
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

import org.apache.ibatis.annotations.Mapper;
import org.crmf.persistency.domain.project.SysProject;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the SysprojectMapper.xml (via the ibatis API)
@Mapper
public interface SysprojectMapper {

  int insert(SysProject record);

  void update(SysProject project);

  void delete(SysProject project);

  void deleteByAssprojectIdentifier(String identifier);

  SysProject getById(Integer id);

  Integer getIdByIdentifier(String identifier);

  SysProject getByAssprojectId(String id);

  Integer getIdByAssprojectIdentifier(String identifier);

  List<SysProject> getAll();
}