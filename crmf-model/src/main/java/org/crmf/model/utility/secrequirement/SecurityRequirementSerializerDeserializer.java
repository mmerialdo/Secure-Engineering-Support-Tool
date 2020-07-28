/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityRequirementSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.secrequirement;

import java.io.Console;
import java.util.ArrayList;

import org.crmf.model.requirement.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

//This class manages the custom serialization/deserialization of the SecurityRequirement
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class SecurityRequirementSerializerDeserializer {
  private static final Logger LOG = LoggerFactory.getLogger(SecurityRequirementSerializerDeserializer.class.getName());

  //Creates JSON from POJO
  //This method transforms a SecurityRequirements list into a Json with a tree structure, more readable
  public String getJSONStringFromSRs(ArrayList<SecurityRequirement> srs) {

    try {

      ArrayList<SecurityRequirement> treeSrs = new ArrayList<>();
      String rootId = "";

      for (SecurityRequirement sr : srs) {
        if (sr == null) {
          continue;
        }

        if (sr.getId().equals("GASF_0000")) {
          rootId = sr.getIdentifier();
        }
      }

      for (SecurityRequirement sr : srs) {
        if (sr == null) {
          continue;
        }

        if (sr.getParentId().equals(rootId)) {
          treeSrs.add(sr);
          continue;
        }
      }

      for (SecurityRequirement treeSr : treeSrs) {
        for (SecurityRequirement sr : srs) {
          if (sr == null) {
            continue;
          }
          if (sr.getId().equals("GASF_0000")) {
            continue;
          }
          if (sr.getParentId().equals(rootId)) {
            continue;
          }

          String parentId = sr.getParentId();

          if (treeSr.getIdentifier().equals(parentId)) {
            treeSr.getChildren().add(sr);
          }
        }
        buildSecurityRequirementsTree(treeSr, srs, rootId);

      }


      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.serializeNulls();
      Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();

      return gson.toJson(treeSrs);
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return null;
    }
  }

  private void buildSecurityRequirementsTree(SecurityRequirement treeSr, ArrayList<SecurityRequirement> srs, String rootId) {
    for (SecurityRequirement innerTreeSr : treeSr.getChildren()) {
      for (SecurityRequirement sr : srs) {
        if (sr == null) {
          continue;
        }
        if (sr.getId().equals("GASF_0000")) {
          continue;
        }
        if (sr.getParentId().equals(rootId)) {
          continue;
        }

        String parentId = sr.getParentId();

        if (innerTreeSr.getIdentifier().equals(parentId)) {
          innerTreeSr.getChildren().add(sr);
        }
      }
      buildSecurityRequirementsTree(innerTreeSr, srs, rootId);

    }

  }

  //This method transforms the GASF json in a POJO collection following SEST SecurityRequirement data model
  //It can be used in order to save it on the database
  public ArrayList<SecurityRequirement> getSRsFromGASFJSONString(String srJsonString) {
    GsonBuilder gsonBuilder = new GsonBuilder();

    gsonBuilder.registerTypeAdapter(SecurityRequirement.class, new GASFSecurityRequirementInstanceCreator());

    gsonBuilder.serializeNulls();
    Gson gson = gsonBuilder.create();

    ArrayList<SecurityRequirement> srs = new ArrayList<>();

    try {
      LOG.info("Starting Deserialization GASF Json");

      srs = gson.fromJson(srJsonString, new TypeToken<ArrayList<SecurityRequirement>>() {
      }.getType());


    } catch (Exception e) {
      LOG.error("Deserialization GASF Json error:: " + e.getMessage());
      return null;
    }

    if (srs == null) {
      LOG.info("Conversion of json into Security Requirements ArrayList returned null");
      return null;
    }

    return srs;
  }
}
