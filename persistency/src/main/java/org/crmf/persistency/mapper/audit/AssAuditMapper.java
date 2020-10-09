/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.crmf.model.audit.SestAuditModel;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AssAuditMapper.xml (via the ibatis API)
@Mapper
public interface AssAuditMapper {

  int insert(SestAuditModel audit);

  void update(SestAuditModel audit);

  void delete(Integer id);

  void deleteByIdentifier(String identifier);

  List<SestAuditModel> getAllForProject(String identifier);

  SestAuditModel getByProjectAndType(@Param("identifier") String identifier, @Param("type") String type);

  Integer getIdByIdentifier(String identifier);

  Integer getProjectIdByIdentifier(String identifier);

}
