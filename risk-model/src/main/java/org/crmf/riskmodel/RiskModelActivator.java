/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.riskmodel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

//This class is needed in order to start the OSGi bundle
//The sest-risk-model bundle holds the business logic related to the management of the risk assessment analysis and treatment. It is separated from the sest-core bundle in order to concentrate here
//the risk assessment logic which may depend on the selected risk assessment methodology
public class RiskModelActivator implements BundleActivator {

  private static final Logger LOG = LoggerFactory.getLogger(RiskModelActivator.class.getName());

  private volatile boolean running = true;

  public void start(BundleContext context) throws Exception {

    LOG.info("RiskModelActivator [ Starting bundle ... ]");

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {

      @Override
      public void run() {

        while (running) {
          LOG.debug("RiskModelActivator [ Running ... ]");

          try {
            Thread.sleep(60 * 1000L);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info("InterruptedException");
          }
        }
      }
    }, 1000);
    LOG.info("RiskModelActivator [ After timer schedule ... ]");
  }

  public void stop(BundleContext context) throws Exception {

    LOG.info("RiskModelActivator [ Stop ]");
    running = false;
  }

}
