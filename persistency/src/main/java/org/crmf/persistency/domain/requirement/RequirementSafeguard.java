/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementSafeguard.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.requirement;

public class RequirementSafeguard {
  private Integer safeguardId;

  private Integer requirementId;

  public Integer getSafeguardId() {
    return safeguardId;
  }

  public void setSafeguardId(Integer safeguardId) {
    this.safeguardId = safeguardId;
  }

  public Integer getRequirementId() {
    return requirementId;
  }

  public void setRequirementId(Integer requirementId) {
    this.requirementId = requirementId;
  }
}