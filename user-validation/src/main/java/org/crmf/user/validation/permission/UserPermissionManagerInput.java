/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserPermissionManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.user.validation.permission;

import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserProfileEnum;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.mapper.user.RoleServiceInterface;
import org.crmf.persistency.mapper.user.UserServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//This class manages the interactions with the sest-proxy bundle. Its methods are invoked in order to add/read/remove User's permissions
public class UserPermissionManagerInput implements UserPermissionManagerInputInterface {
  private static final Logger LOG = LoggerFactory.getLogger(UserPermissionManagerInput.class.getName());
  private Map<UserRoleEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> permissionRolesTable = new HashMap<>();
  private Map<UserProfileEnum, Set<UserRoleEnum>> profileRolesLinkTable = new HashMap<UserProfileEnum, Set<UserRoleEnum>>();
  private Map<UserProfileEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> permissionProfilesTable = new HashMap<>();

  private UserServiceInterface userService;
  private RoleServiceInterface roleService;

  public UserPermissionManagerInput() {
    this.initializePermissions();
  }

  private void initializePermissions() {

    LOG.info("initializePermissions");
    if (permissionRolesTable == null || permissionRolesTable.size() <= 0 ||
      permissionProfilesTable == null || permissionProfilesTable.size() <= 0 ||
      profileRolesLinkTable == null || profileRolesLinkTable.size() <= 0) {
      LOG.info("initializePermissions initialize");
      // create a new Permission File Manager
      PermissionFileManager pfm = new PermissionFileManager();
      // load the configuration contained into the Permission configuration
      // file
      pfm.loadSystemRights();
      if (permissionRolesTable == null || permissionRolesTable.size() <= 0) {
        translateRoleMapToSESTDomain(pfm.getRolesPermissions());
      }
      if (profileRolesLinkTable == null || profileRolesLinkTable.size() <= 0) {
        translateProfileAssociationMapToSESTDomain(pfm.getProfilesRolesAssociation());
      }
      if (permissionProfilesTable == null || permissionProfilesTable.size() <= 0) {
        translateProfileMapToSESTDomain(pfm.getProfilesPermissions());
      }
      LOG.info("initializePermissionRolesTable - end");
    }
  }

  /*
   Translate a generic multimap String/Map<String> to a SEST domain related
   multimap concerning roles rights over SEST objects
   */
  private void translateProfileMapToSESTDomain(Map<String, HashMap<String, List<String>>> rightsMap) {
    LOG.info("translateProfileMapToSESTDomain");
    if (!permissionProfilesTable.isEmpty())
      permissionProfilesTable.clear();

    LOG.info("translateProfileMapToSESTDomain " + rightsMap);
    // maps profile to a map of roles related to <Map<PermissionTypeEnum, List<SESTObjectTypeEnum>>>
    rightsMap.forEach((profile, rights) -> {
      Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> permissionTypeWithObjectList = Collections.unmodifiableMap(translateMapToPermissionTypeMap(rights));
      LOG.info("permissionProfilesTable put !!! " + permissionTypeWithObjectList);
      permissionProfilesTable.put(UserProfileEnum.valueOf(profile), permissionTypeWithObjectList);
    });
  }

  /*
   Translate a generic multimap String/Map<String> to a SEST domain related
   multimap concerning roles rights over SEST objects
   */
  private void translateRoleMapToSESTDomain(Map<String, HashMap<String, List<String>>> rightsMap) {
    if (!permissionRolesTable.isEmpty())
      permissionRolesTable.clear();

    // maps profile to a map of roles related to <Map<PermissionTypeEnum, List<SESTObjectTypeEnum>>>
    rightsMap.forEach((profile, rights) -> {
      Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> permissionTypeWithObjectList = Collections.unmodifiableMap(translateMapToPermissionTypeMap(rights));
      permissionRolesTable.put(UserRoleEnum.valueOf(profile), permissionTypeWithObjectList);
    });
  }

  public Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> translateMapToPermissionTypeMap(Map<String, List<String>> rights) {
    Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> permissionTypeWithObjectList = new HashMap<>();
    rights.forEach((sestObjType, permissionsType) -> {
      permissionsType.forEach(permissionType -> {
        if (permissionTypeWithObjectList.get(PermissionTypeEnum.valueOf(permissionType)) == null) {
          permissionTypeWithObjectList.put(PermissionTypeEnum.valueOf(permissionType), new HashSet<>());
        }
        permissionTypeWithObjectList.get(PermissionTypeEnum.valueOf(permissionType)).add(SESTObjectTypeEnum.valueOf(sestObjType));
      });
    });
    return permissionTypeWithObjectList;
  }

  /*
   Translate a generic multimap String/List<String> to a SEST domain related
   map concerning th association between roles and profiles.
   */
  private void translateProfileAssociationMapToSESTDomain(Map<String, List<String>> associationMap) {
    if (!profileRolesLinkTable.isEmpty())
      profileRolesLinkTable.clear();

    for (String profile : associationMap.keySet()) {
      UserProfileEnum p = UserProfileEnum.valueOf(profile);
      Set<UserRoleEnum> associatedRoles = new HashSet<UserRoleEnum>();
      for (List<String> roles : associationMap.values()) {
        for (String role : roles)
          associatedRoles.add(UserRoleEnum.valueOf(role));
      }
      profileRolesLinkTable.put(p, associatedRoles);
    }

  }

  @Override
  public void updatePermissionOnRoleChange(AssessmentProject project) throws Exception {
    String projectIdentifier = project.getIdentifier();
    List<User> users = project.getUsers();
    LOG.info("saveAssessmentProjectRole users List " + users);
    List<User> oldUsers = roleService.getByProjectIdentifier(projectIdentifier);
    // Set<SESTObject> associatedObjects = this.retrieveAssociatedSestObjectsForProject(projectIdentifier);

    for (User user : users) {
      String userIdentifier = user.getIdentifier();
      boolean userFound = false;
      if (oldUsers != null) {
        for (User oldUser : oldUsers) {
          if (oldUser.getIdentifier().equals(userIdentifier)) {
            userFound = true;
            if (oldUser.getRoles() != null && user.getRoles() != null && this.checkRolesChanged(oldUser.getRoles(), user.getRoles())) {
              LOG.info("saveAssessmentProjectRole users List userIdentifier " + userIdentifier);
              LOG.info("saveAssessmentProjectRole users List projectIdentifier " + projectIdentifier);
              roleService.deleteUserFromProject(userIdentifier, projectIdentifier, null);
              addRole(user, projectIdentifier);
            }
          }
        }
      }
      if (!userFound) {
        addRole(user, projectIdentifier);
      }
    }

    for (User oldUser : oldUsers) {
      boolean userFound = false;
      for (User user : users) {
        if (oldUser.getIdentifier().equals(user.getIdentifier())) {
          userFound = true;
        }
      }
      if (!userFound) {
        roleService.deleteUserFromProject(oldUser.getIdentifier(), projectIdentifier, null);
      }
    }
  }

  private void addRole(User user, String projectIdentifier) throws Exception {
    for (UserRole userRole : user.getRoles()) {
      LOG.info("saveAssessmentProjectRole users List userRole " + userRole);
      roleService.insert(userRole, user.getIdentifier(), projectIdentifier);
    }
  }

  private boolean checkRolesChanged(ArrayList<UserRole> oldUserRoles, ArrayList<UserRole> userRoles) {

    if (oldUserRoles.size() != userRoles.size()) {
      return true;
    }
    for (UserRole oldRole : oldUserRoles) {
      boolean found = false;
      for (UserRole role : userRoles) {
        if (oldRole.getProjectIdentifier().equals(role.getProjectIdentifier()) && oldRole.getRole().equals(role.getRole())) {
          found = true;
        }
      }
      if (!found) {
        return true;
      }
    }
    return false;
  }


  @Override
  public boolean isSestObjectTypeAllowed(String username, PermissionTypeEnum type, SESTObjectTypeEnum objectType, String projectIdentifier) {

    LOG.info("isSestObjectAllowed username " + username + ", type " + type + ", objectType " + objectType + " projectIdentifier" + projectIdentifier);

    User user = userService.getByUsername(username);
    UserProfileEnum profile = user.getProfile();

    LOG.info("retrievePermissionBasedOnProfileAndType1 " + permissionProfilesTable);
    // retrieves the object type allowed to the user, based on permission
    // type, from profile configuration.
    Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> profilePermitList = permissionProfilesTable.get(profile);
    if (profilePermitList != null && profilePermitList.get(type) != null && profilePermitList.get(type).contains(objectType)) {
      return true;
    } else {
      return this.isProjectObjectTypeAllowed(user.getIdentifier(), type, objectType, projectIdentifier);
    }
  }

  public boolean isProjectObjectTypeAllowed(String userIdentifier, PermissionTypeEnum type, SESTObjectTypeEnum objectType, String projectIdentifier) {
    LOG.info("isSestObjectAllowed userIdentifier " + userIdentifier + ", type " + type + ", objectType " + objectType + " projectIdentifier" + projectIdentifier);
    if (projectIdentifier != null && !projectIdentifier.isEmpty()) {
      try {
        List<UserRole> roles = roleService.getRolesByUserAndProjectId(userIdentifier, projectIdentifier);
        if (roles != null) {
          for (UserRole role : roles) {
            if (permissionRolesTable.get(role.getRole()).get(type).contains(objectType)) {
              return true;
            }
          }
        }
      } catch (Exception ex) {
        LOG.error(ex.getMessage(), ex);
      }
    }
    return false;
  }

  @Override
  public Set<SESTObjectTypeEnum> retrievePermissionBasedOnProfileAndType(String username, PermissionTypeEnum type,
                                                                         String projectIdentifier) throws Exception {

    LOG.info(username + ", " + type + ", " + projectIdentifier);

    Set<SESTObjectTypeEnum> permissions = new HashSet<>();

    User user = userService.getByUsername(username);
    UserProfileEnum profile = user.getProfile();

    LOG.info("retrievePermissionBasedOnProfileAndType1 " + permissionProfilesTable);
    LOG.info("retrievePermissionBasedOnProfileAndType2 " + permissions);
    // retrieves the object type allowed to the user, based on permission
    // type, from profile configuration.
    Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> profilePermitList = permissionProfilesTable.get(profile);
    LOG.info("retrievePermissionBasedOnProfileAndType3 " + profilePermitList);
    if (profilePermitList != null) {
      permissions = new HashSet<>(profilePermitList.get(type));
    }
    LOG.info("retrievePermissionBasedOnProfileAndType4 ");
    LOG.info("profile " + profile);
    LOG.info("profile " + permissions.size());
    if (UserProfileEnum.ProjectManager.equals(profile) || checkAssessmentProjectPermission(user.getIdentifier(), type)) {
      permissions.add(SESTObjectTypeEnum.AssessmentProject);
    }
    LOG.info("profile " + permissions.size());

    if (projectIdentifier != null && !projectIdentifier.isEmpty()) {
      LOG.info("projectIdentifier " + projectIdentifier);
      LOG.info("projectIdentifier " + permissions);
      LOG.info("projectIdentifier " + permissions.size());
      permissions.addAll(retrievePermissionSestObjTypeByProjectIdentifier(user.getIdentifier(), type, projectIdentifier));
      LOG.info("projectIdentifier " + permissions);
      LOG.info("projectIdentifier " + permissions.size());
    }

    return permissions;
  }

  public boolean checkAssessmentProjectPermission(String userIdentifier, PermissionTypeEnum type) throws Exception {

    List<UserRole> roles = roleService.getRolesByUserAndProjectId(userIdentifier, null);

    if (roles != null && !roles.isEmpty()) {
      for (UserRole role : roles) {
        Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> objectAndPermissions = permissionRolesTable.get(role.getRole());
        if (objectAndPermissions != null) {
          Set<SESTObjectTypeEnum> objectTypes = objectAndPermissions.get(type);
          if (objectTypes != null && objectTypes.contains(SESTObjectTypeEnum.AssessmentProject)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public Set<SESTObjectTypeEnum> retrievePermissionSestObjTypeByProjectIdentifier(String userIdentifier, PermissionTypeEnum type,
                                                                                  String projectIdentifier) throws Exception {

    Set permissions = new HashSet<>();

    List<UserRole> roles = roleService.getRolesByUserAndProjectId(userIdentifier, projectIdentifier);

    if (roles != null && !roles.isEmpty()) {
      roles.forEach(role -> {
        Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>> rolesMap = permissionRolesTable.get(role.getRole());
        Set<SESTObjectTypeEnum> roleTypeObjectRelated = rolesMap.get(type);
        if (roleTypeObjectRelated != null && !roleTypeObjectRelated.isEmpty()) {
          permissions.addAll(rolesMap.get(type));
          LOG.info("permissions add " + rolesMap.get(type));
        }
      });
    }
    return permissions;
  }

  @Override
  public Set<String> retrieveProjectPermissionBasedOnRoleAndType(String username, PermissionTypeEnum type) throws Exception {
    LOG.info("retrieveProjectPermissionBasedOnRoleAndType username :" + username + ", type " + type);
    Set<String> allowedObjectsByProject = new HashSet<>();
    User user = userService.getByUsername(username);
    if (user != null) {
      LOG.info("retrieveProjectPermissionBasedOnRoleAndType username :" + user);
      List<UserRole> userRoles = roleService.getByUserIdentifier(user.getIdentifier());
      LOG.info("retrieveProjectPermissionBasedOnRoleAndType username :" + userRoles);
      if (userRoles != null) {
        for (UserRole userRole : userRoles) {
          LOG.info("retrieveProjectPermissionBasedOnRoleAndType username :" + username + ", userRole " + userRole);
          if (permissionRolesTable.get(userRole.getRole()) != null) {
            if (permissionRolesTable.get(userRole.getRole()).get(type) != null &&
              permissionRolesTable.get(userRole.getRole()).get(type).contains(SESTObjectTypeEnum.AssessmentProject)) {
              LOG.info("retrieveProjectPermissionBasedOnRoleAndType add " + userRole.getProjectIdentifier());
              allowedObjectsByProject.add(userRole.getProjectIdentifier());
            }
          }
        }
      }
    }
    LOG.info("retrieveProjectPermissionBasedOnRoleAndType allowedObjectsByProject :" + allowedObjectsByProject);
    return allowedObjectsByProject;
  }

  public Map<UserRoleEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> getPermissionRolesTable() {
    return permissionRolesTable;
  }

  public Map<UserProfileEnum, Set<UserRoleEnum>> getProfileRolesLinkTable() {
    return profileRolesLinkTable;
  }

  public Map<UserProfileEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> getPermissionProfilesTable() {
    return permissionProfilesTable;
  }

  public UserServiceInterface getUserService() {
    return userService;
  }

  public RoleServiceInterface getRoleService() {
    return roleService;
  }

  public void setUserService(UserServiceInterface userService) {
    this.userService = userService;
  }

  public void setRoleService(RoleServiceInterface roleService) {
    this.roleService = roleService;
  }

  public void setPermissionRolesTable(Map<UserRoleEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> permissionRolesTable) {
    if (this.permissionRolesTable.isEmpty()) {
      this.permissionRolesTable = permissionRolesTable;
    }
  }

  public void setProfileRolesLinkTable(Map<UserProfileEnum, Set<UserRoleEnum>> profileRolesLinkTable) {
    this.profileRolesLinkTable = profileRolesLinkTable;
  }

  public void setPermissionProfilesTable(Map<UserProfileEnum, Map<PermissionTypeEnum, Set<SESTObjectTypeEnum>>> permissionProfilesTable) {
    if (this.permissionProfilesTable.isEmpty()) {
      this.permissionProfilesTable = permissionProfilesTable;
    }
  }
}