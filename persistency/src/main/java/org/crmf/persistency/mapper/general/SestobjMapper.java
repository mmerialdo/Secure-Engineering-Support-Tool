/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestobjMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.general;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.general.Sestobj;

//This interface allows the bundle to invoke the SQL methods within the SestobjMapper.xml (via the ibatis API)
public interface SestobjMapper {

	int insert(Sestobj record);
	
	int insertWithIdentifier(Sestobj record);

	void updateLock(@Param("sestobjId") String sestobjId, @Param("lockedBy") String lockedBy);

	void delete(Integer sestobjId);

	void deleteByIdentifier(String identifier);

	Sestobj getById(Integer sestobjId);

	Sestobj getByIdentifier(String identifier);

	int getIdByIdentifier(String identifier);
	
	List<Sestobj> getAll();
}