/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysProject.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.project;

import org.crmf.model.riskassessment.SystemProject;

public class SysProject {
  private Integer id;

  private String name;

  private String description;

  private String mandate;

  private String pscope;

  private String sestobjId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }

  public String getMandate() {
    return mandate;
  }

  public void setMandate(String mandate) {
    this.mandate = mandate == null ? null : mandate.trim();
  }

  public String getPscope() {
    return pscope;
  }

  public void setPscope(String pscope) {
    this.pscope = pscope == null ? null : pscope.trim();
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public SystemProject convertToModel() {

    SystemProject project = new SystemProject();

    project.setName(this.name);
    project.setDescription(this.description);
    project.setMandate(this.mandate);
    project.setScope(this.pscope);

    if (this.sestobjId != null) {
      project.setIdentifier(String.valueOf(this.sestobjId));
    }

    return project;
  }

  public void convertFromModel(SystemProject project) {

    this.setName(project.getName());
    this.setDescription(project.getDescription());
    this.setMandate(project.getMandate());
    this.setPscope(project.getScope());

    if (project.getIdentifier() != null) {
      this.setSestobjId(project.getIdentifier());
    }
  }
}