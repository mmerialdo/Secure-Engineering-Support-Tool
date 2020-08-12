/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProjectInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.riskassessment.project.manager;

import org.crmf.core.audit.AuditInputInterface;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssessmentProjects
public class AssessmentProjectInput implements AssessmentProjectInputInterface {

  private final Logger LOG = LoggerFactory.getLogger(AssessmentProjectInput.class.getName());
  private AssprojectServiceInterface assprojectService;
  private AssessmentProjectUserInputInterface assprojectUserInput;
  private UserPermissionManagerInputInterface permissionManager;
  private AuditInputInterface auditInput;

  @Override
  public String createAssessmentProject(AssessmentProject project) throws Exception {

    LOG.info("createAssessmentProject with identifier {}", project.getIdentifier());
    String identifier = assprojectService.insert(project);
    project.setIdentifier(identifier);

    // add project PM ad Admin user
    ArrayList<User> users = project.getUsers();
    if (users == null) {
      users = new ArrayList<>();
    }
    User userPM = project.getProjectManager();
    User userWithIdentifier = new User();
    userWithIdentifier.setIdentifier(userPM.getIdentifier());
    UserRole role = new UserRole();
    role.setRole(UserRoleEnum.Admin);
    role.setProjectIdentifier(project.getIdentifier());
    role.setUser(userWithIdentifier);

    userPM.setRoles(new ArrayList<>(Arrays.asList(role)));
    users.add(userPM);
    project.setUsers(users);

    // edit users roles for project
    assprojectUserInput.editAssessmentProjectRole(project);

    return identifier;
  }

  @Override
  public void editAssessmentProject(AssessmentProject project) throws Exception {

    LOG.info("editAssessmentProject with identifier: {}", project.getIdentifier());
    assprojectService.update(project);
    LOG.info("editAssessmentProjectRole");
    assprojectUserInput.editAssessmentProjectRole(project);
  }

  @Override
  public void deleteAssessmentProject(String identifier) throws Exception {

    LOG.info("deleteAssessmentProject with identifier: {}", identifier);
    assprojectService.deleteCascade(identifier);
  }

  @Override
  public AssessmentProject loadAssessmentProject(String identifier) throws Exception {

    LOG.info("loadAssessmentProject with identifier: {}", identifier);
    return assprojectService.getByIdentifierFull(identifier);
  }

  @Override
  public List<AssessmentProject> loadAssessmentProjectList(String username, String permission) throws Exception {

    LOG.info("loadAssessmentProjectList ");

    List<AssessmentProject> projectsFiltered = new ArrayList<>();
    Set<String> filteredProjects = permissionManager.
      retrieveProjectPermissionBasedOnRoleAndType(username, PermissionTypeEnum.valueOf(permission));
    List<AssessmentProject> projects = assprojectService.getAll();
    projects.forEach(project -> {
      if (filteredProjects.contains(project.getIdentifier())) {
        projectsFiltered.add(project);
      }
    });
    return projectsFiltered;
  }

  public AssprojectServiceInterface getAssprojectService() {
    return assprojectService;
  }

  public void setAssprojectService(AssprojectServiceInterface assprojectService) {
    this.assprojectService = assprojectService;
  }

  public AssessmentProjectUserInputInterface getAssprojectUserInput() {
    return assprojectUserInput;
  }

  public void setAssprojectUserInput(AssessmentProjectUserInputInterface assprojectUserInput) {
    this.assprojectUserInput = assprojectUserInput;
  }

  public UserPermissionManagerInputInterface getPermissionManager() {
    return permissionManager;
  }

  public void setPermissionManager(UserPermissionManagerInputInterface permissionManager) {
    this.permissionManager = permissionManager;
  }

  public AuditInputInterface getAuditInput() {
    return auditInput;
  }

  public void setAuditInput(AuditInputInterface auditInput) {
    this.auditInput = auditInput;
  }

}
