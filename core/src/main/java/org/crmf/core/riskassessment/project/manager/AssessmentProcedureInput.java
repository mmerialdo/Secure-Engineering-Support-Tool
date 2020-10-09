/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureInput.java"
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

import org.crmf.core.riskassessment.utility.RiskAssessmentModelsCloner;
import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.RiskTreatmentModel;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.model.utility.risktreatmentmodel.RiskTreatmentModelSerializerDeserializer;
import org.crmf.persistency.domain.risk.SestRiskTreatmentModel;
import org.crmf.persistency.mapper.asset.AssetServiceInterface;
import org.crmf.persistency.mapper.audit.AssAuditServiceInterface;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.project.AssprojectServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.risk.RiskTreatmentServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.crmf.persistency.mapper.threat.ThreatServiceInterface;
import org.crmf.persistency.mapper.vulnerability.VulnerabilityServiceInterface;
import org.crmf.riskmodel.manager.RiskModelManagerInput;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the AssessmentProcedures
@Service
public class AssessmentProcedureInput {

  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
  private static final Logger LOG = LoggerFactory.getLogger(AssessmentProcedureInput.class.getName());
  @Autowired
  @Qualifier("default")
  private AssprocedureServiceInterface assprocedureService;
  @Autowired
  @Qualifier("default")
  private AssprojectServiceInterface assprojectService;
  @Autowired
  @Qualifier("default")
  private AssetServiceInterface assetModelService;
  @Autowired
  @Qualifier("default")
  private VulnerabilityServiceInterface vulnerabilityModelService;
  @Autowired
  @Qualifier("default")
  private ThreatServiceInterface threatModelService;
  @Autowired
  @Qualifier("default")
  private SafeguardServiceInterface safeguardModelService;
  @Autowired
  @Qualifier("default")
  private RiskServiceInterface riskModelService;
  @Autowired
  @Qualifier("default")
  private RiskTreatmentServiceInterface riskTreatmentModelService;
  @Autowired
  @Qualifier("default")
  private AssAuditServiceInterface auditService;
  @Autowired
  private UserPermissionManagerInput permissionManager;
  @Autowired
  private RiskModelManagerInput riskModelInput;
  @Autowired
  private AssessmentTemplateInput templateInput;
  @Autowired
  private RiskAssessmentModelsCloner modelsCloner;

  public String createAssessmentProcedure(AssessmentProcedure procedure, String projectIdentifier) {
    try {
      LOG.info("createAssessmentprocedure: {}", procedure.getIdentifier());

      // get template identifier associated to project
      //AssessmentTemplate template = assprojectService.getByIdentifierFull(projectIdentifier).getTemplate();
      AssessmentProject project = assprojectService.getByIdentifierFull(projectIdentifier);

      Map<SESTObjectTypeEnum, String> newModelsMap = null;
      //We create the procedure from the template associated to the project
      if (procedure.getPhase().equals(PhaseEnum.Initial)) {
        newModelsMap = getModelsMapFromTemplate(project);
      }
      //We create the procedure from the closed last procedure, if existing (otherwise, from the template)
      else {
        LOG.info("createAssessmentprocedure - cloning the last closed AssessmentProcedure");
        AssessmentProcedure procedureToBeCloned = getLastProcedureForProject(project);

        //We check if there is a closed procedure, if not, we create from the template
        if (procedureToBeCloned == null) {
          newModelsMap = getModelsMapFromTemplate(project);
        } else {
          LOG.info("createAssessmentprocedure - AssessmentProcedure to be cloned identifier: {}", procedureToBeCloned.getIdentifier());

          // copy the old procedure:
          // - a copy of of all models is created
          // - the sest object identifier of each one is changed into a new one
          // - the models with the new identifier are inserted into the db
          newModelsMap = modelsCloner.createModelsCopy(procedureToBeCloned, project);
        }
      }
      // updateQuestionnaireJSON models'id into the new procedure
      updateModels(newModelsMap, procedure);
      // update audit wih treatement and previous safeguard values, for values evolution
      updateAuditQuestionnaires(project.getAudits(), newModelsMap.get(SESTObjectTypeEnum.RiskTreatmentModel));

      //create procedure
      DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);

      Date now = new Date();

      procedure.setCreationTime(df.format(now));
      procedure.setUpdateTime(df.format(now));
      //set type and identifier accordingly to the sest object just created (procedure)
      procedure.setObjType(SESTObjectTypeEnum.AssessmentProcedure);
      LOG.info("procedure with identifier {} created, about to save in the persistency", procedure.getIdentifier());

      AssessmentProcedure newProcedure = assprocedureService.insert(procedure, projectIdentifier);

      riskModelInput.createAssessmentProcedure(newProcedure);

      //return procedure id
      return newProcedure.getIdentifier();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private AssessmentProcedure getLastProcedureForProject(AssessmentProject project) throws ParseException {
    AssessmentProcedure procedureToBeCloned = null;
    project.setProcedures(assprocedureService.getByProjectIdentifier(project.getIdentifier()));
    for (AssessmentProcedure oldProcedure : project.getProcedures()) {
      if (oldProcedure.getStatus().equals(AssessmentStatusEnum.Closed)) {
        if (procedureToBeCloned == null) {
          procedureToBeCloned = oldProcedure;
        }

        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
        Date oldProcedureUpdateDate = df.parse(oldProcedure.getUpdateTime());
        Date procedureToBeClonedUpdateTime = df.parse(procedureToBeCloned.getUpdateTime());

        //We search for the most recent closed procedure
        if (oldProcedureUpdateDate.compareTo(procedureToBeClonedUpdateTime) > 0) {
          procedureToBeCloned = oldProcedure;
        }
      }
    }
    return procedureToBeCloned;
  }

  private Map<SESTObjectTypeEnum, String> getModelsMapFromTemplate(AssessmentProject project) throws Exception {
    Map<SESTObjectTypeEnum, String> newModelsMap;
    String templateId = project.getTemplate().getIdentifier();
    //get the full template from template id
    AssessmentTemplate template = templateInput.loadAssessmentTemplateByIdentifier(templateId);
    LOG.info("createAssessmentprocedure - template id {}", templateId);

    // copy the template:
    // - a copy of of all models is created
    // - the sest object identifier of each one is changed into a new one
    // - the models with the new identifier are inserted into the db
    newModelsMap = modelsCloner.createModelsCopy(template, project);
    return newModelsMap;
  }

  public void editAssessmentProcedure(AssessmentProcedure procedure) {

    LOG.info("editAssessmentprocedure with identifier: {}", procedure.getIdentifier());
    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
    Date now = new Date();

    procedure.setUpdateTime(df.format(now));
    assprocedureService.update(procedure);
  }

  public void deleteAssessmentProcedure(String identifier) {

    LOG.info("deleteAssessmentProcedure with identifier: {}", identifier);
    assprocedureService.deleteCascade(identifier);
  }

  public List<AssessmentProcedure> loadAssessmentProcedure(GenericFilter filter) {

    LOG.info("loadAssessmentProcedure {} ", filter.getFilterMap());
    String identifier = filter.getFilterValue(GenericFilterEnum.IDENTIFIER);
    if (filter.getFilterValue(GenericFilterEnum.PROJECT) != null) {
      return assprocedureService.getByProjectIdentifier(filter.getFilterValue(GenericFilterEnum.PROJECT));
    } else {
      List<AssessmentProcedure> proceduresToReturn = new ArrayList<>();
      proceduresToReturn.add(assprocedureService.getByIdentifierFull(identifier));
      return proceduresToReturn;
    }
  }

  public List<AssessmentProcedure> loadAssessmentProcedureList() {

    LOG.info("loadAssessmentProcedureList ");
    return assprocedureService.getAll();
  }

  //Update the models stored into the Procedure with the models associated to the identifiers in input
  private void updateModels(Map<SESTObjectTypeEnum, String> newModelsMap, AssessmentProcedure procedure) {
    LOG.info("-----------------procedure updateModels:: begin");

    //for each model passed in input
    for (SESTObjectTypeEnum modelType : newModelsMap.keySet()) {
      // get the identifier of the current model sest object
      String identifier = newModelsMap.get(modelType);
      // insert the model into the procedure based on model type
      switch (modelType) {
        case AssetModel:
          LOG.info("-----------------procedure updateModels:: asset id {} ", identifier);
          procedure.setAssetModel(assetModelService.getByIdentifier(identifier).convertToModel());
          break;
        case VulnerabilityModel:
          LOG.info("-----------------procedure updateModels:: vuln id {}", identifier);
          procedure.setVulnerabilityModel(vulnerabilityModelService.getByIdentifier(identifier).convertToModel());
          break;
        case ThreatModel:
          LOG.info("-----------------procedure updateModels:: threat id {}", identifier);
          procedure.setThreatModel(threatModelService.getByIdentifier(identifier).convertToModel());
          break;
        case SafeguardModel:
          LOG.info("-----------------procedure updateModels:: safeguard id {}", identifier);
          procedure.setSafeguardModel(safeguardModelService.getByIdentifier(identifier).convertToModel());
          break;
        case RiskModel:
          LOG.info("-----------------procedure updateModels:: risk id {}", identifier);
          procedure.setRiskModel(riskModelService.getByIdentifier(identifier).convertToModel());
          break;
        case RiskTreatmentModel:
          LOG.info("-----------------procedure updateModels:: risktreatment id {}", identifier);
          procedure.setRiskTreatmentModel(riskTreatmentModelService.getByIdentifier(identifier).convertToModel());
          break;
        default:
          break;
      }
    }
  }

  private void updateAuditQuestionnaires(List<SestAuditModel> audits, String treatmentModelIdentifier) {
    LOG.info("updateAuditQuestionnaires");

    SestRiskTreatmentModel treatmentModel = riskTreatmentModelService.getByIdentifier(treatmentModelIdentifier);
    RiskTreatmentModelSerializerDeserializer rtmsr = new RiskTreatmentModelSerializerDeserializer();
    RiskTreatmentModel riskTreatment = rtmsr.getRTMFromPersistencyJSONString(treatmentModel.getRiskTreatmentModelJson());

    ArrayList<Safeguard> treatmentSafeguards = riskTreatment.getResultingSafeguards();

    SestAuditModel audit = audits.get(0);
    List<SestQuestionnaireModel> questionnaires = audit.getSestQuestionnaireModel();
    questionnaires.forEach(questionnaireJson -> {
      questionnaireJson.getQuestionnaireModelJson();
      QuestionnaireModelSerializerDeserializer deserializer = new QuestionnaireModelSerializerDeserializer();
      Questionnaire questionnaire = deserializer.getQuestionnaireFromJSONString(questionnaireJson.getQuestionnaireModelJson());
      boolean updated = false;
      if (questionnaire != null && questionnaire.getQuestions() != null) {
        for (Question group : questionnaire.getQuestions()) {
          if (group.getChildren() != null) {
            for (Question safeguard : group.getChildren()) {
              try {
                Map<AnswerTypeEnum, String> answers = safeguard.getAnswers();
                String answersV1 = answers.get(AnswerTypeEnum.MEHARI_R_V1);
                String answersTarget = answers.get(AnswerTypeEnum.MEHARI_R_Target);
                if (answersV1 != null && Integer.valueOf(answersV1) > 1) {
                  answers.put(AnswerTypeEnum.MEHARI_R_Prev, answersV1);
                  updated = true;
                }
                Optional<Safeguard> treatmentSafeguard = treatmentSafeguards.stream().filter(safeguardTreatment ->
                  safeguardTreatment.getCatalogueId().equals(safeguard.getCategory())).findFirst();
                if (treatmentSafeguard.isPresent() && treatmentSafeguard.get().getScore() != null
                  && Integer.valueOf(treatmentSafeguard.get().getScoreNumber()) > 1) {
                  answers.put(AnswerTypeEnum.MEHARI_R_Target, treatmentSafeguard.get().getScoreNumber());
                  updated = true;
                }
              } catch (Exception ex) {
                LOG.info("Unable to save answer value for {}", safeguard.getCategory());
              }
            }
          }
        }
      }
      if (updated) {
        String questionnaireJSONUpdated = deserializer.getJSONStringFromQuestionnaire(questionnaire);
        questionnaireJson.setQuestionnaireModelJson(questionnaireJSONUpdated);
      }
    });

    auditService.update(audit);
  }
}
