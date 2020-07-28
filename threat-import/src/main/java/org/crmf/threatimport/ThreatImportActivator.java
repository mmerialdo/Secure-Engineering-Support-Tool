/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ThreatImportActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.threatimport;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-threat-import bundle holds the business logic related to the import of threat taxonomies
//At the end of the SEST project, the implementation was encompassing only a file-based import 
public class ThreatImportActivator implements BundleActivator {

private static final Logger LOG = LoggerFactory.getLogger(ThreatImportActivator.class.getName());
	
	private volatile boolean running = true;

	public void start(BundleContext context) throws Exception {

		LOG.info("ThreatImportActivator [ Starting bundle ... ]");

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				while (running) {
					LOG.debug("ThreatImportActivator [ Running ... ]");

					try {
						Thread.sleep(60 * 1000L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						LOG.info("InterruptedException");
					}
				}
			}
		} , 1000);
		LOG.info("ThreatImportActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("ThreatImportActivator [ Stop ]");
		running = false;
	}


}
