/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Answer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.audit;

import org.crmf.model.audit.AnswerTypeEnum;

public class Answer {
  private Integer id;

  private Integer ix;

  private String atype;

  private String avalue;

  private Integer questionId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIx() {
    return ix;
  }

  public void setIx(Integer ix) {
    this.ix = ix;
  }

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype == null ? null : atype.trim();
  }

  public String getAvalue() {
    return avalue;
  }

  public void setAvalue(String avalue) {
    this.avalue = avalue == null ? null : avalue.trim();
  }

  public Integer getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Integer questionId) {
    this.questionId = questionId;
  }

  public org.crmf.model.audit.Answer convertToModel() {

    org.crmf.model.audit.Answer answer = new org.crmf.model.audit.Answer();

    if (this.atype != null) {
      answer.setType(AnswerTypeEnum.valueOf(this.atype));
    }
    answer.setIndex(this.getIx());
    answer.setValue((this.getAvalue() != null) ? this.getAvalue() : "");

    return answer;
  }

  public void convertFromModel(org.crmf.model.audit.Answer answer) {

    if (answer.getType() != null) {
      this.setAtype(answer.getType().name());
    }
    this.setAvalue(answer.getValue());
    this.setIx(answer.getIndex());
//		this.setQuestionId(questionId);
//		this.setId(id);
  }
}