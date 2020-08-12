/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ModelObject.java"
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelObject {
	private static final Logger LOG = LoggerFactory.getLogger(ModelObject.class.getName());

	private String jsonModel;
	private String objectIdentifier;
	private String lockedBy;
	
	
	public String getJsonModel() {
		return jsonModel;
	}
	public void setJsonModel(String jsonModel) {
		LOG.info("ModelObject setJsonModel:: jsonModel = {} ",
			jsonModel.substring(0, (jsonModel.length() > 500 ? 500 : jsonModel.length())));
		this.jsonModel = jsonModel;
	}
	public String getObjectIdentifier() {
		return objectIdentifier;
	}
	public void setObjectIdentifier(String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
}
