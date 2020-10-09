/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementServiceTest.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.model.requirement.Requirement;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.model.requirement.RequirementTypeEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.persistency.mapper.requirement.RequirementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class RequirementServiceTest {

	@Autowired
	private RequirementService requirementService;
	
	@Test
	public void insertSysRequirementTest() throws Exception{

		Requirement preq = new Requirement();
		preq.setElementType(ElementTypeEnum.Element);
		preq.setType(RequirementTypeEnum.Documentation);
		preq.setCategory("fileName");
		preq.setStatus(RequirementStatusEnum.Validated);
		preq.setSubCategory("K");
		preq.setId("SKNL.SYS.PE.00000");
		preq.setTitle("SKNL.SYS.PE.00000");
		preq.setNote("Unless specified otherwise");
		preq.setVersion("1");
		preq.setStability("S");
		preq.setLastUpdate("1");
		preq.setTag("PSEC");

		requirementService.insertSysRequirement(preq, null);
	}
}
