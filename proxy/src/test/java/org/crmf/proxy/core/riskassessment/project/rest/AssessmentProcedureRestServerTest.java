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
package org.crmf.proxy.core.riskassessment.project.rest;

import org.crmf.core.riskassessment.project.manager.AssessmentProcedureInput;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AssessmentProcedureRestServerTest {
    @Mock
    private AssessmentProcedureInput procedureInput;
    @InjectMocks
    private AssessmentProcedureRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAssessmentProcedure() {

        AssessmentProcedure procedure = new AssessmentProcedure();
        AssessmentProject project = new AssessmentProject();
        project.setProcedures(Arrays.asList(procedure));
        when(procedureInput.createAssessmentProcedure(procedure, project.getIdentifier())).thenReturn("procedureIdentifier");

        ResponseMessage message = manager.createAssessmentProcedure("token", project);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("procedureIdentifier", message.getResponse());
    }

    @Test
    public void editAssessmentProcedure() {

        AssessmentProcedure procedure = new AssessmentProcedure();

        manager.editAssessmentProcedure("token", procedure);

        verify(procedureInput, times(1)).editAssessmentProcedure(procedure);
    }

    @Test
    public void deleteAssessmentProcedure() {

        manager.deleteAssessmentProcedure("token", "procedureIdentifier");

        verify(procedureInput, times(1)).deleteAssessmentProcedure("procedureIdentifier");
    }

    @Test
    public void loadAssessmentProcedureList() {

        manager.loadAssessmentProcedureList("token");

        verify(procedureInput, times(1)).loadAssessmentProcedureList();
    }

    @Test
    public void loadAssessmentProcedure() {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "procedureIdentifier");
        filter.setFilterMap(map);

        manager.loadAssessmentProcedure("token", filter);

        verify(procedureInput, times(1)).loadAssessmentProcedure(filter);
    }
}
