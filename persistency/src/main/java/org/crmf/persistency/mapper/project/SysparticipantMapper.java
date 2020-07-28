/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysparticipantMapper.java"
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

import java.util.ArrayList;

import org.crmf.persistency.domain.project.SysParticipant;

//This interface allows the bundle to invoke the SQL methods within the SysparticipantMapper.xml (via the ibatis API)
public interface SysparticipantMapper {
	
    public int insert(SysParticipant record);
    
	public void update(SysParticipant user);

	public void deleteByProjectId(Integer id);

	public ArrayList<SysParticipant> getByProjectId(Integer id);
}