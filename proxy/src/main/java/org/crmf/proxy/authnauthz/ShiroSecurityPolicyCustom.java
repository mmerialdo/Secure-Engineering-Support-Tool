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

import com.google.gson.Gson;
import org.apache.camel.component.shiro.security.ShiroSecurityPolicy;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.OperationMode;
import org.apache.shiro.crypto.PaddingScheme;
import org.crmf.model.general.AuthToken;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.user.manager.core.UserManagerInput;
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
import java.util.Set;

//This class extends the ShiroSecurityPolicy in order to send and retrieve User permissions
@Service
@Qualifier("ShiroSecurityPolicyCustom")
public class ShiroSecurityPolicyCustom extends ShiroSecurityPolicy {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroSecurityPolicyCustom.class.getName());
  @Autowired
  private UserPermissionManagerInput userPermission;
  @Autowired
  private UserManagerInput userManager;

  public ShiroSecurityPolicyCustom() {
    super();
    super.setPassPhrase("sest012345678910".getBytes());

    AesCipherService cipherService = new AesCipherService();
    cipherService.setMode(OperationMode.CTR);
    cipherService.setPaddingScheme(PaddingScheme.NONE);

    super.setCipherService(cipherService);
    super.setAllPermissionsRequired(false);
  }

  public String getPermissionList(String token) {

    LOG.info("getPermissionList TOKEN : " + token);

    Gson gson = new Gson();
    String decryptedtoken = new String(
      Base64.getDecoder().decode(token),
      StandardCharsets.UTF_8);
    AuthToken inputToken = gson.fromJson(decryptedtoken, AuthToken.class);

    String username = inputToken.getUsername();
    String projectIdentifier = inputToken.getProject();
    LOG.info("TOKEN username : " + username);
    LOG.info("TOKEN projectIdentifier : " + projectIdentifier);

    //send permission for current user
    PermissionToken loggedToken = buildTokenWithPermission(projectIdentifier, username);

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

}
