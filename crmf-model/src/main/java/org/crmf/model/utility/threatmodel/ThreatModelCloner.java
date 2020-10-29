/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelCloner.java"
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

import org.crmf.model.riskassessment.ThreatModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

//This class is responsible for cloning ThreatModels from Json strings and POJO
public class ThreatModelCloner {
    public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
    private String identifier;

    public ThreatModel clone(ThreatModel tm) {

        ThreatModelSerializerDeserializer tmsd = new ThreatModelSerializerDeserializer();
        String tmJson = tmsd.getJSONStringFromTM(tm);

        ThreatModel tmNew = tmsd.getTMFromJSONString(tmJson);

        UUID uuid = UUID.randomUUID();
        tmNew.setIdentifier(uuid.toString());

        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
        Date now = new Date();
        tmNew.setCreationTime(df.format(now));
        tmNew.setUpdateTime(df.format(now));

        identifier = tmNew.getIdentifier();
        return tmNew;
    }

    public String clone(String tmJson) {

        ThreatModelSerializerDeserializer tmsd = new ThreatModelSerializerDeserializer();

        ThreatModel tm = tmsd.getTMFromJSONString(tmJson);

        UUID uuid = UUID.randomUUID();
        tm.setIdentifier(uuid.toString());
        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
        Date now = new Date();
        tm.setCreationTime(df.format(now));
        tm.setUpdateTime(df.format(now));


        identifier = tm.getIdentifier();
        return tmsd.getJSONStringFromTM(tm);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
