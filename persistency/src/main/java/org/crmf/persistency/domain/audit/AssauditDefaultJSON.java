/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssauditDefault.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.persistency.domain.general.Sestobj;

public class AssauditDefaultJSON {

  private Integer id;
  private String ix;
  private String avalue;
  private String atype;
  private String category;
  private String parent;
  private String vw;
  private String vmax;
  private String vmin;
  private String vtype;
  private String viso13;
  private String viso5;
  private String questionnaireJSON;

  public Integer getId() {
    return id;
  }

  public String getIx() {
    return ix;
  }

  public String getAvalue() {
    return avalue;
  }

  public String getAtype() {
    return atype;
  }

  public String getCategory() {
    return category;
  }

  public String getParent() {
    return parent;
  }

  public String getVw() {
    return vw;
  }

  public String getVmax() {
    return vmax;
  }

  public String getVmin() {
    return vmin;
  }

  public String getVtype() {
    return vtype;
  }

  public String getViso13() {
    return viso13;
  }

  public String getViso5() {
    return viso5;
  }

  public String getQuestionnaireJSON() {
    return questionnaireJSON;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setIx(String ix) {
    this.ix = ix;
  }

  public void setAvalue(String avalue) {
    this.avalue = avalue;
  }

  public void setAtype(String atype) {
    this.atype = atype;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public void setVw(String vw) {
    this.vw = vw;
  }

  public void setVmax(String vmax) {
    this.vmax = vmax;
  }

  public void setVmin(String vmin) {
    this.vmin = vmin;
  }

  public void setVtype(String vtype) {
    this.vtype = vtype;
  }

  public void setViso13(String viso13) {
    this.viso13 = viso13;
  }

  public void setViso5(String viso5) {
    this.viso5 = viso5;
  }

  public void setQuestionnaireJSON(String questionnaireJSON) {
    this.questionnaireJSON = questionnaireJSON;
  }
}
