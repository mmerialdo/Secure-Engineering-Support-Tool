/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.audit.rest;

import org.crmf.core.audit.AuditInputInterface;
import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.audit.SestQuestionnaireModel;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.model.utility.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//This class manages the business logic behind the webservices related to the Audits management
public class AuditRestServer implements AuditRestServerInterface {

	private static final Logger LOG = LoggerFactory.getLogger(AuditRestServer.class.getName());
	
	private AuditInputInterface auditInput;

	@Override
	public void editAudit(ModelObject audit) throws Exception {
		try{
			auditInput.editAudit(audit);
		} catch (Exception e) {
			LOG.error("editAuditList " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}
	
	@Override
	public SestAuditModel loadAudit(GenericFilter filter, String token, String permission) throws Exception {

		try{
			return auditInput.loadAudit(filter.getFilterValue(GenericFilterEnum.IDENTIFIER), AuditTypeEnum.SECURITY, false);
		} catch (Exception e) {
			LOG.error("loadAudit " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public List<Question> loadQuestionnaireSafeguard() throws Exception {
		try{
			return auditInput.loadQuestionnaireSafeguard();
		} catch (Exception e) {
			LOG.error("loadQuestionnaire " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public ModelObject loadQuestionnaireJson(GenericFilter filter) throws Exception {
		try{
			return auditInput.loadQuestionnaire(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));
		} catch (Exception e) {
			LOG.error("loadQuestionnaireJson " + e.getMessage());
			throw new Exception("COMMAND_EXCEPTION", e);
		}
	}

	@Override
	public void createDefaultQuestionnaire() {
		auditInput.createDefaultQuestionnaire();
	}

  public AuditInputInterface getAuditInput() {
    return auditInput;
  }

  public void setAuditInput(AuditInputInterface auditInput) {
    this.auditInput = auditInput;
  }
}
