/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.threatmodel.manager.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crmf.core.threatmodel.manager.ThreatModelManagerInputInterface;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.threatmodel.ThreatModelSerializerDeserializer;
import org.crmf.riskmodel.manager.RiskModelManagerInputInterface;
import org.crmf.threatimport.threatimportmanager.ThreatImportManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the ThreatModel management
public class ThreatModelManagerRestServer implements ThreatModelManagerRestServerInterface {
	// the logger of ThreatModelManagerRestServer class
	private static final Logger LOG = LoggerFactory.getLogger(ThreatModelManagerRestServer.class.getName());
	private ThreatModelManagerInputInterface threatModelInput;
	private ThreatImportManagerInputInterface threatImportInput;
	private RiskModelManagerInputInterface riskModelInput;

	@Override
	public void editThreatModel(ModelObject threatModelObject) throws Exception {
		LOG.info("ThreatModelManagerRestServer editThreatModel:: begin");
		try {
			//retrieve the threatModel in json format
			String threatModelJson = threatModelObject.getJsonModel();
			LOG.info("ThreatModelManagerRestServer editThreatModel:: threatModelJson = "+ threatModelJson.substring(0, (threatModelJson.length() > 1000 ? 1000 : threatModelJson.length())));
			//retrieve the threat model identifier
			String identifier = threatModelObject.getObjectIdentifier();
			LOG.info("ThreatModelManagerRestServer editThreatModel:: identifier = "+identifier);
			
			//ThreatModel data validation and updateQuestionnaireJSON time
			ThreatModelSerializerDeserializer tmsd = new ThreatModelSerializerDeserializer();
			ThreatModel tm = tmsd.getTMFromJSONString(threatModelJson);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date now = new Date();
			tm.setUpdateTime(df.format(now));
			threatModelJson = tmsd.getJSONStringFromTM(tm);
			
			// updateQuestionnaireJSON the threat model
			threatModelInput.editThreatModel(threatModelJson, identifier);
		} catch (Exception e) {
			LOG.info("ThreatModelManagerRestServer editThreatModel:: exception " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public ModelObject loadThreatModel(GenericFilter filter) throws Exception {
		LOG.info("ThreatModelManagerRestServer loadThreatModel:: begin");
		try {
			//return the threat model in json format that matches the filters in input 
			return threatModelInput.loadThreatModel(filter);
		} catch (Exception e) {
			LOG.info("ThreatModelManagerRestServer loadThreatModel:: exception " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}
	
	@Override
	public String loadThreatRepository(GenericFilter filter) throws Exception {
		LOG.info("ThreatModelManagerRestServer loadThreatRepository:: begin");
		LOG.info("loadThreatRepository " + filter);
		
		try {
			//return the threat model in json format that matches the filters in input 
			String result = threatModelInput.loadThreatRepository(filter);
			return result;
		} catch (Exception e) {
			LOG.error("ThreatModelManagerRestServer loadThreatRepository " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public void updateThreatRepository(String catalogue) throws Exception {
		LOG.info("ThreatModelManagerRestServer updateThreatRepository:: begin");
		LOG.info("updateThreatRepository " + catalogue);
		
		try {
			ThreatSourceEnum source;
			if(catalogue.equals("MEHARI")){
				source = ThreatSourceEnum.MEHARI;
			}
			else{
				LOG.error("updateThreatRepository - catalogue not supported " + catalogue);
				throw new Exception("COMMAND_EXCEPTION");
			}
			
			threatImportInput.importThreats(source);
			
			// get the RISK SCENARIO REFERENCE
			ArrayList<RiskScenarioReference> rsr = riskModelInput.getRiskScenarioReference();
			//updateQuestionnaireJSON risk scenario reference in order to link the new threat catalogue
			boolean scenarioResult = riskModelInput.updateScenarioRepository(rsr);
			
			if(!scenarioResult){
				throw new Exception("COMMAND_EXCEPTION");
			}
		} catch (Exception e) {
			LOG.error("ThreatModelManagerRestServer updateThreatRepository " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public String createThreatReference(Threat threat) throws Exception {

		try {
			return threatModelInput.createThreat(threat);
		} catch (Exception e) {
			LOG.error("ThreatModelManagerRestServer createThreatRepository " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public void editThreatReference(Threat threat) throws Exception {
		try {
			threatModelInput.editThreat(threat);
		} catch (Exception e) {
			LOG.error("ThreatModelManagerRestServer updateThreatRepository " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public void deleteThreatReference(List<String> identifier) throws Exception {
		try {
			threatModelInput.deleteThreat(identifier);
		} catch (Exception e) {
			LOG.error("ThreatModelManagerRestServer deleteThreatRepository " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	public ThreatModelManagerInputInterface getThreatModelInput() {
		return threatModelInput;
	}

	public void setThreatModelInput(ThreatModelManagerInputInterface threatModelInput) {
		this.threatModelInput = threatModelInput;
	}

	public ThreatImportManagerInputInterface getThreatImportInput() {
		return threatImportInput;
	}

	public void setThreatImportInput(ThreatImportManagerInputInterface threatImportInput) {
		this.threatImportInput = threatImportInput;
	}
	
	public RiskModelManagerInputInterface getRiskModelInput() {
		return riskModelInput;
	}

	public void setRiskModelInput(RiskModelManagerInputInterface riskModelInput) {
		this.riskModelInput = riskModelInput;
	}

}
