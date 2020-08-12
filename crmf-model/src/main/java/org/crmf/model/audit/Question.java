/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Question.java"
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question extends SESTObject {

	private String category;
	private String index;
	private QuestionTypeEnum type;
	private String value;
	private List<Question> children = new ArrayList<>();
	private List<Question> gasf = new ArrayList<>();
	private Map<AnswerTypeEnum, String> answers = new HashMap<>();
	private SESTObject referenceObject;

	public String getIndex(){
		return index;
	}

	public QuestionTypeEnum getType(){
		return type;
	}

	public String getValue(){
		return value;
	}

	public void setIndex(String newVal){
		index = newVal;
	}

	public void setType(QuestionTypeEnum newVal){
		type = newVal;
	}

	public void setValue(String newVal){
		value = newVal;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Question> getChildren() {
		return children;
	}

	public void setChildren(List<Question> children) {
		this.children = children;
	}

	public SESTObject getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(SESTObject referenceObject) {
		this.referenceObject = referenceObject;
	}

	public List<Question> getGasf() {
		return gasf;
	}

	public void setGasf(List<Question> gasf) {
		this.gasf = gasf;
	}

	public Map<AnswerTypeEnum, String> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<AnswerTypeEnum, String> answers) {
		this.answers = answers;
	}
}