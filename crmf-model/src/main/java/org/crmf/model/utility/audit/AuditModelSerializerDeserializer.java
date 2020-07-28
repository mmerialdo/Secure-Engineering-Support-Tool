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

package org.crmf.model.utility.audit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.crmf.model.audit.Audit;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Questionnaire;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.utility.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class manages the custom serialization/deserialization of the Audit
//since the DataModel is different from the Json schema (due to recursive dependencies), it must be serialized/deserialized using a custom class extending the capabilities of the Gson API
public class AuditModelSerializerDeserializer {
  private static final Logger LOG = LoggerFactory.getLogger(AuditModelSerializerDeserializer.class.getName());

  public Audit getAuditFromClientModel(ModelObject modelObj) {

    LOG.info("----------- serializer getAuditFromClientModel!!!!!");

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Audit.class, new AuditInstanceCreator());
    gsonBuilder.serializeNulls();

    Gson gson = gsonBuilder.create();

    String json = modelObj.getJsonModel();
    Audit audit = null;

    try {
      audit = gson.fromJson(json, Audit.class);
      if (audit == null) {
        LOG.info("Conversion of json into Audit returned null");
      } else {
        audit.setIdentifier(modelObj.getObjectIdentifier());
      }
    } catch (Exception e) {
      LOG.error("Deserialization error: " + e.getMessage(), e);
      return null;
    }

    return audit;
  }

  public SestAuditModel getAuditModelFromAudit(Audit audit) {

    SestAuditModel auditModel = new SestAuditModel();
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    Gson gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
    auditModel.setIdentifier(audit.getIdentifier());
    auditModel.setType((audit.getType() == null) ? AuditTypeEnum.SECURITY : audit.getType());
    auditModel.setObjType(SESTObjectTypeEnum.Audit);
    if (audit.getQuestionnaires() != null) {
      audit.getQuestionnaires().forEach(questionnaire -> {
        SestQuestionnaireModel questionnaireModel = new SestQuestionnaireModel();
        questionnaireModel.setIdentifier(questionnaire.getIdentifier());
        questionnaireModel.setCategory(questionnaire.getCategory());
        questionnaireModel.setIx(questionnaire.getIndex());
        questionnaireModel.setType(questionnaire.getType());
        questionnaireModel.setIdentifier(questionnaire.getIdentifier());
        questionnaireModel.setQuestionnaireModelJson(gson.toJson(questionnaire));
        auditModel.getSestQuestionnaireModel().add(questionnaireModel);
      });
    }
    return auditModel;
  }

  public Audit getAuditFromAuditModel(SestAuditModel auditModel, boolean includeQuestionnaires) {
    Audit audit = new Audit();

    try {
      audit.setType(auditModel.getType());
      audit.setIdentifier(auditModel.getIdentifier());
      audit.setObjType(auditModel.getObjType());
      if (includeQuestionnaires) {
        QuestionnaireModelSerializerDeserializer questConverter = new QuestionnaireModelSerializerDeserializer();
        auditModel.getSestQuestionnaireModel().forEach(questionnaireModel -> {
            Questionnaire questionnaire = questConverter.getQuestionnaireFromJSONString(questionnaireModel.getQuestionnaireModelJson());
            audit.getQuestionnaires().add(questionnaire);
          }
        );
      }
    } catch (Exception e) {
      LOG.info("Audit Serialization/Deserialization error:: " + e.getMessage());
      return null;
    }

    return audit;
  }
}
