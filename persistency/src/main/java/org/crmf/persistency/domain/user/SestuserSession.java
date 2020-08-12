/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestuserSession.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.user;

public class SestuserSession {
  private Integer id;

  private String expTime;

  private String sessionId;

  private Integer userId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getExpTime() {
    return expTime;
  }

  public void setExpTime(String expTime) {
    this.expTime = expTime == null ? null : expTime.trim();
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId == null ? null : sessionId.trim();
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}