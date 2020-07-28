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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crmf.core.safeguardmodel.manager.SafeguardModelManagerInputInterface;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.audit.Question;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.utility.ModelObject;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface;
import org.crmf.persistency.mapper.general.SestObjService;
import org.crmf.persistency.mapper.general.SestObjServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInputInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the Audit
public class AuditInput implements AuditInputInterface {

  private static final Logger LOG = LoggerFactory.getLogger(AuditInput.class.getName());
  private AssAuditServiceInterface assauditService;
  private QuestionnaireServiceInterface questionnaireService;
  private AssprojectServiceInterface assprojectService;
  private AssprocedureServiceInterface assprocedureService;
  private SafeguardModelManagerInputInterface safeguardModelInput;
  private RiskModelManagerInputInterface riskModelInput;


  public void editAudit(ModelObject auditJson) {
    try {
      AuditModelSerializerDeserializer converterModel = new AuditModelSerializerDeserializer();
      Audit audit = converterModel.getAuditFromClientModel(auditJson);
      SestAuditModel auditModel = converterModel.getAuditModelFromAudit(audit);
      assauditService.update(auditModel);

      int projectId = assauditService.getProjectIdByIdentifier(auditModel.getIdentifier());

      //Once the audit answers are updated, the Safeguard Model must be updated
      AssessmentProject project = assprojectService.getById(projectId);
      project.setAudits(new ArrayList<>(Arrays.asList(auditModel)));

      editSafeguardModel(project);
    } catch (Exception e) {
      LOG.error("editAudit exception: " + e.getMessage());
    }
  }

  @Override
  public void editSafeguardModel(AssessmentProject project) {
    try {
      LOG.info("editSafeguardModel about to start the updateQuestionnaireJSON of the SafeguardModel for project with id: " + project.getIdentifier());

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
  @Override
  public SestAuditModel loadAudit(String identifier, AuditTypeEnum type, boolean includeModels) {

    LOG.info("loadAudit : " + identifier + ", type : " + type);
    return assauditService.getByProjectAndType(identifier,
      (type != null ? type : AuditTypeEnum.SECURITY), includeModels);
  }

  @Override
  public ModelObject loadQuestionnaire(String identifier) {
    LOG.info("loadQuestionnaire : " + identifier);
    QuestionnaireModelSerializerDeserializer converter = new QuestionnaireModelSerializerDeserializer();
    SestQuestionnaireModel questionnaireModel = questionnaireService.getByIdentifier(identifier);
    String json = converter.getClientJSONStringFromQuestionnaireModel(questionnaireModel);

    ModelObject modelObject = new ModelObject();
    modelObject.setJsonModel(json);
    modelObject.setObjectIdentifier(identifier);
    return modelObject;
  }

  @Override
  public List<Question> loadQuestionnaireSafeguard() {
    return assauditService.getSafeguardByIdentifier();
  }

  @Override
  public void createDefaultQuestionnaire() {
    assauditService.createDefaultQuestionnaire();
  }

  public QuestionnaireServiceInterface getQuestionnaireService() {
    return questionnaireService;
  }

  public AssprojectServiceInterface getAssprojectService() {
    return assprojectService;
  }

  public AssprocedureServiceInterface getAssprocedureService() {
    return assprocedureService;
  }

  public SafeguardModelManagerInputInterface getSafeguardModelInput() {
    return safeguardModelInput;
  }

  public RiskModelManagerInputInterface getRiskModelInput() {
    return riskModelInput;
  }

  public void setQuestionnaireService(QuestionnaireServiceInterface questionnaireService) {
    this.questionnaireService = questionnaireService;
  }

  public void setAssprojectService(AssprojectServiceInterface assprojectService) {
    this.assprojectService = assprojectService;
  }

  public void setAssprocedureService(AssprocedureServiceInterface assprocedureService) {
    this.assprocedureService = assprocedureService;
  }

  public void setSafeguardModelInput(SafeguardModelManagerInputInterface safeguardModelInput) {
    this.safeguardModelInput = safeguardModelInput;
  }

  public void setRiskModelInput(RiskModelManagerInputInterface riskModelInput) {
    this.riskModelInput = riskModelInput;
  }

  public AssAuditServiceInterface getAssauditService() {
    return assauditService;
  }

  public void setAssauditService(AssAuditServiceInterface assauditService) {
    this.assauditService = assauditService;
  }
}
