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

package org.crmf.persistency.domain.audit;

import org.crmf.model.audit.QuestionTypeEnum;

public class Question {
    private Integer id;

    private String ix;

    private String qtype;

    private String qvalue;

    private String category;

    private Integer questionnaireId;

    private Integer parentId;
    
    private String sestobjId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIx() {
        return ix;
    }

    public void setIx(String ix) {
        this.ix = ix;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype == null ? null : qtype.trim();
    }

    public String getQvalue() {
        return qvalue;
    }

    public void setQvalue(String qvalue) {
        this.qvalue = qvalue == null ? null : qvalue.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public Integer getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Integer questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

	public String getSestobjId() {
		return sestobjId;
	}

	public void setSestobjId(String sestobjId) {
		this.sestobjId = sestobjId;
	}
    
	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentQuestionId) {
		this.parentId = parentQuestionId;
	}

	public org.crmf.model.audit.Question convertToModel() {

		org.crmf.model.audit.Question question = new org.crmf.model.audit.Question();

		if (this.qtype != null) {
			question.setType(QuestionTypeEnum.valueOf(this.qtype));
		}
		if (this.sestobjId != null) {
			question.setIdentifier(this.sestobjId);
		}
		question.setIndex(this.getIx());
		question.setValue(this.getQvalue());
		question.setCategory(category);

		return question;
	}

	public void convertFromModel(org.crmf.model.audit.Question question) {

		if (question.getType() != null) {
			this.setQtype(question.getType().name());
		}
		if (question.getIdentifier() != null) {
			this.setSestobjId(question.getIdentifier());
		}
		this.setQvalue(question.getValue());
		this.setIx(question.getIndex());
		this.setCategory(question.getCategory());
//		this.setQuestionnaireId(questionnaireId);
//		this.setId(id);
	}
}