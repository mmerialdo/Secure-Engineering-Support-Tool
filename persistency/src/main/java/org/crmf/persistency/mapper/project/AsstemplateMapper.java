/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AsstemplateMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.project;

import org.crmf.persistency.domain.project.AssTemplate;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AsstemplateMapper.xml (via the ibatis API)
public interface AsstemplateMapper {

  int insert(AssTemplate record);

  void insertProfileAssoc(AssTemplate record);

  void deleteProfileAssoc(AssTemplate record);

  void update(AssTemplate user);

  void delete(Integer id);

  void deleteByIdentifier(String identifier);

  AssTemplate getByIdentifier(String identifier);

  AssTemplate getById(Integer id);

  Integer getIdByIdentifier(String identifier);

  List<AssTemplate> getAll();

  List<AssTemplate> getByMethodology(String methodology);

  List<AssTemplate> getByProfileIdentifier(String identifier);
}