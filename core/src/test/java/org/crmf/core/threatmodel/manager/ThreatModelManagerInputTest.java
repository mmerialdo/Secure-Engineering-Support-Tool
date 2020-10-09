/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.core.threatmodel.manager;

import org.crmf.model.general.SESTObject;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.TaxonomyReferenceBuilder;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreatModelManagerInputTest {

  @Mock
  private ThreatServiceInterface threatService;
  @Mock
  private AssprocedureServiceInterface assprocedureService;
  @Mock
  private SestObjServiceInterface sestObjService;
  @Mock
  private TaxonomyReferenceBuilder taxonomyReferenceBuilder;

  @InjectMocks
  private ThreatModelManagerInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void editThreatModel() {
    String threatModelJson = "this is a threat model json for test";
    String threatModelIdentifier = UUID.randomUUID().toString();

    manager.editThreatModel(threatModelJson, threatModelIdentifier);
    verify(threatService, times(1)).update(threatModelJson, threatModelIdentifier);
  }

  @Test
  public void loadThreatModel() throws Exception {

    String procedureIdentifier = "procedureID";
    String threatIdentifier = "procedureID";

    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.PROCEDURE, procedureIdentifier);
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);

    ThreatModel threatModel = new ThreatModel();
    threatModel.setIdentifier(threatIdentifier);
    SestThreatModel threatModelDb = new SestThreatModel();
    threatModelDb.setSestobjId(threatIdentifier);
    threatModelDb.setThreatModelJson("this is a threat model for test");
    AssessmentProcedure procedure = new AssessmentProcedure();
    procedure.setIdentifier(procedureIdentifier);
    procedure.setThreatModel(threatModel);
    SESTObject sestObject = new SESTObject();
    sestObject.setIdentifier(threatIdentifier);
    sestObject.setLockedBy("someUserLocking");

    ModelObject modelObjectExpected = new ModelObject();
    modelObjectExpected.setJsonModel("this is a threat model for test");
    modelObjectExpected.setObjectIdentifier(threatIdentifier);
    modelObjectExpected.setLockedBy("someUserLocking");

    when(assprocedureService.getByIdentifierFull(procedureIdentifier)).thenReturn(procedure);
    when(threatService.getByIdentifier(threatIdentifier)).thenReturn(threatModelDb);
    when(sestObjService.getByIdentifier(threatIdentifier)).thenReturn(sestObject);

    ModelObject modelObjectReturn = manager.loadThreatModel(filter);

    Assertions.assertEquals(modelObjectExpected.getJsonModel(), modelObjectReturn.getJsonModel());
    Assertions.assertEquals(modelObjectExpected.getObjectIdentifier(), modelObjectReturn.getObjectIdentifier());
    Assertions.assertEquals(modelObjectExpected.getLockedBy(), modelObjectReturn.getLockedBy());
  }

  @Test
  public void loadThreatModelNullProcedure() {
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(new HashMap<>());

    Assertions.assertThrows(Exception.class, () -> {
      manager.loadThreatModel(filter);
    });
  }

  @Test
  public void loadThreatRepository() {

    Map<GenericFilterEnum, String> filterMap = new HashMap<>();
    filterMap.put(GenericFilterEnum.METHODOLOGY, "MEHARI");
    GenericFilter filter = new GenericFilter();
    filter.setFilterMap(filterMap);
    SestThreatModel threatModel = new SestThreatModel();
    threatModel.setThreatModelJson("this is a threat model for test");

    when(threatService.getThreatRepository("MEHARI")).thenReturn(threatModel);

    String threatRepositoryJson = manager.loadThreatRepository(filter);

    Assertions.assertEquals("this is a threat model for test", threatRepositoryJson);
  }

  @Test
  public void createThreat() throws Exception {

    Threat threat = new Threat();
    threat.setName("someThreatName");
    Threat threatChecked = new Threat();
    threatChecked.setName("someThreatNameFull");
    when(taxonomyReferenceBuilder.threatCheckAndFill(threat)).thenReturn(threatChecked);

    manager.createThreat(threat);

    verify(threatService, times(1)).insertThreatReference(threatChecked);
  }

  @Test
  public void editThreat() throws Exception {

    Threat threat = new Threat();
    threat.setName("someThreatName");
    Threat threatChecked = new Threat();
    threatChecked.setName("someThreatNameFull");
    when(taxonomyReferenceBuilder.threatCheckAndFill(threat)).thenReturn(threatChecked);

    manager.editThreat(threat);

    verify(threatService, times(1)).editThreatReference(threatChecked);
  }

  @Test
  public void deleteThreat() throws Exception {
    List<String> identifiers = new ArrayList<>();
    identifiers.add("identifier1");
    identifiers.add("identifier2");

    manager.deleteThreat(identifiers);

    verify(threatService, times(1)).deleteThreatReference(identifiers);
  }
}
