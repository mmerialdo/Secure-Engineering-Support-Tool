/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserExperience.java"
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

import java.util.ArrayList;

import org.crmf.model.general.SESTObject;

public class UserExperience extends SESTObject {

	private ArrayList<Experience> experiences;

	public UserExperience(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public ArrayList<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}
	
}