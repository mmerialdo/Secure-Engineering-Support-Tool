/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatMapper.java"
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

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.threat.SestThreat;
import org.crmf.persistency.domain.threat.SestThreatModel;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the ThreatMapper.xml (via the ibatis API)
public interface ThreatMapper {
  void insert(SestThreatModel threatModel);

  void update(@Param("threatModelJson") String threatModelJson, @Param("sestobjId") String sestobjId);

  SestThreatModel getByIdentifier(String sestobjId);

  SestThreatModel getById(Integer id);

  void updateThreatRepository(SestThreat threat);

  void insertThreatRepository(SestThreat threat);

  List<SestThreat> getThreatRepository(@Param("catalogue") String catalogue);

  Integer retrieveThreatReferenceId(String catalogueId);

  String getReferenceCatalogueById(Integer id);

  String getThreatReferenceSestObjIdByCatalogueId(@Param("catalogueId") String catalogueId);

  void deleteThreatReference(@Param("sestobjIds") List<String> sestobjId);
}
