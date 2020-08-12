/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CWEScore.java"
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

public class CWEScore extends VulnerabilityScore {

	private CWEExploitabilityEnum exploitabilityBasic;
	private ArrayList<Consequence> consequences;

	public CWEExploitabilityEnum getExploitabilityBasic() {
		return exploitabilityBasic;
	}

	public void setExploitabilityBasic(CWEExploitabilityEnum exploitabilityBasic) {
		this.exploitabilityBasic = exploitabilityBasic;
	}

	public ArrayList<Consequence> getConsequences() {
		return consequences;
	}

	public void setConsequences(ArrayList<Consequence> consequences) {
		this.consequences = consequences;
	}
}