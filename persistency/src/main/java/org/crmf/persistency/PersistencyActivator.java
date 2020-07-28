/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PersistencyActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-persistency bundle holds the business logic related to the management of the connection with the SEST database. It is invoked by the other bundles 
//It encompasses a set of classes (within the org.crmf.persistency.domain) mapping the SEST Java Data Model (within the sest-crmf-model bundle) to the SEST Database Data Model  
public class PersistencyActivator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(PersistencyActivator.class.getName());

	private volatile boolean running = true;

	public void start(BundleContext context) throws Exception {

		LOG.info("PersistencyActivator [ Starting bundle ... ]");

		String pathConfig = "config".concat(File.separator).concat("config.xml");
		URL url = context.getBundle().getResource(pathConfig);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				while (running) {
					LOG.debug("PersistencyActivator [ Running ... ]");

					try {
						Thread.sleep(60 * 1000L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						LOG.info("InterruptedException");
					}
				}
			}
		} , 1000);
		LOG.info("PersistencyActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("PersistencyActivator [ Stop ]");
		running = false;
	}
}
