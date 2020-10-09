/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
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

import org.crmf.core.assetmodel.manager.AssetModelManagerInput;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AssetModelManagerRestServerTest {
  @Mock
  private AssetModelManagerInput assetModelInput;
  @Mock
  private RiskModelManagerInput riskModelInput;
  @InjectMocks
  private AssetModelManagerRestServer manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editAssetModel() {

    String assetModelDefault =  "{\n" +
      "  \"creationTime\": \" \",\n" +
      "  \"updateTime\": null,\n" +
      "  \"edges\": [],\n" +
      "  \"nodes\": [],\n" +
      "  \"graphJson\": [],\n" +
      "  \"identifier\": \"1\",\n" +
      "  \"objType\": \"AssetModel\"\n" +
      "}";

    String token = "";
    ModelObject model = new ModelObject();
    model.setJsonModel(assetModelDefault);
    model.setObjectIdentifier("assetModelIdentifier");
    manager.editAssetModel(token, model);

    verify(assetModelInput, times(1)).editAssetModel(anyString(), anyString());
    verify(riskModelInput).editAssetModel("assetModelIdentifier");
  }

  @Test
  public void loadAssetModel() throws Exception {

    String token = "";
    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.IDENTIFIER, "assetModelIdentifier");
    filter.setFilterMap(map);

    manager.loadAssetModel(token, filter);
    verify(assetModelInput).loadAssetModel(filter);
  }

  @Test
  public void loadPrimaryAssetCategoryEnum() {

    String token = "";

    List<PrimaryAssetCategoryEnum> primaryAssetCategoryList = manager.loadPrimaryAssetCategoryEnum(token);
    Assertions.assertEquals(27, primaryAssetCategoryList.size());
  }

  @Test
  public void loadSecondaryAssetCategoryEnum() {

    String token = "";

    List<SecondaryAssetCategoryEnum> secondaryAssetCategoryList = manager.loadSecondaryAssetCategoryEnum(token);
    Assertions.assertEquals(17, secondaryAssetCategoryList.size());
  }
}
