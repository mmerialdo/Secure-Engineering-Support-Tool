/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardModelManagerInput.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.safeguardmodel.manager;

import org.crmf.model.audit.AnswerTypeEnum;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.QuestionTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.riskassessment.AssessmentStatusEnum;
import org.crmf.model.riskassessment.PhaseEnum;
import org.crmf.model.riskassessment.SafeguardModel;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.riskassessmentelements.Safeguard;
import org.crmf.model.riskassessmentelements.SafeguardScopeEnum;
import org.crmf.model.riskassessmentelements.SafeguardScoreEnum;
import org.crmf.model.riskassessmentelements.SafeguardSourceEnum;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.audit.AuditModelSerializerDeserializer;
import org.crmf.model.utility.audit.QuestionnaireModelSerializerDeserializer;
import org.crmf.model.utility.safeguardmodel.SafeguardModelSerializerDeserializer;
import org.crmf.persistency.mapper.project.AssprocedureServiceInterface;
import org.crmf.persistency.mapper.risk.RiskServiceInterface;
import org.crmf.persistency.mapper.safeguard.SafeguardServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//This class is called by the Proxy and manages the entrypoint for the business logic (including the interactions with the Persistency) related to the SafeguardModel
@Service
public class SafeguardModelManagerInput {
    // the logger of SafeguardModelManagerInput class
    private static final Logger LOG = LoggerFactory.getLogger(SafeguardModelManagerInput.class.getName());
    public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
    // Safeguard service variable of persistency component
    @Autowired
    @Qualifier("default")
    private SafeguardServiceInterface safeguardService;
    // Procedure service variable of persistency component
    @Autowired
    @Qualifier("default")
    private AssprocedureServiceInterface assprocedureService;
    @Autowired
    @Qualifier("default")
    private RiskServiceInterface riskModelService;

    public static final String REGEX_QUESTIONNAIRE = "\\d+";
    public static final String REGEX_CATEGORY1 = "\\d+[A-Z]";
    public static final String REGEX_CATEGORY2 = "\\d+[A-Z]\\d+";
    public static final String REGEX_QUESTION = "\\d+[A-Z]+\\d+-\\d+";

    private boolean modelUpdated = false;

    public String loadSafeguardModel(GenericFilter filter) throws Exception {
        // get the procedure identifier passed in input
        String procedureIdentifier = filter.getFilterValue(GenericFilterEnum.PROCEDURE);
        LOG.debug("loadSafeguardModel:: input procedure filter = {}", procedureIdentifier);

        // if the value of procedure identifier is not null
        if (procedureIdentifier != null) {
            // retrieve the assessment procedure associated to the procedure
            // identifier in input
            AssessmentProcedure procedure = assprocedureService.getByIdentifierFull(procedureIdentifier);

            // retrieve the safeguard model identifier associated to the
            // retrieved assessment procedure
            String sestobjId = procedure.getSafeguardModel().getIdentifier();

            // return the json safeguard model associated to the
            // safeguard model identifier retrieved
            return safeguardService.getByIdentifier(sestobjId).getSafeguardModelJson();
        } else {
            throw new Exception("Incorrect procedure identifier in input");
        }
    }

    // This method is called when an Audit is modified. The goal is to updateQuestionnaireJSON
    // the SafeguardModel of all OnGoing AssessmentProcedure
    public void editSafeguardModel(AssessmentProject project) {
        LOG.info("SafeguardModelManagerInput about to editSafeguardModel for project: {}", project.getIdentifier());

        for (AssessmentProcedure procedure : project.getProcedures()) {
            if (procedure.getStatus().equals(AssessmentStatusEnum.OnGoing)) {
                modelUpdated = false;

                editSafeguardModelforProcedure(procedure, project.getAudits());

                if (modelUpdated) {
                    // Here we have to save both the AssessmentProcedure and the
                    // SafeguardModel
                    DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
                    Date now = new Date();

                    procedure.getSafeguardModel().setUpdateTime(df.format(now));

                    LOG.info("editSafeguardModel about to updateQuestionnaireJSON SafeguardModel");
                    SafeguardModelSerializerDeserializer serialiser = new SafeguardModelSerializerDeserializer();
                    safeguardService.update(serialiser.getJSONStringFromSM(procedure.getSafeguardModel()),
                            procedure.getSafeguardModel().getIdentifier());

                    LOG.info("editSafeguardModel about to updateQuestionnaireJSON AssessmentProcedure");
                    procedure.setUpdateTime(df.format(now));
                    assprocedureService.update(procedure);
                }
            }
        }
    }

    // This method is called when a new procedure is created. The existing Audit
    // is transformed into a SafeguardModel for the Procedure
    //As can be seen, we don't use the SafeguardModel from the template, because the SafeguardModel (and the Audit) is the same for each procedure  in the project
    public SafeguardModel createSafeguardModel(SafeguardModel safeguards, AssessmentProject project) {
        LOG.info("SafeguardModelManagerInput about to createSafeguardModel for SafeguardModel: {} ", safeguards.getIdentifier());
        SestAuditModel audit = null;

        for (SestAuditModel aAudit : project.getAudits()) {
            if (aAudit.getType().equals(AuditTypeEnum.SECURITY)) {
                audit = aAudit;
                break;
            }
        }

        if (audit == null) {
            LOG.error("createSafeguardModel Security audit null, unable to create SafeguardModel for a new procedure");
            return safeguards;
        }

        // Here we need to create Safeguards with respect to the Answers in the
        // Audit
        safeguards = createSafeguardsInAudit(safeguards, audit);

        LOG.info("SafeguardModelManagerInput about to Load all existing risk Scenarios References");
        // Load of all existing risk Scenarios References
        List<RiskScenarioReference> allRsr = riskModelService.getRiskScenarioReference();

        // It is necessary to check and assign to each Safeguard its
        // SafeguardScopeEnum. We then check all the RiskScenarioReferences in
        // order to be able to assign each Safeguard to the correct group
        for (RiskScenarioReference rsr : allRsr) {
            String dissuasion = rsr.getDissuasion();
            String prevention = rsr.getPrevention();
            String confining = rsr.getConfining();
            String palliative = rsr.getPalliative();

            for (Safeguard safeguard : safeguards.getSafeguards()) {
                if (safeguard.getScope() != null) {
                    continue;
                }
                if (dissuasion.contains(safeguard.getCatalogueId())) {
                    safeguard.setScope(SafeguardScopeEnum.Dissuasion);
                    break;
                }
                if (prevention.contains(safeguard.getCatalogueId())) {
                    safeguard.setScope(SafeguardScopeEnum.Prevention);
                    break;
                }
                if (confining.contains(safeguard.getCatalogueId())) {
                    safeguard.setScope(SafeguardScopeEnum.Confining);
                    break;
                }
                if (palliative.contains(safeguard.getCatalogueId())) {
                    safeguard.setScope(SafeguardScopeEnum.Pallation);
                    break;
                }
            }
        }

        return safeguards;
    }

    // This method is called when an Audit is modified. The goal is to updateQuestionnaireJSON
    // the SafeguardModel and the RiskModel of an OnGoing AssessmentProcedure
    private void editSafeguardModelforProcedure(AssessmentProcedure procedure, List<SestAuditModel> audits) {
        LOG.info("SafeguardModelManagerInput about to editSafeguardModel for procedure: {}", procedure.getIdentifier());
        SestAuditModel audit = null;

        for (SestAuditModel aAudit : audits) {
            LOG.info("SafeguardModelManagerInput {}, type {}, projectId {}, idetifier {} ",
                    aAudit, aAudit.getType(), aAudit.getProjectId(), aAudit.getIdentifier());
            if (aAudit.getType().equals(AuditTypeEnum.SECURITY)) {
                audit = aAudit;
                break;
            }
        }

        if (audit == null) {
            LOG.error("editSafeguardModelforProcedure Security audit null, unable to updateQuestionnaireJSON SafeguardModel");
            return;
        }

        // Here we compare the actual SafeguardModel and the Audit in order to
        // updateQuestionnaireJSON the SafeguardModel with respect to the actual Audit
        SafeguardModel safeguards = procedure.getSafeguardModel();
        LOG.info("editSafeguardModelforProcedure about to editSafeguardModel with identifier: {}",
                safeguards.getIdentifier());

        procedure.setSafeguardModel(editSafeguardModel(safeguards, audit));
        LOG.info("editSafeguardModelforProcedure SafeguardModel with identifier: {} has been updated", safeguards.getIdentifier());
    }

    // This methods compares an existing SafeguardModel with respect to an Audit
    private SafeguardModel editSafeguardModel(SafeguardModel safeguards, SestAuditModel auditModel) {
        LOG.info("SafeguardModelManagerInput about to editSafeguardModel for SafeguardModel with id: {} " +
                "and Audit with id: {}", safeguards.getIdentifier(), auditModel.getIdentifier());
        // If we don't create a Safeguard, we must go back in the tree. It can't
        // exists a Safeguard with children but without a value (every safeguard
        // with children with values must have a value)

        AuditModelSerializerDeserializer converter = new AuditModelSerializerDeserializer();
        Audit audit = converter.getAuditFromAuditModel(auditModel, true);

        ArrayList<Safeguard> updatedSafeguards = new ArrayList<>();
        for (Safeguard safeguard : safeguards.getSafeguards()) {
            // Here we check all safeguard from SafeguardModel with respect to
            // the Audit. If a safeguard from the audit has been updated, it is
            // updated in the safeguard model
            // The SafeguardModel encompasses all the Audit entries (they can
            // have a value or not)

            for (Questionnaire questionnaire : audit.getQuestionnaires()) {
                for (Question question : questionnaire.getQuestions()) {
                    if (checkSafeguardInQuestion(safeguard, question)) {
                        LOG.info("SafeguardModelManagerInput safeguard found {}", safeguard.getScore());
                    }
                }
            }
            updatedSafeguards.add(safeguard);
        }
        LOG.info("SafeguardModelManagerInput safeguard found {}", updatedSafeguards.size());
        safeguards.setSafeguards(updatedSafeguards);

        return safeguards;
    }

    private SafeguardModel createSafeguardsInAudit(SafeguardModel safeguards, SestAuditModel audit) {
        LOG.info("checkNewSafeguardsInAudit SafeguardModel identifier: {} audit identifier: {}",
                safeguards.getIdentifier(), audit.getIdentifier());
        for (SestQuestionnaireModel questionnaire : audit.getSestQuestionnaireModel()) {
            createSafeguardsInQuestionnaire(safeguards, questionnaire);
        }

        return safeguards;
    }

    private SafeguardModel createSafeguardsInQuestionnaire(SafeguardModel safeguards, SestQuestionnaireModel questionnaireJSON) {
        LOG.info("checkNewSafeguardInQuestionnaire SafeguardModel identifier: {} questionnaire with category",
                safeguards.getIdentifier());
        QuestionnaireModelSerializerDeserializer converter = new QuestionnaireModelSerializerDeserializer();
        Questionnaire questionnaire = converter.getQuestionnaireFromJSONString(questionnaireJSON.getQuestionnaireModelJson());
        for (Question question : questionnaire.getQuestions()) {

            safeguards.setSafeguards(createSafeguardInQuestion(safeguards.getSafeguards(), question));
        }
        return safeguards;
    }

    private ArrayList<Safeguard> createSafeguardInQuestion(ArrayList<Safeguard> safeguards, Question question) {

        LOG.info("checkNewSafeguardInQuestion question with category {}", question.getCategory());
        if (question.getType().equals(QuestionTypeEnum.CATEGORY)) {

            // We check if this question is not a Safeguard, but a Category of Safeguards
            if (question.getCategory().matches(REGEX_CATEGORY2)) {
                // This is a Safeguard
                Safeguard safeguard = null;
                for (Safeguard savedSafeguard : safeguards) {

                    if (savedSafeguard.getCatalogueId().equals(question.getCategory())) {
                        safeguard = savedSafeguard;
                        break;
                    }
                }

                if (safeguard == null) {
                    // The Safeguard is not in our SafeguardModel, so we may
                    // have to add it (if the Answers have a value)
                    safeguard = createNewSafeguard(question);
                    safeguards.add(safeguard);
                    modelUpdated = true;

                    LOG.info("question.getGasf().size() {}", question.getGasf().size());
                    ArrayList<SecurityRequirement> securityRequirements = new ArrayList<>();
                    if (question.getGasf() != null && !question.getGasf().isEmpty()) {
                        for (Question gasfSecRequirement : question.getGasf()) {
                            SecurityRequirement securityRequirement = createNewSecurityRequirement(gasfSecRequirement);
                            securityRequirements.add(securityRequirement);
                        }
                    }
                    safeguard.setRelatedSecurityRequirements(securityRequirements);
                } else {
                    LOG.info("question.getGasf().size() {}", question.getGasf().size());
                    ArrayList<SecurityRequirement> securityRequirements = new ArrayList<>();
                    if (question.getGasf() != null && !question.getGasf().isEmpty()) {
                        for (Question gasfSecRequirement : question.getGasf()) {

                            boolean found = false;
                            if (safeguard.getRelatedSecurityRequirements() != null && !safeguard.getRelatedSecurityRequirements().isEmpty()) {
                                for (SecurityRequirement secRequirement : safeguard.getRelatedSecurityRequirements()) {
                                    if (secRequirement.getId().equals(gasfSecRequirement.getCategory())) {
                                        securityRequirements.add(this.checkSecurityRequirement(gasfSecRequirement, secRequirement));
                                        found = true;
                                    }
                                }
                            }
                            if (!found) {
                                SecurityRequirement securityRequirement = createNewSecurityRequirement(gasfSecRequirement);
                                securityRequirements.add(securityRequirement);
                            }
                        }
                    }
                    safeguard.setRelatedSecurityRequirements(securityRequirements);
                }
            } else {
                for (Question innerQuestion : question.getChildren()) {
                    safeguards = createSafeguardInQuestion(safeguards, innerQuestion);
                }
            }
        }
        return safeguards;
    }

    private ArrayList<SecurityRequirement> createAndCheckSecurityRequirementInQuestion(
            ArrayList<SecurityRequirement> securityRequirements, Question question) {

        LOG.info("createAndCheckSecurityRequirementInQuestion {}", question.getCategory());

        boolean found = false;
        if (securityRequirements != null && !securityRequirements.isEmpty()) {
            for (SecurityRequirement securityRequirement : securityRequirements) {

                if (securityRequirement.getId().equals(question.getCategory())) {
                    checkSecurityRequirement(question, securityRequirement);
                    found = true;
                }
            }
        } else if (securityRequirements == null) {
            securityRequirements = new ArrayList<>();
        }

        if (!found) {
            // The SecurityRequirement is not in our SafeguardModel, so we may
            // have to add it
            SecurityRequirement securityRequirement = createNewSecurityRequirement(question);
            securityRequirements.add(securityRequirement);
            modelUpdated = true;

            // We need to check if the Question has children. In case,
            // we may add children to the SecurityRequirement
            if (question.getChildren() != null && !question.getChildren().isEmpty()) {


                ArrayList<SecurityRequirement> securityRequirementsChild = new ArrayList<>();
                for (Question innerQuestion : question.getChildren()) {
                    securityRequirementsChild = createAndCheckSecurityRequirementInQuestion(
                            securityRequirementsChild, innerQuestion);
                }
                securityRequirement.setChildren(securityRequirementsChild);
            }
        }
        return securityRequirements;
    }

    private Safeguard createNewSafeguard(Question question) {
        LOG.info("createNewSafeguard with category {}", question.getCategory());
        Safeguard safeguard = new Safeguard();

        safeguard.setUserDescription(question.getAnswers().get(AnswerTypeEnum.Comment));
        safeguard.setDescription(question.getAnswers().get(AnswerTypeEnum.Description));
        safeguard.setScore(getScoreValueSafeguard(question.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1)));

        UUID uuid = UUID.randomUUID();
        safeguard.setIdentifier(uuid.toString());

        safeguard.setCatalogue(SafeguardSourceEnum.MEHARI);
        safeguard.setCatalogueId(question.getCategory());
        safeguard.setName(question.getValue());
        safeguard.setElementType(ElementTypeEnum.Element);
        safeguard.setObjType(SESTObjectTypeEnum.SafeguardModel);
        safeguard.setPhase(PhaseEnum.Initial);

        LOG.info("createNewSafeguard question with category " + question.getCategory() + " Safeguard with identifier: "
                + safeguard.getIdentifier());

        return safeguard;
    }

    private SafeguardScoreEnum getScoreValueSafeguard(String value) {
        // If the Question has not been answered, we add a Safeguard
        // with LOW value
        if (value == null || value.equals("")) {
            return SafeguardScoreEnum.NONE;
        } else {
            int answerValue = Integer.parseInt(value);

            switch (answerValue) {
                case 1:
                    return SafeguardScoreEnum.LOW;
                case 2:
                    return SafeguardScoreEnum.MEDIUM;
                case 3:
                    return SafeguardScoreEnum.HIGH;
                case 4:
                    return SafeguardScoreEnum.VERY_HIGH;
                default:
                    return SafeguardScoreEnum.NONE;
            }
        }
    }

    private SafeguardScoreEnum getScoreValueSecurityRequirement(String answer) {
        // If the Question has not been answered, we add a Safeguard
        // with LOW value
        if (answer == null || answer.equals("") || Integer.parseInt(answer) != 1) {
            return SafeguardScoreEnum.NONE;
        } else {
            return SafeguardScoreEnum.LOW;
        }
    }

    private SecurityRequirement createNewSecurityRequirement(Question question) {
        SecurityRequirement secreq = new SecurityRequirement();

        Map<AnswerTypeEnum, String> answers = question.getAnswers();
        secreq.setScore(getScoreValueSecurityRequirement(answers.get(AnswerTypeEnum.MEHARI_R_V1)));
        secreq.setUserDescription(answers.get(AnswerTypeEnum.Comment));
        secreq.setDescription(answers.get(AnswerTypeEnum.Description));
        UUID uuid = UUID.randomUUID();
        secreq.setIdentifier(uuid.toString());

        secreq.setId(question.getCategory());
        secreq.setTitle(question.getValue());
        secreq.setElementType(ElementTypeEnum.Element);
        secreq.setObjType(SESTObjectTypeEnum.SafeguardModel);

        LOG.info("createNewSecurityRequirement question with category {}, SecurityRequirement with identifier:  {}, " +
                "score : {}", question.getCategory(), secreq.getIdentifier(), secreq.getScore());

        return secreq;
    }

    private SecurityRequirement checkSecurityRequirement(Question question, SecurityRequirement secreq) {

        String v1 = question.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);
        if (v1 != null && secreq.getScore() != null
                && !v1.equals(String.valueOf(secreq.getScore().getScore()))) {
            secreq.setScore(getScoreValueSecurityRequirement(v1));
            modelUpdated = true;
        }
        secreq.setUserDescription(question.getAnswers().get(AnswerTypeEnum.Comment));

        LOG.info("updateNewSecurityRequirement question with category {} SecurityRequirement with identifier: {}, score : {}",
                question.getCategory(), secreq.getIdentifier(), secreq.getScore());

        return secreq;
    }

    /*
     Checks if safeguard is the same as question and sets answers and other
     parameters. Returns true if found, false otherwise.
     */
    private boolean checkSafeguardInQuestion(Safeguard safeguard, Question question) {

        if (safeguard.getCatalogueId().equals(question.getCategory())) {

            LOG.info("checkSafeguardInQuestion Safeguard with id: {} question with category {}",
                    safeguard.getCatalogueId(), question.getCategory());

            String v1 = question.getAnswers().get(AnswerTypeEnum.MEHARI_R_V1);
            if (v1 != null) {
                SafeguardScoreEnum score = getScoreValueSafeguard(v1);
                if (!safeguard.getScore().equals(score)) {
                    safeguard.setScore(score);
                    LOG.info("checkSafeguardInQuestion set score {}", score);
                    modelUpdated = true;
                }
            }
            String comment = question.getAnswers().get(AnswerTypeEnum.Comment);
            if (comment != null && (safeguard.getUserDescription() == null ||
                    (safeguard.getUserDescription() != null && !safeguard.getUserDescription().equals(comment)))) {
                safeguard.setUserDescription(comment);
                modelUpdated = true;
            }
            safeguard.setName(question.getValue());

            // We need to check if the Question has children. In case,
            // we may add children to the Safeguard
            // #changed : the questions children are GASF question
            LOG.info("question.getChildren().size() " + question.getChildren().size());
            if (question.getGasf() != null && !question.getGasf().isEmpty()) {

                ArrayList<SecurityRequirement> securityRequirements = new ArrayList<>();
                for (Question innerQuestion : question.getGasf()) {
                    securityRequirements = createAndCheckSecurityRequirementInQuestion(
                            safeguard.getRelatedSecurityRequirements(), innerQuestion);
                }
                safeguard.setRelatedSecurityRequirements(securityRequirements);
            }

            LOG.info("checkSafeguardInQuestion return true ");
            return true;
        } else {
            for (Question innerQuestion : question.getChildren()) {

                if (checkSafeguardInQuestion(safeguard, innerQuestion))
                    return true;
            }
        }
        return false;
    }
}
