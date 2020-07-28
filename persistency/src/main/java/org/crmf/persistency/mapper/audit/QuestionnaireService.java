/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="QuestionnaireService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.audit.Question;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the QuestionnaireJSON
public class QuestionnaireService implements QuestionnaireServiceInterface {

	private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireService.class.getName());
	PersistencySessionFactory sessionFactory;

	public Integer insert(SestQuestionnaireModel questionnaireModel, Integer auditId) throws Exception {

		LOG.info("Insert Questionnaire");
		SqlSession sqlSession = sessionFactory.getSession();

		Sestobj sestobj = null;

		try {
			QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

			LOG.info("Insert sestObject");
			sestobj = new Sestobj();
			sestobj.setObjtype(SESTObjectTypeEnum.Audit.name());
			sestobjMapper.insert(sestobj);

			questionnaireModel.setIdentifier(sestobj.getIdentifier());
			questionnaireMapper.insert(questionnaireModel);

			sqlSession.commit();
			return questionnaireModel.getId();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}
	}

	public void deleteCascade(String identifier) {

		LOG.info("called deleteCascade");
		SqlSession sqlSession = sessionFactory.getSession();

		try {
			QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
			questionnaireMapper.delete(identifier);
		} finally {
			sqlSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface#
	 * getByIdentifier(java.lang.String)
	 */
	@Override
	public SestQuestionnaireModel getByIdentifier(String identifier) {

		LOG.info("called getByIdentifier |"+identifier+"|");
		SqlSession sqlSession = sessionFactory.getSession();

		SestQuestionnaireModel questionnaireToSend = null;
		try {
			QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
			questionnaireToSend = questionnaireMapper.getByIdentifier(identifier);
			LOG.info("called getByIdentifier to send |"+questionnaireToSend+"|");
		} finally {
			sqlSession.close();
		}
		return questionnaireToSend;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface#
	 * getByIdentifier(java.lang.String)
	 */
	@Override
	public List<SestQuestionnaireModel> getAllQuestionnaireNames(String auditIdentifier) {

		LOG.info("called getAllQuestionnaireNames "+auditIdentifier);
		SqlSession sqlSession = sessionFactory.getSession();

		List<SestQuestionnaireModel>  questionnaireToSend = null;
		try {
			QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
			AssAuditMapper auditMapper = sqlSession.getMapper(AssAuditMapper.class);
			LOG.info("getAllQuestionnaireNames");
			LOG.info(auditMapper.toString());
			int auditId = auditMapper.getIdByIdentifier(auditIdentifier);
			questionnaireToSend = questionnaireMapper.getAllQuestionnaireNames(auditId);
		} finally {
			sqlSession.close();
		}
		return questionnaireToSend;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface#
	 * getByIdentifier(java.lang.String)
	 */
	@Override
	public SestQuestionnaireModel getQuestionnaireByCategory(String category) {

		LOG.info("called getByCategory "+category);
		SqlSession sqlSession = sessionFactory.getSession();

		SestQuestionnaireModel questionnaireToSend = null;
		try {
			QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
			questionnaireToSend = questionnaireMapper.getQuestionnaireByCategory(category);
		} finally {
			sqlSession.close();
		}
		return questionnaireToSend;
	}

	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
