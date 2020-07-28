/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.assetmodel.manager;

import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.general.SestObjService;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssetModel
public class AssetModelManagerInput implements AssetModelManagerInputInterface {
	// the logger of AssetModelManagerInput class
	private static final Logger LOG = LoggerFactory.getLogger(AssetModelManagerInput.class.getName());
	// Asset service variable of persistency component
	private AssetServiceInterface assetService;
	// Procedure service variable of persistency component
	private AssprocedureServiceInterface assprocedureService;
	private SestObjServiceInterface sestObjService;

	@Override
	public void editAssetModel(String assetModelJson, String assetModelIdentifier) {
		LOG.info("editAssetModel with identifier: " + assetModelIdentifier + " and Json" + assetModelJson.substring(0, (assetModelJson.length() > 1000 ? 1000 : assetModelJson.length())));
		// updateQuestionnaireJSON the json asset model whose identifier is the asset model identifier in input
		assetService.update(assetModelJson, assetModelIdentifier);
		
	}

	@Override
	public ModelObject loadAssetModel(GenericFilter filter) throws Exception {
		// get the procedure identifier passed in input
		String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);

		// if the value of procedure identifier is not null
		if (procedureIdentifier != null) {
			// retrieve the assessment procedure associated to the procedure identifier in input 
			AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);
			
            //retrieve the asset model identifier associated to the retrieved assessment procedure
			String sestobjId = procedure.getAssetModel().getIdentifier();

			// return the json asset model associated to the asset model identifier retrieved
			ModelObject modelObject = new ModelObject();
			modelObject.setJsonModel(assetService.getByIdentifier(sestobjId).getAssetModelJson());
			modelObject.setObjectIdentifier(sestobjId);
			modelObject.setLockedBy(sestObjService.getByIdentifier(sestobjId).getLockedBy());
			return modelObject;
		} else 
			throw new Exception("Incorrect procedure identifier in input");
	}
	
	public AssetServiceInterface getAssetService() {
		return assetService;
	}

	public void setAssetService(AssetServiceInterface assetService) {
		this.assetService = assetService;
	}
	
	public AssprocedureServiceInterface getAssprocedureService() {
		return assprocedureService;
	}

	public void setAssprocedureService(AssprocedureServiceInterface assprocedureService) {
		this.assprocedureService = assprocedureService;
	}

	public SestObjServiceInterface getSestObjService() {
		return sestObjService;
	}

	public void setSestObjService(SestObjServiceInterface sestObjService) {
		this.sestObjService = sestObjService;
	}
}
