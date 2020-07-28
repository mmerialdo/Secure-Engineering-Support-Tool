/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestObjService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.general;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObject;
import org.crmf.persistency.domain.general.Sestobj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the SestObjects (each object in the SEST Data Model derives from the SESTObject class, encompassing an unique id which is 
//used in order to register/updateQuestionnaireJSON the permission attributions
public class SestObjService implements SestObjServiceInterface{

	private static final Logger LOG = LoggerFactory.getLogger(SestObjService.class.getName());
	org.crmf.persistency.session.PersistencySessionFactory sessionFactory;
	
	public Integer insert(Sestobj sestobj) throws Exception {
		SqlSession sqlSession = sessionFactory.getSession();
		
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			sestObjMapper.insert(sestobj);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
		return sestobj.getId();
	}

	public String updateLock(String viewIdentifier, String userIdentifier) throws Exception {
		SqlSession sqlSession = sessionFactory.getSession();

		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			sestObjMapper.updateLock(viewIdentifier, userIdentifier);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error("Unable to lock view ", ex);
			throw ex;
		} finally {
			sqlSession.close();
		}
		return viewIdentifier;
	}

	public void delete(Integer sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			sestObjMapper.delete(sestobjId);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}
	}

	public SESTObject getById(Integer sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			
			return sestObjMapper.getById(sestobjId).convertToModel();
		} finally {
			sqlSession.close();
		}
	}
	

	public SESTObject getByIdentifier(String identifier){
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			Sestobj obj = sestObjMapper.getByIdentifier(identifier);
			LOG.info("getByIdentifier {} result :{} ",identifier,obj);
			
			return obj.convertToModel();
		}catch(Exception ex){
			LOG.error(ex.getMessage());
		} finally {
			sqlSession.close();
		}
		return null;
	}

	public int getIdByIdentifier(String identifier){
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			return sestObjMapper.getIdByIdentifier(identifier);
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public List<SESTObject> getAll() {
		SqlSession sqlSession = sessionFactory.getSession();
		List<SESTObject> sestObjs = new ArrayList<>();
		try {
			SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
			List<Sestobj> objs = sestObjMapper.getAll();
			for (Sestobj sestobj : objs) {
				sestObjs.add(sestobj.convertToModel());
			}
		} finally {
			sqlSession.close();
		}
		return sestObjs;
	}

	public org.crmf.persistency.session.PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(org.crmf.persistency.session.PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
