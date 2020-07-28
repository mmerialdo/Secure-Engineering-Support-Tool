/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CVEScore.java"
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

public class CVEScore extends VulnerabilityScore {

	private CVEAccessComplexityEnum accessComplexity;
	private CVEAccessVectorEnum accessVector;
	private CVEAuthenticationEnum authentication;
	private CVECollateralDamagePotentialEnum collateralDamagePotential;
	private CVEExploitabilityEnum exploitabilityBasic;
	private CVEExploitabilityEnum exploitabilityTemporal;
	private CVERemediationLevelEnum remediationLevel;
	private CVEReportConfidenceEnum reportConfidence;
	private float scoreBase;
	private float scoreEnvironmental;
	private float scoreOverall;
	private float scoreTemporal;
	private CVETargetDistributionEnum targetDistribution;
	private ArrayList<CVESecurityImpact> securityImpacts;

	public CVEScore(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}
}