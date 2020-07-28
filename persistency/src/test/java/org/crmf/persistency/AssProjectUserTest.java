/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProjectUserTest.java"
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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssProjectUserTest {
	
	PersistencySessionFactory sessionFactory;
	private UserService userService;
	private RoleService roleService;
	private AssessmentProject project = null;

	@Before	
	public void setUp() throws Exception {

		sessionFactory = new PersistencySessionFactory();
		sessionFactory.createSessionFactory();

		userService = new UserService();
		userService.setSessionFactory(sessionFactory);
		
		roleService = new RoleService();
		roleService.setSessionFactory(sessionFactory);
		
		prefill();
	}

	@After
	public void tearDown() throws Exception {

		CleanDatabaseService cleaner = new CleanDatabaseService();
		cleaner.setSessionFactory(sessionFactory);
		
		cleaner.delete();
	}

	@Test
	public void testInsertUserForProject(){
		
		assertTrue(true);
	}
	
	private void prefill() throws Exception{

		AssessmentProject project = new AssessmentProject();
		
		User user = new User();
		user.setName("user");
		user.setSurname("surname");
		user.setPassword("paSS00");
		user.setUsername("username"); //used
		user.setEmail("email");
		user.setProfile(UserProfileEnum.Administrator);
		user.setObjType(SESTObjectTypeEnum.User);
		String identifier = userService.insert(user);
			user.setIdentifier(identifier); //used

		UserRole ur1 = new UserRole();
		//ur1.setProject(project);
		ur1.setRole(UserRoleEnum.Admin);
		
		ArrayList<UserRole> roles = new ArrayList<UserRole>();
		roles.add(ur1);
		user.setRoles(roles);

		roleService.insertUserForProject(user, null);

	}
}
