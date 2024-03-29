/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Questionnaire.java"
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
import java.util.List;

public class Questionnaire extends SESTObject {

	private String category;
	private String index;
	private QuestionnaireTypeEnum type;
	private List<Question> questions = new ArrayList<>();

	public String getIndex(){
		return index;
	}

	public QuestionnaireTypeEnum getType(){
		return type;
	}

	public void setIndex(String newVal){
		index = newVal;
	}

	public void setType(QuestionnaireTypeEnum newVal){
		type = newVal;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

}