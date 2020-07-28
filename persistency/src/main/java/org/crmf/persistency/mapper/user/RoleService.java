/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RoleService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObject;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.domain.project.AssProject;
import org.crmf.persistency.domain.user.Role;
import org.crmf.persistency.domain.user.Sestuser;
import org.crmf.persistency.mapper.project.AssprojectMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the database interactions related to Roles
public class RoleService implements RoleServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(RoleService.class.getName());

  PersistencySessionFactory sessionFactory;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.RoleServiceInterfac#insert(org.crmf.
   * persistency.domain.user.Role)
   */
  @Override
  public Integer insert(UserRole role, String userIdentifier, String projectIdentifier) throws Exception {

    SqlSession sqlSession = sessionFactory.getSession();

    Role roleDb = new Role();
    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);

      Sestuser sestuser = userMapper.getByIdentifier(userIdentifier);
      Integer projectId = projectMapper.getIdByIdentifier(projectIdentifier);

      roleDb.setUserId(sestuser.getId());
      roleDb.setRole(role.getRole().name());
      roleDb.setProjectId(projectId);
      roleMapper.insert(roleDb);

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }
    return roleDb.getId();
  }

  @Override
  public void insertUserForProject(User user, String projectIdentifier) throws Exception {

    SqlSession sqlSession = sessionFactory.getSession();
    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);

      LOG.info("Insert insertUserForProject" + user.getIdentifier());
      Sestuser userDB = userMapper.getByIdentifier(user.getIdentifier());
      Integer projectId = projectMapper.getIdByIdentifier(projectIdentifier);

      List<UserRole> rolesDM = user.getRoles();
      for (UserRole roleDM : rolesDM) {

        LOG.info("Insert Role : " + roleDM + " user : " + userDB.getId());
        Role role = new Role();
        role.convertFromModel(roleDM);
        role.setUserId(userDB.getId());
        role.setProjectId(projectId);

        roleMapper.insert(role);
      }

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }
  }

  @Override
  public void deleteUserFromProject(String userIdentifier, String projectIdentifier, Set<SESTObject> associatedObjects) {

    SqlSession sqlSession = sessionFactory.getSession();
    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);

      Integer userId = userMapper.getIdByIdentifier(userIdentifier);
      Integer projectId = projectMapper.getIdByIdentifier(projectIdentifier);
      LOG.info("delete role " + userId + ", project " + projectId);
      roleMapper.delete(userId, projectId);
      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }
  }

  @Override
  public List<UserRole> getByUserIdentifier(String userIdentifier) throws Exception {
    List<UserRole> result = new ArrayList<UserRole>();
    SqlSession sqlSession = sessionFactory.getSession();

    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      AssprojectMapper projectMapper = sqlSession.getMapper(AssprojectMapper.class);

      Sestuser sestuser = userMapper.getByIdentifier(userIdentifier);
      List<Role> roles = roleMapper.getByUserId(sestuser.getId());

      roles.forEach(role -> {
        UserRole userRole = role.convertToModel();
        if (role.getProjectId() != null && sestuser != null) {
          userRole.setUser(sestuser.convertToModel());
          AssProject project = projectMapper.getById(role.getProjectId());
          userRole.setProjectIdentifier(project.getSestobjId());
          userRole.setProjectName(project.getName());

          result.add(userRole);
        }
      });
      return result;
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    } finally {
      sqlSession.close();
    }
    return null;
  }

  @Override
  public List<User> getByProjectIdentifier(String identifier) throws Exception {

    SqlSession sqlSession = sessionFactory.getSession();

    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      List<Role> roles = roleMapper.getByUserIdentifierAndProjectIdentifier(null, identifier);
      Map<Integer, User> users = new HashMap<Integer, User>();
      for (Role role : roles) {

        UserRole userRole = new UserRole();
        userRole.setProjectIdentifier(identifier);
        userRole.setRole(UserRoleEnum.valueOf(role.getRole()));

        if (users.containsKey(role.getUserId())) {
          users.get(role.getUserId()).getRoles().add(userRole);
        } else {
          Sestuser userDb = userMapper.getById(role.getUserId());
          User user = userDb.convertToModel();
          user.getRoles().add(userRole);
          users.put(role.getUserId(), user);
        }
      }

      return new ArrayList<User>(users.values());

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    } finally {
      sqlSession.close();
    }
    return null;
  }

  @Override
  public List<UserRole> getRolesByUserAndProjectId(String userId, String projectId) {
    List<UserRole> result = new ArrayList<UserRole>();
    SqlSession sqlSession = sessionFactory.getSession();

    try {
      RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);

      LOG.info("getRolesByUserId userId {} ", userId);
      LOG.info("getRolesByUserId projectId {} ", projectId);
      List<Role> retrievedRoles = roleMapper.getByUserIdentifierAndProjectIdentifier(userId, projectId);
      LOG.info("retrievedRoles " + retrievedRoles);
      for (Role role : retrievedRoles) {

        UserRole userRole = role.convertToModel();
        result.add(userRole);
      }
      return result;
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    } finally {
      sqlSession.close();
    }
    return null;
  }

  public PersistencySessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(PersistencySessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
