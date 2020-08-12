/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditBuilder.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility;

import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.QuestionnaireTypeEnum;
import org.crmf.model.general.SESTObjectTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//This class creates a mock-up Audit for testing reasons
public class AuditBuilder implements ObjectBuilder {

	@Override
	public Audit getObject(){

		Map<AnswerTypeEnum, String> answers = new HashMap<>();
		answers.put(AnswerTypeEnum.MEHARI_R_V1, "ok");

		Question question = new Question();
		question.setIdentifier("104");
		question.setObjType(SESTObjectTypeEnum.Audit);
		question.setAnswers(answers);
		question.setIndex("01");
		question.setCategory("01A01");
		question.setType(QuestionTypeEnum.QUESTION);
		question.setValue("Do all areas ..");
		
		Question question0 = new Question();
		question0.setIdentifier("104");
		question0.setObjType(SESTObjectTypeEnum.Audit);
		question0.setAnswers(answers);
		question0.setIndex("01");
		question0.setCategory("01A01");
		question0.setType(QuestionTypeEnum.QUESTION);
		question0.setValue("Do all areas ..");

		Question question01 = new Question();
		question01.setIdentifier("103");
		question01.setObjType(SESTObjectTypeEnum.Audit);
		question01.setAnswers(answers);
		question01.setIndex("01");
		question01.setCategory("01A");
		question01.setType(QuestionTypeEnum.CATEGORY);
		question01.setValue("Organization and piloting...");
		question01.setChildren(new ArrayList<>(Arrays.asList(question0,question)));
		
		Question question02 = new Question();
		question02.setIdentifier("106");
		question02.setObjType(SESTObjectTypeEnum.Audit);
		question02.setAnswers(answers);
		question02.setCategory("01A");
		question02.setType(QuestionTypeEnum.CATEGORY);
		question02.setValue("Roles and structures ...");
		question02.setChildren(new ArrayList<>(Arrays.asList(question01)));
		
		
		ArrayList<Question> questions = new ArrayList<>();
		questions.add(question02);
		
		Questionnaire quest = new Questionnaire();
		quest.setIdentifier("102");
		quest.setObjType(SESTObjectTypeEnum.Audit);
		quest.setIndex("01");
		quest.setType(QuestionnaireTypeEnum.MEHARI_SecurityPremises);
		quest.setQuestions(questions);
		
		ArrayList<Questionnaire> quests = new ArrayList<>();
		quests.add(quest);
		
		Audit audit = new Audit();
		audit.setIdentifier("101");
		audit.setObjType(SESTObjectTypeEnum.Audit);
		audit.setType(AuditTypeEnum.SECURITY);
		audit.setQuestionnaires(quests);
		
		return audit;
	}

	@Override
	public void printGson(String json) {
		// TODO Auto-generated method stub
	}

}
