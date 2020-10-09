/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssetModelManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.audit;

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInput;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the Audit
@Service
public class AuditInput  {

  private static final Logger LOG = LoggerFactory.getLogger(AuditInput.class.getName());
  @Autowired
  @Qualifier("default")
  private AssAuditServiceInterface assauditService;
  @Autowired
  @Qualifier("default")
  private QuestionnaireServiceInterface questionnaireService;
  @Autowired
  @Qualifier("default")
  private AssprojectServiceInterface assprojectService;
  @Autowired
  @Qualifier("default")
  private AssprocedureServiceInterface assprocedureService;
  @Autowired
  private SafeguardModelManagerInput safeguardModelInput;
  @Autowired
  private RiskModelManagerInput riskModelInput;

  public void editAudit(ModelObject auditJson) {
    try {
      AuditModelSerializerDeserializer converterModel = new AuditModelSerializerDeserializer();
      Audit audit = converterModel.getAuditFromClientModel(auditJson);
      SestAuditModel auditModel = converterModel.getAuditModelFromAudit(audit);
      assauditService.update(auditModel);

      int projectId = assauditService.getProjectIdByIdentifier(auditModel.getIdentifier());

      //Once the audit answers are updated, the Safeguard Model must be updated
      AssessmentProject project = assprojectService.getById(projectId);
      project.setAudits(Arrays.asList(auditModel));

      editSafeguardModel(project);
    } catch (Exception e) {
      LOG.error("editAudit exception: " + e.getMessage());
    }
  }

  public void editSafeguardModel(AssessmentProject project) {
    try {
      LOG.info("editSafeguardModel about to start the updateQuestionnaireJSON of the SafeguardModel for project with id: {} ", project.getIdentifier());

      //Loading the full set of procedures for this assessmentproject
      project.setProcedures((ArrayList<AssessmentProcedure>) assprocedureService.getByProjectIdentifier(project.getIdentifier()));
      safeguardModelInput.editSafeguardModel(project);

      //After updating the Safeguard Model I need to updateQuestionnaireJSON the RiskModel (some RiskScenario may have been updated by the updateQuestionnaireJSON on the SafeguardModel)
      for (AssessmentProcedure procedure : project.getProcedures()) {
        if (procedure.getStatus().equals(AssessmentStatusEnum.OnGoing)) {
          riskModelInput.editSafeguardModel(procedure.getIdentifier());
        }
      }
    } catch (Exception e) {
      LOG.error("editSafeguardModel exception: " + e.getMessage(), e);
    }
  }

  //Load a single Audit of a specific Type for a specific AssessmentProject
  public SestAuditModel loadAudit(String identifier, AuditTypeEnum type, boolean includeModels) {

    LOG.info("loadAudit : {}, type : {}", identifier, type);
    return assauditService.getByProjectAndType(identifier,
      (type != null ? type : AuditTypeEnum.SECURITY), includeModels);
  }

  public ModelObject loadQuestionnaire(String identifier) {
    LOG.info("loadQuestionnaire : {} ", identifier);
    QuestionnaireModelSerializerDeserializer converter = new QuestionnaireModelSerializerDeserializer();
    SestQuestionnaireModel questionnaireModel = questionnaireService.getByIdentifier(identifier);
    String json = converter.getClientJSONStringFromQuestionnaireModel(questionnaireModel);

    ModelObject modelObject = new ModelObject();
    modelObject.setJsonModel(json);
    modelObject.setObjectIdentifier(identifier);
    return modelObject;
  }

  public List<Question> loadQuestionnaireSafeguard() {
    return assauditService.getSafeguardByIdentifier();
  }

  public void createDefaultQuestionnaire() {
    assauditService.createDefaultQuestionnaire();
  }
}
