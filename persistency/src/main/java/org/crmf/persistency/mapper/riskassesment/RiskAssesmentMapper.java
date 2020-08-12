/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskAssesmentMapper.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.riskassesment;

import org.crmf.persistency.domain.riskassesment.RiskAssesment;

import java.util.List;

public interface RiskAssesmentMapper {

  void insertRiskAssesment(RiskAssesment riskassesment);

  RiskAssesment getRiskAssesmentById(Integer riskassesmentId);

  List<RiskAssesment> getAllRiskAssesments();

  void updatRiskAssesment(RiskAssesment riskassesment);

  void deleteRiskAssesment(Integer riskassesmentId);
}
