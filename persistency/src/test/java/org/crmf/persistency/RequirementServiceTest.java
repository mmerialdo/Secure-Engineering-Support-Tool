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

import org.crmf.model.requirement.ProjectRequirement;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.model.requirement.RequirementTypeEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.requirement.RequirementService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RequirementServiceTest {

	PersistencySessionFactory sessionFactory;
	private RequirementService requirementService;
	
	@Before
	public void setUp() throws Exception {

		
		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		requirementService = new RequirementService();
		requirementService.setSessionFactory(sessionFactory);
		
	}

	@After
	public void tearDown() throws Exception {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);
		
		cleaner.delete();
	}
	
	@Test
	public void insertSysRequirementTest() throws Exception{
		
		ProjectRequirement preq = new ProjectRequirement();
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
