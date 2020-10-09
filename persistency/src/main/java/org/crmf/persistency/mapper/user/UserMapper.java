/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserMapper.java"
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
import org.crmf.persistency.domain.user.Sestuser;

import java.util.Date;
import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the UserMapper.xml (via the ibatis API)
@Mapper
public interface UserMapper {

  int insert(Sestuser user);

  int insertUserPswHistory(@Param("id") int id, @Param("password") String password);

  void updatePassword(@Param("id") int id, @Param("password") String password);

  void update(Sestuser user);

  void delete(Integer id);

  void deleteByIdentifier(String identifier);

  Sestuser getById(Integer userId);

  Sestuser getByIdentifier(String identifier);

  Sestuser getByUsername(String userName);

  Integer getIdByIdentifier(String identifier);

  List<Sestuser> getAll();

  void deleteUserPswByIdentifier(String identifier);

  void deleteUserPswById(Integer id);

  List<String> getOldUserPswByUserId(Integer id);

  Date getUserPswLastUpdate(Integer id);

}
