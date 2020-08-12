/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.safeguardmodel.manager;

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.utility.GenericFilter;

public interface SafeguardModelManagerInputInterface {
	String loadSafeguardModel(GenericFilter filter) throws Exception;
	
	void editSafeguardModel(AssessmentProject project);
	
	SafeguardModel createSafeguardModel(SafeguardModel safeguards, AssessmentProject project);
}
