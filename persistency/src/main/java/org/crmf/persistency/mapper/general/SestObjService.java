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

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObject;
import org.crmf.persistency.domain.general.Sestobj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the SestObjects (each object in the SEST Data Model derives from the SESTObject class, encompassing an unique id which is 
//used in order to register/updateQuestionnaireJSON the permission attributions
@Service
@Qualifier("default")
public class SestObjService implements SestObjServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SestObjService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  public Integer insert(Sestobj sestobj) throws Exception {

    SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
    sestObjMapper.insert(sestobj);
    return sestobj.getId();
  }

  public String updateLock(String viewIdentifier, String userIdentifier) throws Exception {

    try {
      SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
      sestObjMapper.updateLock(viewIdentifier, userIdentifier);
    } catch (Exception ex) {
      LOG.error("Unable to lock view ", ex);
      throw ex;
    }
    return viewIdentifier;
  }

  public void delete(Integer sestobjId) {
    SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
    sestObjMapper.delete(sestobjId);
  }

  public SESTObject getById(Integer sestobjId) {
    SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
    return sestObjMapper.getById(sestobjId).convertToModel();
  }


  public SESTObject getByIdentifier(String identifier) {
    try {
      SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
      Sestobj obj = sestObjMapper.getByIdentifier(identifier);
      LOG.info("getByIdentifier {} result :{} ", identifier, obj);

      return obj.convertToModel();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
    return null;
  }

  public int getIdByIdentifier(String identifier) {
    SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
    return sestObjMapper.getIdByIdentifier(identifier);
  }

  @Override
  public List<SESTObject> getAll() {
    List<SESTObject> sestObjs = new ArrayList<>();

    SestobjMapper sestObjMapper = sqlSession.getMapper(SestobjMapper.class);
    List<Sestobj> objs = sestObjMapper.getAll();
    for (Sestobj sestobj : objs) {
      sestObjs.add(sestobj.convertToModel());
    }
    return sestObjs;
  }
}
