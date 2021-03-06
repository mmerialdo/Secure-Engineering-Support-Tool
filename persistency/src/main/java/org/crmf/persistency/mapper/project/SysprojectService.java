/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SysprojectService.java"
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
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.SysProject;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

//This class manages the database interactions related to the SystemProject
@Service
@Qualifier("default")
public class SysprojectService implements SysprojectServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SysprojectService.class.getName());

  @Autowired
  private SysparticipantService syspService;
  @Autowired
  private SqlSession sqlSession;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.SysprojectServiceInterface#insert(org
   * .crmf.model.riskassessment.SystemProject)
   */
  @Override
  public Integer insert(SystemProject sysprojectDM) throws Exception {
    LOG.info("Insert SysProject");
    SysProject project = new SysProject();
    project.convertFromModel(sysprojectDM);

    Sestobj sestobj = null;
    try {
      SysprojectMapper projectMapper = sqlSession.getMapper(SysprojectMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProject.name());
      sestobjMapper.insert(sestobj);
      LOG.info("Insert sestObject identifier ");

      project.setSestobjId(sestobj.getIdentifier());
      projectMapper.insert(project);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    if (sysprojectDM.getParticipants() != null) {
      try {
        syspService.insert(sysprojectDM.getParticipants(), project.getId());
      } catch (Exception ex) {
        LOG.error(ex.getMessage());
      }
    }
    return project.getId();
  }

  @Override
  public void update(SystemProject sysprojectDM) {
    LOG.info("Update SysProject");
    SysProject project = new SysProject();
    project.convertFromModel(sysprojectDM);

    try {
      SysprojectMapper projectMapper = sqlSession.getMapper(SysprojectMapper.class);

      projectMapper.update(project);
      syspService.update(sysprojectDM.getParticipants(), project.getId());
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }
}
