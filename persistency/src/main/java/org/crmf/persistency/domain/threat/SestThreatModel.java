/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestThreatModel.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.threat;

import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SestThreatModel {

  private static final Logger LOG = LoggerFactory.getLogger(SestThreatModel.class.getName());
  private Integer id;
  private String sestobjId;
  private String threatModelJson;


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

  public String getThreatModelJson() {
    return threatModelJson;
  }

  public void setThreatModelJson(String threatModelJson) {
    this.threatModelJson = threatModelJson;
  }

  public ThreatModel convertToModel() {
    LOG.info("ThreatModel convertToModel");
    ThreatModelSerializerDeserializer threatModelDeserializer = new ThreatModelSerializerDeserializer();

    return threatModelDeserializer.getTMFromJSONString(this.getThreatModelJson());
  }
}
