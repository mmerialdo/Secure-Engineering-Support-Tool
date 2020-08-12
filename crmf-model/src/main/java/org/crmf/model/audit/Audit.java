/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="Audit.java"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

package org.crmf.model.audit;

import org.crmf.model.general.SESTObject;

import java.util.ArrayList;

public class Audit extends SESTObject {

	private AuditTypeEnum type;
	private ArrayList<Questionnaire> questionnaires = new ArrayList<>();

	public AuditTypeEnum getType(){
		return type;
	}

	/**
	 * 
	 * @param newVal    newVal
	 */
	public void setType(AuditTypeEnum newVal){
		type = newVal;
	}

	public ArrayList<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(ArrayList<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}
	
}//end Audit