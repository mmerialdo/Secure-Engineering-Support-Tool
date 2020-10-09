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

import org.crmf.core.riskassessment.project.manager.AssessmentProjectUserInput;
import org.crmf.model.riskassessment.AssessmentProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AssessmentProjectUserRestServerTest {
    @Mock
    private AssessmentProjectUserInput projectInput;
    @InjectMocks
    private AssessmentProjectUserRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editAssessmentProjectUserRoles() throws Exception {
        AssessmentProject project = new AssessmentProject();
        manager.editAssessmentProjectUserRoles("token", project);
        verify(projectInput, times(1)).editAssessmentProjectRole(project);
    }
}
