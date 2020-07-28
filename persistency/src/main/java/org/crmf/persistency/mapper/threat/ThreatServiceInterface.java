/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.threat;

import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.Vulnerability;
import org.crmf.persistency.domain.threat.SestThreatModel;

import java.util.List;

public interface ThreatServiceInterface {

  void insert(String threatModelJson, String sestobjId);

  void update(String threatModelJson, String sestobjId);

  SestThreatModel getByIdentifier(String sestobjId);

  SestThreatModel getThreatRepository(String catalogue);

  boolean updateThreatRepository(ThreatModel tmToAdd, ThreatModel tmToUpdate);

  Integer retrieveThreatReferenceId(String catalogueId);

  String insertThreatReference(Threat threatModelJson) throws Exception ;

  void deleteThreatReference(List<String> identifier) throws Exception ;

  void editThreatReference(Threat threat) throws Exception ;
}
