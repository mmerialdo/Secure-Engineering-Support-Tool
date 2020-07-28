/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Questionnaire.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.audit;

import org.crmf.model.general.SESTObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestQuestionnaireModel extends SESTObject {

  private static final Logger LOG = LoggerFactory.getLogger(SestQuestionnaireModel.class.getName());
  private Integer id;
  private String ix;
  private Integer auditId;
  private String category;
  private QuestionnaireTypeEnum type;
  private String questionnaireModelJson;

  public Integer getId() {
    return id;
  }

  public String getQuestionnaireModelJson() {
    return questionnaireModelJson;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setQuestionnaireModelJson(String questionnaireModelJson) {
    this.questionnaireModelJson = questionnaireModelJson;
  }

  public QuestionnaireTypeEnum getType() {
    return type;
  }

  public void setType(QuestionnaireTypeEnum type) {
    this.type = type;
  }

  public void setIx(String ix) {
    this.ix = ix;
  }

  public String getIx() {
    return ix;
  }

  public Integer getAuditId() {
    return auditId;
  }

  public void setAuditId(Integer auditId) {
    this.auditId = auditId;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}