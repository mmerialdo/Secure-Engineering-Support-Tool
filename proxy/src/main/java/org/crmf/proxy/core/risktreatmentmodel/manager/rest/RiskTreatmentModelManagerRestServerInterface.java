/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelManagerRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.risktreatmentmodel.manager.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

public interface RiskTreatmentModelManagerRestServerInterface {

	@POST
    @Consumes("application/json") 
	@Produces("application/json")
	String editRiskTreatmentModel(ModelObject riskModel) throws Exception;
	
	@POST
    @Consumes("application/json") 
	@Produces("application/json")
	String editRiskTreatmentModelDetail(ModelObject riskModel) throws Exception;

	@POST
	@Produces("application/json")
    @Consumes("text/html") 
	ModelObject loadRiskTreatmentModel(GenericFilter filter) throws Exception;
	
	@POST
    @Consumes("application/json") 
	@Produces("application/json")
	String loadRiskTreatmentModelDetail(GenericFilter filter) throws Exception;

	@POST
    @Consumes("application/json") 
	@Produces("application/json")
	String calculateRiskTreatmentModel(ModelObject riskModel) throws Exception;
	
	@POST
    @Consumes("application/json") 
	@Produces("application/json")
	String calculateRiskTreatmentModelDetail(ModelObject riskModel) throws Exception;
}
