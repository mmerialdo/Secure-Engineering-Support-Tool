/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestObjServiceInterface.java"
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

import org.crmf.model.general.SESTObject;

import java.util.List;

public interface SestObjServiceInterface {
	SESTObject getById(Integer sestobjId);
	SESTObject getByIdentifier(String identifier);
	List<SESTObject> getAll();
	String updateLock(String viewIdentifier, String userIdentifier) throws Exception;
}
