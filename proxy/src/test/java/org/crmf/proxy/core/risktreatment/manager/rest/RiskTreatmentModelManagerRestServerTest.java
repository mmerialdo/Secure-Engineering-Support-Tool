/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.proxy.core.risktreatment.manager.rest;

import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.proxy.core.risktreatmentmodel.manager.rest.RiskTreatmentModelManagerRestServer;
import org.crmf.risktreatmentmodel.manager.RiskTreatmentModelManagerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

public class RiskTreatmentModelManagerRestServerTest {
    @Mock
    private RiskTreatmentModelManagerInput riskTreatmentModelInput;
    @InjectMocks
    private RiskTreatmentModelManagerRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editRiskTreatmentModel() {
        ModelObject riskTreatmentModel = new ModelObject();
        riskTreatmentModel.setJsonModel("{}");
        riskTreatmentModel.setObjectIdentifier("riskTreatmentIdentifier");
        manager.editRiskTreatmentModel("token", riskTreatmentModel);

        Mockito.verify(riskTreatmentModelInput).editRiskTreatmentModel("{}", "riskTreatmentIdentifier");
    }

    @Test
    public void editRiskTreatmentModelDetail() throws Exception {
        ModelObject riskTreatmentModel = new ModelObject();
        riskTreatmentModel.setJsonModel("{}");
        riskTreatmentModel.setObjectIdentifier("riskTreatmentIdentifier");
        manager.editRiskTreatmentModelDetail("token", riskTreatmentModel);

        Mockito.verify(riskTreatmentModelInput).editRiskTreatmentModelDetail("{}", "riskTreatmentIdentifier");
    }

    @Test
    public void loadRiskTreatmentModel() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "RiskTreatmentIdentifier");
        filter.setFilterMap(map);

        manager.loadRiskTreatmentModel("token", filter);

        Mockito.verify(riskTreatmentModelInput).loadRiskTreatmentModel(filter);
    }

    @Test
    public void loadRiskTreatmentModelDetail() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "RiskTreatmentIdentifier");
        filter.setFilterMap(map);

        manager.loadRiskTreatmentModelDetail("token", filter);

        Mockito.verify(riskTreatmentModelInput).loadRiskTreatmentModelDetail(filter);
    }

    @Test
    public void calculateRiskTreatmentModel() throws Exception {

        ModelObject riskTreatmentModel = new ModelObject();
        riskTreatmentModel.setJsonModel("{}");
        riskTreatmentModel.setObjectIdentifier("riskTreatmentIdentifier");
        manager.calculateRiskTreatmentModel("token", riskTreatmentModel);

        Mockito.verify(riskTreatmentModelInput).calculateRiskTreatmentModel("{}", "riskTreatmentIdentifier");
    }

    @Test
    public void calculateRiskTreatmentModelDetail() throws Exception {

        ModelObject riskTreatmentModel = new ModelObject();
        riskTreatmentModel.setJsonModel("{}");
        riskTreatmentModel.setObjectIdentifier("riskTreatmentIdentifier");
        manager.calculateRiskTreatmentModelDetail("token", riskTreatmentModel);

        Mockito.verify(riskTreatmentModelInput).calculateRiskTreatmentModelDetail("{}", "riskTreatmentIdentifier");
    }
}
