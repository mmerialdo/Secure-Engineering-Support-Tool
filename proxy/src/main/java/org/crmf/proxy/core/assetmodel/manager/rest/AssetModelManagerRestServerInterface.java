/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelManagerRestServerInterface.java"
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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

import java.util.List;

public interface AssetModelManagerRestServerInterface {

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  void editAssetModel(ModelObject assetModel) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  ModelObject loadAssetModel(GenericFilter filter) throws Exception;

  @POST
  @Produces("application/json")
  List<PrimaryAssetCategoryEnum> loadPrimaryAssetCategoryEnum() throws Exception;

  @POST
  @Produces("application/json")
  public List<SecondaryAssetCategoryEnum> loadSecondaryAssetCategoryEnum() throws Exception;
}
