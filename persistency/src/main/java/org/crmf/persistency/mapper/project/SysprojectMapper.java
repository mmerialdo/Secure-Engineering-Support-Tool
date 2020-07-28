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

import java.util.List;

import org.crmf.persistency.domain.project.SysProject;

//This interface allows the bundle to invoke the SQL methods within the SysprojectMapper.xml (via the ibatis API)
public interface SysprojectMapper {

    public int insert(SysProject record);
    
	public void update(SysProject project);

	public void delete(SysProject project);

	public void deleteByAssprojectIdentifier(String identifier);

	public SysProject getById(Integer id);

	public Integer getIdByIdentifier(String identifier);
	
	public SysProject getByAssprojectId(String id);

	public Integer getIdByAssprojectIdentifier(String identifier);

	public List<SysProject> getAll();
}