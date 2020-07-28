/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserAuthenticationRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.user.auth.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Headers;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.shiro.security.ShiroSecurityToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionContext;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.crmf.model.user.User;
import org.crmf.proxy.authnauthz.ShiroSecurityPolicyCustomInterface;
import org.crmf.user.manager.authentication.UserAuthenticationInterface;
import org.crmf.user.manager.core.UserManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

//This class manages the business logic behind the webservices related to the User Authentication management
public class UserAuthenticationRestServer implements UserAuthenticationRestServerInterface {

  private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationRestServer.class.getName());
  private ShiroSecurityPolicyCustomInterface policy;
  private org.apache.shiro.mgt.DefaultSecurityManager manager;

  @Produce(uri = "direct:sendSession")
  private ProducerTemplate sender;

  private UserAuthenticationInterface userAuthentication;
  private UserManagerInputInterface userManager;

  @Override
  public String authenticateUser(String token) throws Exception {

    LOG.info(" ==== LOGIN ==== ");
    byte[] message = Base64.getDecoder().decode(token);

    Gson gson = new Gson();
    LoginToken tokenLogin = gson.fromJson(new String(message, StandardCharsets.UTF_8), LoginToken.class);
    String username = tokenLogin.getUsername();
    String password = tokenLogin.getPassword();
    LOG.info("TOKEN username : " + username + ", password : " + password);
    AuthenticationToken tokenCredentials = new UsernamePasswordToken(username, password);

    //create new session for each login
    SessionContext sessionCtx = new DefaultSessionContext();
    sessionCtx.setSessionId(UUID.randomUUID());
    Session session = manager.start(sessionCtx);
    session.setAttribute("username", username);
    Subject.Builder builder = new Subject.Builder(SecurityUtils.getSecurityManager());
    builder.sessionId(session.getId());
    Subject sessionUser = builder.buildSubject();
    LOG.info("currentUserBySession : " + sessionUser);
    LOG.info("active session " + session.getId());

    LOG.info("Before LOGIN ");
    try {
      sessionUser.login(tokenCredentials);
    } catch (UnknownAccountException uae) {
      LOG.error(uae.getMessage());
      throw new Exception("WRONG_USERNAME_PASSWORD", uae);
    } catch (IncorrectCredentialsException credentialEx) {
      LOG.error(credentialEx.getMessage());
      throw new Exception("WRONG_USERNAME_PASSWORD", credentialEx);
    } catch (AuthenticationException authEx) {
      LOG.error(authEx.getMessage());
      throw new Exception("AUTHN_EXCEPTION", authEx);
    }
    if (userAuthentication.isPasswordExpired(username)) {
      LOG.info("Password expired for username: " + username);
      throw new Exception("PASSWORD_EXPIRED");
    }

    User user = userManager.retrieveUserByUsername(username);
    LoggedToken loggedToken = new LoggedToken();
    loggedToken.setToken(new ShiroSecurityToken(username, session.getId().toString()));
    loggedToken.setUserid(user != null ? user.getIdentifier() : null);

    String loginSessionJson = gson.toJson(loggedToken);
    return loginSessionJson;
  }

  @Override
  public void logoutUser(String token) throws Exception {

    LOG.info("LOGOUT TOKEN : " + token);

    Gson gson = new Gson();
    String decryptedtoken = new String(
      Base64.getDecoder().decode((String) token),
      StandardCharsets.UTF_8);
    ShiroSecurityToken securityToken = gson.fromJson(decryptedtoken, ShiroSecurityToken.class);
    LOG.info("TOKEN username : " + securityToken.getUsername());
    LOG.info("TOKEN username : " + securityToken.getPassword());

    DefaultSessionManager sessionManager = (DefaultSessionManager) manager.getSessionManager();
    Collection<Session> activeSessions = sessionManager.getSessionDAO().getActiveSessions();
    for (Session session : activeSessions) {
      LOG.info("LOGOUT TOKEN session : " + session.getId());
      if (securityToken.getPassword().equals(session.getId())) {
        LOG.info("LOGOUT TOKEN session found : " + session.getId());
        session.stop();
      }
    }
  }

  @Override
  public String getPermissionList(@Headers Map<String, Object> headers) {

    LOG.info("getPermissionList ");
    LOG.info("headers " + headers.keySet());

    String token = "";
    if (headers.get("SHIRO_SECURITY_TOKEN") != null) {
      token = headers.get("SHIRO_SECURITY_TOKEN").toString();
    }
    LOG.info("headers " + token);
    String projectIdentifier = "";
    if (headers.get("projectIdentifier") != null) {
      projectIdentifier = headers.get("projectIdentifier").toString();
    }
    LOG.info("headers " + projectIdentifier);
    return this.policy.getPermissionList(token, projectIdentifier);
  }

  public ShiroSecurityPolicyCustomInterface getPolicy() {
    return policy;
  }

  public void setPolicy(ShiroSecurityPolicyCustomInterface policy) {
    this.policy = policy;
  }

  public org.apache.shiro.mgt.DefaultSecurityManager getManager() {
    return manager;
  }

  public void setManager(org.apache.shiro.mgt.DefaultSecurityManager manager) {
    this.manager = manager;
  }

  public UserAuthenticationInterface getUserAuthentication() {
    return userAuthentication;
  }

  public void setUserAuthentication(UserAuthenticationInterface userAuthentication) {
    this.userAuthentication = userAuthentication;
  }

  public UserManagerInputInterface getUserManager() {
    return userManager;
  }

  public void setUserManager(UserManagerInputInterface userManager) {
    this.userManager = userManager;
  }

  private class LoginToken {
    String username;
    String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  private class LoggedToken {
    ShiroSecurityToken token;
    String userid;

    public ShiroSecurityToken getToken() {
      return token;
    }

    public void setToken(ShiroSecurityToken token) {
      this.token = token;
    }

    public String getUserid() {
      return userid;
    }

    public void setUserid(String userid) {
      this.userid = userid;
    }
  }
}
