/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
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

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.user.UserRole;
import org.crmf.model.user.UserRoleEnum;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentProjectInputTest {

  @Mock
  private AssprojectServiceInterface assprojectService;
  @Mock
  private AssessmentProjectUserInput assprojectUserInput;
  @Mock
  private UserPermissionManagerInput permissionManager;

  @InjectMocks
  private AssessmentProjectInput manager;

  @BeforeEach()
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createAssessmentProject() throws Exception {

    User pm = new User();
    pm.setUsername("pm");
    pm.setIdentifier("pmIdentifier");
    AssessmentProject project = new AssessmentProject();
    project.setProjectManager(pm);

    when(assprojectService.insert(project)).thenReturn("projectIdentifier");

    manager.createAssessmentProject(project);

    project.setIdentifier("projectIdentifier");

    ArrayList<User> users = project.getUsers();
    if (users == null) {
      users = new ArrayList<>();
    }
    User userPM = project.getProjectManager();
    User userWithIdentifier = new User();
    userWithIdentifier.setIdentifier(pm.getIdentifier());
    UserRole role = new UserRole();
    role.setRole(UserRoleEnum.Admin);
    role.setProjectIdentifier(project.getIdentifier());
    role.setUser(userWithIdentifier);

    userPM.setRoles(new ArrayList<>(Arrays.asList(role)));
    users.add(userPM);
    project.setUsers(users);

    verify(assprojectUserInput, times(1)).editAssessmentProjectRole(project);
  }


  @Test
  public void deleteAssessmentProject() throws Exception {

    manager.deleteAssessmentProject("projectIdentifier");
    verify(assprojectService, times(1)).deleteCascade("projectIdentifier");
  }

  @Test
  public void loadAssessmentProject() throws Exception {

    manager.loadAssessmentProject("projectIdentifier");
    verify(assprojectService, times(1)).getByIdentifierFull("projectIdentifier");
  }

  @Test
  public void loadAssessmentProjectList() throws Exception {

    manager.loadAssessmentProjectList("username", "Read");
    verify(permissionManager, times(1)).retrieveProjectPermissionBasedOnRoleAndType(
      "username", PermissionTypeEnum.valueOf("Read"));
    verify(assprojectService, times(1)).getAll();
  }

  @Test
  public void loadAssessmentProjectListFilter() throws Exception {
    List<AssessmentProject> projects = new ArrayList<>();
    AssessmentProject project = new AssessmentProject();
    project.setIdentifier("projectIdentifier");
    AssessmentProject projectNotAllowed = new AssessmentProject();
    projectNotAllowed.setIdentifier("projectIdentifierNotAlloed");
    projects.add(project);
    projects.add(projectNotAllowed);
    HashSet<String> filteredProjects = new HashSet<>();
    filteredProjects.add("projectIdentifier");

    when(permissionManager.retrieveProjectPermissionBasedOnRoleAndType(
      "username", PermissionTypeEnum.valueOf("Read"))).thenReturn(filteredProjects);
    when(assprojectService.getAll()).thenReturn(projects);

    List<AssessmentProject> projectsResulted = manager.loadAssessmentProjectList("username", "Read");
    Assertions.assertEquals(1, projectsResulted.size());
    Assertions.assertEquals("projectIdentifier", projectsResulted.get(0).getIdentifier());
  }
}
