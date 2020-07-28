/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CoreActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-core bundle holds the business logic related to the management of the Models related to the Risk Assessment, with the exception of the 
//Risk computation, managed by the sest-risk-model bundle (this is due to the fact that SEST should be able to be adapted to different RA methodologies, and the RA specific logics are contained almost only
//in the sest-risk-model bundle
public class CoreActivator implements BundleActivator
{
	private static final Logger LOG = LoggerFactory.getLogger(CoreActivator.class.getName());

	private volatile boolean running = true;

	public void start(BundleContext context) throws Exception {

		LOG.info("CoreActivator [ Starting bundle ... ]");

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				while (running) {

					try {
						Thread.sleep(60 * 1000L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						LOG.info("InterruptedException");
					}
				}
			}
		} , 1000);
		LOG.info("CoreActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("CoreActivator [ Stop ]");
		running = false;
	}
}
