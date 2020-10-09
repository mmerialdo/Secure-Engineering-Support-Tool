package org.crmf.proxy.core.riskassessment.project.rest;/* --------------------------------------------------------------------------------------------------------------------
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

import org.crmf.core.riskassessment.project.manager.AssessmentProjectInput;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AssessmentProjectRestServerTest {
    @Mock
    private AssessmentProjectInput projectInput;
    @InjectMocks
    private AssessmentProjectRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAssessmentProject() throws Exception {

        AssessmentProject project = new AssessmentProject();
        when(projectInput.createAssessmentProject(project)).thenReturn("projectIdentifier");

        ResponseMessage message = manager.createAssessmentProject("token", project);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("projectIdentifier", message.getResponse());
    }

    @Test
    public void editAssessmentProject() throws Exception {

        AssessmentProject project = new AssessmentProject();

        manager.editAssessmentProject("token", project);

        verify(projectInput, times(1)).editAssessmentProject(project);
    }

    @Test
    public void deleteAssessmentProcedure() {

        manager.deleteAssessmentProject("token", "projectIdentifier");

        verify(projectInput, times(1)).deleteAssessmentProject("projectIdentifier");
    }

    @Test
    public void loadAssessmentProcedureList() throws Exception {

        manager.loadAssessmentProjectList(
                "eyJ1c2VybmFtZSI6ImFkbWluMDEiLCJwYXNzd29yZCI6IjUzY2E5MWFkLTFhMzMtNGM0OS05MzljLTcyMmFkMDBjZjJkMSJ9");

        verify(projectInput, times(1)).loadAssessmentProjectList("admin01", "Read");
    }

    @Test
    public void loadAssessmentProject() {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        filter.setFilterMap(map);

        manager.loadAssessmentProject("token", filter);

        verify(projectInput, times(1)).loadAssessmentProject("projectIdentifier");
    }
}
