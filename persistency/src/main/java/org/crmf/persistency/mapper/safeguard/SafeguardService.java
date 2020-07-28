/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.safeguard;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the SafeguardModel
public class SafeguardService implements SafeguardServiceInterface {

	private static final Logger LOG = LoggerFactory.getLogger(SafeguardService.class.getName());
	PersistencySessionFactory sessionFactory;
	
	@Override
	public void insert(String safeguardModelJson, String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("Insert Safeguard Model");

		Sestobj sestobj = null;
		
		try {
			//create a new Safeguard Mapper
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
			
			//create a new sestObj with the identifier in input and type SafeguardModel
			LOG.info("Insert sestObject");
			sestobj = new Sestobj();
			sestobj.setObjtype(SESTObjectTypeEnum.SafeguardModel.name());
			sestobj.setIdentifier(sestobjId);
			sestobjMapper.insertWithIdentifier(sestobj);
			
			
			//use the Safeguard Mapper to insert the Safeguard Model
			SestSafeguardModel safeguardModel= new SestSafeguardModel();
			safeguardModel.setSafeguardModelJson(safeguardModelJson);
			safeguardModel.setSestobjId(sestobjId);
			safeguardMapper.insert(safeguardModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}


	}

	@Override
	public void update(String safeguardModelJson, String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("updateQuestionnaireJSON Safeguard Model");

		try {
			//create a new Safeguard Mapper
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			//use the Safeguard Mapper to insert the Safeguard Model
			safeguardMapper.update(safeguardModelJson, sestobjId);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}

	}

	@Override
	public SestSafeguardModel getByIdentifier(String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("get By Identifier -  Safeguard Model");
		SestSafeguardModel safeguardModel;

		try {
			//create a new Safeguard Mapper
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			//use the Threat Mapper to insert the Threat Model
			safeguardModel = safeguardMapper.getByIdentifier(sestobjId);
			LOG.info("get By Identifier -  Safeguard Model returned: " + safeguardModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}
		
		return (null != safeguardModel)?safeguardModel: null;
	}

	@Override
	public SestSafeguardModel getLastByProjectIdentifier(String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("getLastByProjectIdentifier -  Safeguard Model");
		SestSafeguardModel safeguardModel;

		try {
			//create a new Safeguard Mapper
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			safeguardModel = safeguardMapper.getLastByProjectIdentifier(sestobjId);
			LOG.info("getLastByProjectIdentifier -  Safeguard Model returned: " + safeguardModel);
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}

		return (null != safeguardModel)?safeguardModel: null;
	}


	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
