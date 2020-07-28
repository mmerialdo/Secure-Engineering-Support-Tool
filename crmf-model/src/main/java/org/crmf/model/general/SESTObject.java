/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SESTObject.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.general;

public class SESTObject {

	private String identifier;
	private SESTObjectTypeEnum objType;
	private String lockedBy;

	public SESTObject(){

	}

	public void finalize() throws Throwable {

	}
	public String getIdentifier(){
		return identifier;
	}

	public SESTObjectTypeEnum getObjType(){
		return objType;
	}

	public void setIdentifier(String newVal){
		identifier = newVal;
	}

	public void setObjType(SESTObjectTypeEnum newVal){
		objType = newVal;
	}

	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}
}