/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelManagerRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.assetmodel.manager.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.crmf.core.assetmodel.manager.AssetModelManagerInputInterface;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.assetmodel.AssetModelSerializerDeserializer;
import org.crmf.riskmodel.manager.RiskModelManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the AssetModel management
public class AssetModelManagerRestServer implements AssetModelManagerRestServerInterface {
  // the logger of AssetModelManagerRestServer class
  private static final Logger LOG = LoggerFactory.getLogger(AssetModelManagerRestServer.class.getName());
  private AssetModelManagerInputInterface assetModelInput;
  private RiskModelManagerInputInterface riskModelInput;

  @Override
  public void editAssetModel(ModelObject assetModelObject) throws Exception {
    LOG.info("AssetModelManagerRestServer editAssetModel:: begin");
    try {
      //retrieve the assetModel in json format
      String assetModelJson = assetModelObject.getJsonModel();
      LOG.info("AssetModelManagerRestServer editAssetModel:: assetModelJson = " + assetModelJson.substring(0, (assetModelJson.length() > 1000 ? 1000 : assetModelJson.length())));
      //retrieve the asset model identifier
      String identifier = assetModelObject.getObjectIdentifier();
      LOG.info("AssetModelManagerRestServer editAssetModel:: identifier = " + identifier);

      //AssetModel data validation and updateQuestionnaireJSON time
      AssetModelSerializerDeserializer amsd = new AssetModelSerializerDeserializer();
      AssetModel am = amsd.getAMFromJSONString(assetModelJson);
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      Date now = new Date();
      am.setUpdateTime(df.format(now));
      assetModelJson = amsd.getJSONStringFromAM(am);

      // updateQuestionnaireJSON the asset model
      assetModelInput.editAssetModel(assetModelJson, identifier);

      //we need to check if the updateQuestionnaireJSON on the assetmodel triggered any updateQuestionnaireJSON on the risk model or other models (since it is the asset model driving the scenario definition
      riskModelInput.editAssetModel(identifier);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public ModelObject loadAssetModel(GenericFilter filter) throws Exception {
    LOG.info("AssetModelManagerRestServer loadAssetModel:: begin");
    try {
      //return the asset model in json format that matches the filters in input
      return assetModelInput.loadAssetModel(filter);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new Exception("COMMAND_EXCEPTION", e);
    }
  }

  @Override
  public List<PrimaryAssetCategoryEnum> loadPrimaryAssetCategoryEnum() throws Exception {
  	LOG.info("" + PrimaryAssetCategoryEnum.PRIMARY_ASSET_CATEGORY_LIST);
    return PrimaryAssetCategoryEnum.PRIMARY_ASSET_CATEGORY_LIST;
  }

	@Override
	public List<SecondaryAssetCategoryEnum> loadSecondaryAssetCategoryEnum() throws Exception {
		LOG.info("" + SecondaryAssetCategoryEnum.SECONDARY_ASSET_CATEGORY_LIST);
		return SecondaryAssetCategoryEnum.SECONDARY_ASSET_CATEGORY_LIST;
	}

  public AssetModelManagerInputInterface getAssetModelInput() {
    return assetModelInput;
  }

  public void setAssetModelInput(AssetModelManagerInputInterface assetModelInput) {
    this.assetModelInput = assetModelInput;
  }

  public RiskModelManagerInputInterface getRiskModelInput() {
    return riskModelInput;
  }

  public void setRiskModelInput(RiskModelManagerInputInterface riskModelInput) {
    this.riskModelInput = riskModelInput;
  }

}
