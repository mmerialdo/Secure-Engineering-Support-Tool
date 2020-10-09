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
package org.crmf.proxy.core.audit.rest;

import org.crmf.core.audit.AuditInput;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AuditRestServerTest {

    @Mock
    private AuditInput auditInput;
    @InjectMocks
    private AuditRestServer manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editAudit() {
        String token = "";
        ModelObject model = new ModelObject();

        manager.editAudit(token, model);

        verify(auditInput, times(1)).editAudit(model);
    }

    @Test
    public void loadAudit() {
        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "auditIdentifier");
        filter.setFilterMap(map);

        manager.loadAudit(token, filter);

        verify(auditInput, times(1)).loadAudit(filter.getFilterValue(GenericFilterEnum.IDENTIFIER), AuditTypeEnum.SECURITY, false);
    }

    @Test
    public void loadQuestionnaireSafeguard() {
        String token = "";
        manager.loadQuestionnaireSafeguard(token);

        verify(auditInput, times(1)).loadQuestionnaireSafeguard();
    }

    @Test
    public void loadQuestionnaireJson() {

        String token = "";
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "questionnaireIdentifier");
        filter.setFilterMap(map);
        manager.loadQuestionnaireJson(token, filter);

        verify(auditInput, times(1)).loadQuestionnaire("questionnaireIdentifier");
    }
}
