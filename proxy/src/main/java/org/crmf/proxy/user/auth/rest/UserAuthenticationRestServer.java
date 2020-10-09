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

import com.google.gson.Gson;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.session.mgt.DefaultWebSessionContext;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.AuthToken;
import org.crmf.model.user.User;
import org.crmf.proxy.authnauthz.ShiroSecurityPolicyCustom;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.user.manager.authentication.UserAuthentication;
import org.crmf.user.manager.core.UserManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;

//This class manages the business logic behind the webservices related to the User Authentication management
@RestController
@RequestMapping("api")
public class UserAuthenticationRestServer {

  private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationRestServer.class.getName());
  @Autowired
  private ShiroSecurityPolicyCustom policy;
  @Autowired
  @Qualifier("securityManager")
  private org.apache.shiro.mgt.DefaultSecurityManager manager;
  @Autowired
  private UserAuthentication userAuthentication;
  @Autowired
  private UserManagerInput userManager;

  @PostMapping("login")
  public String authenticateUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {

    LOG.info(" ==== LOGIN ==== ");
    LOG.info(" ==== LOGIN ==== " + token);
    byte[] message = Base64.getDecoder().decode(token);

    Gson gson = new Gson();
    LoginToken tokenLogin = gson.fromJson(new String(message, StandardCharsets.UTF_8), LoginToken.class);
    String username = tokenLogin.getUsername();
    String password = tokenLogin.getPassword();
    LOG.info("TOKEN username : " + username + ", password : " + password);
    AuthenticationToken tokenCredentials = new UsernamePasswordToken(username, password);

    //create new session for each login
    SessionContext sessionCtx = new DefaultWebSessionContext();
    sessionCtx.setSessionId(UUID.randomUUID());
    Session session = manager.start(sessionCtx);
    session.setAttribute("username", username);
    Subject.Builder builder = new Subject.Builder(manager);
    builder.sessionId(session.getId());
    Subject sessionUser = builder.buildSubject();
    LOG.info("currentUserBySession : " + sessionUser);
    LOG.info("active session " + session.getId());

    LOG.info("Before LOGIN ");
    try {
      sessionUser.login(tokenCredentials);
    } catch (UnknownAccountException uae) {
      LOG.error("WRONG_USERNAME_PASSWORD " + uae.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.WRONG_USERNAME_PASSWORD, uae);
    } catch (IncorrectCredentialsException credentialEx) {
      LOG.error("WRONG_USERNAME_PASSWORD " + credentialEx.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.WRONG_USERNAME_PASSWORD, credentialEx);
    } catch (AuthenticationException authEx) {
      LOG.error("AUTHN_EXCEPTION " + authEx.getMessage());
      throw new RemoteComponentException(ApiExceptionEnum.WRONG_USERNAME_PASSWORD, authEx);
    }
    if (userAuthentication.isPasswordExpired(username)) {
      LOG.info("PASSWORD_EXPIRED " + username);
      throw new RemoteComponentException(ApiExceptionEnum.PASSWORD_EXPIRED);
    }

    User user = userManager.retrieveUserByUsername(username);
    LoggedToken loggedToken = new LoggedToken();
    loggedToken.setToken(new AuthToken(username, session.getId().toString()));
    loggedToken.setUserid(user != null ? user.getIdentifier() : null);

    String loginSessionJson = gson.toJson(loggedToken);
    return loginSessionJson;
  }

  @PostMapping("logout")
  public void logoutUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) throws Exception {

    LOG.info("LOGOUT TOKEN : " + token);

    Gson gson = new Gson();
    String decryptedtoken = new String(
      Base64.getDecoder().decode((String) token),
      StandardCharsets.UTF_8);
    AuthToken securityToken = gson.fromJson(decryptedtoken, AuthToken.class);
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

  @GetMapping("permission/list")
  public String getPermissionList(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {

    return this.policy.getPermissionList(token);
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
    AuthToken token;
    String userid;

    public AuthToken getToken() {
      return token;
    }

    public void setToken(AuthToken token) {
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
