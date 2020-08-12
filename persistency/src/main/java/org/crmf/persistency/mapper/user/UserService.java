/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserService.java"
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

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.exception.ObjectNotFoundException;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserRole;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.user.Sestuser;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.crmf.persistency.session.PersistencySessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

//This class manages the database interactions related to User
public class UserService implements UserServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class.getName());
  PersistencySessionFactory sessionFactory;
  RoleService roleService;

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#insert(org.crmf.
   * persistency.domain.user.Sestuser)
   */
  @Override
  public String insert(User userDM) {
    SqlSession sqlSession = sessionFactory.getSession();

    LOG.info("Insert User");
    Sestuser user = new Sestuser();
    user.convertFromModel(userDM);

    Sestobj sestobj = null;

    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      String psw = user.getPassword();
      String validPsw = checkPasswordValid(psw, user.getUsername());
      if (!validPsw.equals("OK")) {
        throw new Exception("Invalid password. " + validPsw);
      }

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(userDM.getObjType().name());
      sestobjMapper.insert(sestobj);

      user.setSestobjId(sestobj.getIdentifier());
      userMapper.insert(user);
      userMapper.insertUserPswHistory(user.getId(), psw);

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
      return null;
    } finally {
      sqlSession.close();
    }
    return sestobj.getIdentifier();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#updateQuestionnaireJSON(org.crmf.
   * persistency.domain.user.Sestuser)
   */
  @Override
  public void update(User userDM) {
    SqlSession sqlSession = sessionFactory.getSession();

    Sestuser user = new Sestuser();
    user.convertFromModel(userDM);

    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      Sestuser olduser = userMapper.getByIdentifier(userDM.getIdentifier());
      user.setId(olduser.getId());
      userMapper.update(user);

      sqlSession.commit();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      sqlSession.rollback();
    } finally {
      sqlSession.close();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#updatePassword(
   * java.lang.String, java.lang.String)
   */
  @Override
  public void updatePassword(String username, String psw) {
    SqlSession sqlSession = sessionFactory.getSession();
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    LOG.info("Updating password username " + username);
    if (username == null) {
      LOG.error("Unable to updateQuestionnaireJSON password. Username null. ");
      throw new RemoteComponentException("USERNAME_NULL");
    } else {
      Sestuser user = userMapper.getByUsername(username);
      String validPsw = checkPasswordValid(psw, username);
      if (validPsw.equals("OK")) {
        // check password history requirement. Not the same as the last
        // two passwords.
        List<String> psws = userMapper.getOldUserPswByUserId(user.getId());
        if (psws != null && !psws.isEmpty()) {
          for (String pws : psws) {
            if (psw != null && psw.equals(pws)) {

              LOG.error("history same password");
              throw new RemoteComponentException("HISTORY_PASSWORD_FOUND");
            }
          }
        }
        try {
          //updateQuestionnaireJSON password in user table
          userMapper.updatePassword(user.getId(), psw);
          //insert to user_password history
          userMapper.insertUserPswHistory(user.getId(), psw);
          sqlSession.commit();

        } catch (Exception ex) {
          LOG.error("Unable to updateQuestionnaireJSON password! " + ex.getMessage(), ex);
          sqlSession.rollback();
        } finally {
          sqlSession.close();
        }
      } else {

        LOG.error("Unable to updateQuestionnaireJSON password. Password invalid. ");
        throw new RemoteComponentException("PASSWORD_INVALID ".concat(validPsw));
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#deleteCascade(org.
   * crmf.persistency.domain.user.Sestuser)
   */
  @Override
  public void deleteCascade(String identifier) {
    SqlSession sqlSession = sessionFactory.getSession();

    LOG.info("Delete User cascade" + identifier);
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      Integer id = userMapper.getIdByIdentifier(identifier);
      LOG.info("Delete user ");
      userMapper.delete(id);
      sqlSession.commit();
    } finally {
      sqlSession.close();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#getById(java.lang.
   * Integer)
   */
  @Override
  public User getById(Integer userId) {
    SqlSession sqlSession = sessionFactory.getSession();
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      Sestuser sestuser = userMapper.getById(userId);
      User userToSend = sestuser.convertToModel();
      userToSend.setObjType(SESTObjectTypeEnum.User);

      return userToSend;
    } finally {
      sqlSession.close();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.crmf.persistency.mapper.user.UserServiceInterface1#getByIdentifier(
   * java.lang.String)
   */
  @Override
  public User getByIdentifier(String identifier) {
    SqlSession sqlSession = sessionFactory.getSession();
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      Sestuser sestuser = userMapper.getByIdentifier(identifier);
      User userToSend = sestuser.convertToModel();

      userToSend.setIdentifier(identifier);
      userToSend.setObjType(SESTObjectTypeEnum.User);

      return userToSend;
    } finally {
      sqlSession.close();
    }
  }

  @Override
  public User getByUsername(String username) {
    SqlSession sqlSession = sessionFactory.getSession();
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      Sestuser sestuser = userMapper.getByUsername(username);
      LOG.info("sestuser : " + sestuser);
      if (sestuser != null) {
        LOG.info("sestuser : " + sestuser.getSestobjId());
        User userToSend = sestuser.convertToModel();
        userToSend.setObjType(SESTObjectTypeEnum.User);

        return userToSend;
      } else {

        LOG.error("Unable to find username : " + username);
        throw new ObjectNotFoundException("WRONG_USERNAME_PASSWORD " + username);
      }
    } finally {
      sqlSession.close();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.user.UserServiceInterface1#getAll()
   */
  @Override
  public List<User> getAll() {
    LOG.info("called getAll");
    SqlSession sqlSession = sessionFactory.getSession();
    List<User> usersToSend = new ArrayList<>();
    List<Sestuser> users = new ArrayList<>();
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      users = userMapper.getAll();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    } finally {
      sqlSession.close();
    }

    for (Iterator<Sestuser> iterator = users.iterator(); iterator.hasNext(); ) {
      Sestuser sestuser = (Sestuser) iterator.next();
      User userToSend = sestuser.convertToModel();
      userToSend.setObjType(SESTObjectTypeEnum.User);

      LOG.info("sestuser identifier {}", sestuser.getSestobjId());

      List<UserRole> roles = new ArrayList<>();
      // adding roles
      if (sestuser.getSestobjId() != null) {
        try {
          roles = roleService.getByUserIdentifier(sestuser.getSestobjId());
          userToSend.setRoles(new ArrayList<>(roles));
        } catch (Exception e) {
          LOG.error(e.getMessage());
        }
      }

      usersToSend.add(userToSend);
      LOG.info("added user to listUser");
    }
    return usersToSend;
  }

  @Override
  public boolean isPasswordExpired(String username) {

    SqlSession sqlSession = sessionFactory.getSession();
    try {
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      Sestuser sestuser = userMapper.getByUsername(username);
      java.util.Date lastUpd = userMapper.getUserPswLastUpdate(sestuser.getId());

      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -120);
      if (lastUpd != null && lastUpd.before(cal.getTime())) {
        return true;
      }

    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw new RemoteComponentException("Password Expired exception " + ex.getMessage());
    } finally {
      sqlSession.close();
    }
    return false;
  }

  private String checkPasswordValid(String password, String username) {

    if (password == null) {

      LOG.info("password is null");
      return "PASSWORD_NULL";
    }
    if (password.length() < 6) {

      LOG.info("wrong length");
      return "PASSWORD_WRONG_LENGTH";
    }
    if (password.toLowerCase().contains(username.toLowerCase())) {

      LOG.info("contains username");
      return "PASSWORD_CONTAINS_USERNAME";
    }
    if (password.equals(password.toLowerCase())) {

      LOG.info("has no upercase");
      return "PASSWORD_NO_UPPERCASE";
    }
    if (password.equals(password.toUpperCase())) {

      LOG.info("has no lowercase");
      return "PASSWORD_NO_LOWERCASE";
    }
    if (!password.matches(".*[0-9].*")) {

      LOG.info("has no digit");
      return "PASSWORD_NO_DIGIT";
    }

    return "OK";
  }

  public String securePassword(String passwordToHash) throws NoSuchAlgorithmException, NoSuchProviderException {
    byte[] salt = getSalt();

    String securePassword = getSecurePassword(passwordToHash, salt);
    System.out.println(securePassword);

    String regeneratedPassowrdToVerify = getSecurePassword(passwordToHash, salt);
    System.out.println(regeneratedPassowrdToVerify);
    return securePassword;
  }

  private static String getSecurePassword(String passwordToHash, byte[] salt) {
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(salt);
      byte[] bytes = md.digest(passwordToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      LOG.error(e.getMessage());
    }
    return generatedPassword;
  }

  private static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }

  public PersistencySessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(PersistencySessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public RoleService getRoleService() {
    return roleService;
  }

  public void setRoleService(RoleService roleService) {
    this.roleService = roleService;
  }

}
