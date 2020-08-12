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

import org.crmf.persistency.domain.project.SysParticipant;

import java.util.ArrayList;

//This interface allows the bundle to invoke the SQL methods within the SysparticipantMapper.xml (via the ibatis API)
public interface SysparticipantMapper {

  int insert(SysParticipant record);

  void update(SysParticipant user);

  void deleteByProjectId(Integer id);

  ArrayList<SysParticipant> getByProjectId(Integer id);
}