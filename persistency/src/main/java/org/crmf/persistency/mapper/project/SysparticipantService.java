/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysparticipantService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.project;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.SysParticipant;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

//This class manages the database interactions related to the SystemParticipant
@Service
@Qualifier("default")
public class SysparticipantService {

  private static final Logger LOG = LoggerFactory.getLogger(SysparticipantService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  public void insert(ArrayList<SystemParticipant> partecipants, Integer sysprjId) throws Exception {

    Sestobj sestobj = null;
    try {
      SysparticipantMapper participantMapper = sqlSession.getMapper(SysparticipantMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      for (SystemParticipant systemParticipant : partecipants) {
        SysParticipant sysParticipant = new SysParticipant();
        sysParticipant.convertFromModel(systemParticipant);

        sestobj = new Sestobj();
        sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProject.name());
        sestobjMapper.insert(sestobj);
        LOG.info("Insert sestObject identifier ");

        sysParticipant.setSestobjId(sestobj.getIdentifier());
        sysParticipant.setSysprojectId(sysprjId);
        participantMapper.insert(sysParticipant);
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public void update(ArrayList<SystemParticipant> partecipants, Integer sysprjId) {

    try {
      deleteByProjectId(sysprjId);
      insert(partecipants, sysprjId);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public void deleteByProjectId(Integer id) {

    SysparticipantMapper participantMapper = sqlSession.getMapper(SysparticipantMapper.class);

    try {
      participantMapper.deleteByProjectId(id);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  public ArrayList<SystemParticipant> getByProjectId(Integer id) {

    SysparticipantMapper participantMapper = sqlSession.getMapper(SysparticipantMapper.class);

    ArrayList<SystemParticipant> systempartecipants = new ArrayList<>();
    try {

      ArrayList<SysParticipant> partecipants = participantMapper.getByProjectId(id);

      for (SysParticipant sysParticipant : partecipants) {

        systempartecipants.add(sysParticipant.convertToModel());
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
    return systempartecipants;
  }
}
