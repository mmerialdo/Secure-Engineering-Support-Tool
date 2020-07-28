/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.asset;

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.asset.SestAssetModel;

//This interface allows the bundle to invoke the SQL methods within the AssetMapper.xml (via the ibatis API)
public interface AssetMapper {

	public void insert(SestAssetModel assetModel);
	
	public void update(@Param("assetModelJson") String assetModelJson, @Param("sestobjId") String sestobjId);

	public SestAssetModel getByIdentifier(String sestobjId);
	
	public SestAssetModel getById(Integer id);

}
