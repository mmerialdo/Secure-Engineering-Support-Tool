/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirementProcessor.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport.processor;

import org.crmf.model.requirement.Requirement;
import org.crmf.model.requirement.RequirementStatusEnum;
import org.crmf.model.requirement.RequirementTypeEnum;
import org.crmf.model.riskassessmentelements.ElementTypeEnum;
import org.crmf.persistency.mapper.requirement.RequirementServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemRequirementProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(SystemRequirementProcessor.class.getName());
  private RequirementServiceInterface requirementService;
  private HashMap<String, List<String>> map = new HashMap<String, List<String>>();

  public void manageRequirement(SystemRequirement requirement, String sysprojectIdentifier, String filename) {
    List<String> reqFromFile = (List<String>) this.map.get(filename);
    if (reqFromFile == null) {
      reqFromFile = new ArrayList();
    }
    reqFromFile.add(requirement.getId());
    this.map.put(filename, reqFromFile);
    LOG.info("added to processed requirements");

    LOG.info("-----------------------insert requirement " + requirement);
    String reqId = requirement.getId();

    Requirement preq = new Requirement();
    preq.setElementType(ElementTypeEnum.Element);
    preq.setCategory(filename);
    preq.setSubCategory(parseSystemRequirementSubcategory(reqId));
    preq.setId(reqId);
    preq.setTitle(reqId);
    preq.setDescription(requirement.getDescription());
    preq.setNote(requirement.getNotes());
    preq.setTag(requirement.getSource());
    preq.setPriority(requirement.getPriority());
    preq.setVersion(requirement.getVersion());
    preq.setLastUpdate(requirement.getIssuechanged());
    preq.setStability(requirement.getStability());
    preq.setStatus(RequirementStatusEnum.Validated);
    if (requirement.getType() != null) {
      preq.setType(RequirementTypeEnum.valueOf(requirement.getType().trim()));
    }
    this.requirementService.insertSysRequirement(preq, sysprojectIdentifier);
    LOG.info("inserted");
  }

  private String parseSystemRequirementSubcategory(String reqId) {
    Pattern pSysReq = Pattern.compile(".*-([a-z A-Z]+)-.*");
    Pattern pSoftReq = Pattern.compile(".*\\.([a-z A-Z]+)\\.");
    Matcher mSysReq = pSysReq.matcher(reqId);
    Matcher mSoftReq = pSoftReq.matcher(reqId);

    String subcategory = "";
    if (mSysReq.find()) {
      subcategory = mSysReq.group(1);
      LOG.info("subcategory : " + subcategory);
    } else if (mSoftReq.find()) {
      subcategory = mSoftReq.group(1);
      LOG.info("subcategory : " + subcategory);
    }
    if (subcategory != null) {
      SystemRequirementTypeEnum type = SystemRequirementTypeEnum.valueOf(subcategory.trim());
      switch (type) {
        case EIF:
          return "External Interface";
        case FCN:
          return "Functional";
        case PER:
          return "Performance";
        case DEP:
          return "Dependability";
        case OPS:
          return "Operational";
        case SEC:
          return "Security";
        case INT:
          return "Integration and Test";
        case FU:
          return "Functional";
        case PE:
          return "Performance";
        case OP:
          return "Operational";
        case RE:
          return "Resource";
        case SE:
          return "Security";
        case AV:
          return "Availability";
        case MA:
          return "Maintenance";
        case CO:
          return "Delivery";
        case IM:
          return "Design";
        default:
          return "";
      }
    }
    return "";
  }

  public void cancelRequirement(String sysprojectIdentifier, String filename) {
    LOG.info("----------------------- cancelling requirement ");
    LOG.info("1.cancellingRequirement sysprojectIdentifier : " + sysprojectIdentifier);
    LOG.info("2.cancellingRequirement filename : " + filename);

    List<Requirement> requirements = this.requirementService.getBySysProjectAndFile(sysprojectIdentifier, filename);
    if (requirements != null) {
      List<String> reqIds = (List<String>) this.map.get(filename);
      LOG.info("cancellingRequirement req size : " + reqIds.size());
      if (reqIds != null) {
        for (Requirement projectRequirement : requirements) {
          if (!reqIds.contains(projectRequirement.getId())) {
            LOG.info("3.cancellingRequirement found : " + projectRequirement.getId());
            this.requirementService.deleteSysRequirement(projectRequirement);
          }
        }
        this.map.remove(filename);
      }
    }
  }

  public List<String> listRequirementLoadedFile(String sysprojectIdentifier) {
    LOG.info("listRequirementLoadedFile sysprojectIdentifier : " + sysprojectIdentifier);
    return requirementService.getFilenameByProject(sysprojectIdentifier);
  }

  public RequirementServiceInterface getRequirementService() {
    return this.requirementService;
  }

  public void setRequirementService(RequirementServiceInterface requirementService) {
    this.requirementService = requirementService;
  }
}
