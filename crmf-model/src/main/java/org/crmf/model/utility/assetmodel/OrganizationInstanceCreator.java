/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="OrganizationInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.assetmodel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.riskassessmentelements.NodeTypeEnum;
import org.crmf.model.riskassessmentelements.Organization;

import java.lang.reflect.Type;

//This class manages the deserialization of Organization classes
class OrganizationInstanceCreator implements JsonDeserializer<Organization>, JsonSerializer<Organization> {

  @Override
  public JsonElement serialize(Organization organization, Type arg1, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    return jsonObject;
  }

  @Override
  public Organization deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
    Organization organization = new Organization();
    organization.setNodeType(NodeTypeEnum.Organization);

    return organization;
  }

}
