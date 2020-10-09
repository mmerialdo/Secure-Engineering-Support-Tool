/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.requirement;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.requirement.Requirement;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.project.SysprojectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the Requirement
@Service
@Qualifier("default")
public class RequirementService implements RequirementServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RequirementService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  @Override
  public void insertSysRequirement(org.crmf.model.requirement.Requirement requirementDM, String sysprojectIdentifier) {
    LOG.info("Insert SysRequirement");
    Requirement requirement = new Requirement();
    requirement.convertFromModel(requirementDM);

    Sestobj sestobj = null;

    try {
      RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);
      SysprojectMapper sysProjectMapper = sqlSession.getMapper(SysprojectMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProject.name());
      sestobjMapper.insert(sestobj);

      Integer sysprjId = sysProjectMapper.getIdByIdentifier(sysprojectIdentifier);
      requirement.setSysprojectId(sysprjId);
      requirement.setSestobjId(sestobj.getIdentifier());
      requirementMapper.insert(requirement);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void deleteSysRequirement(org.crmf.model.requirement.Requirement requirementDM) {
    Requirement requirement = new Requirement();
    requirement.convertFromModel(requirementDM);

    try {
      RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

      requirement.setStatus(RequirementStatusEnum.Canceled.name());
      requirementMapper.insert(requirement);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public List<org.crmf.model.requirement.Requirement> getByIds(List<String> ids) {
    LOG.info("called getByIds ");
    List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<>();
    RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

    List<Requirement> requirements = requirementMapper.getByIds(ids);

    for (Requirement requirement : requirements) {

      org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
      prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

      requirementsToSend.add(prjRequirement);
    }
    return requirementsToSend;
  }

  @Override
  public List<org.crmf.model.requirement.Requirement> getBySysProject(String sysprojectIdentifier) {

    LOG.info("called getSysProject " + sysprojectIdentifier);
    List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<>();
    RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);
    SysprojectMapper sysProjectMapper = sqlSession.getMapper(SysprojectMapper.class);

    Integer sysprjId = sysProjectMapper.getIdByIdentifier(sysprojectIdentifier);
    List<Requirement> requirements = requirementMapper.getBySysProject(sysprjId);

    for (Requirement requirement : requirements) {

      org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
      prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

      requirementsToSend.add(prjRequirement);
    }
    return requirementsToSend;
  }

  @Override
  public List<org.crmf.model.requirement.Requirement> getBySysProjectAndFile(String sysprojectIdentifier, String filename) {

    LOG.info("called getSysProject " + sysprojectIdentifier);
    LOG.info("called filename " + filename);
    List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<org.crmf.model.requirement.Requirement>();
    RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

    List<Requirement> requirements = requirementMapper.getBySysProjectAndFile(sysprojectIdentifier, filename);

    for (Requirement requirement : requirements) {

      org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
      prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

      requirementsToSend.add(prjRequirement);
    }
    return requirementsToSend;
  }

  @Override
  public List<String> getFilenameByProject(String sysprojectIdentifier) {

    LOG.info("called getSysProject " + sysprojectIdentifier);
    try {
      RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);
      SysprojectMapper sysProjectMapper = sqlSession.getMapper(SysprojectMapper.class);

      Integer sysprjId = sysProjectMapper.getIdByIdentifier(sysprojectIdentifier);
      return requirementMapper.getFilenameByProject(String.valueOf(sysprjId));
    } catch (Exception ex) {
      LOG.error("Unable to load requirement files name.", ex);
    }
    return null;
  }
}
