/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="QuestionnaireMapper.java"
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

import org.apache.ibatis.annotations.Param;
import org.crmf.model.audit.SestQuestionnaireModel;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the QuestionnaireMapper.xml (via the ibatis API)
public interface QuestionnaireMapper {

  int insert(SestQuestionnaireModel questionnaire);

  void update(@Param("sestobjId") String sestobjId, @Param("questionnaireModelJson") String questionnaireModelJson);

  void delete(String identifier);

  List<SestQuestionnaireModel> getByAuditId(Integer id);

  List<SestQuestionnaireModel> getTypeByAuditId(Integer id);

  SestQuestionnaireModel getByIdentifier(String identifier);

  Integer getIdByIdentifier(String identifier);

  List<SestQuestionnaireModel> getAllQuestionnaireNames(int auditId);

  SestQuestionnaireModel getQuestionnaireByCategory(String category);
}
