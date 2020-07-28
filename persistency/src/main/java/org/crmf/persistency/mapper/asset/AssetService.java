/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.asset;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.persistency.domain.asset.SestAssetModel;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the AssetModel
public class AssetService implements AssetServiceInterface {
	private static final Logger LOG = LoggerFactory.getLogger(AssetService.class.getName());
	PersistencySessionFactory sessionFactory;


	@Override
	public void insert(String assetModelJson, String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("Insert Asset Model");

		Sestobj sestobj = null;
		
		try {
			//create a new Asset Mapper
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
			
			//create a new sestObj with the identifier in input and type AssetModel
			LOG.info("Insert sestObject");
			sestobj = new Sestobj();
			sestobj.setObjtype(SESTObjectTypeEnum.AssetModel.name());
			sestobj.setIdentifier(sestobjId);
			sestobjMapper.insertWithIdentifier(sestobj);
			
			
			//use the Asset Mapper to insert the Asset Model
			SestAssetModel assetModel= new SestAssetModel();
			assetModel.setAssetModelJson(assetModelJson);
			assetModel.setSestobjId(sestobjId);
			assetMapper.insert(assetModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error("AssetModel insert exception: " + ex.getMessage(), ex);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public void update(String assetModelJson, String identifier){
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("Update Asset Model");

		try {
			//create a new Asset Mapper
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			//use the Asset Mapper to insert the Asset Model
			assetMapper.update(assetModelJson, identifier);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error("AssetModel updateQuestionnaireJSON exception: " + ex.getMessage(), ex);
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public SestAssetModel getByIdentifier(String sestobjId) {
		SqlSession sqlSession = sessionFactory.getSession();
		LOG.info("get By Identifier -  Asset Model");
		SestAssetModel assetModel;

		try {
			//create a new Asset Mapper
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			//use the Asset Mapper to insert the Asset Model
			assetModel = assetMapper.getByIdentifier(sestobjId);
			LOG.info("get By Identifier -  Asset Model returned: " + assetModel);
			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error("AssetModel getByIdentifier exception: " + ex.getMessage(), ex);
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}
		
		return (null != assetModel)?assetModel: null;
	}
	
//	@Override
//	public SestAssetModel getById(Integer id){
//		SqlSession sqlSession = sessionFactory.getSession();
//		LOG.info("get By Id - Asset Model");
//		SestAssetModel assetModel;
//
//		try {
//			//create a new Asset Mapper
//			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
//			//use the Asset Mapper to insert the Asset Model
//			assetModel = assetMapper.getById(id);
//			sqlSession.commit();
//		} catch (Exception ex) {
//			LOG.error(ex.getMessage());
//			sqlSession.rollback();
//			return null;
//		} finally {
//			sqlSession.close();
//		}
//		
//		return (null != assetModel)?assetModel: null;
//	}
	
	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
