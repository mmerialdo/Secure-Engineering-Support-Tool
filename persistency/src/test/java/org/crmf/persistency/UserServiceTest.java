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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.persistency.mapper.general.CleanDatabaseService;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class UserServiceTest {

  PersistencySessionFactory sessionFactory;
  private UserService userService;
  private User user = null;


  @Before
  public void setUp() {

    sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    RoleService roleService = new RoleService();
    roleService.setSessionFactory(sessionFactory);
    userService = new UserService();
    userService.setSessionFactory(sessionFactory);
    userService.setRoleService(roleService);

    user = prefill();
  }

  @After
  public void tearDown() throws Exception {

    CleanDatabaseService cleaner = new CleanDatabaseService();
    cleaner.setSessionFactory(sessionFactory);

    cleaner.delete();
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
    assertNotNull(user);
    assertEquals("user1", users.getName());
    assertEquals("surname1", users.getSurname());
    assertEquals("paSS01", users.getPassword());
    assertEquals("username1", users.getUsername());
    assertEquals("email1", users.getEmail());
    assertEquals(UserProfileEnum.ProjectManager, users.getProfile());
    assertEquals(SESTObjectTypeEnum.User, users.getObjType());

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
    assertNotNull(user);
    assertEquals("user2", users.getName());
    assertEquals("surname2", users.getSurname());
    //assertEquals("paSS02", users.getPassword());
    assertEquals("username2", users.getUsername());
    assertEquals("email2", users.getEmail());
    assertEquals(UserProfileEnum.GeneralUser, users.getProfile());
    assertEquals(SESTObjectTypeEnum.User, users.getObjType());
  }

  @Test
  public void testUpdatePassword() {

    user.setPassword("paSS03");

    userService.updatePassword(user.getUsername(), user.getPassword());

    User users = userService.getByIdentifier(user.getIdentifier());
    assertEquals("paSS03", users.getPassword());
  }

  @Test(expected = RemoteComponentException.class)
  public void testUpdatePassword_ErrorHistory() {

    user.setPassword("paSS03");

    userService.updatePassword(user.getUsername(), user.getPassword());
    user.setPassword("paSS04");
    userService.updatePassword(user.getUsername(), user.getPassword());
    user.setPassword("paSS03");

    userService.updatePassword(user.getUsername(), user.getPassword());

    User users = userService.getByIdentifier(user.getIdentifier());
    assertEquals("paSS04", users.getPassword());
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
    assertNotNull(users);
    assertEquals(2, users.size());

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
