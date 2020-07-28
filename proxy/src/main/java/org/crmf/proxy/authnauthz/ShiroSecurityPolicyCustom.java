/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ShiroSecurityPolicyCustom.java"
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.apache.camel.Header;
import org.apache.camel.Processor;
import org.apache.camel.component.shiro.security.ShiroSecurityPolicy;
import org.apache.camel.component.shiro.security.ShiroSecurityToken;
import org.apache.camel.spi.RouteContext;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.OperationMode;
import org.apache.shiro.crypto.PaddingScheme;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.user.manager.authentication.UserAuthenticationInterface;
import org.crmf.user.manager.core.UserManagerInputInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

//This class extends the ShiroSecurityPolicy in order to send and retrieve User permissions
public class ShiroSecurityPolicyCustom extends ShiroSecurityPolicy implements ShiroSecurityPolicyCustomInterface {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroSecurityPolicyCustom.class.getName());
  private UserPermissionManagerInputInterface userPermission;
  private UserManagerInputInterface userManager;

  public ShiroSecurityPolicyCustom() {
    super();
    super.setPassPhrase("sest012345678910".getBytes());

    AesCipherService cipherService = new AesCipherService();
    cipherService.setMode(OperationMode.CTR);
    cipherService.setPaddingScheme(PaddingScheme.NONE);

    super.setCipherService(cipherService);
    super.setAllPermissionsRequired(false);
  }

  @Override
  public String getPermissionList(String token, String projectIdentifier) {

    LOG.info("LOGIN sendPermissionList TOKEN : " + token);
    LOG.info("LOGIN sendPermissionList PROJECT : " + projectIdentifier);

    Gson gson = new Gson();
    String decryptedtoken = new String(
      Base64.getDecoder().decode(token),
      StandardCharsets.UTF_8);
    ShiroSecurityToken inputToken = gson.fromJson(decryptedtoken, ShiroSecurityToken.class);

    String username = inputToken.getUsername();
    LOG.info("TOKEN username : " + username);

    //send permission for current user
    PermissionToken loggedToken = buildTokenWithPermission(projectIdentifier, username);

    LOG.info("currentUser : " + username);

    return gson.toJson(loggedToken);
  }

  public PermissionToken buildTokenWithPermission(String projectIdentifier, String username) {

    PermissionToken loggedToken = new PermissionToken();

    try {
      User user = userManager.retrieveUserByUsername(username);
      loggedToken.setUserId(user.getIdentifier());
      loggedToken.setUserProfile(user.getProfile().name());
      loggedToken.setRead(retrieveProjectPermissionBasedOnRoleAndType(username, PermissionTypeEnum.Read));
      loggedToken.setUpdate(retrieveProjectPermissionBasedOnRoleAndType(username, PermissionTypeEnum.Update));
      loggedToken.setCreate(retrieveUserPermissionByProfile(username, PermissionTypeEnum.Update, projectIdentifier));
      loggedToken.setView(retrieveUserPermissionByProfile(username, PermissionTypeEnum.Read, projectIdentifier));
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
    return loggedToken;
  }

  private List<String> retrieveUserPermissionByProfile(String username, PermissionTypeEnum type,
                                                       String projectIdentifier) {
    List<String> types = new ArrayList<>();
    try {
      Set<SESTObjectTypeEnum> typesAllowed = userPermission.retrievePermissionBasedOnProfileAndType(username, type,
        projectIdentifier);
      LOG.info("retrieveUserPermissionByProfile : " + typesAllowed);
      for (SESTObjectTypeEnum sestObjectTypeEnum : typesAllowed) {
        types.add(sestObjectTypeEnum.name());
      }
    } catch (Exception e1) {
      LOG.error(e1.getMessage());
    }

    return types;
  }

  private List<String> retrieveProjectPermissionBasedOnRoleAndType(String username, PermissionTypeEnum type) {
    try {
      return new ArrayList<>(userPermission.retrieveProjectPermissionBasedOnRoleAndType(username, type));
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
    return null;
  }

  public List<Permission> getPermissionsList() {
    List<Permission> permissions = super.getPermissionsList();
    return permissions;
  }

  @Override
  public Processor wrap(RouteContext routeContext, final Processor processor) {
    ShiroSecurityProcessorCustom routeProcessor = new ShiroSecurityProcessorCustom(processor, this);
    routeProcessor.setEncrypted(false);
    return routeProcessor;
  }

  public UserManagerInputInterface getUserManager() {
    return userManager;
  }

  public void setUserManager(UserManagerInputInterface userManager) {
    this.userManager = userManager;
  }

  public UserPermissionManagerInputInterface getUserPermission() {
    return userPermission;
  }

  public void setUserPermission(UserPermissionManagerInputInterface userPermission) {
    this.userPermission = userPermission;
  }

  private class PermissionToken {
    String userId;
    String userProfile;
    List<String> read;
    List<String> update;
    List<String> create;
    List<String> view;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public List<String> getRead() {
      return read;
    }

    public void setRead(List<String> read) {
      this.read = read;
    }

    public List<String> getUpdate() {
      return update;
    }

    public void setUpdate(List<String> update) {
      this.update = update;
    }

    public List<String> getCreate() {
      return create;
    }

    public void setCreate(List<String> create) {
      this.create = create;
    }

    public List<String> getView() {
      return view;
    }

    public void setView(List<String> view) {
      this.view = view;
    }

    public String getUserProfile() { return userProfile;}

    public void setUserProfile(String userProfile) { this.userProfile = userProfile; }
  }
}
