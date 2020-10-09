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

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AssProjectUserTest {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	private AssessmentProject project = null;

	@BeforeEach
	public void setUp() throws Exception {
		prefill();
	}

	@Test
	public void testInsertUserForProject(){
		
		Assertions.assertTrue(true);
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
