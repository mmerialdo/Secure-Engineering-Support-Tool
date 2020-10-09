/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CleanDatabaseService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.general;

import org.apache.ibatis.session.SqlSession;
import org.crmf.persistency.mapper.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

//THis class encompasses methods for cleaning the database
@Service
@Qualifier("default")
public class CleanDatabaseService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class.getName());
	@Autowired
	private SqlSession sqlSession;

	public void delete() {
		try {
			CleanDatabaseMapper dbMapper = sqlSession.getMapper(CleanDatabaseMapper.class);
			
			// cancels PROJECT, PROCEDURE, ROLE, SESTOBJ
			dbMapper.deleteAssproject();
			// cancels PROCEDURE, SESTOBJ
			dbMapper.deleteAssprocedure();
			dbMapper.deleteAssauditDefault();
			//cancels AUDIT, QUESTIONNAIRE
			dbMapper.deleteAssaudit();
			// cancels SYSPROJECT->SYSPARTECIPANT, SESTOBJ
			dbMapper.deleteSysproject();
			//cancels TEMPLATE, PROFILE-TEMPLATE, SESTOBJ
			dbMapper.deleteAssprofileTemplate();
			//cancels PROFILE, SESTOBJ
			dbMapper.deleteAssprofile();
			//cancels ASSET, SESTOBJ
			dbMapper.deleteAssetModel();
			//cancels VULNERABILITY, SESTOBJ
			dbMapper.deleteVulnerabilityModel();
			//cancels THREAT, SESTOBJ
			dbMapper.deleteThreatModel();
			//cancels RISK, SESTOBJ
			dbMapper.deleteRiskModel();
			dbMapper.deleteRiskScenarioReference();
			//cancels SAFEGUARD, SESTOBJ
			dbMapper.deleteSafeguardModel();
			
			//cancels USER, USER_PSW, PERMISSIONGROUP, PERMISSIONGROUP_SESTOBJ, ROLE, SESTOBJ
			dbMapper.deleteUser();
			dbMapper.cleanVulnerability();
			dbMapper.cleanThreat();

			dbMapper.cleanVulnerabilityReference();
			dbMapper.cleanThreatReference();

		}  catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}
}
