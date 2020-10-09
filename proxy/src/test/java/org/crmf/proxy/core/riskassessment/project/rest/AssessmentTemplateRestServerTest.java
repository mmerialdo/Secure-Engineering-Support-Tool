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

import org.crmf.core.riskassessment.project.manager.AssessmentTemplateInput;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
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

public class AssessmentTemplateRestServerTest {
    @Mock
    private AssessmentTemplateInput templateInput;
    @InjectMocks
    private AssessmentTemplateRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAssessmentTemplate() {

        AssessmentProfile profile = new AssessmentProfile();
        profile.setIdentifier("profileIdentifier");
        AssessmentTemplate template = new AssessmentTemplate();
        profile.setTemplates(Arrays.asList(template));
        when(templateInput.createAssessmentTemplate(template, "profileIdentifier")).thenReturn("templateIdentifier");

        ResponseMessage message = manager.createAssessmentTemplate("token", profile);

        Assertions.assertNotNull(message);
        Assertions.assertEquals("templateIdentifier", message.getResponse());
    }

    @Test
    public void loadAssessmentTemplateList() {

        manager.loadAssessmentTemplateList("token");

        verify(templateInput, times(1)).loadAssessmentTemplateList();
    }

    @Test
    public void loadAssessmentTemplate() {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "templateIdentifier");
        filter.setFilterMap(map);

        manager.loadAssessmentTemplate("token", filter);

        verify(templateInput, times(1)).loadAssessmentTemplate(filter);
    }
}
