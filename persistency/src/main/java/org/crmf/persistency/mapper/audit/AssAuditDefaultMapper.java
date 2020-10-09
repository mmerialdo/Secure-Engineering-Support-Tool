/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditDefaultMapper.java"
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

import org.apache.ibatis.annotations.Mapper;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AssAuditDefaultMapper.xml (via the ibatis API)
@Mapper
public interface AssAuditDefaultMapper {

  void insert(AssauditDefaultJSON questionnaire);

  void updateQuestionnaireJSON(AssauditDefaultJSON questionnaire);

  List<AssauditDefaultJSON> getAll();

  List<AssauditDefaultJSON> getAllQuestionnaires();

  List<AssauditDefaultJSON> getAllQuestionnaireNames();

  List<AssauditDefaultJSON> getAllByParentCategory(String category);

  AssauditDefaultJSON getByCategory(String category);

  List<AssauditDefaultJSON> getSafeguardByIdentifier();
}
