/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.safeguard;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;

//This interface allows the bundle to invoke the SQL methods within the SafeguardMapper.xml (via the ibatis API)
@Mapper
public interface SafeguardMapper {

  void insert(SestSafeguardModel safeguardModel);

  void update(@Param("safeguardModelJson") String safeguardModelJson, @Param("sestobjId") String sestobjId);

  SestSafeguardModel getByIdentifier(String sestobjId);

  SestSafeguardModel getById(Integer id);

  SestSafeguardModel getLastByProjectIdentifier(String sestobjId);
}

