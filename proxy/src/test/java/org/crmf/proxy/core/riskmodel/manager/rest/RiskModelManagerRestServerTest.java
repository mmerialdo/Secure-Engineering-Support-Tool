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
package org.crmf.proxy.core.riskmodel.manager.rest;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiskModelManagerRestServerTest {
    @Mock
    private RiskModelManagerInput riskModelInput;
    @InjectMocks
    private RiskModelManagerRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editRiskModel() {
        ModelObject riskModelObject = new ModelObject();
        riskModelObject.setJsonModel("{}");
        riskModelObject.setObjectIdentifier("risModelIdentifier");

        manager.editRiskModel("token", riskModelObject);

        Mockito.verify(riskModelInput, Mockito.times(1)).editRiskModel("{}", "risModelIdentifier");
    }

    @Test
    public void editRiskScenario() {
        ModelObject riskModelObject = new ModelObject();
        riskModelObject.setJsonModel("{}");
        riskModelObject.setObjectIdentifier("risModelIdentifier");

        manager.editRiskScenario("token", riskModelObject);

        Mockito.verify(riskModelInput, Mockito.times(1)).editRiskScenario("{}", "risModelIdentifier");
    }

    @Test
    public void loadRiskModel() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        filter.setFilterMap(map);

        manager.loadRiskModel("token", filter);

        Mockito.verify(riskModelInput, Mockito.times(1)).loadRiskModel(filter);
    }

    @Test
    public void updateScenarioRepository() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        filter.setFilterMap(map);

        manager.updateScenarioRepository("token", "MEHARI");

        Mockito.verify(riskModelInput, Mockito.times(1)).updateScenarioRepository("MEHARI");
    }

    @Test
    public void updateScenarioRepositoryNotMehari() throws Exception {

        Exception exception = Assertions.assertThrows(RemoteComponentException.class, () -> {
            manager.updateScenarioRepository("token", "Custom");
        });

        Assertions.assertEquals(ApiExceptionEnum.COMMAND_EXCEPTION, exception.getMessage());
    }

    @Test
    public void createRiskScenarioReference() throws Exception {
        RiskScenarioReference riskScenarioReference = new RiskScenarioReference();

        manager.createRiskScenarioReference("token", riskScenarioReference);

        Mockito.verify(riskModelInput, Mockito.times(1)).insertRiskScenarioReference(riskScenarioReference);
    }

    @Test
    public void updateRiskScenarioReference() throws Exception {
        RiskScenarioReference riskScenarioReference = new RiskScenarioReference();

        manager.updateRiskScenarioReference("token", riskScenarioReference);

        Mockito.verify(riskModelInput, Mockito.times(1)).editRiskScenarioReference(riskScenarioReference);
    }

    @Test
    public void deleteRiskScenarioReference() throws Exception {
        List<String> identifiers = Arrays.asList("scenario1", "scenario2");

        manager.deleteRiskScenarioReference("token", identifiers);

        Mockito.verify(riskModelInput, Mockito.times(1)).deleteRiskScenarioReference(identifiers);
    }

    @Test
    public void loadRiskScenarioReference() {

        manager.loadRiskScenarioReference("token");

        Mockito.verify(riskModelInput, Mockito.times(1)).getRiskScenarioReference();
    }
}
