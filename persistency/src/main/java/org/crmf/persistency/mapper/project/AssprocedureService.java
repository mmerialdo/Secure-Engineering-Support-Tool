/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssprocedureService.java"
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
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssetModel;
import org.crmf.model.riskassessment.RiskModel;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessment.VulnerabilityModel;
import org.crmf.persistency.domain.asset.SestAssetModel;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.project.AssProcedure;
import org.crmf.persistency.domain.risk.SestRiskModel;
import org.crmf.persistency.domain.risk.SestRiskTreatmentModel;
import org.crmf.persistency.domain.safeguard.SestSafeguardModel;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.domain.vulnerability.SestVulnerabilityModel;
import org.crmf.persistency.mapper.asset.AssetMapper;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.risk.RiskMapper;
import org.crmf.persistency.mapper.risk.RiskTreatmentMapper;
import org.crmf.persistency.mapper.safeguard.SafeguardMapper;
import org.crmf.persistency.mapper.threat.ThreatMapper;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//This class manages the database interactions related to the AssessmentProcedure
public class AssprocedureService implements AssprocedureServiceInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AssprocedureService.class.getName());
	PersistencySessionFactory sessionFactory;
	
	/* (non-Javadoc)
	 * @see org.crmf.persistency.mapper.project.AssprocedureServiceInterface#insert(org.crmf.model.riskassessment.AssessmentProcedure)
	 */
	@Override
	public AssessmentProcedure insert(AssessmentProcedure assprocedureDM, String projectIdentifier) throws Exception {
		SqlSession sqlSession = sessionFactory.getSession();

		LOG.info("Insert Procedure with identifier: " + assprocedureDM.getIdentifier());
		AssProcedure procedure = new AssProcedure();
		procedure.convertFromModel(assprocedureDM);

		Sestobj sestobj = null;
		
		try {
			AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			VulnerabilityMapper vulnMapper = sqlSession.getMapper(VulnerabilityMapper.class);
			ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
			RiskTreatmentMapper riskTreatmentMapper = sqlSession.getMapper(RiskTreatmentMapper.class);

			LOG.info("Insert sestObject");
			sestobj = new Sestobj();
			sestobj.setObjtype(SESTObjectTypeEnum.AssessmentProcedure.name());
			sestobjMapper.insert(sestobj);

			procedure.setProjectId(projectMapper.getIdByIdentifier(projectIdentifier));			
			procedure.setSestobjId(sestobj.getIdentifier());
			assprocedureDM.setIdentifier(sestobj.getIdentifier());
			
			// get the asset model
			SestAssetModel am = assetMapper.getByIdentifier(assprocedureDM.getAssetModel().getIdentifier());
			// set the table id of the retrieved assetModel
			procedure.setAssetId(am.getId());
			
			// get the vulnerability model
			SestVulnerabilityModel vm = vulnMapper.getByIdentifier(assprocedureDM.getVulnerabilityModel().getIdentifier());
			// set the table id of the retrieved vulnerabilityModel
			procedure.setVulnerabilityId(vm.getId());
			
			// get the threat model
			SestThreatModel tm = threatMapper.getByIdentifier(assprocedureDM.getThreatModel().getIdentifier());
			// set the table id of the retrieved threatModel
			procedure.setThreatId(tm.getId());
			
			// get the safeguard model
			SestSafeguardModel sm =  safeguardMapper.getByIdentifier(assprocedureDM.getSafeguardModel().getIdentifier());
			// set the table id of the retrieved safeguardModel
			procedure.setSafeguardId(sm.getId());
			
			//retrieve the risk model
			SestRiskModel rm = riskMapper.getByIdentifier(assprocedureDM.getRiskModel().getIdentifier());
			procedure.setRiskModelId(rm.getId());

			//retrieve the risk treatment model
			SestRiskTreatmentModel rtm = riskTreatmentMapper.getByIdentifier(assprocedureDM.getRiskTreatmentModel().getIdentifier());
			procedure.setRiskTreatmentModelId(rtm.getId());
			
			procedureMapper.insert(procedure);

			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
			return null;
		} finally {
			sqlSession.close();
		}
		return assprocedureDM;
	}

	/* (non-Javadoc)
	 * @see org.crmf.persistency.mapper.project.AssprocedureServiceInterface#deleteCascade(java.lang.String)
	 */
	@Override
	public void deleteCascade(String identifier) {
		LOG.info("called deleteCascade");
		SqlSession sqlSession = sessionFactory.getSession();

		LOG.info("Delete assessment procedure cascade" + identifier);
		try {
			AssprocedureMapper assprocedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

			LOG.info("Delete assessment procedure with identifier: " + identifier);
			assprocedureMapper.deleteByIdentifier(identifier);
			sqlSession.commit();
			LOG.info("Delete sestobj ");
			sestobjMapper.deleteByIdentifier(identifier);
			sqlSession.commit();
		} finally {
			sqlSession.close();
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.crmf.persistency.mapper.project.AssprocedureServiceInterface#getByIdentifier(java.lang.String)
	 */
	@Override
	public AssessmentProcedure getByIdentifier(String identifier) {
		LOG.info("called getByIdentifier for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByIdentifier(identifier);
			AssessmentProcedure procedureToSend = procedure.convertToModel();
			procedureToSend.setObjType(SESTObjectTypeEnum.AssessmentProcedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	
	@Override
	public AssessmentProcedure getByIdentifierFull(String identifier) {
		LOG.info("called getByIdentifierFull for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
			AssProcedure procedure = procedureMapper.getByIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public AssessmentProcedure getByAssetModelIdentifier(String identifier) {
		LOG.info("called getByAssetModelIdentifier for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByAssetModelIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public AssessmentProcedure getByVulnerabilityModelIdentifier(String identifier) {
		LOG.info("called getByVulnerabilityModelIdentifier for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByVulnerabilityModelIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public AssessmentProcedure getByThreatModelIdentifier(String identifier) {
		LOG.info("called getByThreatModelIdentifier for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByThreatModelIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public AssessmentProcedure getByRiskModelIdentifier(String identifier) {
		LOG.info("called getByRiskModelIdentifier for identifier: " + identifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByRiskModelIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}
	
	private AssessmentProcedure convertIntoAssessmentProcedure(AssProcedure procedure){
		LOG.info("called convertIntoAssessmentProcedure with identifier: " + procedure.getSestobjId());
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
			ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
			RiskTreatmentMapper riskTreatmentMapper = sqlSession.getMapper(RiskTreatmentMapper.class);

			AssessmentProcedure procedureToSend = procedure.convertToModel();
			procedureToSend.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
			
			// retrieve the Asset Model
			SestAssetModel am = assetMapper.getById(procedure.getAssetId());
			procedureToSend.setAssetModel(am.convertToModel());
			
			// retrieve the vulnerability model
			SestVulnerabilityModel vm = vulnerabilityMapper.getById(procedure.getVulnerabilityId());
			procedureToSend.setVulnerabilityModel(vm.convertToModel());
			
			//retrieve the threat model
			SestThreatModel tm = threatMapper.getById(procedure.getThreatId());
			procedureToSend.setThreatModel(tm.convertToModel());
						
			// retrieve the safeguard model
			SestSafeguardModel sm =  safeguardMapper.getById(procedure.getSafeguardId());
			procedureToSend.setSafeguardModel(sm.convertToModel());
			
			//retrieve the risk model
			SestRiskModel rm = riskMapper.getById(procedure.getRiskModelId());
			procedureToSend.setRiskModel(rm.convertToModel());

			//retrieve the risk treatment model
			SestRiskTreatmentModel rtm = riskTreatmentMapper.getById(procedure.getRiskTreatmentModelId());
			procedureToSend.setRiskTreatmentModel(rtm.convertToModel());
			
			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public List<AssessmentProcedure> getByProjectIdentifier(String projectIdentifier) {
		LOG.info("called getByProjectIdentifier for identifier: " + projectIdentifier);
		SqlSession sqlSession = sessionFactory.getSession();
		List<AssessmentProcedure> proceduresToSend = new ArrayList<>();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
			AssetMapper assetMapper = sqlSession.getMapper(AssetMapper.class);
			VulnerabilityMapper vulnerabilityMapper = sqlSession.getMapper(VulnerabilityMapper.class);
			ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
			SafeguardMapper safeguardMapper = sqlSession.getMapper(SafeguardMapper.class);
			RiskMapper riskMapper = sqlSession.getMapper(RiskMapper.class);
			RiskTreatmentMapper riskTreatmentMapper = sqlSession.getMapper(RiskTreatmentMapper.class);
			
			List<AssProcedure> procedure = procedureMapper.getByProjectIdentifier(projectIdentifier);
			for (AssProcedure assProcedure : procedure) {

				AssessmentProcedure procedureToSend = assProcedure.convertToModel();
				procedureToSend.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
				
				// retrieve the Asset Model
				SestAssetModel am = assetMapper.getById(assProcedure.getAssetId());
				AssetModel assetmodel = am.convertToModel();
				assetmodel.setObjType(SESTObjectTypeEnum.AssetModel);
				procedureToSend.setAssetModel(assetmodel);
				
				// retrieve the vulnerability model
				SestVulnerabilityModel vm = vulnerabilityMapper.getById(assProcedure.getVulnerabilityId());
				VulnerabilityModel vulnmodel = vm.convertToModel();
				vulnmodel.setObjType(SESTObjectTypeEnum.VulnerabilityModel);
				procedureToSend.setVulnerabilityModel(vulnmodel);
				
				//retrieve the threat model
				SestThreatModel tm = threatMapper.getById(assProcedure.getThreatId());
				ThreatModel threatModel = tm.convertToModel();
				threatModel.setObjType(SESTObjectTypeEnum.ThreatModel);
				procedureToSend.setThreatModel(threatModel);
							
				//retrieve the safeguard model
				SestSafeguardModel sm = safeguardMapper.getById(assProcedure.getSafeguardId());
				SafeguardModel safeguardModel = sm.convertToModel();
				safeguardModel.setObjType(SESTObjectTypeEnum.SafeguardModel);
				procedureToSend.setSafeguardModel(safeguardModel);
				
				//retrieve the risk model
				SestRiskModel rm = riskMapper.getById(assProcedure.getRiskModelId());
				RiskModel riskModel = rm.convertToModel();
				riskModel.setObjType(SESTObjectTypeEnum.RiskModel);
				procedureToSend.setRiskModel(riskModel);

				//retrieve the risk treatment model
				SestRiskTreatmentModel rtm = riskTreatmentMapper.getById(assProcedure.getRiskTreatmentModelId());
				RiskTreatmentModel riskTreatmentModel = rtm.convertToModel();
				riskTreatmentModel.setObjType(SESTObjectTypeEnum.RiskTreatmentModel);
				procedureToSend.setRiskTreatmentModel(riskTreatmentModel);
				
				proceduresToSend.add(procedureToSend);
			}
			
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			sqlSession.close();
		}
		return proceduresToSend;
	}
	
	@Override
	public int getProjectdIdByIdentifier(String identifier){
		LOG.info("called getProjectdIdByIdentifier with identifier: " + identifier);
		Integer progetId = null;
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			progetId = procedureMapper.getProjectdIdByIdentifier(identifier);
			
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			sqlSession.close();
		}
		
		return progetId;
	}
	
	/* (non-Javadoc)
	 * @see org.crmf.persistency.mapper.project.AssprocedureServiceInterface#getAll()
	 */
	@Override
	public List<AssessmentProcedure> getAll() {
		LOG.info("called getAll");
		SqlSession sqlSession = sessionFactory.getSession();
		List<AssessmentProcedure> proceduresToSend = new ArrayList<>();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);
			List<AssProcedure> procedures = procedureMapper.getAll();

			for (AssProcedure assprocedure : procedures) {
				AssessmentProcedure procedureToSend = assprocedure.convertToModel();
				procedureToSend.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
				
				proceduresToSend.add(procedureToSend);
				LOG.info("added procedure to listUser");
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			sqlSession.close();
		}
		return proceduresToSend;
	}

	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void update(AssessmentProcedure assprocedureDM) {
		LOG.info("called updateQuestionnaireJSON");
		SqlSession sqlSession = sessionFactory.getSession();
		
		AssProcedure procedure = new AssProcedure();
		procedure.convertFromModel(assprocedureDM);
		
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			procedureMapper.update(procedure);

			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public AssessmentProcedure getByRiskTreatmentModelIdentifier(String riskTreatmentModelIdentifier) {
		LOG.info("called getByRiskTreatmentModelIdentifier: " + riskTreatmentModelIdentifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getByRiskTreatmentModelIdentifier(riskTreatmentModelIdentifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public AssessmentProcedure getBySafeguardModelIdentifier(String identifier) {
		LOG.info("called getBySafeguardModelIdentifier");
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			AssprocedureMapper procedureMapper = sqlSession.getMapper(AssprocedureMapper.class);

			AssProcedure procedure = procedureMapper.getBySafeguardModelIdentifier(identifier);
			AssessmentProcedure procedureToSend = convertIntoAssessmentProcedure(procedure);

			return procedureToSend;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			return null;
		} finally {
			sqlSession.close();
		}
	}

		
}
