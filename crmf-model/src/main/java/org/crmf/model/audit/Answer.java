/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="Answer.java"
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

public class Answer {

	private int index;
	private AnswerTypeEnum type;
	private String value;

	public Answer(){

	}

	public void finalize() throws Throwable {

	}
	public int getIndex(){
		return index;
	}

	public AnswerTypeEnum getType(){
		return type;
	}

	public String getValue(){
		return value;
	}

	public void setIndex(int newVal){
		index = newVal;
	}

	public void setValue(String newVal){
		value = newVal;
	}

	public void setType(AnswerTypeEnum type) {
		this.type = type;
	}
	
}