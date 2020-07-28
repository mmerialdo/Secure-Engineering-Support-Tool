/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestThreat.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.threat;

import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatClassEnum;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.persistency.domain.vulnerability.SestVulnerability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SestThreat {
  private static final Logger LOG = LoggerFactory.getLogger(SestThreat.class.getName());
  private Integer id;
  private String sestobjId;
  private String likelihood;
  private String score;
  private String threatClass;
  private String phase;
  private String name;
  private Date updateTime;
  private String catalogueId;
  private String catalogue;
  private String threatJson;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSestobjId() {
    return sestobjId;
  }

  public void setSestobjId(String sestobjId) {
    this.sestobjId = sestobjId;
  }

  public String getLikelihood() {
    return likelihood;
  }

  public void setLikelihood(String likelihood) {
    this.likelihood = likelihood;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }

  public String getThreatClass() {
    return threatClass;
  }

  public void setThreatClass(String threatClass) {
    this.threatClass = threatClass;
  }

  public String getPhase() {
    return phase;
  }

  public void setPhase(String phase) {
    this.phase = phase;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public String getCatalogueId() {
    return catalogueId;
  }

  public void setCatalogueId(String catalogueId) {
    this.catalogueId = catalogueId;
  }

  public String getCatalogue() {
    return catalogue;
  }

  public void setCatalogue(String catalogue) {
    this.catalogue = catalogue;
  }

  public String getThreatJson() {
    return threatJson;
  }

  public void setThreatJson(String threatJson) {
    this.threatJson = threatJson;
  }

  public Threat convertToModel() {

    Threat threat = new Threat();
    threat.setPhase(PhaseEnum.valueOf(this.phase));
    threat.setName(this.name);
    threat.setCatalogueId(this.getCatalogueId());
    threat.setCatalogue(ThreatSourceEnum.valueOf(this.catalogue));
    threat.setThreatClass(ThreatClassEnum.valueOf(this.threatClass));
    threat.setIdentifier(this.sestobjId);
    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    String threatDate = df.format(this.updateTime);
    threat.setLastUpdate(threatDate);


    ThreatScore score = new ThreatScore();
    score.setScore(ThreatScoreEnum.valueOf(this.getScore()));
    score.setLikelihood(LikelihoodEnum.valueOf(this.getLikelihood()));
    threat.setScore(score);

    return threat;
  }

  public void convertFromModel(Threat threat) {

    this.setLikelihood(threat.getScore().getLikelihood().name());
    this.setScore(threat.getScore().getScore().name());
    this.setScore(threat.getScore().getLikelihood().name());
		this.setPhase(threat.getPhase().name());
    this.setThreatClass(threat.getThreatClass().name());
    this.setName(threat.getName());
		this.setCatalogue(threat.getCatalogue().name());
    this.setCatalogueId(threat.getCatalogueId());
    this.setSestobjId(threat.getIdentifier());

    try {
      DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      Date vulnerabilityDate = threat.getLastUpdate() != null ? df.parse(threat.getLastUpdate()) : new Date();
      this.setUpdateTime(vulnerabilityDate);
    } catch(ParseException pe) {
      LOG.info("Unable to parse date " + threat.getLastUpdate());
    }
  }
}
