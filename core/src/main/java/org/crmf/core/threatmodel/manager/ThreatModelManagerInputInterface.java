/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatModelManagerInputInterface.java"
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

import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

import java.util.List;

public interface ThreatModelManagerInputInterface {
	
	public void editThreatModel(String threatModel, String threatModelIdentifier);
	
	public ModelObject loadThreatModel(GenericFilter filter) throws Exception;
	
	public String loadThreatRepository(GenericFilter filter);

	String createThreat(Threat threat) throws Exception;

	void editThreat(Threat threat) throws Exception;

	void deleteThreat(List<String> identifier) throws Exception;
}
