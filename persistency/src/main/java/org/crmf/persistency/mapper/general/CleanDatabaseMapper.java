/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CleanDatabaseMapper.java"
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

import org.apache.ibatis.annotations.Mapper;

//This interface allows the bundle to invoke the SQL methods within the CleanDatabaseMapper.xml (via the ibatis API)
@Mapper
public interface CleanDatabaseMapper {

	void deleteSestObj();

	void deleteUser();

	void deleteUserPsw();

	void deleteRole();

	void deleteAssproject();

	void deleteAssprofileTemplate();

	void deleteAssprofile();

	void deleteAsstemplate();

	void deleteAssprocedure();

	void deleteSyspartecipant();

	void deleteSysproject();

	void deleteAnswer();

	void deleteQuestion();

	void deleteQuestionnaire();

	void deleteQuestionnaireModel();

	void deleteAssauditDefault();

	void deleteAssaudit();

	void deletePermissiongroup();

	void deletePermissiongroupSestobj();

	void deleteAssetModel();

	void deleteVulnerabilityModel();

	void deleteThreatModel();

	void deleteRiskModel();

	void deleteRiskScenarioReference();

	void deleteSafeguardModel();

	void cleanSestObj();

	void cleanAsset();

	void cleanVulnerability();

	void cleanThreat();

	void cleanSafeguard();

	void cleanRiskModel();

	void cleanRiskTreatment();

	void cleanVulnerabilityReference();

	void cleanThreatReference();
}
