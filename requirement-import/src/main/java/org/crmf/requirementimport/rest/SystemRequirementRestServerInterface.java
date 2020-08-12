/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SystemRequirementRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport.rest;

import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface SystemRequirementRestServerInterface {

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  void uploadRequirement(@FormParam("file") String file,
                         @FormParam("sysprojectIdentifier") String sysprojectIdentifier) throws Exception;

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  List<String> listRequirementLoadedFile(GenericFilter filter);

}