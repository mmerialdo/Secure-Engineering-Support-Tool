/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestSafeguardModel.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.safeguard;

import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestSafeguardModel {
  private static final Logger LOG = LoggerFactory.getLogger(SestSafeguardModel.class.getName());
  private Integer id;
  private String sestobjId;
  private String safeguardModelJson;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public String getSafeguardModelJson() {
    return safeguardModelJson;
  }

  public void setSafeguardModelJson(String safeguardModelJson) {
    this.safeguardModelJson = safeguardModelJson;
  }

  public SafeguardModel convertToModel() {
    LOG.info("SafeguardModel convertToModel");
    SafeguardModelSerializerDeserializer safeguardModelDeserializer = new SafeguardModelSerializerDeserializer();

    return safeguardModelDeserializer.getSMFromJSONString(this.getSafeguardModelJson());
  }
}
