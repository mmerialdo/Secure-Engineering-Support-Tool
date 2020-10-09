/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprojectService.java"
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
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.SystemParticipant;
import org.crmf.model.riskassessment.SystemProject;
import org.crmf.model.user.User;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.AssProfile;
import org.crmf.persistency.domain.project.AssProject;
import org.crmf.persistency.domain.project.AssTemplate;
import org.crmf.persistency.domain.project.SysParticipant;
import org.crmf.persistency.domain.project.SysProject;
import org.crmf.persistency.domain.user.Sestuser;
import org.crmf.persistency.mapper.audit.AssAuditMapper;
import org.crmf.persistency.mapper.audit.AssAuditService;
import org.crmf.persistency.mapper.general.CleanDatabaseMapper;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//This class manages the database interactions related to the AssessmentProject
@Service
@Qualifier("default")
public class AssprojectService implements AssprojectServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AssprojectService.class.getName());

  @Autowired
  SysprojectService sysprjService;
  @Autowired
  SysparticipantService syspartService;
  @Autowired
  RoleService roleService;
  @Autowired
  AssAuditService auditService;
  @Autowired
  private SqlSession sqlSession;
  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.AssprojectServiceInterface#insert(org
   * .crmf.model.riskassessment.AssessmentProject)
   */
  @Override
  public String insert(AssessmentProject assprojectDM) throws Exception {

    LOG.info("Insert project");
    AssProject project = new AssProject();
    project.convertFromModel(assprojectDM);

    // String identifier = UUID.randomUUID().toString();
    Sestobj sestobj = new Sestobj();

    try {
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProject.name());
      sestobjMapper.insert(sestobj);
      LOG.info("Insert sestObject identifier ");

      Integer sysprjId = sysprjService.insert(assprojectDM.getSystemProject());

      project.setSysprojectId(sysprjId);
      project.setTemplateId(templateMapper.getIdByIdentifier(assprojectDM.getTemplate().getIdentifier()));
      project.setProfileId(profileMapper.getIdByIdentifier(assprojectDM.getProfile().getIdentifier()));
      project.setUserpm(userMapper.getIdByIdentifier(assprojectDM.getProjectManager().getIdentifier()));
      project.setSestobjId(sestobj.getIdentifier());

      projectMapper.insert(project);
      LOG.info("1.Insert audit ");

      auditService.insertDefaultQuestionnaires(project.getId());
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      throw ex;
    }
    return sestobj.getIdentifier();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.project.AssprojectServiceInterface#
   * deleteCascade(java.lang.String)
   */
  @Override
  public void deleteCascade(String identifier) {

    LOG.info("Delete assessment project cascade " + identifier);
    try {
      AssprojectMapper assprojectMapper = sqlSession.getMapper(AssprojectMapper.class);
      AssAuditMapper auditMapper = sqlSession.getMapper(AssAuditMapper.class);
      CleanDatabaseMapper cleandbMapper = sqlSession.getMapper(CleanDatabaseMapper.class);

      SestAuditModel audit = auditMapper.getByProjectAndType(identifier, AuditTypeEnum.SECURITY.name());
      LOG.info("Delete assessment project " + identifier);
      assprojectMapper.deleteByIdentifier(identifier);
      LOG.info("Delete project audit " + audit.getId());
      auditMapper.delete(audit.getId());

      cleandbMapper.cleanAsset();
      cleandbMapper.cleanVulnerability();
      cleandbMapper.cleanThreat();
      cleandbMapper.cleanRiskModel();
      cleandbMapper.cleanRiskTreatment();
      cleandbMapper.cleanSafeguard();
      cleandbMapper.cleanSestObj();

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.project.AssprojectServiceInterface#
   * getByIdentifier(java.lang.String)
   */
  @Override
  public AssessmentProject getByIdentifier(String identifier) {

    try {
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
      SysprojectMapper sysprojectMapper = sqlSession.getMapper(SysprojectMapper.class);

      AssProject project = projectMapper.getByIdentifier(identifier);
      SysProject sysproject = sysprojectMapper.getById(project.getSysprojectId());
      AssessmentProject projectToSend = project.convertToModel();
      projectToSend.setObjType(SESTObjectTypeEnum.AssessmentProject);
      projectToSend.setSystemProject(sysproject.convertToModel());

      return projectToSend;
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
  }

  @Override
  public AssessmentProject getByIdentifierFull(String identifier) {

    LOG.info("getByIdentifierFull " + identifier);
    AssessmentProject projectToSend = null;
    try {
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
      SysprojectMapper sysprojectMapper = sqlSession.getMapper(SysprojectMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      SysparticipantMapper syspartecipantMapper = sqlSession.getMapper(SysparticipantMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      AssProject project = projectMapper.getByIdentifier(identifier);
      projectToSend = project.convertToModel();
      projectToSend.setObjType(SESTObjectTypeEnum.AssessmentProject);

      Integer userpmId = project.getUserpm();
      if (userpmId != null) {
        LOG.info("getting User PM " + userpmId);
        Sestuser userpm = userMapper.getById(userpmId);
        User user = userpm.convertToModel();
        user.setObjType(SESTObjectTypeEnum.User);
        projectToSend.setProjectManager(user);
      }

      Integer sysprjId = project.getSysprojectId();
      if (sysprjId != null) {
        LOG.info("getting sysproject " + sysprjId);
        SysProject sysproject = sysprojectMapper.getById(sysprjId);

        ArrayList<SysParticipant> partecipants = syspartecipantMapper.getByProjectId(sysprjId);
        ArrayList<SystemParticipant> systempartecipants = new ArrayList<>();
        for (SysParticipant participant : partecipants) {
          SystemParticipant part = participant.convertToModel();
          part.setObjType(SESTObjectTypeEnum.AssessmentProject);
          systempartecipants.add(part);
        }
        SystemProject systemproject = sysproject.convertToModel();
        systemproject.setParticipants(systempartecipants);
        systemproject.setObjType(SESTObjectTypeEnum.AssessmentProject);

        projectToSend.setSystemProject(systemproject);
      }

      Integer profileId = project.getProfileId();
      if (profileId != null) {
        LOG.info("getting profile " + profileId);
        AssProfile profile = profileMapper.getById(profileId);
        AssessmentProfile profileToSend = profile.convertToModel();
        profileToSend.setObjType(SESTObjectTypeEnum.AssessmentProfile);
        projectToSend.setProfile(profileToSend);
      }

      Integer templateId = project.getTemplateId();
      if (templateId != null) {
        LOG.info("getting template " + templateId);
        AssTemplate template = templateMapper.getById(templateId);
        AssessmentTemplate modelTemplate = template.convertToModel();
        modelTemplate.setObjType(SESTObjectTypeEnum.AssessmentTemplate);
        projectToSend.setTemplate(modelTemplate);
      }

      SestAuditModel audit = auditService.getByProjectAndType(project.getSestobjId(), AuditTypeEnum.SECURITY, true);
      if (audit != null) {
        LOG.info("getting audit " + audit.getId());
        projectToSend.setAudits(Arrays.asList(audit));
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }

    try {
      LOG.info("getByIdentifierFull users " + identifier);
      ArrayList<User> users = new ArrayList<>(roleService.getByProjectIdentifier(identifier));
      LOG.info("getByIdentifierFull users " + users.size());
      projectToSend.setUsers(users);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    return projectToSend;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.AssprojectServiceInterface#getAll()
   */
  @Override
  public List<AssessmentProject> getAll() {
    LOG.info("called getAll");
    AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
    SysprojectMapper sysprojectMapper = sqlSession.getMapper(SysprojectMapper.class);
    AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

    List<AssessmentProject> projectsToSend = new ArrayList<>();
    try {
      List<AssProject> projects = projectMapper.getAll();

      for (AssProject assproject : projects) {
        AssessmentProject projectToSend = assproject.convertToModel();
        projectToSend.setObjType(SESTObjectTypeEnum.AssessmentProject);

        Integer sysprojectId = assproject.getSysprojectId();
        LOG.info("getting sysproject " + sysprojectId);
        if (sysprojectId != null) {
          SysProject sysproject = sysprojectMapper.getById(sysprojectId);
          projectToSend.setSystemProject(sysproject.convertToModel());
        }

        Integer templateId = assproject.getTemplateId();
        LOG.info("getting template " + templateId);
        if (templateId != null) {
          AssTemplate template = templateMapper.getById(templateId);
          projectToSend.setTemplate(template.convertToModel());
        }

        Integer userpmId = assproject.getUserpm();
        LOG.info("getting User PM " + userpmId);
        if (userpmId != null) {
          Sestuser userpm = userMapper.getById(userpmId);
          projectToSend.setProjectManager(userpm.convertToModel());
        }

        projectsToSend.add(projectToSend);
        LOG.info("added project to listUser");
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return projectsToSend;
  }

  @Override
  public Integer getIdByIdentifier(String identifier) {
    LOG.info("called getIdByIdentifier");
    AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
    return projectMapper.getIdByIdentifier(identifier);
  }

  @Override
  public AssessmentProject getById(int id) {
    LOG.info("called getById");
    AssessmentProject projectToSend;

    try {
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
      projectToSend = projectMapper.getById(id).convertToModel();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }

    return projectToSend;
  }

  @Override
  public void update(AssessmentProject assprojectDM) {
    LOG.info("Update project with identifier " + assprojectDM.getIdentifier());

    AssProject project = new AssProject();
    project.convertFromModel(assprojectDM);

    SystemProject sysprojectDM = assprojectDM.getSystemProject();
    SysProject sysproject = new SysProject();
    sysproject.convertFromModel(sysprojectDM);

    ArrayList<SystemParticipant> participants = sysprojectDM.getParticipants();
    Integer sysprojectId = null;
    try {
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      SysprojectMapper sysprojectMapper = sqlSession.getMapper(SysprojectMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      sysprojectId = sysprojectMapper.getIdByAssprojectIdentifier(project.getSestobjId());
      project.setTemplateId(templateMapper.getIdByIdentifier(assprojectDM.getTemplate().getIdentifier()));
      project.setProfileId(profileMapper.getIdByIdentifier(assprojectDM.getProfile().getIdentifier()));
      project.setUserpm(userMapper.getIdByIdentifier(assprojectDM.getProjectManager().getIdentifier()));
      project.setSysprojectId(sysprojectId);
      projectMapper.update(project);

      sysproject.setId(sysprojectId);
      sysprojectMapper.update(sysproject);

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }

    try {
      syspartService.deleteByProjectId(sysprojectId);
      syspartService.insert(participants, sysprojectId);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }
}
