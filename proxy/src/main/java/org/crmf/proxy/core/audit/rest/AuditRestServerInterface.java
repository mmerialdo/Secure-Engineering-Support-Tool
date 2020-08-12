/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditRestServerInterface.java"
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

import org.apache.camel.Header;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.List;

public interface AuditRestServerInterface {

  @POST
  @Consumes("application/json")
  void editAudit(ModelObject audit) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  SestAuditModel loadAudit(GenericFilter filter, @Header("SHIRO_SECURITY_TOKEN") String token, @Header("filterList") String permission) throws Exception;

  @POST
  @Produces("text/html")
  @Consumes("text/html")
  ModelObject loadQuestionnaireJson(GenericFilter filter) throws Exception;

  @POST
  @Produces("application/json")
  List<Question> loadQuestionnaireSafeguard() throws Exception;

  @POST
  void createDefaultQuestionnaire();
}