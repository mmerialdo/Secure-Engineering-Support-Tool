/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatScore.java"
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

public class ThreatScore {

	private LikelihoodEnum likelihood;
	private ThreatScoreEnum score;
	private ArrayList<SecurityImpact> securityImpacts;

	public ThreatScore(){

	}

	public void finalize() throws Throwable {

	}
	
	public LikelihoodEnum getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(LikelihoodEnum likelihood) {
		this.likelihood = likelihood;
	}

	public ThreatScoreEnum getScore() {
		return score;
	}

	public void setScore(ThreatScoreEnum score) {
		this.score = score;
	}

	public ArrayList<SecurityImpact> getSecurityImpacts() {
		return securityImpacts;
	}

	public void setSecurityImpacts(ArrayList<SecurityImpact> securityImpacts) {
		this.securityImpacts = securityImpacts;
	}
}