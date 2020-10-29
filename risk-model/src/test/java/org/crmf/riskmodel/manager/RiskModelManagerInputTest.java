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
package org.crmf.riskmodel.manager;

import org.crmf.model.riskassessment.*;
import org.crmf.persistency.domain.risk.SeriousnessScale;
import org.crmf.persistency.domain.risk.StatusImpactScale;
import org.crmf.persistency.domain.risk.StatusLikelihoodScale;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.riskmodel.utility.SecurityMeasuresInterpreter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class RiskModelManagerInputTest {

    @Mock
    @Qualifier("default")
    private AssprocedureServiceInterface assprocedureService;
    @Mock
    @Qualifier("default")
    private VulnerabilityServiceInterface vulnerabilityModelService;
    @Mock
    @Qualifier("default")
    private ThreatServiceInterface threatModelService;
    @Mock
    @Qualifier("default")
    private RiskServiceInterface riskModelService;
    @Mock
    @Qualifier("default")
    private RiskTreatmentServiceInterface riskTreatmentModelService;
    @Mock
    @Qualifier("default")
    private SestObjServiceInterface sestObjService;
    @Mock
    private RiskScenariosReferenceImporter importer;
    @Mock
    private SecurityMeasuresInterpreter interpreter;

    @InjectMocks
    private RiskModelManagerInput manager;

    private ArrayList<SeriousnessScale> scales;
    private ArrayList<StatusImpactScale> statusImpactScales;
    private ArrayList<StatusLikelihoodScale> statusLikelihoodScales;

    private boolean modelsVulnThreatUpdated = false;
    private boolean modelsRiskTreatmentUpdated = false;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void editRiskModel() {

        String riskModelIdentifier = "riskModelIdentifier";
        String riskModel = "{}";
        AssessmentProcedure procedure = new AssessmentProcedure();
        RiskModel rm = new RiskModel();
        rm.setIdentifier("riskModelIdentifier");
        procedure.setIdentifier("procedureIdentifier");
        procedure.setRiskModel(rm);
        VulnerabilityModel vm = new VulnerabilityModel();
        vm.setIdentifier("vulnerabilityModelIdentifier");
        procedure.setVulnerabilityModel(vm);
        ThreatModel tm = new ThreatModel();
        tm.setIdentifier("threatModelIdentifier");
        procedure.setThreatModel(tm);
        SafeguardModel sgm = new SafeguardModel();
        sgm.setIdentifier("safeguardModelIdentifier");
        procedure.setSafeguardModel(sgm);
        RiskTreatmentModel rtm = new RiskTreatmentModel();
        rtm.setIdentifier("riskTreatmentModelIdentifier");
        procedure.setRiskTreatmentModel(rtm);
        List<SeriousnessScale> scales = new ArrayList<>();
        List<StatusImpactScale> statusImpactScales = new ArrayList<>();
        List<StatusLikelihoodScale> statusLikelihoodScales = new ArrayList<>();

        when(assprocedureService.getByRiskModelIdentifier(riskModelIdentifier)).thenReturn(procedure);
        when(riskModelService.getSeriousness(1)).thenReturn(scales);
        when(riskModelService.getStatusImpact(1)).thenReturn(statusImpactScales);
        when(riskModelService.getStatusLikelihood(1)).thenReturn(statusLikelihoodScales);

        manager.editRiskModel(riskModel, riskModelIdentifier);

        // verify(auditInput, times(1)).loadQuestionnaire("questionnaireIdentifier");
    }
}
