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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//This class manages the business logic behind the webservices related to the AssetModel management
public class AssetModelManagerRestServer implements AssetModelManagerRestServerInterface {
  // the logger of AssetModelManagerRestServer class
  private static final Logger LOG = LoggerFactory.getLogger(AssetModelManagerRestServer.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  private AssetModelManagerInputInterface assetModelInput;
  private RiskModelManagerInputInterface riskModelInput;

  @Override
  public void editAssetModel(ModelObject assetModelObject) throws Exception {
    LOG.info("AssetModelManagerRestServer editAssetModel:: begin");
    try {
      //retrieve the assetModel in json format
      String assetModelJson = assetModelObject.getJsonModel();
      LOG.info("AssetModelManagerRestServer editAssetModel:: assetModelJson = {}", assetModelJson.substring(0, (assetModelJson.length() > 1000 ? 1000 : assetModelJson.length())));
      //retrieve the asset model identifier
      String identifier = assetModelObject.getObjectIdentifier();
      LOG.info("AssetModelManagerRestServer editAssetModel:: identifier = {}", identifier);

      //AssetModel data validation and updateQuestionnaireJSON time
      AssetModelSerializerDeserializer amsd = new AssetModelSerializerDeserializer();
      AssetModel am = amsd.getAMFromJSONString(assetModelJson);
      DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
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

    List<PrimaryAssetCategoryEnum> primaryAssetCategoryList = Arrays.asList(
      PrimaryAssetCategoryEnum.Data_DataFile_Database,
      PrimaryAssetCategoryEnum.Data_Shared_Office_File,
      PrimaryAssetCategoryEnum.Data_Personal_Office_File,
      PrimaryAssetCategoryEnum.Data_Physical_File,
      PrimaryAssetCategoryEnum.Data_Exchanged_Message,
      PrimaryAssetCategoryEnum.Data_Digital_Mail,
      PrimaryAssetCategoryEnum.Data_Physical_Mail,
      PrimaryAssetCategoryEnum.Data_Physical_Archive,
      PrimaryAssetCategoryEnum.Data_IT_Archive,
      PrimaryAssetCategoryEnum.Data_Published_Data,
      PrimaryAssetCategoryEnum.Data_Published_Data,
      PrimaryAssetCategoryEnum.Data_Published_Data,
      PrimaryAssetCategoryEnum.Service_User_Workspace,
      PrimaryAssetCategoryEnum.Service_Telecommunication_Service,
      PrimaryAssetCategoryEnum.Service_Extended_Network_Service,
      PrimaryAssetCategoryEnum.Service_Local_Network_Service,
      PrimaryAssetCategoryEnum.Service_Application_Service,
      PrimaryAssetCategoryEnum.Service_Shared_Service,
      PrimaryAssetCategoryEnum.Service_User_Hardware,
      PrimaryAssetCategoryEnum.Service_Common_Service,
      PrimaryAssetCategoryEnum.Service_Web_editing_Service,
      PrimaryAssetCategoryEnum.Compliance_Policy_Personal_Information_Protection,
      PrimaryAssetCategoryEnum.Compliance_Policy_Financial_Communication,
      PrimaryAssetCategoryEnum.Compliance_Policy_Digital_Accounting_Control,
      PrimaryAssetCategoryEnum.Compliance_Policy_Intellectual_Property,
      PrimaryAssetCategoryEnum.Compliance_Policy_Protection_Of_Information_Systems,
      PrimaryAssetCategoryEnum.Compliance_Policy_People_And_Environment_Safety);
    return primaryAssetCategoryList;
  }

  @Override
  public List<SecondaryAssetCategoryEnum> loadSecondaryAssetCategoryEnum() throws Exception {

    List<SecondaryAssetCategoryEnum> secondaryAssetCategoryList = Arrays.asList(
      SecondaryAssetCategoryEnum.Personnel,
      SecondaryAssetCategoryEnum.Hardware,
      SecondaryAssetCategoryEnum.Software_Off_the_Shelf,
      SecondaryAssetCategoryEnum.Premise,
      SecondaryAssetCategoryEnum.Data_File,
      SecondaryAssetCategoryEnum.Firmware,
      SecondaryAssetCategoryEnum.Data_Message,
      SecondaryAssetCategoryEnum.Software_Configuration,
      SecondaryAssetCategoryEnum.Policy,
      SecondaryAssetCategoryEnum.Electronic_Media,
      SecondaryAssetCategoryEnum.Non_Electronic_Media,
      SecondaryAssetCategoryEnum.Communication_Network,
      SecondaryAssetCategoryEnum.Software_Custom,
      SecondaryAssetCategoryEnum.Hardware_Configuration,
      SecondaryAssetCategoryEnum.Auxiliary_Equipment,
      SecondaryAssetCategoryEnum.Service_Access_Mean,
      SecondaryAssetCategoryEnum.Data_Access_Mean
    );
    return secondaryAssetCategoryList;
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
