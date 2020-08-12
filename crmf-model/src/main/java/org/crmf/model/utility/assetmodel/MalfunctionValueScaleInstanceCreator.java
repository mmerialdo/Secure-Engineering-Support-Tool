/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="MalfunctionValueScaleInstanceCreator.java"
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
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.MalfunctionValueScale;

import java.lang.reflect.Type;

//This class manages the deserialization of MalfunctionValueScale classes
class MalfunctionValueScaleInstanceCreator implements JsonDeserializer<MalfunctionValueScale> , JsonSerializer<MalfunctionValueScale> {

	public static final String SERIOUSNESS = "seriousness";
	public static final String DESCRIPTION = "description";

	@Override
	public JsonElement serialize(MalfunctionValueScale mal, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(SERIOUSNESS, mal.getSeriousness().toString());
		jsonObject.addProperty(DESCRIPTION, mal.getDescription());
		
		return jsonObject;
	}

	@Override
	public MalfunctionValueScale deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		MalfunctionValueScale malfunctionValueScale = new MalfunctionValueScale();

		if (!jsonObject.get(SERIOUSNESS).isJsonNull()) {
			String seriousness = jsonObject.get(SERIOUSNESS).getAsString();

			if (seriousness.equals("")) {
				malfunctionValueScale.setSeriousness(ImpactEnum.LOW);
			}
			else{
				malfunctionValueScale.setSeriousness(ImpactEnum.valueOf(seriousness));
			}
		} else {
			malfunctionValueScale.setSeriousness(ImpactEnum.LOW);
		}

		if (!jsonObject.get(DESCRIPTION).isJsonNull()) {
			String description = jsonObject.get(DESCRIPTION).getAsString();
			malfunctionValueScale.setDescription(description);
		}

		return malfunctionValueScale;
	}

}