/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="ISOControlsSerializerDeserializer.java"
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.crmf.model.audit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISOControlsSerializerDeserializer {
    private static final Logger LOG = LoggerFactory.getLogger(ISOControlsSerializerDeserializer.class.getName());

    //Creates POJO from JSON
    public ISOControls getISOControlsFromJSONString(String json){

        LOG.info("----------- serializer getISOControlsFromJSONString!!!!!");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ISOControls.class, new ISOControlsInstanceCreator());

        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        ISOControls controls = null;

        try {
            controls = gson.fromJson(json, ISOControls.class);

        } catch (Exception e) {
            LOG.error("Deserialization error: " + e.getMessage(), e);
            return null;
        }

        return controls;

    }
}
