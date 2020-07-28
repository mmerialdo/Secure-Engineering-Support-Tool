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

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.ProjectRequirement;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.requirement.Requirement;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.mapper.project.SysprojectMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to the Requirement
public class RequirementService implements RequirementServiceInterface {

	private static final Logger LOG = LoggerFactory.getLogger(RequirementService.class.getName());
	PersistencySessionFactory sessionFactory;

	@Override
	public void insertSysRequirement(ProjectRequirement requirementDM, String sysprojectIdentifier) {

		SqlSession sqlSession = sessionFactory.getSession();

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

			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public void deleteSysRequirement(org.crmf.model.requirement.Requirement requirementDM) {

		SqlSession sqlSession = sessionFactory.getSession();
		Requirement requirement = new Requirement();
		requirement.convertFromModel(requirementDM);

		try {
			RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

			requirement.setStatus(RequirementStatusEnum.Canceled.name());
			requirementMapper.insert(requirement);

			sqlSession.commit();
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			sqlSession.rollback();
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public List<org.crmf.model.requirement.Requirement> getByIds(List<String> ids) {

		LOG.info("called getByIds ");
		SqlSession sqlSession = sessionFactory.getSession();
		List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<org.crmf.model.requirement.Requirement>();
		try {
			RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

			List<Requirement> requirements = requirementMapper.getByIds(ids);

			for (Requirement requirement : requirements) {

				org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
				prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

				requirementsToSend.add(prjRequirement);
			}
		} finally {
			sqlSession.close();
		}
		return requirementsToSend;
	}

	@Override
	public List<org.crmf.model.requirement.Requirement> getBySysProject(String sysprojectIdentifier) {

		LOG.info("called getSysProject " + sysprojectIdentifier);
		SqlSession sqlSession = sessionFactory.getSession();
		List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<org.crmf.model.requirement.Requirement>();
		try {
			RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);
			SysprojectMapper sysProjectMapper = sqlSession.getMapper(SysprojectMapper.class);

			Integer sysprjId = sysProjectMapper.getIdByIdentifier(sysprojectIdentifier);
			List<Requirement> requirements = requirementMapper.getBySysProject(sysprjId);

			for (Requirement requirement : requirements) {

				org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
				prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

				requirementsToSend.add(prjRequirement);
			}
		} finally {
			sqlSession.close();
		}
		return requirementsToSend;
	}

	public PersistencySessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(PersistencySessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<org.crmf.model.requirement.Requirement> getBySysProjectAndFile(String sysprojectIdentifier, String filename) {
		
		LOG.info("called getSysProject " + sysprojectIdentifier);
		LOG.info("called filename " + filename);
		SqlSession sqlSession = sessionFactory.getSession();
		List<org.crmf.model.requirement.Requirement> requirementsToSend = new ArrayList<org.crmf.model.requirement.Requirement>();
		try {
			RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);

			List<Requirement> requirements = requirementMapper.getBySysProjectAndFile(sysprojectIdentifier, filename);

			for (Requirement requirement : requirements) {

				org.crmf.model.requirement.Requirement prjRequirement = requirement.convertToModel();
				prjRequirement.setObjType(SESTObjectTypeEnum.AssessmentProject);

				requirementsToSend.add(prjRequirement);
			}
		} finally {
			sqlSession.close();
		}
		return requirementsToSend;
	}

	@Override
	public List<String> getFilenameByProject(String sysprojectIdentifier) {

		LOG.info("called getSysProject " + sysprojectIdentifier);
		SqlSession sqlSession = sessionFactory.getSession();
		try {
			RequirementMapper requirementMapper = sqlSession.getMapper(RequirementMapper.class);
			SysprojectMapper sysProjectMapper = sqlSession.getMapper(SysprojectMapper.class);

			Integer sysprjId = sysProjectMapper.getIdByIdentifier(sysprojectIdentifier);
			return requirementMapper.getFilenameByProject(String.valueOf(sysprjId));
		} catch(Exception ex) {
			LOG.error("Unable to load requirement files name.", ex);
		} finally {
			sqlSession.close();
		}
		return null;
	}
}
