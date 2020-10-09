/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RoleMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.user.Role;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the RoleMapper.xml (via the ibatis API)
@Mapper
public interface RoleMapper {

  int insert(Role role);

  void delete(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

  List<Role> getByUserId(@Param("userId") Integer userId);

  List<Role> getByUserIdentifierAndProjectIdentifier(@Param("userId") String userId, @Param("projectId") String projectId);
}
