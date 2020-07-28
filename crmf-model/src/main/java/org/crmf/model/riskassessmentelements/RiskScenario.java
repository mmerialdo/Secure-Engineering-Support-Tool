/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskScenario.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessmentelements;

import java.util.ArrayList;

import org.crmf.model.general.SESTObject;

public class RiskScenario extends SESTObject {

	private ImpactEnum calculatedImpact;
	private LikelihoodEnum calculatedLikelihood;
	private ImpactEnum calculatedSeriousness;
	private String description;
	private String userDescription;
	private SafeguardEffectiveness dissuasion;
	private SafeguardEffectiveness prevention;
	private SafeguardEffectiveness confining;
	private SafeguardEffectiveness palliation;
	private ImpactEnum expertImpact;
	private String expertImpactDescription;
	private LikelihoodEnum expertLikelihood;
	private String expertLikelihoodDescription;
	private String family;
	private SecurityImpactScopeEnum impactScope;
	private ImpactEnum intrinsicImpact;
	private LikelihoodEnum intrinsicLikelihood;
	private ImpactEnum intrinsicSeriousness;
	private ScenarioResultEnum scenarioResult;
	private ThreatClassEnum threatClass;
	private boolean excluded;
	private ImpactEnum expertConfinability;
	private ArrayList<String> safeguardIds;
	private String assetId;
	private String threatId;
	private String vulnerabilityId;

	public RiskScenario(){
		safeguardIds = new ArrayList<String>();
		excluded = false;
		scenarioResult = ScenarioResultEnum.Reduce;
		dissuasion = SafeguardEffectiveness.LOW;
		prevention = SafeguardEffectiveness.LOW;
		confining = SafeguardEffectiveness.LOW;
		palliation = SafeguardEffectiveness.LOW;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public ImpactEnum getCalculatedImpact() {
		return calculatedImpact;
	}

	public void setCalculatedImpact(ImpactEnum calculatedImpact) {
		this.calculatedImpact = calculatedImpact;
	}

	public LikelihoodEnum getCalculatedLikelihood() {
		return calculatedLikelihood;
	}

	public void setCalculatedLikelihood(LikelihoodEnum calculatedLikelihood) {
		this.calculatedLikelihood = calculatedLikelihood;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImpactEnum getExpertImpact() {
		return expertImpact;
	}

	public void setExpertImpact(ImpactEnum expertImpact) {
		this.expertImpact = expertImpact;
	}

	public LikelihoodEnum getExpertLikelihood() {
		return expertLikelihood;
	}

	public void setExpertLikelihood(LikelihoodEnum expertLikelihood) {
		this.expertLikelihood = expertLikelihood;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public SecurityImpactScopeEnum getImpactScope() {
		return impactScope;
	}

	public void setImpactScope(SecurityImpactScopeEnum impactScope) {
		this.impactScope = impactScope;
	}

	public ImpactEnum getIntrinsicImpact() {
		return intrinsicImpact;
	}

	public void setIntrinsicImpact(ImpactEnum intrinsicImpact) {
		this.intrinsicImpact = intrinsicImpact;
	}

	public LikelihoodEnum getIntrinsicLikelihood() {
		return intrinsicLikelihood;
	}

	public void setIntrinsicLikelihood(LikelihoodEnum intrinsicLikelihood) {
		this.intrinsicLikelihood = intrinsicLikelihood;
	}

	public ImpactEnum getIntrinsicSeriousness() {
		return intrinsicSeriousness;
	}

	public void setIntrinsicSeriousness(ImpactEnum intrinsicSeriousness) {
		this.intrinsicSeriousness = intrinsicSeriousness;
	}

	public ArrayList<String> getSafeguardIds() {
		return safeguardIds;
	}

	public void setSafeguardIds(ArrayList<String> safeguardIds) {
		this.safeguardIds = safeguardIds;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getThreatId() {
		return threatId;
	}

	public void setThreatId(String threatId) {
		this.threatId = threatId;
	}

	public String getVulnerabilityId() {
		return vulnerabilityId;
	}

	public void setVulnerabilityId(String vulnerabilityId) {
		this.vulnerabilityId = vulnerabilityId;
	}

	public ImpactEnum getCalculatedSeriousness() {
		return calculatedSeriousness;
	}

	public void setCalculatedSeriousness(ImpactEnum calculatedSeriousness) {
		this.calculatedSeriousness = calculatedSeriousness;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public ScenarioResultEnum getScenarioResult() {
		return scenarioResult;
	}

	public void setScenarioResult(ScenarioResultEnum scenarioResult) {
		this.scenarioResult = scenarioResult;
	}

	public boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

	public ImpactEnum getExpertConfinability() {
		return expertConfinability;
	}

	public void setExpertConfinability(ImpactEnum expertConfinability) {
		this.expertConfinability = expertConfinability;
	}

	public SafeguardEffectiveness getDissuasion() {
		return dissuasion;
	}

	public void setDissuasion(SafeguardEffectiveness dissuasion) {
		this.dissuasion = dissuasion;
	}

	public SafeguardEffectiveness getPrevention() {
		return prevention;
	}

	public void setPrevention(SafeguardEffectiveness prevention) {
		this.prevention = prevention;
	}

	public SafeguardEffectiveness getConfining() {
		return confining;
	}

	public void setConfining(SafeguardEffectiveness confining) {
		this.confining = confining;
	}

	public SafeguardEffectiveness getPalliation() {
		return palliation;
	}

	public void setPalliation(SafeguardEffectiveness palliation) {
		this.palliation = palliation;
	}

	public ThreatClassEnum getThreatClass() {
		return threatClass;
	}

	public void setThreatClass(ThreatClassEnum threatClass) {
		this.threatClass = threatClass;
	}

	public String getExpertImpactDescription() {
		return expertImpactDescription;
	}

	public void setExpertImpactDescription(String expertImpactDescription) {
		this.expertImpactDescription = expertImpactDescription;
	}

	public String getExpertLikelihoodDescription() {
		return expertLikelihoodDescription;
	}

	public void setExpertLikelihoodDescription(String expertLikelihoodDescription) {
		this.expertLikelihoodDescription = expertLikelihoodDescription;
	}

	
}