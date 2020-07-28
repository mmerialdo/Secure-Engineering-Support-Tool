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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.user.Sestuser;

//This interface allows the bundle to invoke the SQL methods within the UserMapper.xml (via the ibatis API)
public interface UserMapper {
	
	public int insert(Sestuser user);

	public int insertUserPswHistory(@Param("id") int id, @Param("password") String password);
	
	public void updatePassword(@Param("id") int id, @Param("password") String password);

	public void update(Sestuser user);

	public void delete(Integer id);

	public void deleteByIdentifier(String identifier);

	public Sestuser getById(Integer userId);

	public Sestuser getByIdentifier(String identifier);

	public Sestuser getByUsername(String userName);
	
	public Integer getIdByIdentifier(String identifier);

	public List<Sestuser> getAll();

	public void deleteUserPswByIdentifier(String identifier);

	public void deleteUserPswById(Integer id);
	
	public List<String> getOldUserPswByUserId(Integer id);
	
	public Date getUserPswLastUpdate(Integer id);
	
}
