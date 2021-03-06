/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysprojectServiceInterface.java"
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

import org.crmf.model.riskassessment.SystemProject;

public interface SysprojectServiceInterface {

  Integer insert(SystemProject sysprojectDM) throws Exception;

  void update(SystemProject sysprojectDM);

}