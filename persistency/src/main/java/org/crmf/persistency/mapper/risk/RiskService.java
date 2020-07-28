/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessmentelements.PrimaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.SecondaryAssetCategoryEnum;
import org.crmf.model.riskassessmentelements.SecurityImpactScopeEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.risk.SeriousnessScale;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.risk.SestRiskScenarioReference;
import org.crmf.persistency.domain.risk.StatusImpactScale;
import org.crmf.persistency.domain.risk.StatusLikelihoodScale;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.threat.ThreatMapper;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the RiskModel
public class RiskService implements RiskServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RiskService.class.getName());
  PersistencySessionFactory sessionFactory;

  @Override
  public void insert(String riskModelJson, String sestobjId) {
    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("Insert Risk Model");

    Sestobj sestobj = null;

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      //create a new sestObj with the identifier in input and type RiskModel
      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.RiskModel.name());
      sestobj.setIdentifier(sestobjId);
      sestobjMapper.insertWithIdentifier(sestobj);


      //use the Risk Mapper to insert the Risk Model
      SestRiskModel riskModel = new SestRiskModel();
      riskModel.setRiskModelJson(riskModelJson);
      riskModel.setSestobjId(sestobjId);
      riskMapper.insert(riskModel);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }


  }

  @Override
  public void update(String riskModelJson, String identifier) {
    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("updateQuestionnaireJSON Risk Model");

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      //use the Risk Mapper to insert the Risk Model
      riskMapper.update(riskModelJson, identifier);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }


  }

  @Override
  public SestRiskModel getByIdentifier(String sestobjId) {
    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("get By Identifier -  Risk Model");
    SestRiskModel riskModel;

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      //use the Risk Mapper to insert the Risk Model
      riskModel = riskMapper.getByIdentifier(sestobjId);
      LOG.info("get By Identifier -  Risk Model returned: " + riskModel);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }

    return (null != riskModel) ? riskModel : null;
  }

  @Override
  public List<SeriousnessScale> getSeriousness(int projectId) {

    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("getSeriousness -  ProjectId: " + projectId);

    List<SeriousnessScale> scales = new ArrayList<>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


      //Retrieve the SeriousnessScales for a RIskAssessmentProject
      scales = riskMapper.getSeriousnessByProjectId(projectId);

      LOG.info("getSeriousness -  Scales returned");

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }

    return (null != scales) ? scales : null;

  }

  @Override
  public List<StatusImpactScale> getStatusImpact(int projectId) {

    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("getStatusImpact -  ProjectId: " + projectId);

    List<StatusImpactScale> scales = new ArrayList<>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


      //Retrieve the StatusImpactScale for a RIskAssessmentProject
      scales = riskMapper.getStatusImpactByProjectId(projectId);

      LOG.info("getStatusImpact -  Scales returned");

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }

    return (null != scales) ? scales : null;

  }

  @Override
  public List<StatusLikelihoodScale> getStatusLikelihood(int projectId) {

    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("getStatusLikelihood -  ProjectId: " + projectId);

    List<StatusLikelihoodScale> scales = new ArrayList<>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


      //Retrieve the StatusLikelihoodScale for a RIskAssessmentProject
      scales = riskMapper.getStatusLikelihoodByProjectId(projectId);

      LOG.info("getStatusLikelihood -  Scales returned: " + scales);

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }

    return (null != scales) ? scales : null;

  }

  @Override
  public boolean updateScenarioRepository(ArrayList<RiskScenarioReference> rsr) {
    SqlSession sqlSession = sessionFactory.getSession();

    Set<SestRiskScenarioReference> convertedModelScenarioReference = new HashSet<SestRiskScenarioReference>();
    boolean result = true;

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);

      //for each Scenario in input converts it and adds to Reference Model
      for (RiskScenarioReference currentScenario : rsr) {
        //Converts the input scenario into a new Sest Risk Scenario Reference
        SestRiskScenarioReference sestRsr = convertModelRiskScenarioReferenceToDbStandard(currentScenario);

        if (null != sestRsr)
          convertedModelScenarioReference.add(sestRsr);
      }

      // clears the table RISK_SCENARIO_REFERENCE
      riskMapper.clearRiskReferenceScenario();

      // updateQuestionnaireJSON the scenario reference with the new entries
      for (SestRiskScenarioReference referenceEntry : convertedModelScenarioReference) {
        referenceEntry.setSestobjId(UUID.randomUUID().toString());
        result = result & riskMapper.insertScenarioReference(referenceEntry);
      }

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error("Exception in updateScenarioRepository " + ex.getMessage());
      sqlSession.rollback();
      return false;
    } finally {
      sqlSession.close();
    }

    return result;
  }

  /**
   * Converts the risk scenario Reference from model format to DB format
   *
   * @param riskScenario
   * @return a sest DB standard compliant format risk scenario reference
   */
  private SestRiskScenarioReference convertModelRiskScenarioReferenceToDbStandard(RiskScenarioReference riskScenario) {
    SestRiskScenarioReference sestRsr = new SestRiskScenarioReference();
    SqlSession sqlSession = sessionFactory.getSession();

    try {
      //create a new Vulnerability Mapper
      VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);

      //set asset type
      sestRsr.setAssetType(riskScenario.getAssetType().toString());
      //set asset type
      sestRsr.setSecondaryAssetType(riskScenario.getSupportingAsset().toString());
      //set security Scope
      sestRsr.setSecurityScope(riskScenario.getAice().toString());

      //retrieve and set vulnerability Reference id

      String vulnCatalogueId = riskScenario.getVulnerabilityCode().trim();

      Integer vulnerabilityReferenceId = vulnerabilityMapper.getVulnerabilityReferenceIdByCatalogueId(vulnCatalogueId);
      if (null == vulnerabilityReferenceId) {
        LOG.error("no Vulnerability Reference found for Risk Scenario Reference:" + vulnCatalogueId);
        return null;
      }
      sestRsr.setVulnerabilityReferenceId(vulnerabilityReferenceId.toString());

      //retrieve and set threat Reference id
      String catalogueId = riskScenario.getEventType() + "." + riskScenario.getEventSubType() + "-" + riskScenario.getPlace() + "-" + riskScenario.getTime() + "-" + riskScenario.getAccess() + "-" + riskScenario.getProcess() + "-" + riskScenario.getActor();
      Integer threatReferenceId = threatMapper.retrieveThreatReferenceId(catalogueId);
      if (null == threatReferenceId) {
        LOG.error("no Threat Reference found for Risk Scenario Reference: " + catalogueId);
        return null;
      }
      sestRsr.setThreatReferenceId(threatReferenceId.toString());

      //set security measures fields
      sestRsr.setDissuasion(riskScenario.getDissuasion());
      sestRsr.setPrevention(riskScenario.getPrevention());
      sestRsr.setConfining(riskScenario.getConfining());
      sestRsr.setPalliative(riskScenario.getPalliative());

      if (riskScenario.getIdentifier() != null) {
        sestRsr.setSestobjId(riskScenario.getIdentifier());
      }
    } catch (Exception ex) {
      LOG.error("Exception in convertModelRiskScenarioReferenceToDbStandard" + ex.getMessage(), ex);
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }

    return sestRsr;
  }

  /**
   * Converts the risk scenario Reference from DB format to model format
   *
   * @param sqlSession
   * @param referenceEntry
   * @return a sest model format risk scenario reference containing the info of the input DB risk scenario Reference
   */
  private RiskScenarioReference convertToModelRiskScenarioReference(SqlSession sqlSession, SestRiskScenarioReference referenceEntry) {

    RiskScenarioReference rsr = new RiskScenarioReference();

    try {
      //create a new Vulnerability Mapper
      VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);

      rsr.setIdentifier(referenceEntry.getSestobjId());
      //set asset type
      rsr.setAssetType(PrimaryAssetCategoryEnum.valueOf(referenceEntry.getAssetType()));
      //set secondary asset type
      rsr.setSupportingAsset(SecondaryAssetCategoryEnum.valueOf(referenceEntry.getSecondaryAssetType()));
      //set security Scope
      rsr.setAice(SecurityImpactScopeEnum.valueOf(referenceEntry.getSecurityScope()));

      //retrieve vulnerability associated to vulnerability Reference id and set corresponding fields
      String vulnRiskCatalogue = vulnerabilityMapper.getReferenceCatalogueById(Integer.valueOf(referenceEntry.getVulnerabilityReferenceId()));
      if (null == vulnRiskCatalogue) {
        LOG.error("no Vulnerability Reference found with id: " + referenceEntry.getVulnerabilityReferenceId());
        return null;
      }

      rsr.setVulnerabilityCode(vulnRiskCatalogue);

      //retrieve threat associated to threat Reference id and set corresponding fields
      String threatRiskCatalogue = threatMapper.getReferenceCatalogueById(Integer.valueOf(referenceEntry.getThreatReferenceId()));
      if (null == threatRiskCatalogue) {
        LOG.error("no Threat Reference found with id: " + referenceEntry.getThreatReferenceId());
        return null;
      }

      // split the threat catalogueId in order to get all threat info stored in it
      String[] chunks = threatRiskCatalogue.split("-", -1);

      if (chunks.length < 6) {
        LOG.error("Chunk size < 6 for threatRiskCatalogue: " + threatRiskCatalogue);
        return null;
      }

      for (int i = 0; i < 6; i++) {
        String value = chunks[i];
        switch (i) {
          case 0:
            String[] event = value.split("\\.");
            rsr.setEventType(event[0] + "." + event[1]);
            rsr.setEventSubType(event[2]);
            break;
          case 1:
            if (value == null) {
              value = "";
            }
            rsr.setPlace(value);
            break;
          case 2:
            if (value == null) {
              value = "";
            }
            rsr.setTime(value);
            break;
          case 3:
            if (value == null) {
              value = "";
            }
            rsr.setAccess(value);
            break;
          case 4:
            if (value == null) {
              value = "";
            }
            rsr.setProcess(value);
            break;
          case 5:
            if (value == null) {
              value = "";
            }
            rsr.setActor(value);
            break;
          default:
            break;
        }
      }


      //set security measures fields
      rsr.setDissuasion(referenceEntry.getDissuasion());
      if (rsr.getDissuasion() == null) {
        rsr.setDissuasion("");
      }
      rsr.setPrevention(referenceEntry.getPrevention());
      if (rsr.getPrevention() == null) {
        rsr.setPrevention("");
      }
      rsr.setConfining(referenceEntry.getConfining());
      if (rsr.getConfining() == null) {
        rsr.setConfining("");
      }
      rsr.setPalliative(referenceEntry.getPalliative());
      if (rsr.getPalliative() == null) {
        rsr.setPalliative("");
      }
    } catch (Exception ex) {
      LOG.error("Exception during convertToModelRiskScenarioReference: " + ex.getStackTrace(), ex);
      return null;
    }

    return rsr;
  }

  @Override
  public ArrayList<RiskScenarioReference> getRiskScenarioReference() {
    SqlSession sqlSession = sessionFactory.getSession();
    ArrayList<RiskScenarioReference> rsr = new ArrayList<RiskScenarioReference>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);

      // updateQuestionnaireJSON the scenario reference with the new entries
      for (SestRiskScenarioReference referenceEntry : riskMapper.getRiskScenarioReference()) {
        RiskScenarioReference currentScenario = convertToModelRiskScenarioReference(sqlSession, referenceEntry);
        if (currentScenario != null) {
          rsr.add(currentScenario);
        }
      }
    } catch (Exception ex) {
      LOG.error("Exception during getRiskScenarioReference: ", ex);
      sqlSession.rollback();
      return rsr;
    } finally {
      sqlSession.close();
    }

    return rsr;
  }

  public String insertRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    SqlSession sqlSession = sessionFactory.getSession();
    LOG.info("insert " + riskScenarioReference.getAssetType());
    SestRiskScenarioReference sestRiskScenarioReference = new SestRiskScenarioReference();
    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      sestRiskScenarioReference = convertModelRiskScenarioReferenceToDbStandard(riskScenarioReference);
      sestRiskScenarioReference.setSestobjId(UUID.randomUUID().toString());

      riskMapper.insertScenarioReference(sestRiskScenarioReference);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      sqlSession.rollback();
      throw ex;
    } finally {
      sqlSession.close();
    }
    return sestRiskScenarioReference.getSestobjId();
  }

  public void deleteRiskScenarioReference(List<String> identifier) throws Exception {
    SqlSession sqlSession = sessionFactory.getSession();
		identifier.forEach(id -> LOG.info("deleting risk scenario : "+identifier));

    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      riskMapper.deleteScenarioReference(identifier);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      sqlSession.rollback();
      throw ex;
    } finally {
      sqlSession.close();
    }
  }

  public void editRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    SqlSession sqlSession = sessionFactory.getSession();
    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      SestRiskScenarioReference sestRiskScenarioReference = convertModelRiskScenarioReferenceToDbStandard(riskScenarioReference);
      riskMapper.editScenarioReference(sestRiskScenarioReference);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      sqlSession.rollback();
      throw ex;
    } finally {
      sqlSession.close();
    }
  }

  public PersistencySessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(PersistencySessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
