/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatScoreInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.threatmodel;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

//This class manages the serialization/deserialization of ThreatScore classes
public class ThreatScoreInstanceCreator implements JsonDeserializer<ThreatScore> {
  private static final Logger LOG = LoggerFactory.getLogger(ThreatScoreInstanceCreator.class.getName());

  @Override
  public ThreatScore deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    ThreatScore score = new ThreatScore();
    // score attribute conversion
    if (jsonObject.has("score")) {
      String scoreValue = jsonObject.get("score").getAsString();

      if (scoreValue.equals("")) {
        score.setScore(ThreatScoreEnum.VERY_HIGH);
      } else if (scoreValue.equals("VERY_LOW")) {
        score.setScore(ThreatScoreEnum.VERY_LOW);
      } else if (scoreValue.equals("LOW")) {
        score.setScore(ThreatScoreEnum.LOW);
      } else if (scoreValue.equals("MEDIUM")) {
        score.setScore(ThreatScoreEnum.MEDIUM);
      } else if (scoreValue.equals("HIGH")) {
        score.setScore(ThreatScoreEnum.HIGH);
      } else if (scoreValue.equals("VERY_HIGH")) {
        score.setScore(ThreatScoreEnum.VERY_HIGH);
      }
    } else {
      score.setScore(ThreatScoreEnum.VERY_HIGH);
    }

    // likelihood attribute conversion
    if (jsonObject.has("likelihood")) {
      String likelihood = jsonObject.get("likelihood").getAsString();

      if (likelihood.equals("")) {
        score.setLikelihood(LikelihoodEnum.VERY_HIGH);
      } else if (likelihood.equals("LOW")) {
        score.setLikelihood(LikelihoodEnum.LOW);
      } else if (likelihood.equals("MEDIUM")) {
        score.setLikelihood(LikelihoodEnum.MEDIUM);
      } else if (likelihood.equals("HIGH")) {
        score.setLikelihood(LikelihoodEnum.HIGH);
      } else if (likelihood.equals("VERY_HIGH")) {
        score.setLikelihood(LikelihoodEnum.VERY_HIGH);
      }
    } else {
      score.setLikelihood(LikelihoodEnum.VERY_HIGH);
    }

    // securityImpacts attribute conversion
    GsonBuilder gsonBuilder = new GsonBuilder();

    gsonBuilder.serializeNulls();
    Gson gson = gsonBuilder.create();

    ArrayList<SecurityImpact> impactsArray = new ArrayList<>();

    if (jsonObject.has("securityImpacts")) {
      JsonArray secImpacts = jsonObject.get("securityImpacts").getAsJsonArray();
      secImpacts.forEach(item -> {
        JsonElement obj = (JsonElement) item;
        SecurityImpact sImpact = gson.fromJson(obj, SecurityImpact.class);
        impactsArray.add(sImpact);

      });
    }

    score.setSecurityImpacts(impactsArray);
    return score;
  }

}
