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
package org.crmf.proxy.core.requirement.manager.rest;

import org.crmf.core.riskassessment.project.requirement.SystemProjectRequirementInput;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.requirementimport.processor.SystemRequirementExcelProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class SystemRequirementRestServerTest {

    @Mock
    private SystemProjectRequirementInput requirementInput;
    @Mock
    private SystemRequirementExcelProcessor processor;
    @InjectMocks
    private SystemRequirementRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void listRequirementLoadedFile() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.SYS_PROJECT, "sysprjIdentifier");
        filter.setFilterMap(map);

        when(processor.listRequirementLoadedFile(filter)).thenReturn(Arrays.asList("file1.txt","file2.txt"));
        List<String> filenames = manager.listRequirementLoadedFile(token, filter);

        Assertions.assertEquals(Arrays.asList("\"file1.txt\"","\"file2.txt\""), filenames);
    }

    @Test
    public void uploadRequirement() throws Exception {
        String token = "";
        MultipartFile multipartFile = new MockMultipartFile("req-file", "req.txt",
                null, "test data".getBytes());

        manager.uploadRequirement(token, multipartFile,"filename","sysprojectIdentifier");

        verify(processor, times(1)).process(any(), anyString(), anyString());
    }

    @Test
    public void loadProjectRequirement() throws Exception {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.SYS_PROJECT, "sysprjIdentifier");
        filter.setFilterMap(map);

        manager.loadProjectRequirement(token,filter);

        verify(requirementInput, times(1)).loadProjectRequirement(filter);
    }
}
