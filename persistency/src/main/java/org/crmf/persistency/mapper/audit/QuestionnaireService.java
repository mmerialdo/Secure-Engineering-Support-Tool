/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="QuestionnaireService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

//This class manages the database interactions related to the QuestionnaireJSON
@Service
@Qualifier("default")
public class QuestionnaireService implements QuestionnaireServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(QuestionnaireService.class.getName());

  @Autowired
  private SqlSession sqlSession;

  public Integer insert(SestQuestionnaireModel questionnaireModel, Integer auditId) {

    LOG.info("Insert Questionnaire");
    Sestobj sestobj = null;

    try {
      QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      LOG.info("Insert sestObject");
      sestobj = new Sestobj();
      sestobj.setObjtype(SESTObjectTypeEnum.Audit.name());
      sestobjMapper.insert(sestobj);

      questionnaireModel.setIdentifier(sestobj.getIdentifier());
      questionnaireMapper.insert(questionnaireModel);
      return questionnaireModel.getId();
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
      return null;
    }
  }

  public void deleteCascade(String identifier) {

    LOG.info("called deleteCascade");
    QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
    questionnaireMapper.delete(identifier);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface#
   * getByIdentifier(java.lang.String)
   */
  @Override
  public SestQuestionnaireModel getByIdentifier(String identifier) {

    LOG.info("called getByIdentifier |" + identifier + "|");
    QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
    return questionnaireMapper.getByIdentifier(identifier);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.crmf.persistency.mapper.audit.QuestionnaireServiceInterface#
   * getByIdentifier(java.lang.String)
   */
  @Override
  public List<SestQuestionnaireModel> getAllQuestionnaireNames(String auditIdentifier) {

    LOG.info("called getAllQuestionnaireNames " + auditIdentifier);
    QuestionnaireMapper questionnaireMapper = sqlSession.getMapper(QuestionnaireMapper.class);
    AssAuditMapper auditMapper = sqlSession.getMapper(AssAuditMapper.class);
    LOG.info("getAllQuestionnaireNames");
    LOG.info(auditMapper.toString());
    int auditId = auditMapper.getIdByIdentifier(auditIdentifier);
    return questionnaireMapper.getAllQuestionnaireNames(auditId);
  }
}
