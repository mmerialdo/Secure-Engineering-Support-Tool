/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentMapper.java"
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

import org.apache.ibatis.annotations.Param;
import org.crmf.persistency.domain.risk.SestRiskTreatmentModel;

//This interface allows the bundle to invoke the SQL methods within the RiskTreatmentMapper.xml (via the ibatis API)
public interface RiskTreatmentMapper {

	public void insert(SestRiskTreatmentModel riskTreatmentModel);

	public void update(@Param("riskTreatmentModelJson") String riskTreatmentModelJson,
			@Param("sestobjId") String sestobjId);

	public SestRiskTreatmentModel getByIdentifier(String sestobjId);
	
	public SestRiskTreatmentModel getById(Integer id);
}
