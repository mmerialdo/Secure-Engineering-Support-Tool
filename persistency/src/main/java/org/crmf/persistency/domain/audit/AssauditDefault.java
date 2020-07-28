/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssauditDefault.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.audit;

public class AssauditDefault {

	private Integer id;
	
	private String avalue;

	private String atype;

	private String questioncategory;
	
	private String parentcategory;
	
	private String answers;
	
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getParentcategory() {
		return parentcategory;
	}

	public void setParentcategory(String parentcategory) {
		this.parentcategory = parentcategory;
	}

	public String getQuestioncategory() {
		return questioncategory;
	}

	public void setQuestioncategory(String questioncategory) {
		this.questioncategory = questioncategory;
	}

	public String getAnswers() {
		return answers;
	}

	public void setAnswers(String answers) {
		this.answers = answers;
	}

	public String getAvalue() {
		return avalue;
	}

	public void setAvalue(String avalue) {
		this.avalue = avalue;
	}

	public String getAtype() {
		return atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
