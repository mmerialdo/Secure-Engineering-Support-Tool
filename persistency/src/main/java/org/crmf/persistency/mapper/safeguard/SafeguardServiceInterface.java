/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardServiceInterface.java"
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

import org.crmf.persistency.domain.safeguard.SestSafeguardModel;

public interface SafeguardServiceInterface {

  void insert(String safeguardModelJson, String sestobjId);

  void update(String safeguardModelJson, String sestobjId);

  SestSafeguardModel getByIdentifier(String sestobjId);

  SestSafeguardModel getLastByProjectIdentifier(String sestobjId);
}
