/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemProjectRequirementInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.project.requirement;

import java.util.List;

import org.crmf.model.requirement.Requirement;
import org.crmf.model.utility.GenericFilter;

public interface SystemProjectRequirementInputInterface {

	List<Requirement> loadProjectRequirement(GenericFilter filter) throws Exception;
    
    List<Requirement> loadProjectRequirementByIds(List<String> ids) throws Exception;
}
