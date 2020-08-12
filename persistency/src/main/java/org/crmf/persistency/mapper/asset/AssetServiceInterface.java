/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetServiceInterface.java"
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

import org.crmf.persistency.domain.asset.SestAssetModel;

public interface AssetServiceInterface {

  void insert(String assetModelJson, String sestobjId);

  void update(String assetModelJson, String sestobjId);

  SestAssetModel getByIdentifier(String sestobjId);
}
