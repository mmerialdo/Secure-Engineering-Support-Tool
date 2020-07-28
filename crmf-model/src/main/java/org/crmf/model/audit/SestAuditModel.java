/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="SestAuditModel.java"
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

import java.util.ArrayList;
import java.util.List;

/************************************************************************
 * Created: 08/11/2019                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class SestAuditModel extends SESTObject {

  private static final Logger LOG = LoggerFactory.getLogger(SestAuditModel.class.getName());
  private Integer id;
  private AuditTypeEnum type;
  private Integer projectId;
  private Integer profileId;
  private List<SestQuestionnaireModel> sestQuestionnaireModel = new ArrayList<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<SestQuestionnaireModel> getSestQuestionnaireModel() {
    return sestQuestionnaireModel;
  }

  public void setSestQuestionnaireModel(List<SestQuestionnaireModel> sestQuestionnaireModel) {
    this.sestQuestionnaireModel = sestQuestionnaireModel;
  }

  public AuditTypeEnum getType() {
    return type;
  }

  public void setType(AuditTypeEnum type) {
    this.type = type;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public void setProfileId(Integer profileId) {
    this.profileId = profileId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public Integer getProfileId() {
    return profileId;
  }
}
