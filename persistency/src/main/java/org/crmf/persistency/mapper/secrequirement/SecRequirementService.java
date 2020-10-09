/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecRequirementService.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.secrequirement;

import org.apache.ibatis.session.SqlSession;
import org.crmf.model.audit.ISOControls;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.requirement.SecurityRequirement;
import org.crmf.persistency.domain.audit.AssauditDefaultJSON;
import org.crmf.persistency.domain.general.Sestobj;
import org.crmf.persistency.domain.secrequirement.SecRequirement;
import org.crmf.persistency.domain.secrequirement.SecRequirementSafeguard;
import org.crmf.persistency.mapper.audit.AssAuditDefaultMapper;
import org.crmf.persistency.mapper.audit.AssAuditDefaultService;
import org.crmf.persistency.mapper.general.SestobjMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

//This class manages the database interactions related to the SecurityRequirement
@Service
@Qualifier("default")
public class SecRequirementService implements SecRequirementServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(SecRequirementService.class.getName());

  @Autowired
  private SqlSession sqlSession;
  @Autowired
  private AssAuditDefaultService auditDefaultService;

  @Override
  public void insertSecurityRequirement(SecurityRequirement secreq) {
    LOG.info("Insert SecurityRequirement {} ", secreq);
    SecRequirement secrequirement = new SecRequirement();
    secrequirement.convertFromModel(secreq);

    try {
      SecRequirementMapper requirementMapper = sqlSession.getMapper(SecRequirementMapper.class);
      SestobjMapper sestobjMapper = sqlSession.getMapper(SestobjMapper.class);

      if (sestobjMapper.getByIdentifier(secreq.getIdentifier()) == null) {
        LOG.info("Insert sestObject");
        Sestobj sestobj = new Sestobj();
        sestobj.setIdentifier(secreq.getIdentifier());
        sestobj.setObjtype(SESTObjectTypeEnum.Audit.name());
        sestobjMapper.insertWithIdentifier(sestobj);
      }

      secrequirement.setSestobjId(secreq.getIdentifier());
      requirementMapper.insert(secrequirement);
    } catch (Exception ex) {
      LOG.error(ex.getMessage());
    }
  }

  @Override
  public void insertSecRequirementParent(SecurityRequirement secreq) {
    LOG.info("insertSecRequirementRelated");

    try {
      SecRequirementMapper requirementMapper = sqlSession.getMapper(SecRequirementMapper.class);
      SecRequirement parent = requirementMapper.getSecRequirementByIdentifier(secreq.getParentId());
      SecRequirement securityReq = requirementMapper.getSecRequirementByIdentifier(secreq.getIdentifier());
      if (securityReq != null && parent != null) {
        securityReq.setParent(parent.getId());
        requirementMapper.update(securityReq);
      } else {
        LOG.error("Null requirements sec : " + secreq.getIdentifier() + ", parent : " + secreq.getParentId());
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
  }

  @Override
  public void insertSecRequirementSafeguard(List<String[]> values, ISOControls controls) {
    LOG.info("InsertSecRequirementAuditDefault");

    try {
      SecRequirementMapper requirementMapper = sqlSession.getMapper(SecRequirementMapper.class);
      AssAuditDefaultMapper auditDefaultMapper = sqlSession.getMapper(AssAuditDefaultMapper.class);

      for (String[] valueItem : values) {
        SecRequirement securityReq = requirementMapper.getSecRequirementByReqId(valueItem[0]);
        AssauditDefaultJSON safeguard = auditDefaultMapper.getByCategory(valueItem[1]);
        String contribution = valueItem[2];

        LOG.info("InsertSecRequirementAuditDefault " + securityReq + ", safeguard " + safeguard + ", contribution " + contribution);
        if (securityReq != null && safeguard != null && contribution != null) {
          SecRequirementSafeguard secRequirementSafeguard = new SecRequirementSafeguard();
          secRequirementSafeguard.setRequirementId(securityReq.getId());
          secRequirementSafeguard.setSafeguardId(safeguard.getId());
          secRequirementSafeguard.setContribution(Integer.valueOf(contribution));
          try {
            requirementMapper.insertSafeguardAssoc(secRequirementSafeguard);
          } catch (Exception constraintEx) {
            LOG.error("Exception " + constraintEx +
              "InsertSecRequirementAuditDefault securityReq " + securityReq + ", safeguard " + safeguard + ", contribution " + contribution, constraintEx);
          }
        } else {
          LOG.error("null values on InsertSecRequirementAuditDefault securityReq " + securityReq + ", safeguard " + safeguard + ", contribution " + contribution);
        }
      }

      auditDefaultService.setIsoControls(controls);
      auditDefaultService.createQuestionnaire();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
  }

  public void deleteSecRequirement() {
    LOG.info("deleteSecRequirement");

    try {
      SecRequirementMapper requirementMapper = sqlSession.getMapper(SecRequirementMapper.class);
      requirementMapper.deleteSecRequirementAssoc();
      requirementMapper.deleteSecRequirement();
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
  }
}
