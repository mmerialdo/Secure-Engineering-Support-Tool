/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.Question;

import java.util.List;

public interface AssAuditServiceInterface {

  List<SestAuditModel> getAllForProject(String identifier);

  SestAuditModel getByProjectAndType(String identifier, AuditTypeEnum type, boolean includeModels);

  int getProjectIdByIdentifier(String identifier);

  void update(SestAuditModel audit);

  void createDefaultQuestionnaire();

  List<Question> getSafeguardByIdentifier();

}