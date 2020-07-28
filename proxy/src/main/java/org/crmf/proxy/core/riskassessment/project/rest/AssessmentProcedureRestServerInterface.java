/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AssessmentProcedureRestServerInterface.java"
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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;

import org.apache.camel.Header;
import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.GenericFilter;

public interface AssessmentProcedureRestServerInterface {

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  String createAssessmentProcedure(AssessmentProject procedurePrj) throws Exception;

  @POST
  @Consumes("application/json")
  void editAssessmentProcedure(AssessmentProcedure procedure) throws Exception;

  @DELETE
  @Consumes("text/html")
  @Produces("text/html")
  String deleteAssessmentProcedure(@Header("CamelHttpQuery") String identifier) throws Exception;

  @GET
  @Produces("application/json")
  List<AssessmentProcedure> loadAssessmentProcedureList(@Header("SHIRO_SECURITY_TOKEN") String token, @Header("filterList") String permission) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  List<String> loadAssessmentProcedure(GenericFilter filter) throws Exception;

}