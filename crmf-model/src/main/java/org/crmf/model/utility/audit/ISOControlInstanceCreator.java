/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ISOControlInstanceCreator.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.model.utility.audit;

import com.google.gson.*;
import org.crmf.model.audit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ISOControlInstanceCreator implements JsonDeserializer<ISOControl> {

    private static final Logger LOG = LoggerFactory.getLogger(ISOControlInstanceCreator.class.getName());

    public ISOControlInstanceCreator() {
        super();
    }


    @Override
    public ISOControl deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
            throws JsonParseException {

        return deserializeISOControl(json, context);
    }

    private ISOControl deserializeISOControl(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        ISOControl control = new ISOControl();

        if (jsonObject == null) {
            return null;
        }

        if (!jsonObject.get("ClauseId").isJsonNull()) {
            String clauseId = jsonObject.get("ClauseId").getAsString();

            control.setClauseId(clauseId);
        }
        if (!jsonObject.get("Clause").isJsonNull()) {
            String clause = jsonObject.get("Clause").getAsString();

            control.setClause(clause);
        }
        if (!jsonObject.get("ObjectiveId").isJsonNull()) {
            String objectiveId = jsonObject.get("ObjectiveId").getAsString();

            control.setObjectiveId(objectiveId);
        }
        if (!jsonObject.get("Objective").isJsonNull()) {
            String objective = jsonObject.get("Objective").getAsString();

            control.setObjective(objective);
        }
        if (!jsonObject.get("ControlId").isJsonNull()) {
            String controlId = jsonObject.get("ControlId").getAsString();

            control.setControlId(controlId);
        }
        if (!jsonObject.get("Control").isJsonNull()) {
            String controlS = jsonObject.get("Control").getAsString();

            control.setControl(controlS);
        }

        return control;
    }

}
