/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprofileService.java"
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
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.AssProfile;
import org.crmf.persistency.domain.project.AssTemplate;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the AssessmentProfile

@Service
@Qualifier("default")
public class AssprofileService implements AssprofileServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AssprofileService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.AssprofileServiceInterface#insert(org
   * .crmf.model.riskassessment.AssessmentProfile)
   */
  @Override
  public String insert(AssessmentProfile assprofileDM) {
    LOG.info("Insert Profile");
    AssProfile profile = new AssProfile();
    profile.convertFromModel(assprofileDM);
    LOG.info("Insert profile " + profile);

    // String identifier = UUID.randomUUID().toString();
    Sestobj sestobj = null;

    try {
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProfile.name());
      sestobjMapper.insert(sestobj);

      profile.setSestobjId(sestobj.getIdentifier());
      profileMapper.insert(profile);

      LOG.info("After Insert Profile");

      List<AssessmentTemplate> templates = assprofileDM.getTemplates();
      if (templates != null && !templates.isEmpty()) {

        for (AssessmentTemplate assTemplate : templates) {

          Integer templateID = templateMapper.getIdByIdentifier(assTemplate.getIdentifier());

          AssTemplate templateDB = new AssTemplate();
          templateDB.convertFromModel(assTemplate);
          templateDB.setId(templateID);
          templateDB.setProfileId(profile.getId());
          templateMapper.insertProfileAssoc(templateDB);
          LOG.info("Insert Template " + templateDB.getId() + ", profile " + profile.getId());
        }
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return sestobj.getIdentifier();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.project.AssprofileServiceInterface#
   * deleteCascade(java.lang.String)
   */
  @Override
  public void deleteCascade(String identifier) {
    LOG.info("Delete assessment profile cascade " + identifier);
    AssprofileMapper assprofileMapper = sqlSession.getMapper(AssprofileMapper.class);

    LOG.info("Delete assessment profile ");
    assprofileMapper.deleteByIdentifier(identifier);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.project.AssprofileServiceInterface#
   * getByIdentifier(java.lang.String)
   */
  @Override
  public AssessmentProfile getByIdentifier(String identifier) {
    AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
    AssessmentProfile profileToSend = null;
    AssProfile profile = profileMapper.getByIdentifier(identifier);
    if (profile != null) {
      profileToSend = profile.convertToModel();
      profileToSend.setObjType(SESTObjectTypeEnum.AssessmentProfile);
    }
    return profileToSend;
  }

  public Integer getIdByIdentifier(String identifier) {
    AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
    return profileMapper.getIdByIdentifier(identifier);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.AssprofileServiceInterface#getAll()
   */
  @Override
  public List<AssessmentProfile> getAll() {
    LOG.info("called getAll");
    List<AssessmentProfile> profilesToSend = new ArrayList<>();
    AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
    List<AssProfile> profiles = profileMapper.getAll();

    for (AssProfile assProfile : profiles) {
      AssessmentProfile profileToSend = assProfile.convertToModel();
      profileToSend.setObjType(SESTObjectTypeEnum.AssessmentProfile);

      profilesToSend.add(profileToSend);
      LOG.info("added profile to listUser");
    }
    return profilesToSend;
  }

  @Override
  public void update(AssessmentProfile assprofileDM) {

    LOG.info("Update Profile");
    AssProfile profile = new AssProfile();
    profile.convertFromModel(assprofileDM);
    try {
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);

      Integer id = profileMapper.getIdByIdentifier(assprofileDM.getIdentifier());
      profile.setId(id);
      LOG.info("Update profile " + profile);
      profileMapper.insert(profile);

      LOG.info("After Insert Profile");

      List<AssTemplate> templatesOld = templateMapper.getByProfileIdentifier(profile.getSestobjId());
      if (templatesOld != null && !templatesOld.isEmpty()) {
        LOG.info("Delete templatesOld " + templatesOld.size());

        for (AssTemplate assTemplate : templatesOld) {

          assTemplate.setProfileId(profile.getId());
          LOG.info("Delete Template " + assTemplate.getId() + ", profile " + assTemplate.getProfileId());
          templateMapper.deleteProfileAssoc(assTemplate);
        }
      }

      ArrayList<AssTemplate> templates = profile.getTemplates();
      if (templates != null && !templates.isEmpty()) {
        LOG.info("Insert templates new " + templates.size());

        for (AssTemplate assTemplate : templates) {

          Integer templateId = templateMapper.getIdByIdentifier(assTemplate.getSestobjId());
          assTemplate.setProfileId(profile.getId());
          assTemplate.setId(templateId);
          templateMapper.insertProfileAssoc(assTemplate);
          LOG.info("Insert Template " + templateId + ", profile " + profile.getId());
        }
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }
}
