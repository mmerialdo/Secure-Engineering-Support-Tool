/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementSource.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.secrequirement;

public class SecRequirementSource {

  private Integer requirementId;

  private String secrequirementSource;

  private String secrequirementSourceReference;

  public Integer getRequirementId() {
    return requirementId;
  }

  public void setRequirementId(Integer requirementId) {
    this.requirementId = requirementId;
  }

  public String getSecrequirementSource() {
    return secrequirementSource;
  }

  public void setSecrequirementSource(String secrequirementSource) {
    this.secrequirementSource = secrequirementSource;
  }

  public String getSecrequirementSourceReference() {
    return secrequirementSourceReference;
  }

  public void setSecrequirementSourceReference(String secrequirementSourceReference) {
    this.secrequirementSourceReference = secrequirementSourceReference;
  }
}
