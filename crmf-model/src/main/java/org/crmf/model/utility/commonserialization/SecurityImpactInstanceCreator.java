/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityImpactInstanceCreator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility.commonserialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.riskassessmentelements.SecurityImpact;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;

import java.lang.reflect.Type;
import java.util.ArrayList;

//This class manages the serialization/deserialization of SecurityImpact classes
public class SecurityImpactInstanceCreator implements JsonDeserializer<SecurityImpact> , JsonSerializer<SecurityImpact> {

	public static final String IMPACT = "impact";
	public static final String SCOPE = "scope";
	public static final String TECNICAL_IMPACTS = "tecnicalImpacts";

	@Override
	public JsonElement serialize(SecurityImpact securityImpact, Type arg1, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addProperty(IMPACT, securityImpact.getImpact().toString());
		jsonObject.addProperty(SCOPE, securityImpact.getScope().toString());
		
        JsonArray technicalImpacts = new JsonArray();
		
        securityImpact.getTechnicalImpacts().forEach(item -> {
			
        	technicalImpacts.add(item);
		});
	
        jsonObject.add(TECNICAL_IMPACTS, technicalImpacts);
		
		return jsonObject;
	}
	
	@Override
	public SecurityImpact deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();

		SecurityImpact securityImpact = new SecurityImpact();
		securityImpact.setTechnicalImpacts(new ArrayList<>());

		if (!jsonObject.get(IMPACT).isJsonNull()) {
			String impact = jsonObject.get(IMPACT).getAsString();

			if (impact.equals("")) {
				securityImpact.setImpact(ImpactEnum.LOW);
			} else if (impact.equals("LOW")) {
				securityImpact.setImpact(ImpactEnum.LOW);
			} else if (impact.equals("MEDIUM")) {
				securityImpact.setImpact(ImpactEnum.MEDIUM);
			} else if (impact.equals("HIGH")) {
				securityImpact.setImpact(ImpactEnum.HIGH);
			} else if (impact.equals("CRITICAL")) {
				securityImpact.setImpact(ImpactEnum.CRITICAL);
			}
		} else {
			securityImpact.setImpact(ImpactEnum.LOW);
		}

		if (!jsonObject.get(SCOPE).isJsonNull()) {
			String scope = jsonObject.get(SCOPE).getAsString();

			if (scope.equals("")) {
				securityImpact.setScope(SecurityImpactScopeEnum.Other);
			} else if (scope.equals(SecurityImpactScopeEnum.Confidentiality.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Confidentiality);
			} else if (scope.equals(SecurityImpactScopeEnum.Integrity.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Integrity);
			} else if (scope.equals(SecurityImpactScopeEnum.Availability.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Availability);
			} else if (scope.equals(SecurityImpactScopeEnum.Efficiency.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Efficiency);
			} else if (scope.equals(SecurityImpactScopeEnum.Other.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Other);
			} else if (scope.equals(SecurityImpactScopeEnum.NonRepudiation.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.NonRepudiation);
			} else if (scope.equals(SecurityImpactScopeEnum.AccessControl.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.AccessControl);
			} else if (scope.equals(SecurityImpactScopeEnum.Accountability.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Accountability);
			} else if (scope.equals(SecurityImpactScopeEnum.Authorization.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Authorization);
			} else if (scope.equals(SecurityImpactScopeEnum.Authentication.toString())) {
				securityImpact.setScope(SecurityImpactScopeEnum.Authentication);
			}
		} else {
			securityImpact.setScope(SecurityImpactScopeEnum.Other);
		}

		if (!jsonObject.get(TECNICAL_IMPACTS).isJsonNull()) {
			JsonArray technicalImpacts = jsonObject.get(TECNICAL_IMPACTS).getAsJsonArray();

			technicalImpacts.forEach(item -> {
				String technicalImpact = item.getAsString();

				securityImpact.getTechnicalImpacts().add(technicalImpact);
			});
		}

		return securityImpact;
	}

}
