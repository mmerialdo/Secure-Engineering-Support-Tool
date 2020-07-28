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

import java.util.ArrayList;

import org.crmf.model.general.SESTObject;

public class Question extends SESTObject {

	private String category;
	private String index;
	private QuestionTypeEnum type;
	private String value;
	private ArrayList<Question> children = new ArrayList<>();
	private ArrayList<Question> gasf = new ArrayList<>();
	private ArrayList<Answer> answers = new ArrayList<>();
	private SESTObject referenceObject;

	public Question(){

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

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

	public ArrayList<Question> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<Question> children) {
		this.children = children;
	}

	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public SESTObject getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(SESTObject referenceObject) {
		this.referenceObject = referenceObject;
	}

	public ArrayList<Question> getGasf() {
		return gasf;
	}

	public void setGasf(ArrayList<Question> gasf) {
		this.gasf = gasf;
	}
}