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

import org.crmf.core.riskassessment.project.manager.AssessmentProfileInput;
import org.crmf.model.riskassessment.AssessmentProfile;
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

public class AssessmentProfileRestServerTest {

    @Mock
    private AssessmentProfileInput profileInput;
    @InjectMocks
    private AssessmentProfileRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAssessmentProfile() throws Exception {

        AssessmentProfile profile = new AssessmentProfile();
        when(profileInput.createAssessmentProfile(profile)).thenReturn("profileIdentifier");

        ResponseMessage message = manager.createAssessmentProfile("token", profile);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("profileIdentifier", message.getResponse());
    }

    @Test
    public void editAssessmentProfile() {

        AssessmentProfile profile = new AssessmentProfile();

        manager.editAssessmentProfile("token", profile);

        verify(profileInput, times(1)).editAssessmentProfile(profile);
    }

    @Test
    public void deleteAssessmentProfile() {

        manager.deleteAssessmentProfile("token", "profileIdentifier");

        verify(profileInput, times(1)).deleteAssessmentProfile("profileIdentifier");
    }

    @Test
    public void loadAssessmentProfileList() {

        manager.loadAssessmentProfileList("token");

        verify(profileInput, times(1)).loadAssessmentProfileList();
    }

    @Test
    public void loadAssessmentProfile() {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "profileIdentifier");
        filter.setFilterMap(map);

        manager.loadAssessmentProfile("token", filter);

        verify(profileInput, times(1)).loadAssessmentProfile("profileIdentifier");
    }
}
