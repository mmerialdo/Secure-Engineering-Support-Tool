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

import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.riskmodel.manager.RiskScenariosReferenceImporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class RiskScenarioImportExportRestServerTest {

    @Mock
    private RiskServiceInterface riskDBService;
    @Mock
    private RiskScenariosReferenceImporter riskScenariosImporter;
    @InjectMocks
    private RiskScenarioImportExportRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void exportRiskScenario() throws Exception {

        manager.exportRiskScenario("token");

        Mockito.verify(riskDBService, Mockito.times(1)).getRiskScenarioReference();
    }

    @Test
    public void importRiskScenario() throws Exception {

        MultipartFile multipartFile = new MockMultipartFile("riskScenario", "riskScenario.txt", MediaType.TEXT_PLAIN_VALUE,
                "riskScenarios".getBytes());

        manager.importRiskScenario("token", multipartFile);

        Mockito.verify(riskScenariosImporter, Mockito.times(1)).importRiskScenariosFromInput(ArgumentMatchers.any());
    }
}
