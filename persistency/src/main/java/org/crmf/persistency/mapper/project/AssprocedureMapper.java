/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprocedureMapper.java"
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

import org.apache.ibatis.annotations.Mapper;
import org.crmf.persistency.domain.project.AssProcedure;

import java.util.List;

//This interface allows the bundle to invoke the SQL methods within the AssprocedureMapper.xml (via the ibatis API)
@Mapper
public interface AssprocedureMapper {

  int insert(AssProcedure record);

  void update(AssProcedure procedure);

  void delete(Integer id);

  void deleteByIdentifier(String identifier);

  AssProcedure getByIdentifier(String identifier);

  Integer getIdByIdentifier(String identifier);

  public List<AssProcedure> getAll();

  public List<AssProcedure> getByProjectIdentifier(String identifier);

  public Integer getProjectdIdByIdentifier(String identifier);

  public AssProcedure getByAssetModelIdentifier(String identifier);

  public AssProcedure getByVulnerabilityModelIdentifier(String identifier);

  public AssProcedure getByThreatModelIdentifier(String identifier);

  public AssProcedure getByRiskModelIdentifier(String identifier);

  public AssProcedure getOpenByProjectId(Integer id);

  public AssProcedure getByRiskTreatmentModelIdentifier(String riskTreatmentModelIdentifier);

  public AssProcedure getBySafeguardModelIdentifier(String safeguardModelIdentifier);

}