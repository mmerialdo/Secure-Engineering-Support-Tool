/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.assetmodel.manager;

import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

public interface AssetModelManagerInputInterface {

	public void editAssetModel(String assetModel, String assetModelIdentifier);
	
	public ModelObject loadAssetModel(GenericFilter filter) throws Exception;
}
