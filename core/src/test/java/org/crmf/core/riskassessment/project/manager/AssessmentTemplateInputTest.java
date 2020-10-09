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
package org.crmf.core.riskassessment.project.manager;

import org.crmf.core.riskassessment.utility.RiskAssessmentModelsCloner;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AsstemplateServiceInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentTemplateInputTest {
  @Mock
  private AsstemplateServiceInterface asstemplateService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private RiskAssessmentModelsCloner modelsCloner;
  @InjectMocks
  private AssessmentTemplateInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createAssessmentTemplate() throws Exception {

    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");
    AssessmentProcedure procedure = new AssessmentProcedure();

    when(assprocedureService.getByIdentifierFull("templateIdentifier")).thenReturn(procedure);

    manager.createAssessmentTemplate(template, "profileIdentifier");

    template.setObjType(SESTObjectTypeEnum.AssessmentTemplate);
    verify(asstemplateService, times(1)).insert(template, "profileIdentifier");
  }

  @Test
  public void loadAssessmentTemplateByFilterIdentifier() throws Exception {

    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.IDENTIFIER, "templateIdentifier");
    filter.setFilterMap(map);
    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");

    when(asstemplateService.getByIdentifier("templateIdentifier")).thenReturn(template);

    List<AssessmentTemplate> templatesResulted = manager.loadAssessmentTemplate(filter);

    Assertions.assertEquals(1, templatesResulted.size());
    Assertions.assertEquals("templateIdentifier", templatesResulted.get(0).getIdentifier());
  }

  @Test
  public void loadAssessmentTemplateByMethodology() throws Exception {

    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.METHODOLOGY, "Mehari");
    filter.setFilterMap(map);
    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");
    List<AssessmentTemplate> templates = Arrays.asList(template);

    when(asstemplateService.getByMethodology("Mehari")).thenReturn(templates);

    List<AssessmentTemplate> templatesResulted = manager.loadAssessmentTemplate(filter);

    Assertions.assertEquals(1, templatesResulted.size());
    Assertions.assertEquals("templateIdentifier", templatesResulted.get(0).getIdentifier());
  }

  @Test
  public void loadAssessmentTemplateByProfile() throws Exception {

    GenericFilter filter = new GenericFilter();
    Map<GenericFilterEnum, String> map = new HashMap();
    map.put(GenericFilterEnum.PROFILE, "profileIdentifier");
    filter.setFilterMap(map);
    AssessmentTemplate template = new AssessmentTemplate();
    template.setIdentifier("templateIdentifier");
    List<AssessmentTemplate> templates = Arrays.asList(template);

    when(asstemplateService.getByProfileIdentifier("profileIdentifier")).thenReturn(templates);

    List<AssessmentTemplate> templatesResulted = manager.loadAssessmentTemplate(filter);

    Assertions.assertEquals(1, templatesResulted.size());
    Assertions.assertEquals("templateIdentifier", templatesResulted.get(0).getIdentifier());
  }

  @Test
  public void loadAssessmentTemplateByIdentifier() throws Exception {

    AssessmentTemplate templateResulted = manager.loadAssessmentTemplateByIdentifier("templateIdentifier");
    verify(asstemplateService, times(1)).getByIdentifierFull("templateIdentifier");
  }

  @Test
  public void loadAssessmentTemplateList() throws Exception {

    manager.loadAssessmentTemplateList();
    verify(asstemplateService, times(1)).getAll();
  }
}
