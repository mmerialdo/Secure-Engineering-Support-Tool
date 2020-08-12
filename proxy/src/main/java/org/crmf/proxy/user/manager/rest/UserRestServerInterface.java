/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.user.manager.rest;

import org.apache.camel.Header;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.crmf.model.user.User;
import org.crmf.model.utility.GenericFilter;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.List;

//import javax.ws.rs.Path;

@CrossOriginResourceSharing(allowAllOrigins = true)
public interface UserRestServerInterface {

  @GET
  @Produces("application/json")
  List<User> loadUserList(@Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

  @POST
  @Produces("text/html")
  @Consumes("application/json")
  String createUser(User user) throws Exception;

  @POST
  @Consumes("application/json")
  void editUser(User user) throws Exception;

  @POST
  @Consumes("application/json")
  void editUserPassword(User user, @Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

  @POST
  @Produces("application/json")
  @Consumes("text/html")
  User loadUser(GenericFilter filter) throws Exception;

  @DELETE
  @Produces("text/html")
  @Consumes("text/html")
  String deleteUser(String identifier) throws Exception;

}
