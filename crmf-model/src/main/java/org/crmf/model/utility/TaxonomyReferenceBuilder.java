/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditBuilder.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.model.utility;

import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.model.riskassessmentelements.GeneralScore;
import org.crmf.model.riskassessmentelements.LikelihoodEnum;
import org.crmf.model.riskassessmentelements.ScoreEnum;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.riskassessmentelements.ThreatClassEnum;
import org.crmf.model.riskassessmentelements.ThreatScore;
import org.crmf.model.riskassessmentelements.ThreatScoreEnum;
import org.crmf.model.riskassessmentelements.ThreatSourceEnum;
import org.crmf.model.riskassessmentelements.Vulnerability;
import org.crmf.model.riskassessmentelements.VulnerabilityExploitabilityEnum;
import org.crmf.model.riskassessmentelements.VulnerabilityScoreEnum;
import org.crmf.model.riskassessmentelements.VulnerabilitySourceEnum;

public class TaxonomyReferenceBuilder {


  public static final void vulnearbilityCheckAndFill(Vulnerability vulnerability) {

    vulnerability.setCanBeSelected(true);
    if (vulnerability.getScore() == null) {
      GeneralScore scoreVulnerability = new GeneralScore();
      scoreVulnerability.setScore(VulnerabilityScoreEnum.HIGH);
      scoreVulnerability.setScoringType(ScoreEnum.CUSTOM);
      scoreVulnerability.setExploitability(VulnerabilityExploitabilityEnum.VERY_HIGH);
      scoreVulnerability.setExploitabilityBasic(VulnerabilityExploitabilityEnum.VERY_HIGH);
      vulnerability.setScore(scoreVulnerability);
    } else {
      if (vulnerability.getScore().getScore() == null){
        vulnerability.getScore().setScore(VulnerabilityScoreEnum.HIGH);
      }
      if (vulnerability.getScore().getScoringType() == null){
        vulnerability.getScore().setScoringType(ScoreEnum.CUSTOM);
      }
      if (vulnerability.getScore().getExploitability() == null){
        vulnerability.getScore().setExploitability(VulnerabilityExploitabilityEnum.VERY_HIGH);
      }
      if (vulnerability.getScore().getExploitabilityBasic() == null){
        vulnerability.getScore().setExploitabilityBasic(VulnerabilityExploitabilityEnum.VERY_HIGH);
      }
    }
    if (vulnerability.getPhase() == null) {
      vulnerability.setPhase(PhaseEnum.Initial);
    }
    if (vulnerability.getCatalogue() == null) {
      vulnerability.setCatalogue(VulnerabilitySourceEnum.CUSTOM);
    }
  }

  public static final void threatCheckAndFill(Threat threat) {

    threat.setCanBeSelected(true);
    threat.setElementType(ElementTypeEnum.Element);
    if (threat.getScore() == null) {
      ThreatScore scoreThreat = new ThreatScore();
      scoreThreat.setLikelihood(LikelihoodEnum.HIGH);
      scoreThreat.setScore(ThreatScoreEnum.VERY_HIGH);
    } else {
      if (threat.getScore().getLikelihood() == null) {
        threat.getScore().setLikelihood(LikelihoodEnum.HIGH);
      }
      if (threat.getScore().getScore() == null) {
        threat.getScore().setScore(ThreatScoreEnum.VERY_HIGH);
      }
    }
    if (threat.getPhase() == null) {
      threat.setPhase(PhaseEnum.Initial);
    }
    if (threat.getCatalogue() == null) {
      threat.setCatalogue(ThreatSourceEnum.CUSTOM);
    }
    if (threat.getThreatClass() == null) {
      threat.setThreatClass(ThreatClassEnum.Error);
    }
    if (threat.getActor() != null && threat.getActor().getName() != null) {
      if (threat.getActor().getCatalogueId() == null) {
        threat.getActor().setCatalogueId(threat.getActor().getName());
      }
      if (threat.getActor().getCatalogue() == null) {
        threat.getActor().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
    if (threat.getAccess() != null && threat.getAccess().getName() != null) {
      if (threat.getAccess().getCatalogueId() == null) {
        threat.getAccess().setCatalogueId(threat.getAccess().getName());
      }
      if (threat.getAccess().getCatalogue() == null) {
        threat.getAccess().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
    if (threat.getEvent() != null && threat.getEvent().getName() != null) {
      if (threat.getEvent().getCatalogueId() == null) {
        threat.getEvent().setCatalogueId(threat.getEvent().getName());
      }
      if (threat.getEvent().getCatalogue() == null) {
        threat.getEvent().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
    if (threat.getProcess() != null && threat.getProcess().getName() != null) {
      if (threat.getProcess().getCatalogueId() == null) {
        threat.getProcess().setCatalogueId(threat.getProcess().getName());
      }
      if (threat.getProcess().getCatalogue() == null) {
        threat.getProcess().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
    if (threat.getPlace() != null && threat.getPlace().getName() != null) {
      if (threat.getPlace().getCatalogueId() == null) {
        threat.getPlace().setCatalogueId(threat.getPlace().getName());
      }
      if (threat.getPlace().getCatalogue() == null) {
        threat.getPlace().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
    if (threat.getTime() != null && threat.getTime().getName() != null) {
      if (threat.getTime().getCatalogueId() == null) {
        threat.getTime().setCatalogueId(threat.getTime().getName());
      }
      if (threat.getTime().getCatalogue() == null) {
        threat.getTime().setCatalogue(ThreatSourceEnum.CUSTOM);
      }
    }
  }

}
