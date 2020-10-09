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
package org.crmf.proxy.core.report.manager.rest;

import org.crmf.model.riskassessmentelements.ImpactEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.reportgenerator.manager.ReportGeneratorInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ReportGeneratorRestServerTest {

    @Mock
    private ReportGeneratorInput reportgeneratorInput;
    @InjectMocks
    private ReportGeneratorRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editReport() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.PROCEDURE, "procedureIdentifier");
        filter.setFilterMap(map);

        manager.editReport(token, filter);

        verify(reportgeneratorInput, times(1)).editReport("procedureIdentifier");
    }

    @Test
    public void editLightReport() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.PROCEDURE, "procedureIdentifier");
        map.put(GenericFilterEnum.IMPACT, "LOW");
        filter.setFilterMap(map);

        manager.editLightReport(token, filter);

        verify(reportgeneratorInput, times(1)).editLightReport("procedureIdentifier", ImpactEnum.LOW);
    }

    @Test
    public void editISOReport() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.PROCEDURE, "procedureIdentifier");
        filter.setFilterMap(map);

        manager.editISOReport(token, filter);

        verify(reportgeneratorInput, times(1)).editISOReport("procedureIdentifier");
    }

    @Test
    public void downloadReport() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.PROCEDURE, "procedureIdentifier");
        filter.setFilterMap(map);

        manager.downloadReport(token, filter);

        verify(reportgeneratorInput, times(1)).download(filter);
    }
}
