/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementManagerRestServer.java"
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

import org.crmf.secreqimport.manager.gasf.GASFInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the business logic behind the webservices related to the SecurityRequirements management
public class SecRequirementManagerRestServer implements SecRequirementManagerRestServerInterface{

	private GASFInputInterface secrequirementInput;
	private static final Logger LOG = LoggerFactory.getLogger(SecRequirementManagerRestServer.class.getName());
	
	@Override
	public void importGasfRequirement() throws Exception {
		try{
			LOG.info("importGasfRequirement");
			secrequirementInput.importGASFRequirementsFull();
		} catch (Exception e) {
			LOG.info("importGasfRequirement:: exception " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	public GASFInputInterface getSecrequirementInput() {
		return secrequirementInput;
	}

	public void setSecrequirementInput(GASFInputInterface secrequirementInput) {
		this.secrequirementInput = secrequirementInput;
	}

}
