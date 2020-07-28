/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskMapper.java"
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

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.risk.SeriousnessScale;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.risk.SestRiskScenarioReference;
import org.crmf.persistency.domain.risk.StatusImpactScale;
import org.crmf.persistency.domain.risk.StatusLikelihoodScale;

//This interface allows the bundle to invoke the SQL methods within the RiskMapper.xml (via the ibatis API)
public interface RiskMapper {
public void insert(SestRiskModel riskModel);
	
	void update(@Param("riskModelJson") String riskModelJson, @Param("sestobjId") String sestobjId);

	SestRiskModel getByIdentifier(String sestobjId);
	
	SestRiskModel getById(Integer id);
	
	List<SeriousnessScale> getSeriousnessByProjectId(int projectId);
	
	List<StatusImpactScale> getStatusImpactByProjectId(int projectId);
	
	List<StatusLikelihoodScale> getStatusLikelihoodByProjectId(int projectId);
	
	boolean insertScenarioReference(SestRiskScenarioReference scenarioReference);
	
	void clearRiskReferenceScenario();
	
	Set<SestRiskScenarioReference> getRiskScenarioReference();

	void deleteScenarioReference(@Param("sestobjIds") List<String> identifiers);

	void editScenarioReference(SestRiskScenarioReference scenarioReference);
}
