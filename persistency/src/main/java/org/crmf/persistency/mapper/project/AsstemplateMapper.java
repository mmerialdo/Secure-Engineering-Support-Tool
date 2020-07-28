/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AsstemplateMapper.java"
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

import org.crmf.persistency.domain.project.AssTemplate;

//This interface allows the bundle to invoke the SQL methods within the AsstemplateMapper.xml (via the ibatis API)
public interface AsstemplateMapper {

    public int insert(AssTemplate record);

    public void insertProfileAssoc(AssTemplate record);
    
    public void deleteProfileAssoc(AssTemplate record);
    
	public void update(AssTemplate user);

	public void delete(Integer id);

	public void deleteByIdentifier(String identifier);

	public AssTemplate getByIdentifier(String identifier);
	
	public AssTemplate getById(Integer id);

	public Integer getIdByIdentifier(String identifier);

	public List<AssTemplate> getAll();
	
	public List<AssTemplate> getByMethodology(String methodology);
	
	public List<AssTemplate> getByProfileIdentifier(String identifier);
}