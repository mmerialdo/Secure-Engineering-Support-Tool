/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ISOControlService.java"
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

import org.crmf.model.audit.ISOControls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISOControlService implements ISOControlServiceInterface {

  private static final Logger LOG = LoggerFactory.getLogger(ISOControlService.class.getName());
  AssAuditDefaultService auditDefaultService;

  public void createDefaultQuestionnaireWithISO(ISOControls controls) {

    LOG.info("createDefaultQuestionnaire auditDefaultService " + auditDefaultService);
    auditDefaultService.setIsoControls(controls);
    auditDefaultService.createQuestionnaire();
  }

  public AssAuditDefaultService getAuditDefaultService() {
    return auditDefaultService;
  }

  public void setAuditDefaultService(AssAuditDefaultService auditDefaultService) {
    this.auditDefaultService = auditDefaultService;
  }
}
