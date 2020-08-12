/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Sestobj.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.general;

import org.crmf.model.general.SESTObject;
import org.crmf.model.general.SESTObjectTypeEnum;

public class Sestobj {
  private Integer id;
  private String identifier;
  private String objtype;
  private String lockedBy;


  public Sestobj() {
    super();
  }

  public Sestobj(String identifier) {
    super();
    this.identifier = identifier;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getObjtype() {
    return objtype;
  }

  public void setObjtype(String objtype) {
    this.objtype = objtype == null ? null : objtype.trim();
  }

  public String getLockedBy() {
    return lockedBy;
  }

  public void setLockedBy(String lockedBy) {
    this.lockedBy = lockedBy;
  }

  public SESTObject convertToModel() {
    SESTObject so = new SESTObject();
    so.setIdentifier(this.identifier);
    if (this.objtype != null)
      so.setObjType(SESTObjectTypeEnum.valueOf(this.objtype));
    if (this.lockedBy != null)
      so.setLockedBy(this.lockedBy);
    return so;
  }

  @Override
  public String toString() {
    return "Sestobj [id=" + id + ", identifier=" + identifier + ", objtype=" + objtype + ", lockedBy=" + lockedBy + "]";
  }

}