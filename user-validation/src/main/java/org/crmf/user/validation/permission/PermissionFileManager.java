/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PermissionFileManager.java"
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

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 Class responsible for the management of Permission configuration files
 whose goal is to define the User/Profile/Role rights into SEST system.
 */
@Service
public class PermissionFileManager {
  private static final Logger LOG = LoggerFactory.getLogger(PermissionFileManager.class.getName());
  private Map<String, HashMap<String, List<String>>> rolesPermissions = new HashMap<String, HashMap<String, List<String>>>();
  private Map<String, List<String>> profilesRolesAssociation = new HashMap<String, List<String>>();
  private Map<String, HashMap<String, List<String>>> profilesPermissions = new HashMap<String, HashMap<String, List<String>>>();

  @Autowired
  ResourceLoader resourceLoader;
  /*
   Loads the permissions associated to a defined role and the profiles/roles association.
   It returns a Map of role/list of role associated rights pair values
   */
  public void loadSystemRights() {
    Ini iniRole;
    Wini iniProfile;
    try {
      InputStream resourceRoles = new ClassPathResource("permissionRoles.ini").getInputStream();
      InputStream resourceProfiles = new ClassPathResource("permissionProfiles.ini").getInputStream();
     // LOG.info(String.valueOf(resourceRoles.readAllBytes().length));
    //  LOG.info(String.valueOf(resourceProfiles.readAllBytes().length));
      iniRole = new Ini(resourceRoles);
      iniProfile = new Wini(resourceProfiles);
      LOG.info(String.valueOf(iniRole.values().size()));
      LOG.info(String.valueOf(iniProfile.values().size()));

      // load permission Roles files
      //output names of all sections (each section is a role exception "Profiles association" section)
      Collection<Profile.Section> listSections = iniRole.values();
      // for each section
      for (Profile.Section section : listSections) {
        String sectionName = section.getName();
        // if the section is "Profile Association"
        if (sectionName.equals("ProfilesAssociation"))
          // loads the configuration related to profiles and roles association
          loadProfilesRolesAssociation(iniRole, sectionName);
        else
          //loads the permissions configuration related to the role whose name is the sectionName
          loadRolePermissions(iniRole, sectionName);
      }
      // load permission Profiles files
      //output names of all profile sections
      Collection<Profile.Section> listProfileSections = iniProfile.values();
      for (Profile.Section section : listProfileSections) {
        String sectionName = section.getName();
        loadProfilePermissions(iniProfile, sectionName);
      }

    } catch (IOException ex) {
      LOG.error(ex.getMessage());
    }
  }

  /*
   Loads the profiles/roles association, stored into a section of the .ini configuration file.
   */
  private void loadProfilesRolesAssociation(Ini ini, String profileSectionName) {
    // for each profile into Profile/Role association section
    for (String profile : ini.get(profileSectionName).keySet()) {
      //fetch the roles associated to the current SEST profile (one or more, separated by commas)
      String roles = ini.fetch(profileSectionName, profile);
      List<String> profileRolesLinks = new ArrayList<String>();
      // retrieve the single role value (split the comma separated values and remove the spaces)
      for (String value : roles.split(",")) {
        String roleToAdd = value.trim();
        profileRolesLinks.add(roleToAdd);
      }
      profilesRolesAssociation.put(profile, profileRolesLinks);
    }
  }

  /*
   Loads the permissions related to a role, stored into a section of the .ini configuration file.
   */
  private void loadRolePermissions(Ini ini, String sectionName) {
    LOG.info("loadRolePermissions");
    //output keys of one section (keys are the SEST objects)
    Profile.Section roleSection = ini.get(sectionName);
    // for each SEST object
    HashMap<String, List<String>> objectPermissions = new HashMap<String, List<String>>();
    for (String sestObject : roleSection.keySet()) {
      //fetch the permission values associated to the sest object (one or more, separated by commas)
      String permissions = ini.fetch(sectionName, sestObject);
      List<String> singlePermissions = new ArrayList<String>();
      // retrieve the single permission value (split the comma separated values and remove the spaces)
      for (String value : permissions.split(",")) {
        String permissionToAdd = value.trim();
        singlePermissions.add(permissionToAdd);
      }
      objectPermissions.put(sestObject, singlePermissions);
      rolesPermissions.put(sectionName, objectPermissions);
    }
  }

  private void loadProfilePermissions(Wini ini, String sectionName) {
    LOG.info("loadProfilePermissions");
    //output keys of one section (keys are the SEST objects)
    Profile.Section profileSection = ini.get(sectionName);
    // for each SEST object
    HashMap<String, List<String>> objectPermissions = new HashMap<String, List<String>>();
    for (String sestObject : profileSection.keySet()) {
      //fetch the permission values associated to the sest object (one or more, separated by commas)
      String permissions = ini.fetch(sectionName, sestObject);
      List<String> singlePermissions = new ArrayList<String>();
      // retrieve the single permission value (split the comma separated values and remove the spaces)
      if (permissions != null)
        for (String value : permissions.split(",")) {
          String permissionToAdd = value.trim();
          singlePermissions.add(permissionToAdd);
        }
      objectPermissions.put(sestObject, singlePermissions);
      LOG.info("loadProfilePermissions put " + sectionName + ", " + objectPermissions);
      profilesPermissions.put(sectionName, objectPermissions);
    }
  }


  public Map<String, HashMap<String, List<String>>> getRolesPermissions() {
    return rolesPermissions;
  }

  public Map<String, List<String>> getProfilesRolesAssociation() {
    return profilesRolesAssociation;
  }

  public Map<String, HashMap<String, List<String>>> getProfilesPermissions() {
    return profilesPermissions;
  }


  public static void main(String[] args) {
    new PermissionFileManager().loadSystemRights();
  }
}
