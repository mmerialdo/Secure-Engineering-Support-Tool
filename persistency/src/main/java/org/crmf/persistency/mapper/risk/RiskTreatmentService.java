/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.risk;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.risk.SestRiskTreatmentModel;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the RiskTreatmentModel
public class RiskTreatmentService implements RiskTreatmentServiceInterface {

	private static final Logger LOG = LoggerFactory.getLogger(RiskTreatmentService.class.getName());
	PersistencySessionFactory sessionFactory;
	
	@Override
	public void insert(String riskTreatmentModelJson, String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("Insert Risk Treatment Model");

		Sestobj sestobj = null;
		
		try {
			//create a new Risk Treatment Mapper
			RiskTreatmentMapper riskMapper = sqlSession.getMapper(RiskTreatmentMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
			
			//create a new sestObj with the identifier in input and type RiskModel
			LOG.info("Insert sestObject");
			sestobj = new Sestobj();
			sestobj.setObjtype(SESTObjectTypeEnum.RiskTreatmentModel.name());
			sestobj.setIdentifier(sestobjId);
			sestobjMapper.insertWithIdentifier(sestobj);
			
			
			//use the Risk Mapper to insert the Risk Model
			SestRiskTreatmentModel riskTreatmentModel= new SestRiskTreatmentModel();
			riskTreatmentModel.setRiskTreatmentModelJson(riskTreatmentModelJson);
			riskTreatmentModel.setSestobjId(sestobjId);
			riskMapper.insert(riskTreatmentModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public void update(String riskTreatmentModelJson, String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("updateQuestionnaireJSON Risk Treatment Model");

		try {
			//create a new Risk Treatment Mapper
			RiskTreatmentMapper riskTreatmentMapper = sqlSession.getMapper(RiskTreatmentMapper.class);
			//use the Risk Treatment Mapper to insert the Risk Model
			riskTreatmentMapper.update(riskTreatmentModelJson, sestobjId);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public SestRiskTreatmentModel getByIdentifier(String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("get By Identifier -  Risk Treatment Model");
		SestRiskTreatmentModel riskTreatmentModel;

		try {
			//create a new Risk Treatment Mapper
			RiskTreatmentMapper riskTreatmentMapper = sqlSession.getMapper(RiskTreatmentMapper.class);
			//use the Risk Treatment Mapper to insert the Risk Model
			riskTreatmentModel = riskTreatmentMapper.getByIdentifier(sestobjId);
			LOG.info("get By Identifier -  Risk Treatment Model returned: "+riskTreatmentModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}
		
		return riskTreatmentModel;
	}

	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
