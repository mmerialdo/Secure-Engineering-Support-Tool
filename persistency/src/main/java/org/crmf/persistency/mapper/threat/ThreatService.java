/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.threat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.ibatis.session.SqlSession;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.riskassessment.ThreatModel;
import org.crmf.model.riskassessmentelements.Threat;
import org.crmf.model.utility.threatmodel.ThreatSerializerDeserializer;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.threat.SestThreat;
import org.crmf.persistency.domain.threat.SestThreatModel;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//This class manages the database interactions related to the ThreatModel
@Service
@Qualifier("default")
public class ThreatService implements ThreatServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(ThreatService.class.getName());
  public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";

  @Autowired
  private SqlSession sqlSession;

  @Override
  public void insert(String threatModelJson, String sestobjId) {
    LOG.info("Insert Threat Model");

    Sestobj sestobj = null;

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      //create a new sestObj with the identifier in input and type ThreatModel
      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.ThreatModel.name());
      sestobj.setIdentifier(sestobjId);
      sestobjMapper.insertWithIdentifier(sestobj);

      //use the Threat Mapper to insert the Threat Model
      SestThreatModel threatModel = new SestThreatModel();
      threatModel.setThreatModelJson(threatModelJson);
      threatModel.setSestobjId(sestobjId);
      threatMapper.insert(threatModel);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void update(String threatModelJson, String identifier) {
    LOG.info("updateQuestionnaireJSON Threat Model");

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      //use the Threat Mapper to insert the Threat Model
      threatMapper.update(threatModelJson, identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public SestThreatModel getByIdentifier(String sestobjId) {
    LOG.info("get By Identifier -  Threat Model");
    SestThreatModel threatModel;

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      //use the Threat Mapper to insert the Threat Model
      threatModel = threatMapper.getByIdentifier(sestobjId);
      LOG.info("get By Identifier -  Threat Model returned: " + threatModel);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return threatModel;
  }

  @Override
  public SestThreatModel getThreatRepository(String catalogue) {
    LOG.info("getThreatRepository -  Threat Model " + catalogue);
    SestThreatModel threatModel;

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      //use the Threat Mapper to load the Threat repository (in form of a list of Threats)
      List<SestThreat> threats = threatMapper.getThreatRepository(catalogue);

      threatModel = new SestThreatModel();
      JsonObject jsonObject = new JsonObject();
      JsonArray threatsJson = new JsonArray();

      GsonBuilder gsonBuilder = new GsonBuilder();
      Gson gson = gsonBuilder.create();


      for (SestThreat threat : threats) {

        JsonElement element = gson.fromJson(threat.getThreatJson(), JsonElement.class);
        threatsJson.add(element);

      }
      jsonObject.add("threats", threatsJson);
      jsonObject.addProperty("creationTime", "");
      jsonObject.addProperty("updateTime", "");
      jsonObject.addProperty("identifier", "");
      jsonObject.addProperty("objType", "ThreatModel");

      gson = gsonBuilder.setPrettyPrinting().disableHtmlEscaping().create();
      threatModel.setThreatModelJson(gson.toJson(jsonObject));
      LOG.info("getThreatRepository -  Threat Model returned: " + threatModel);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
    return threatModel;
  }

  @Override
  public boolean updateThreatRepository(ThreatModel tmToAdd, ThreatModel tmToUpdate) {
    LOG.info("updatethreatModelRepository");

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);

      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      for (org.crmf.model.riskassessmentelements.Threat newThreat : tmToAdd.getThreats()) {
        LOG.info("Insert sestObject");
        Sestobj sestobj = null;
        sestobj = new Sestobj();
        sestobj.setObjtype(SESTObjectTypeEnum.ThreatRef.name());
        UUID uuid = UUID.randomUUID();
        sestobj.setIdentifier(uuid.toString());
        newThreat.setIdentifier(uuid.toString());
        sestobjMapper.insertWithIdentifier(sestobj);


        LOG.info("Insert threat - sestObjectId " + uuid.toString());
        ThreatSerializerDeserializer threatSerializer = new ThreatSerializerDeserializer();
        String threatJson = threatSerializer.getJSONStringFromTM(newThreat);

        DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
        Date threatDate = df.parse(newThreat.getLastUpdate());
        SestThreat threat = new SestThreat();

        threat.setScore(newThreat.getScore().getScore().toString());
        threat.setLikelihood(newThreat.getScore().getLikelihood().toString());
        threat.setPhase(newThreat.getPhase().toString());
        threat.setName(newThreat.getName());
        threat.setThreatClass(newThreat.getThreatClass().toString());
        threat.setUpdateTime(threatDate);
        threat.setCatalogueId(newThreat.getCatalogueId());
        threat.setCatalogue(newThreat.getCatalogue().toString());
        threat.setThreatJson(threatJson);
        threat.setSestobjId(newThreat.getIdentifier());

        threatMapper.insertThreatRepository(threat);
      }

      for (org.crmf.model.riskassessmentelements.Threat newThreat : tmToUpdate.getThreats()) {

        LOG.info("Update threat - sestObjectId " + newThreat.getIdentifier());
        SestThreat sestThreat = new SestThreat();
        sestThreat.convertFromModel(newThreat);
        sestThreat.setUpdateTime(new Date());

        ThreatSerializerDeserializer threatSerializer = new ThreatSerializerDeserializer();
        String threatJson = threatSerializer.getJSONStringFromTM(newThreat);
        sestThreat.setThreatJson(threatJson);
        sestThreat.setUpdateTime(new Date());

        threatMapper.updateThreatRepository(sestThreat);
      }
      LOG.info("updateThreatRepository end");
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public Integer retrieveThreatReferenceId(String catalogueId) {
    LOG.info("retrieveThreatReferenceId begin");
    Integer result = null;

    try {
      //create a new Threat Mapper
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);

      result = threatMapper.retrieveThreatReferenceId(catalogueId);
      LOG.info("retrieveThreatReferenceId end");
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }

    return result;
  }

  @Override
  public String insertThreatReference(Threat threatModelJson) throws Exception {
    LOG.info("insert " + threatModelJson.getCatalogueId());
    SestThreat sestThreat = new SestThreat();
    try {
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);

      String alreadyExistingThreat = threatMapper.getThreatReferenceSestObjIdByCatalogueId(threatModelJson.getCatalogueId());
      if (alreadyExistingThreat != null) {
        LOG.error("Already existing threat with catalogue id : " + threatModelJson.getCatalogueId());
        throw new Exception("Already existing threat with catalogue id : " + threatModelJson.getCatalogueId());
      }

      sestThreat.convertFromModel(threatModelJson);
      sestThreat.setUpdateTime(new Date());

      Sestobj sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.ThreatRef.name());
      sestobj.setIdentifier(UUID.randomUUID().toString());
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);
      sestobjMapper.insertWithIdentifier(sestobj);
      sestThreat.setSestobjId(sestobj.getIdentifier());

      ThreatSerializerDeserializer threatSerializer = new ThreatSerializerDeserializer();
      threatModelJson.setIdentifier(sestobj.getIdentifier());
      threatModelJson.setObjType(SESTObjectTypeEnum.ThreatRef);
      DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
      threatModelJson.setLastUpdate(df.format(new Date()));
      String threatJson = threatSerializer.getJSONStringFromTM(threatModelJson);
      sestThreat.setThreatJson(threatJson);

      threatMapper.insertThreatRepository(sestThreat);
    } catch (Exception ex) {
      LOG.error("Unable to save threat " + ex.getMessage(), ex);
      throw ex;
    }
    return sestThreat.getSestobjId();
  }

  @Override
  public void deleteThreatReference(List<String> identifier) {
    LOG.info("delete " + identifier);
    try {
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      threatMapper.deleteThreatReference(identifier);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw ex;
    }
  }

  @Override
  public void editThreatReference(Threat threatModelJson) throws Exception {
    LOG.info("insert " + threatModelJson.getCatalogueId());
    try {
      ThreatMapper threatMapper = sqlSession.getMapper(ThreatMapper.class);
      SestThreat sestThreat = new SestThreat();
      sestThreat.convertFromModel(threatModelJson);
      sestThreat.setUpdateTime(new Date());

      String alreadyExistingThreat = threatMapper.getThreatReferenceSestObjIdByCatalogueId(threatModelJson.getCatalogueId());
      if (alreadyExistingThreat != null && !alreadyExistingThreat.equals(threatModelJson.getIdentifier())) {
        LOG.error("Already existing threat with catalogue id : " + threatModelJson.getCatalogueId());
        throw new Exception("Already existing threat with catalogue id : " + threatModelJson.getCatalogueId());
      }

      ThreatSerializerDeserializer threatSerializer = new ThreatSerializerDeserializer();
      DateFormat df = new SimpleDateFormat(DD_MM_YYYY_HH_MM);
      threatModelJson.setLastUpdate(df.format(new Date()));
      String threatJson = threatSerializer.getJSONStringFromTM(threatModelJson);
      sestThreat.setThreatJson(threatJson);

      threatMapper.updateThreatRepository(sestThreat);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      throw ex;
    }
  }
}
