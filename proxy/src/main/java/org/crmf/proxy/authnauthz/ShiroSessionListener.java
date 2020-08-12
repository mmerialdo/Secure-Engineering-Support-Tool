/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ShiroSessionListener.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.authnauthz;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This listener works with websockets in order to notify SEST Clients when the current Session is expired
public class ShiroSessionListener implements SessionListener {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroSecurityPolicyCustom.class.getName());

  @Produce(uri = "direct:sendSession")
  private ProducerTemplate sender;

  @Override
  public void onExpiration(Session session) {
    LOG.info("On EXPIRATION " + session.getId());
    LOG.info("On EXPIRATION " + sender);
    //  sender.requestBody("{\"sessionId\":\"" + session.getId() + "\",\"status\":\"EXPIRED\"}");
  }

  @Override
  public void onStart(Session session) {
    LOG.info("On START " + session.getId());
    //sender.sendMessage("{\"sessionId\":\""+session.getId()+"\",\"status\":\"STARTED\"}");
  }

  @Override
  public void onStop(Session session) {
    LOG.info("On STOP " + session.getId());
    //sender.sendMessage("{\"sessionId\":\""+session.getId()+"\",\"status\":\"STOPPED\"}");
  }
}
