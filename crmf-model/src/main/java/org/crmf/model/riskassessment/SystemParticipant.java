/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemParticipant.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessment;

import org.crmf.model.general.SESTObject;

public class SystemParticipant extends SESTObject {

	private String name;
	private String role;
	private String surname;

	public String getName(){
		return name;
	}

	public String getRole(){
		return role;
	}

	public String getSurname(){
		return surname;
	}

	public void setName(String newVal){
		name = newVal;
	}

	public void setRole(String newVal){
		role = newVal;
	}

	public void setSurname(String newVal){
		surname = newVal;
	}
}