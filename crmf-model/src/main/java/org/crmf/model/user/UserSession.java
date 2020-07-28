/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserSession.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.user;

public class UserSession {

	private String expirationTime;

	public UserSession(){

	}

	public void finalize()
	  throws Throwable{

	}

	public String getExpirationTime(){
		return expirationTime;
	}

	public void setExpirationTime(String newVal){
		expirationTime = newVal;
	}
}