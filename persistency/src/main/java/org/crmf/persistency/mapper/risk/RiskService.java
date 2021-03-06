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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//This class manages the database interactions related to the RiskModel
@Service
@Qualifier("default")
public class RiskService implements RiskServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RiskService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  @Override
  public void insert(String riskModelJson, String sestobjId) {
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
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void update(String riskModelJson, String identifier) {
    LOG.info("updateQuestionnaireJSON Risk Model");

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      //use the Risk Mapper to insert the Risk Model
      riskMapper.update(riskModelJson, identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public SestRiskModel getByIdentifier(String sestobjId) {
    LOG.info("get By Identifier -  Risk Model");
    SestRiskModel riskModel;

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      //use the Risk Mapper to insert the Risk Model
      riskModel = riskMapper.getByIdentifier(sestobjId);
      LOG.info("get By Identifier -  Risk Model returned: " + riskModel);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return riskModel;
  }

  @Override
  public List<SeriousnessScale> getSeriousness(int projectId) {

    LOG.info("getSeriousness -  ProjectId: " + projectId);

    List<SeriousnessScale> scales;

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


      //Retrieve the SeriousnessScales for a RIskAssessmentProject
      scales = riskMapper.getSeriousnessByProjectId(projectId);

      LOG.info("getSeriousness -  Scales returned");
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return scales;
  }

  @Override
  public List<StatusImpactScale> getStatusImpact(int projectId) {

    LOG.info("getStatusImpact -  ProjectId: " + projectId);

    List<StatusImpactScale> scales = new ArrayList<>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);


      //Retrieve the StatusImpactScale for a RIskAssessmentProject
      scales = riskMapper.getStatusImpactByProjectId(projectId);

      LOG.info("getStatusImpact -  Scales returned");

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return scales;

  }

  @Override
  public List<StatusLikelihoodScale> getStatusLikelihood(int projectId) {

    LOG.info("getStatusLikelihood -  ProjectId: " + projectId);

    List<StatusLikelihoodScale> scales = new ArrayList<>();

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);

      //Retrieve the StatusLikelihoodScale for a RIskAssessmentProject
      scales = riskMapper.getStatusLikelihoodByProjectId(projectId);

      LOG.info("getStatusLikelihood -  Scales returned: " + scales);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return scales;
  }

  @Override
  public boolean updateScenarioRepository(List<RiskScenarioReference> rsr) {

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
        result = result && riskMapper.insertScenarioReference(referenceEntry);
      }
    } catch (Exception ex) {
      LOG.error("Exception in updateScenarioRepository " + ex.getMessage());
      return false;
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
      return null;
    }
    return sestRsr;
  }

  /**
   * Converts the risk scenario Reference from DB format to model format
   *
   * @param referenceEntry
   * @return a sest model format risk scenario reference containing the info of the input DB risk scenario Reference
   */
  private RiskScenarioReference convertToModelRiskScenarioReference(SestRiskScenarioReference referenceEntry) {

    RiskScenarioReference rsr = new RiskScenarioReference();

    try {
      rsr.setIdentifier(referenceEntry.getSestobjId());
      rsr.setAssetType(PrimaryAssetCategoryEnum.valueOf(referenceEntry.getAssetType()));
      rsr.setSupportingAsset(SecondaryAssetCategoryEnum.valueOf(referenceEntry.getSecondaryAssetType()));
      rsr.setAice(SecurityImpactScopeEnum.valueOf(referenceEntry.getSecurityScope()));
      rsr.setVulnerabilityCode(referenceEntry.getVulnerabilityReferenceCatalogue());

      String threatCatalogueId = referenceEntry.getThreatReferenceCatalogue();

      if (null == threatCatalogueId) {
        LOG.error("no Threat Reference found with scenario identifier: " + referenceEntry.getSestobjId());
        return null;
      }

      // split the threat catalogueId in order to get all threat info stored in it
      String[] chunks = threatCatalogueId.split("-", -1);

      if (chunks.length < 6) {
        LOG.error("Chunk size < 6 for threatRiskCatalogue: " + threatCatalogueId);
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
    ArrayList<RiskScenarioReference> rsr = new ArrayList<>();
    LOG.error("getRiskScenarioReference ");

    try {
      //create a new Risk Mapper
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      LOG.error("getRiskScenarioReference riskMapper " + riskMapper);

      Set<SestRiskScenarioReference> scenarios = riskMapper.getRiskScenarioReferenceWithCataloguesId();
      // updateQuestionnaireJSON the scenario reference with the new entries
      for (SestRiskScenarioReference referenceEntry : scenarios) {

        RiskScenarioReference currentScenario = convertToModelRiskScenarioReference(referenceEntry);
        if (currentScenario != null) {
          rsr.add(currentScenario);
          LOG.error("getRiskScenarioReference currentScenario " + currentScenario);
        }
      }
    } catch (Exception ex) {
      LOG.error("Exception during getRiskScenarioReference: ", ex);
      return rsr;
    }
    return rsr;
  }

  public Map<String, Map<RiskScenarioReference.SecurityMeasureEnum, String>> getRiskScenarioReferenceSafeguards() {
    Map<String, Map<RiskScenarioReference.SecurityMeasureEnum, String>> safeguards = new HashMap<>();
    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      Map<RiskScenarioReference.SecurityMeasureEnum, String> measuresType = new HashMap<>();
      Set<SestRiskScenarioReference> scenarios = riskMapper.getRiskScenarioReference();
      for (SestRiskScenarioReference referenceEntry : scenarios) {
        measuresType.put(RiskScenarioReference.SecurityMeasureEnum.DISSUASION, referenceEntry.getDissuasion());
        measuresType.put(RiskScenarioReference.SecurityMeasureEnum.PALLIATION, referenceEntry.getPalliative());
        measuresType.put(RiskScenarioReference.SecurityMeasureEnum.PREVENTION, referenceEntry.getPrevention());
        measuresType.put(RiskScenarioReference.SecurityMeasureEnum.CONFINING, referenceEntry.getConfining());
        safeguards.put(referenceEntry.getSestobjId(), measuresType);
      }
    } catch (Exception ex) {
      LOG.error("Exception during getRiskScenarioReference: ", ex);
    }
    return safeguards;
  }

  public String insertRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    LOG.info("insert " + riskScenarioReference.getAssetType());
    SestRiskScenarioReference sestRiskScenarioReference = new SestRiskScenarioReference();
    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      sestRiskScenarioReference = convertModelRiskScenarioReferenceToDbStandard(riskScenarioReference);
      if (sestRiskScenarioReference != null) {
        sestRiskScenarioReference.setSestobjId(UUID.randomUUID().toString());

        riskMapper.insertScenarioReference(sestRiskScenarioReference);
        return sestRiskScenarioReference.getSestobjId();
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      throw ex;
    }
    return null;
  }

  public void deleteRiskScenarioReference(List<String> identifier) throws Exception {
    identifier.forEach(id -> LOG.info("deleting risk scenario : " + identifier));

    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      riskMapper.deleteScenarioReference(identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      throw ex;
    }
  }

  public void editRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception {
    try {
      RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
      SestRiskScenarioReference sestRiskScenarioReference = convertModelRiskScenarioReferenceToDbStandard(riskScenarioReference);
      riskMapper.editScenarioReference(sestRiskScenarioReference);
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
  }
}
