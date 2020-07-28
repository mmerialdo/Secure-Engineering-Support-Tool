/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementManagerRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.secrequirement.manager.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;

public interface SecRequirementManagerRestServerInterface {

	@POST
	void importGasfRequirement() throws Exception;
}
