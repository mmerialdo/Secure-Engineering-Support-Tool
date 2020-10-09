/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AsstemplateService.java"
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
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.persistency.domain.asset.SestAssetModel;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.AssTemplate;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.domain.vulnerability.SestVulnerabilityModel;
import org.crmf.persistency.mapper.asset.AssetMapper;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.risk.RiskMapper;
import org.crmf.persistency.mapper.safeguard.SafeguardMapper;
import org.crmf.persistency.mapper.threat.ThreatMapper;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the AssessmentTemplate
@Service
@Qualifier("default")
public class AsstemplateService implements AsstemplateServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AsstemplateService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.project.AsstemplateServiceInterface#insert(
   * org.crmf.model.riskassessment.AssessmentTemplate)
   */
  @Override
  public String insert(AssessmentTemplate asstemplateDM, String profileIdentifier) {
    LOG.info("Insert Template");
    AssTemplate template = new AssTemplate();
    template.convertFromModel(asstemplateDM);

    Sestobj sestobj = null;

    try {
      AssprofileMapper profileMapper = sqlSession.getMapper(AssprofileMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
      AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
      VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.AssessmentTemplate.name());
      sestobjMapper.insert(sestobj);

      template.setSestobjId(sestobj.getIdentifier());
      //asstemplateDM.setIdentifier(sestobj.getIdentifier());

      // get the asset model
      SestAssetModel am = assetMapper.getByIdentifier(asstemplateDM.getAssetModel().getIdentifier());
      // set the table id of the retrieved assetModel
      template.setAssetId(am.getId());

      // get the vulnerability model
      SestVulnerabilityModel vm = vulnerabilityMapper.getByIdentifier(asstemplateDM.getVulnerabilityModel().getIdentifier());
      // set the table id of the retrieved vulnerabilityModel
      template.setVulnerabilityId(vm.getId());

      // get the threat model
      SestThreatModel tm = threatMapper.getByIdentifier(asstemplateDM.getThreatModel().getIdentifier());
      // set the table id of the retrieved threatModel
      template.setThreatId(tm.getId());

      //get the safeguard model
      SestSafeguardModel sm = safeguardMapper.getByIdentifier(asstemplateDM.getSafeguardModel().getIdentifier());
      // set the table id of the retrieved safeguardModel
      template.setSafeguardId(sm.getId());

      //retrieve the risk model
      SestRiskModel rm = riskMapper.getByIdentifier(asstemplateDM.getRiskModel().getIdentifier());
      template.setRiskModelId(rm.getId());

      // insert the new template into db
      templateMapper.insert(template);
      if (profileIdentifier != null) {
        // AssProject project =
        // projectMapper.getById(procedure.getProjectId());
        template.setProfileId(profileMapper.getIdByIdentifier(profileIdentifier));
        templateMapper.insertProfileAssoc(template);
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return sestobj.getIdentifier();
    //return asstemplateDM;
  }

  public void insertProfileAssoc(AssTemplate record) {
    try {
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);

      templateMapper.insertProfileAssoc(record);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void update(AssessmentTemplate asstemplateDM) {

    LOG.info("Update Template");
    AssTemplate template = new AssTemplate();
    template.convertFromModel(asstemplateDM);

    try {
      AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
      AsstemplateMapper templateMapper = sqlSession.getMapper(AsstemplateMapper.class);

      // Integer procedureId = procedureMapper
      // .getIdByIdentifier(Integer.valueOf(asstemplateDM.getAssociatedProcedure().getIdentifier()));
      // template.setProcedureId(procedureId);
      templateMapper.insert(template);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void deleteCascade(String identifier) {
    LOG.info("Delete assessment template cascade" + identifier);
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

    LOG.info("Delete assessment template ");
    asstemplateMapper.deleteByIdentifier(identifier);

    LOG.info("Delete sestobj ");
    sestobjMapper.deleteByIdentifier(identifier);
  }

  @Override
  public AssessmentTemplate getByIdentifier(String identifier) {
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);

    AssTemplate template = asstemplateMapper.getByIdentifier(identifier);
    AssessmentTemplate templateToSend = template.convertToModel();
    templateToSend.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

    return templateToSend;
  }

  public AssessmentTemplate getByIdentifierFull(String identifier) {

    LOG.info("getByIdentifierFull " + identifier);
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
    VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
    ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
    SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
    RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


    AssTemplate template = asstemplateMapper.getByIdentifier(identifier);
    AssessmentTemplate templateToSend = template.convertToModel();
    templateToSend.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

    // retrieve the Asset Model
    SestAssetModel am = assetMapper.getById(template.getAssetId());
    templateToSend.setAssetModel(am.convertToModel());

    // retrieve the vulnerability model
    SestVulnerabilityModel vm = vulnerabilityMapper.getById(template.getVulnerabilityId());
    templateToSend.setVulnerabilityModel(vm.convertToModel());

    //retrieve the threat model
    SestThreatModel tm = threatMapper.getById(template.getThreatId());
    templateToSend.setThreatModel(tm.convertToModel());

    //retrieve the safeguard model
    SestSafeguardModel sm = safeguardMapper.getById(template.getSafeguardId());
    templateToSend.setSafeguardModel(sm.convertToModel());

    //retrieve the risk model
    SestRiskModel rm = riskMapper.getById(template.getRiskModelId());
    templateToSend.setRiskModel(rm.convertToModel());


    return templateToSend;
  }


  @Override
  public Integer getIdByIdentifier(String identifier) {
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    return asstemplateMapper.getIdByIdentifier(identifier);
  }

  @Override
  public List<AssessmentTemplate> getAll() {
    LOG.info("called getAll");
    List<AssessmentTemplate> templatesToSend = new ArrayList<>();
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    List<AssTemplate> templates = asstemplateMapper.getAll();

    for (AssTemplate assTemplate : templates) {
      AssessmentTemplate templateToSend = assTemplate.convertToModel();
      templateToSend.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

      templatesToSend.add(templateToSend);
      LOG.info("added template");
    }
    return templatesToSend;
  }

  @Override
  public List<AssessmentTemplate> getByMethodology(String methodology) {

    LOG.info("called getByMethodology " + methodology);
    List<AssessmentTemplate> templatesToSend = new ArrayList<>();
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    List<AssTemplate> templates = asstemplateMapper.getByMethodology(methodology);

    for (AssTemplate assTemplate : templates) {
      AssessmentTemplate templateToSend = assTemplate.convertToModel();
      templateToSend.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

      templatesToSend.add(templateToSend);
      LOG.info("added template");
    }
    return templatesToSend;
  }

  @Override
  public List<AssessmentTemplate> getByProfileIdentifier(String identifier) {

    LOG.info("called getByProfileIdentifier " + identifier);
    List<AssessmentTemplate> templatesToSend = new ArrayList<>();
    AsstemplateMapper asstemplateMapper = sqlSession.getMapper(AsstemplateMapper.class);
    List<AssTemplate> templates = asstemplateMapper.getByProfileIdentifier(identifier);

    for (AssTemplate assTemplate : templates) {
      AssessmentTemplate templateToSend = assTemplate.convertToModel();
      templateToSend.setObjType(SESTObjectTypeEnum.AssessmentTemplate);

      templatesToSend.add(templateToSend);
      LOG.info("added template");
    }
    return templatesToSend;
  }
}
