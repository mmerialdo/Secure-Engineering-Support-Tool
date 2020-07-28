/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="GASFSecurityRequirementInstanceCreator.java"
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

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.RequirementSourceEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.requirement.SecurityRequirementSource;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

//The json structure to be returned to the client is quite different
//from the Java GASFSecurityRequirement structure
//we then need to serialize/deserialize accordingly
public class GASFSecurityRequirementInstanceCreator implements JsonDeserializer<SecurityRequirement> {
	private static final Logger LOG = LoggerFactory.getLogger(GASFSecurityRequirementInstanceCreator.class.getName());

	@Override
	public SecurityRequirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		try {
			JsonObject jsonObject = json.getAsJsonObject();

			SecurityRequirement rq = new SecurityRequirement();
			rq.setSource(RequirementSourceEnum.GASF);
			rq.setObjType(SESTObjectTypeEnum.SafeguardModel);

			if (!jsonObject.get("item_type").isJsonNull()) {
				int item_type = jsonObject.get("item_type").getAsInt();

				// Item type 1000 is the root node. We are not interested on
				// that
				if (item_type == 1000) {
					return null;
				}
				if (item_type == 1003) {
					rq.setElementType(ElementTypeEnum.Element);
				}
				if (item_type == 1007) {
					rq.setElementType(ElementTypeEnum.Category);
				}
			}
			if (!jsonObject.get("child_index").isJsonNull()) {
				String child_index = jsonObject.get("child_index").getAsString();
				rq.setPriority(child_index);
			}
			if (!jsonObject.get("item_id").isJsonNull()) {
				String item_id = jsonObject.get("item_id").getAsString();
				rq.setIdentifier(item_id);
			} else {
				UUID uuid = UUID.randomUUID();
				rq.setIdentifier(uuid.toString());
				LOG.error("Deserialization GASF Json, GASF element without unique id. Added unique ID:: "
						+ uuid.toString());
			}
			if (!jsonObject.get("item_version").isJsonNull()) {
				int item_version = jsonObject.get("item_version").getAsInt();
				rq.setVersion(Integer.toString(item_version));
			}
			if (jsonObject.has("parent_id")) {
				if (!jsonObject.get("parent_id").isJsonNull()) {
					String parent_id = jsonObject.get("parent_id").getAsString();
					rq.setParentId(parent_id);
				}
			}

			if (jsonObject.has("singleFields")) {
				JsonObject singleFields = jsonObject.get("singleFields").getAsJsonObject();

				if (singleFields.has("2003")) {
					if (!singleFields.get("2003").isJsonNull()) {
						String title = singleFields.get("2003").getAsString();
						rq.setTitle(title);
					}
				}
				if (singleFields.has("2051") && !singleFields.get("2051").isJsonNull()) {
					String id = singleFields.get("2051").getAsString();
					rq.setId(id);
				}
				if (singleFields.has("2004")) {
					if (!singleFields.get("2004").isJsonNull()) {
						String description = singleFields.get("2004").getAsString();
						rq.setDescription(description);
					}
				}
				if (singleFields.has("2005")) {
					if (!singleFields.get("2005").isJsonNull()) {
						String note = singleFields.get("2005").getAsString();
						LOG.info("note: " + note);
						rq.setNote(note);
					}
				}

			} else {
				LOG.error("Deserialization GASF Json, GASF element without singleFields: " + rq.getIdentifier());
			}

			if (jsonObject.has("multipleFields")) {

				rq.setSourcesJson(jsonObject.get("multipleFields").toString());
		/*		JsonObject multipleFields = jsonObject.get("multipleFields").getAsJsonObject();

				if (multipleFields.has("2009")) {
					JsonObject sourceJson = multipleFields.get("2009").getAsJsonObject();

					Set<String> keys = sourceJson.keySet();

					for (String key : keys) {
						String source = sourceJson.get(key).getAsString();

						SecurityRequirementSource requirementSource = new SecurityRequirementSource();
						requirementSource.setSource(source);
						requirementSource.setSourceReference(key);

						rq.getSources().add(requirementSource);
					}
				}
				if (multipleFields.has("2010")) {
					JsonObject sourceReferenceJson = multipleFields.get("2010").getAsJsonObject();

					Set<String> keys = sourceReferenceJson.keySet();

					for (String key : keys) {
						String sourceReference = sourceReferenceJson.get(key).getAsString();

						for (SecurityRequirementSource source : rq.getSources()) {
							if (source.getSourceReference().equals(key)) {
								source.setSourceReference(sourceReference);
							}
						}
					}
				} */
			}

			return rq;
		} catch (Exception e) {
			LOG.error("Deserialization GASF Json exception: " + e.getMessage(), e);
			return null;
		}
	}

}
