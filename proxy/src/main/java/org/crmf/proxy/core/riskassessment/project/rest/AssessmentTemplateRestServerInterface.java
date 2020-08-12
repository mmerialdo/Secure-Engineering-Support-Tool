/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentTemplateRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.core.riskassessment.project.rest;

import org.apache.camel.Header;
import org.crmf.model.riskassessment.AssessmentProfile;
import org.crmf.model.riskassessment.AssessmentTemplate;
import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.List;

public interface AssessmentTemplateRestServerInterface {

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  String createAssessmentTemplate(AssessmentProfile Template) throws Exception;

  @GET
  @Produces("application/json")
  List<AssessmentTemplate> loadAssessmentTemplateList(@Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  List<String> loadAssessmentTemplate(GenericFilter filter, @Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

}