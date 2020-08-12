/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserAuthenticationRestServerInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.user.auth.rest;

import org.apache.camel.Header;
import org.apache.camel.Headers;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.Map;

public interface UserAuthenticationRestServerInterface {

  @POST
  @Produces("text/plain")
  String authenticateUser(@Header("SHIRO_SECURITY_TOKEN") String header) throws Exception;


  @POST
  @Produces("text/plain")
  void logoutUser(@Header("SHIRO_SECURITY_TOKEN") String token) throws Exception;

  @POST
  @Produces("text/plain")
  String getPermissionList(@Headers Map<String, Object> headers);
}
