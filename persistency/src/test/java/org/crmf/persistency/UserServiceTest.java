/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserServiceTest.java"
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

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@Import({UserService.class, RoleService.class})
@ContextConfiguration(classes=Application.class)
@ActiveProfiles("test")
public class UserServiceTest {

  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;
  private User user = null;


  @BeforeEach
  public void setUp() {
    user = prefill();
  }

  @Test
  public void testInsert() {
    //assertTrue(true);

    User user = new User();
    user.setName("user1");
    user.setSurname("surname1");
    user.setPassword("paSS01");
    user.setUsername("username1");
    user.setEmail("email1");
    user.setProfile(UserProfileEnum.ProjectManager);
    user.setObjType(SESTObjectTypeEnum.User);

    String identifier = userService.insert(user);
    user.setIdentifier(identifier);

    User users = userService.getByIdentifier(identifier);
    Assertions.assertNotNull(user);
    Assertions.assertEquals("user1", users.getName());
    Assertions.assertEquals("surname1", users.getSurname());
    Assertions.assertEquals("paSS01", users.getPassword());
    Assertions.assertEquals("username1", users.getUsername());
    Assertions.assertEquals("email1", users.getEmail());
    Assertions.assertEquals(UserProfileEnum.ProjectManager, users.getProfile());
    Assertions.assertEquals(SESTObjectTypeEnum.User, users.getObjType());

    userService.deleteCascade(user.getIdentifier());
  }

  @Test
  public void testUpdate() {

    user.setName("user2");
    user.setSurname("surname2");
    //user.setPassword("paSS02");
    user.setUsername("username2");
    user.setEmail("email2");
    user.setProfile(UserProfileEnum.GeneralUser);
    user.setObjType(SESTObjectTypeEnum.User);

    userService.update(user);

    User users = userService.getByIdentifier(user.getIdentifier());
    Assertions.assertNotNull(user);
    Assertions.assertEquals("user2", users.getName());
    Assertions.assertEquals("surname2", users.getSurname());
    //assertEquals("paSS02", users.getPassword());
    Assertions.assertEquals("username2", users.getUsername());
    Assertions.assertEquals("email2", users.getEmail());
    Assertions.assertEquals(UserProfileEnum.GeneralUser, users.getProfile());
    Assertions.assertEquals(SESTObjectTypeEnum.User, users.getObjType());
  }

  @Test
  public void testUpdatePassword() {

    user.setPassword("paSS03");

    userService.updatePassword(user.getUsername(), user.getPassword());

    User users = userService.getByIdentifier(user.getIdentifier());
    Assertions.assertEquals("paSS03", users.getPassword());
  }

  @Test
  public void testUpdatePassword_ErrorHistory() {

    user.setPassword("paSS03");

    userService.updatePassword(user.getUsername(), user.getPassword());
    user.setPassword("paSS04");
    userService.updatePassword(user.getUsername(), user.getPassword());
    user.setPassword("paSS03");

    Assertions.assertThrows(RemoteComponentException.class, () -> {
      userService.updatePassword(user.getUsername(), user.getPassword());
    });
  }

  @Test
  public void testGetAll() {

    User user = new User();
    user.setName("user1");
    user.setSurname("surname1");
    user.setPassword("paSS01");
    user.setUsername("username1");
    user.setEmail("email1");
    user.setProfile(UserProfileEnum.ProjectManager);
    user.setObjType(SESTObjectTypeEnum.User);

    String identifier = userService.insert(user);
    user.setIdentifier(identifier);

    List<User> users = userService.getAll();
    Assertions.assertNotNull(users);
    Assertions.assertEquals(2, users.size());

    userService.deleteCascade(user.getIdentifier());
  }

  private User prefill() {

    User user = new User();
    user.setName("user");
    user.setSurname("surname");
    user.setPassword("paSS00");
    user.setUsername("username");
    user.setEmail("email");
    user.setProfile(UserProfileEnum.Administrator);
    user.setObjType(SESTObjectTypeEnum.User);

    String identifier = userService.insert(user);
    user.setIdentifier(identifier);
    return user;
  }
}
