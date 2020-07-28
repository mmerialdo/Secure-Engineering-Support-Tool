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

import java.util.List;

import org.crmf.persistency.domain.project.AssProject;

//This interface allows the bundle to invoke the SQL methods within the AssprojectMapper.xml (via the ibatis API)
public interface AssprojectMapper {

    public int insert(AssProject record);
    
	public void update(AssProject project);

	public void delete(Integer id);

	public void deleteByIdentifier(String identifier);

	public AssProject getById(Integer id);
	
	public AssProject getByIdentifier(String identifier);

	Integer getIdByIdentifier(String identifier);
	
	public AssProject getByProfileId(String profileId);
	
	public AssProject getByTemplateId(String templateId);
	
	public List<AssProject> getAll();
}