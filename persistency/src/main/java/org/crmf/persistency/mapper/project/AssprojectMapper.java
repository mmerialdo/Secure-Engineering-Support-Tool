/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprofileServiceInterface.java"
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
import org.crmf.persistency.domain.project.AssProject;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AssprojectMapper.xml (via the ibatis API)
@Mapper
public interface AssprojectMapper {

  int insert(AssProject record);

  void update(AssProject project);

  void delete(Integer id);

  void deleteByIdentifier(String identifier);

  AssProject getById(Integer id);

  AssProject getByIdentifier(String identifier);

  Integer getIdByIdentifier(String identifier);

  AssProject getByProfileId(String profileId);

  AssProject getByTemplateId(String templateId);

  List<AssProject> getAll();
}