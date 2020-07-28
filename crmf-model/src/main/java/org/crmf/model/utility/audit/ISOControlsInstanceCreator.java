/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ISOControlsInstanceCreator.java"
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
import org.crmf.model.audit.ISOControl;
import org.crmf.model.audit.ISOControls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ISOControlsInstanceCreator implements JsonDeserializer<ISOControls> {
    private ISOControlInstanceCreator convertor = new ISOControlInstanceCreator();
    private static final Logger LOG = LoggerFactory.getLogger(ISOControlsInstanceCreator.class.getName());

    @Override
    public ISOControls deserialize(JsonElement json, Type arg1, JsonDeserializationContext context)
            throws JsonParseException {

        ISOControls isoControls = new ISOControls();
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray children = jsonObject.getAsJsonArray("ISO 27002 controls");

        ArrayList<ISOControl> controls = new ArrayList<ISOControl>();

        if (children != null && !children.isJsonNull()) {
            for (int i = 0; i < children.size(); i++) {
                ISOControl control = convertor.deserialize(children.get(i), arg1, context);
                if (control != null) {
                    control.setControlJson(children.get(i).toString());
                    controls.add(control);
                }
            }
            isoControls.setControls(controls);
        }
        return isoControls;
    }
}
