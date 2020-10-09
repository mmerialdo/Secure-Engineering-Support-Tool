/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssProjectTest.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.RiskMethodologyEnum;
import org.crmf.persistency.mapper.project.AssprocedureService;
import org.crmf.persistency.mapper.project.AssprofileService;
import org.crmf.persistency.mapper.project.AssprojectService;
import org.crmf.persistency.mapper.project.AsstemplateService;
import org.crmf.persistency.mapper.project.SysprojectService;
import org.crmf.persistency.mapper.user.RoleService;
import org.crmf.persistency.mapper.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@MybatisTest
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("test")
public class AssProjectTest {

  @Autowired
  private SysprojectService sysprojectService;
  @Autowired
  private AssprojectService projectService;
  @Autowired
  private AssprofileService profileService;
  @Autowired
  private AssprocedureService procedureService;
  @Autowired
  private AsstemplateService templateService;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private TestData data;

  AssessmentProject project;

  @BeforeEach
  public void setUp() throws Exception {
    this.data.prefillModels();
    project = prefill();
  }

  @Test
  public void insert() throws Exception {

    AssessmentProject project = data.buildModelAssProject("2");
    project.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = projectService.insert(project);
    project.setIdentifier(identifier);

    AssessmentProject projectResulted = projectService.getByIdentifierFull(identifier);
    Assertions.assertEquals("project02", projectResulted.getName());
    Assertions.assertEquals("project for test2", projectResulted.getDescription());
    Assertions.assertEquals("HTRA", projectResulted.getRiskMethodology().name());
    Assertions.assertNotNull(projectResulted.getSystemProject());
    Assertions.assertEquals("euclid2", projectResulted.getSystemProject().getName());
  }

  @Test
  public void insertProjectWithAudit() throws Exception {

    AssessmentProject project = data.buildModelAssProject("3");
    project.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = projectService.insert(project);
    project.setIdentifier(identifier);

    AssessmentProject projectResulted = projectService.getByIdentifierFull(identifier);
    Assertions.assertEquals("project03", projectResulted.getName());
    Assertions.assertEquals("project for test3", projectResulted.getDescription());
    Assertions.assertEquals("HTRA", projectResulted.getRiskMethodology().name());
    Assertions.assertNotNull(projectResulted.getSystemProject());
    Assertions.assertEquals("euclid3", projectResulted.getSystemProject().getName());
  }


  @Test
  public void delete() {
    projectService.deleteCascade(project.getIdentifier());
    AssessmentProject projectResulted = projectService.getByIdentifierFull(project.getIdentifier());
    Assertions.assertNull(projectResulted);
  }

  @Test
  public void getAll() throws Exception {

    AssessmentProject project = data.buildModelAssProject("2");
    project.setRiskMethodology(RiskMethodologyEnum.HTRA);

    String identifier = projectService.insert(project);
    project.setIdentifier(identifier);

    List<AssessmentProject> projectResulted = projectService.getAll();
    Assertions.assertEquals(2, projectResulted.size());
  }

  private AssessmentProject prefill() throws Exception {

    project = data.buildModelAssProject("1");
    String identifier = projectService.insert(project);
    project.setIdentifier(identifier);

    return project;
  }

}
