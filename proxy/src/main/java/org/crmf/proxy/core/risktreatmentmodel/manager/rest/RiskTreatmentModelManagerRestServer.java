/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelManagerRestServer.java"
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


import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.risktreatmentmodel.manager.RiskTreatmentModelManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the RiskTreatmentModel management
public class RiskTreatmentModelManagerRestServer implements RiskTreatmentModelManagerRestServerInterface {

	private static final Logger LOG = LoggerFactory.getLogger(RiskTreatmentModelManagerRestServer.class.getName());
	private RiskTreatmentModelManagerInputInterface riskTreatmentModelInput;

	@Override
	public String editRiskTreatmentModel(ModelObject riskTreatmentModel) throws Exception {
		LOG.info("editRiskTreatmentModel:: begin");
		try {
			String riskTreatmentModelJson = riskTreatmentModel.getJsonModel();
			String identifier = riskTreatmentModel.getObjectIdentifier();
			LOG.info("editRiskTreatmentModel:: identifier = "+identifier);
			return riskTreatmentModelInput.editRiskTreatmentModel(riskTreatmentModelJson, identifier);
		} catch (Exception e) {
			LOG.error("editThreatModel:: exception " + e.getMessage(), e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}	
	}
	
	@Override
	public String editRiskTreatmentModelDetail(ModelObject riskTreatmentModel) throws Exception {
		LOG.info("editRiskTreatmentModelDetail:: begin");
		try {
			String riskTreatmentModelJson = riskTreatmentModel.getJsonModel();
			String identifier = riskTreatmentModel.getObjectIdentifier();
			LOG.info("editRiskTreatmentModelDetail:: identifier = "+identifier);
			return riskTreatmentModelInput.editRiskTreatmentModelDetail(riskTreatmentModelJson, identifier);
		} catch (Exception e) {
			LOG.error("editThreatModel:: exception " + e.getMessage(), e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}	
	}

	@Override
	public ModelObject loadRiskTreatmentModel(GenericFilter filter) throws Exception {
		LOG.info("loadRiskTreatmentModel:: begin");
		try {
			return riskTreatmentModelInput.loadRiskTreatmentModel(filter);
		} catch (Exception e) {
			LOG.error("loadRiskTreatmentModel:: exception " + e.getMessage(), e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public String loadRiskTreatmentModelDetail(GenericFilter filter) throws Exception {
		LOG.info("loadRiskTreatmentModelDetail:: begin");
		try {
			String result = riskTreatmentModelInput.loadRiskTreatmentModelDetail(filter);
			return result;
		} catch (Exception e) {
			LOG.error("loadRiskTreatmentModelDetail:: exception " + e.getMessage(),e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public String calculateRiskTreatmentModel(ModelObject riskModel) throws Exception {
		LOG.info("calculateRiskTreatmentModel:: begin");
		try {
			String riskTreatmentModelJson = riskModel.getJsonModel();
			String identifier = riskModel.getObjectIdentifier();
			LOG.info("calculateRiskTreatmentModel:: identifier = "+identifier);
			return riskTreatmentModelInput.calculateRiskTreatmentModel(riskTreatmentModelJson, identifier);
		} catch (Exception e) {
			LOG.error("calculateRiskTreatmentModel:: exception " + e.getMessage(), e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}	
	}
	
	@Override
	public String calculateRiskTreatmentModelDetail(ModelObject riskModel) throws Exception {
		LOG.info("calculateRiskTreatmentModelDetail:: begin");
		try {
			String riskTreatmentModelJson = riskModel.getJsonModel();
			String identifier = riskModel.getObjectIdentifier();
			LOG.info("calculateRiskTreatmentModelDetail:: identifier = "+identifier);
			return riskTreatmentModelInput.calculateRiskTreatmentModelDetail(riskTreatmentModelJson, identifier);
		} catch (Exception e) {
			LOG.error("calculateRiskTreatmentModel:: exception " + e.getMessage(), e);
			throw new Exception("COMMAND_EXCEPTION", e);
		}	
	}

	public RiskTreatmentModelManagerInputInterface getRiskTreatmentModelInput() {
		return riskTreatmentModelInput;
	}

	public void setRiskTreatmentModelInput(RiskTreatmentModelManagerInputInterface riskTreatmentModelInput) {
		this.riskTreatmentModelInput = riskTreatmentModelInput;
	}

}
