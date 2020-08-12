/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProfileRestServerInterface.java"
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
import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.List;

public interface AssessmentProfileRestServerInterface {

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  String createAssessmentProfile(AssessmentProfile profile) throws Exception;

  @POST
  @Consumes("application/json")
  void editAssessmentProfile(AssessmentProfile profile) throws Exception;

  @DELETE
  @Consumes("text/html")
  @Produces("text/html")
  String deleteAssessmentProfile(String identifier) throws Exception;

  @GET
  @Produces("application/json")
  List<AssessmentProfile> loadAssessmentProfileList(@Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  AssessmentProfile loadAssessmentProfile(GenericFilter filter, @Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

}