/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.requirement;

import org.crmf.model.requirement.Requirement;

import java.util.List;

public interface RequirementServiceInterface {

  void insertSysRequirement(Requirement requirement, String sysprojectIdentifier);

  void deleteSysRequirement(Requirement requirement);

  List<Requirement> getByIds(List<String> ids);

  List<Requirement> getBySysProject(String sysprojectIdentifier);

  List<Requirement> getBySysProjectAndFile(String sysprojectIdentifier, String filename);

  List<String> getFilenameByProject(String sysprojectIdentifier);
}
