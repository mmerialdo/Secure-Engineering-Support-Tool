/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.risk;

import java.util.ArrayList;
import java.util.List;

import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.persistency.domain.risk.SeriousnessScale;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.risk.StatusImpactScale;
import org.crmf.persistency.domain.risk.StatusLikelihoodScale;
import org.crmf.persistency.domain.riskassesment.RiskAssesment;

public interface RiskServiceInterface {
  void insert(String riskModelJson, String sestobjId);

  void update(String riskModelJson, String sestobjId);

  SestRiskModel getByIdentifier(String sestobjId);

  List<SeriousnessScale> getSeriousness(int projectId);

  List<StatusImpactScale> getStatusImpact(int projectId);

  List<StatusLikelihoodScale> getStatusLikelihood(int projectId);

  boolean updateScenarioRepository(ArrayList<RiskScenarioReference> rsr);

  ArrayList<RiskScenarioReference> getRiskScenarioReference();

  String insertRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;

  void deleteRiskScenarioReference(List<String> identifier) throws Exception;

  void editRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;
}
