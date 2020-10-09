/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ShiroSecurityProcessorCustom.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.authnauthz;

import com.google.gson.Gson;
import org.apache.camel.component.shiro.security.ShiroSecurityPolicy;
import org.apache.camel.component.shiro.security.ShiroSecurityProcessor;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.crmf.model.general.AuthToken;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

//This class extends Apache Camel processor in order to manage authentication and authorization
@Service
public class ShiroSecurityProcessorCustom {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroSecurityProcessor.class);
  @Autowired
  @Qualifier("ShiroSecurityPolicyCustom")
  private ShiroSecurityPolicy policy;
  @Autowired
  private UserPermissionManagerInput userPermission;
  @Autowired
  @Qualifier("securityManager")
  private org.apache.shiro.mgt.DefaultSecurityManager manager;

  public void applySecurityPolicy(String permission, String token) throws Exception {
    ByteSource encryptedToken;
    LOG.info("---------------- applySecurityPolicy ---------------------");
    LOG.info("token " + token);
    /** SEST token hashed 64, no encryption for the moment **/
    Gson gson = new Gson();
    String decryptedtoken = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    AuthToken securityToken = gson.fromJson(decryptedtoken, AuthToken.class);

    this.setCustomPermissionsList(permission, securityToken.getProject());

    // getting current user by session id - SecurityUtils.getSubject() is
    // empty
    if (securityToken != null) {
      String sessionId = securityToken.getPassword();

      Subject currentUser = new Subject.Builder(manager)
        .sessionId(sessionId)
        .sessionCreationEnabled(false)
        .buildSubject();

      // Authenticate user if not authenticated
      authenticateUser(currentUser, securityToken);

      // Test whether user's role is authorized to perform functions in the permissions list
      authorizeUser(currentUser);
    }
  }

  private void setCustomPermissionsList(String permission, String projectIdentifier) {
    LOG.info("permission : " + permission);
    LOG.info("projectIdentifier : " + projectIdentifier);
    List<Permission> permissions = new ArrayList<>();
    if (permission != null && projectIdentifier != null) {
      permission = permission.concat(":").concat(projectIdentifier);
    }
    LOG.info("permission : " + permission);
    permissions.add(new WildcardPermission(permission, true));
    policy.setPermissionsList(permissions);
  }

  private void authenticateUser(Subject currentUser, AuthToken securityToken) throws Exception {
    LOG.info("---------------- authenticateUser ---------------------");

    String currentusername = null;
    if (currentUser.getPrincipals() != null && currentUser.getPrincipals().getPrimaryPrincipal() != null) {
      String[] principals = String.valueOf(currentUser.getPrincipals()).split(",");
      currentusername = principals[0];
    } else if (currentUser.getPrincipal() != null) {
      currentusername = currentUser.getPrincipal().toString();
    }

    boolean authenticated = currentUser.isAuthenticated();
    boolean sameUser = securityToken.getUsername().equals(currentusername);
    LOG.info("Authenticated currentusername : {}", currentusername);
    LOG.info("Authenticated: {}, same Username: {}", authenticated, sameUser);

    // extend session on actions
    if (authenticated && sameUser) {

      Session session = currentUser.getSession(false);
      LOG.info("securityToken session : " + session);
      LOG.info("currentUserBySession lastaccess : " + currentUser.getSession().getLastAccessTime());
      LOG.info("currentUserBySession start : " + currentUser.getSession().getStartTimestamp());
      LOG.info("currentUserBySession timeout : " + currentUser.getSession().getTimeout());
      try {
        session.touch();
      } catch (ExpiredSessionException exp) {
        LOG.error("SessionExpired {} !", session.getId());
      }
    } else {
      // if not authenticated or not the same user. Logout and close session.
      try {
        LOG.info("Before LOGOUT caused by !authenticated || !sameUser. ");
        currentUser.logout();
        Session session = currentUser.getSession(false);
        if (session != null) {
          session.stop();
        }
      } catch (Exception ex) {
        LOG.error(ex.getMessage());
      }
      LOG.info("After LOGOUT ");
      throw new Exception("SESSION_EXCEPTION");
    }
  }

  private void authorizeUser(Subject currentUser) throws Exception {
    LOG.info("---------------- authorizeUser ---------------------");
    boolean authorized = false;
    if (!policy.getPermissionsList().isEmpty()) {
      if (policy.isAllPermissionsRequired()) {
        authorized = currentUser.isPermittedAll(policy.getPermissionsList());
      } else {
        for (Permission permission : policy.getPermissionsList()) {

          try {
            // retrieve requested permission for current operation
            LOG.info("permission.toString() " + permission.toString());
            String[] permissionSplit = permission.toString().split(":");
            LOG.info("permission length " + permissionSplit.length);
            String objType = permissionSplit[0];
            String operationType = permissionSplit[1];
            String projectIdentifier = null;
            if (permissionSplit.length == 3) {
              projectIdentifier = permissionSplit[2];
            }
            //  String identifier = permissionSplit[3];

            LOG.info("permissionSplit length " + permissionSplit.length);
            LOG.info("permissionSplit[0] " + objType);
            LOG.info("permissionSplit[1] " + operationType);
            if (permissionSplit.length == 3) {
              LOG.info("permissionSplit[2] " + projectIdentifier);
            }
            // LOG.info("permissionSplit[3] " + identifier);
            LOG.info("currentUser.getPrincipals " + currentUser.getPrincipals());

            String[] principals = String.valueOf(currentUser.getPrincipals()).split(",");
            String username = principals[0];
            LOG.info("username " + username);
            authorized = userPermission.isSestObjectTypeAllowed(username, PermissionTypeEnum.valueOf(operationType),
              SESTObjectTypeEnum.valueOf(objType), projectIdentifier);
            break;
          } catch (Exception ex) {
            LOG.error("Unable to authorize user " + ex.getMessage());
          }
        }
      }
    } else {
      LOG.info("Valid Permissions or Roles List not specified for ShiroSecurityPolicy. "
        + "No authorization checks will be performed for current user.");
      authorized = true;
    }

    if (!authorized) {
      throw new Exception("Authorization Failed. Subject's role set does "
        + "not have the necessary roles or permissions to perform further processing.");
    }

    LOG.debug("Current user {} is successfully authorized.", currentUser.getPrincipal());
  }
}
