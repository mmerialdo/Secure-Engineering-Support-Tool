/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssAuditDefaultJSONTest.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatClassEnum;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.threat.ThreatService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThreatServiceTest {

  PersistencySessionFactory sessionFactory;
  ThreatService threatService;

  @Before
  public void setUp() throws Exception {

    sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    threatService = new ThreatService();
    threatService.setSessionFactory(sessionFactory);
  }

  @After
  public void tearDown(){

    CleanDatabaseService cleaner = new CleanDatabaseService();
    cleaner.setSessionFactory(sessionFactory);
    cleaner.delete();
  }

  @Test
  public void insertThreat() throws Exception {

    Threat threat = new Threat();
    threat.setCatalogue(ThreatSourceEnum.MEHARI);
    threat.setCatalogueId("abc.def");
    threat.setName("abc.def");
    threat.setPhase(PhaseEnum.Design);
    ThreatScore score =new ThreatScore();
    score.setLikelihood(LikelihoodEnum.HIGH);
    score.setScore(ThreatScoreEnum.HIGH);
    threat.setScore(score);
    threat.setDescription("abcname");
    threat.setThreatClass(ThreatClassEnum.Accidental);

    threatService.insertThreatReference(threat);
    SestThreatModel model = threatService.getThreatRepository(ThreatSourceEnum.MEHARI.name());
    assertNotNull(model);
    assertNotNull(model.convertToModel());
    assertEquals(1, model.convertToModel().getThreats().size());
  }

  @Test
  public void editThreat() throws Exception {

    Threat threat = new Threat();
    threat.setCatalogue(ThreatSourceEnum.MEHARI);
    threat.setCatalogueId("abc.def");
    threat.setName("abc.def");
    threat.setPhase(PhaseEnum.Design);
    ThreatScore score =new ThreatScore();
    score.setLikelihood(LikelihoodEnum.HIGH);
    score.setScore(ThreatScoreEnum.HIGH);
    threat.setScore(score);
    threat.setDescription("abcname");
    threat.setThreatClass(ThreatClassEnum.Accidental);

    String sestObjId = threatService.insertThreatReference(threat);
    threat.setIdentifier(sestObjId);
    threat.setThreatClass(ThreatClassEnum.Error);
    threatService.editThreatReference(threat);
    SestThreatModel model = threatService.getThreatRepository(ThreatSourceEnum.MEHARI.name());
    assertNotNull(model);
    assertNotNull(model.convertToModel());
    assertNotNull(model.convertToModel().getThreats().get(0));
    assertEquals(1, model.convertToModel().getThreats().size());
    assertEquals(ThreatClassEnum.Error, model.convertToModel().getThreats().get(0).getThreatClass());
  }

  @Test
  public void deleteThreat() throws Exception {

    Threat threat = new Threat();
    threat.setCatalogue(ThreatSourceEnum.MEHARI);
    threat.setCatalogueId("abc.def");
    threat.setName("abc.def");
    threat.setPhase(PhaseEnum.Design);
    ThreatScore score =new ThreatScore();
    score.setLikelihood(LikelihoodEnum.HIGH);
    score.setScore(ThreatScoreEnum.HIGH);
    threat.setThreatClass(ThreatClassEnum.Accidental);
    threat.setScore(score);
    threat.setDescription("abcname");

    String sestObjId = threatService.insertThreatReference(threat);
    threatService.deleteThreatReference(new ArrayList<String>() {{add(sestObjId);}});
    SestThreatModel model = threatService.getThreatRepository(ThreatSourceEnum.MEHARI.name());
    assertNotNull(model);
    assertNotNull(model.convertToModel());
    System.out.println(model.convertToModel().getThreats().size());
    assertEquals(0, model.convertToModel().getThreats().size());
  }
}
