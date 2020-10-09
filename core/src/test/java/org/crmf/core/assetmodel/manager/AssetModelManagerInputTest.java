/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateInput.java"
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

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.persistency.domain.asset.SestAssetModel;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/************************************************************************
 * Created: 16/09/2020                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class AssetModelManagerInputTest {
  @Mock
  private AssetServiceInterface assetService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private SestObjServiceInterface sestObjService;
  @InjectMocks
  private AssetModelManagerInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editAssetModel() {
    String assetModelJson = "some text of asset model json";
    String assetModelIdentifier = "asset model identifer";

    manager.editAssetModel(assetModelJson, assetModelIdentifier);

    verify(assetService, times(1)).update(assetModelJson, assetModelIdentifier);
  }

  @Test
  public void loadAssetModel() throws Exception {
    String procedureIdentifier = "procedureIdentifier";
    String assetModelIdentifier = "asset model identifer";

    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);
    AssetModel assetModel = new AssetModel();
    assetModel.setIdentifier(assetModelIdentifier);
    SestAssetModel assetModelDb = new SestAssetModel();
    assetModelDb.setSestobjId(assetModelIdentifier);
    assetModelDb.setAssetModelJson("this is an asset model for test");
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    procedure.setAssetModel(assetModel);
    SESTObject sestObject = new SESTObject();
    sestObject.setIdentifier(assetModelIdentifier);
    sestObject.setLockedBy("someUserLocking");

    ModelObject modelObjectExpected = new ModelObject();
    modelObjectExpected.setJsonModel("this is an asset model for test");
    modelObjectExpected.setObjectIdentifier(assetModelIdentifier);
    modelObjectExpected.setLockedBy("someUserLocking");

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(assetService.getByIdentifier(assetModelIdentifier)).thenReturn(assetModelDb);
    when(sestObjService.getByIdentifier(assetModelIdentifier)).thenReturn(sestObject);

    ModelObject modelObjectReturn = manager.loadAssetModel(filter);

    Assertions.assertEquals(modelObjectExpected.getJsonModel(), modelObjectReturn.getJsonModel());
    Assertions.assertEquals(modelObjectExpected.getObjectIdentifier(), modelObjectReturn.getObjectIdentifier());
    Assertions.assertEquals(modelObjectExpected.getLockedBy(), modelObjectReturn.getLockedBy());
  }

  @Test
  public void loadAssetModelNullProcedure() {
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(new HashMap<>());

    Assertions.assertThrows(Exception.class, () -> {
      manager.loadAssetModel(filter);
    });
  }
}
