/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprofileMapper.java"
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

import org.crmf.persistency.domain.project.AssProfile;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AssprofileMapper.xml (via the ibatis API)
public interface AssprofileMapper {

    public int insert(AssProfile record);
    
	public void delete(Integer id);

	public void deleteByIdentifier(String identifier);

	public AssProfile getById(Integer id);
	
	public AssProfile getByIdentifier(String identifier);

	public Integer getIdByIdentifier(String identifier);
	
	public List<AssProfile> getAll();
}