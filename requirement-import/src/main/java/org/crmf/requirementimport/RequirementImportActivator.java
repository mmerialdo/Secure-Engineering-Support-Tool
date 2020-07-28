/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RequirementImportActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.requirementimport;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-requirement-import bundle holds the business logic related to the import of project requirements
//as a difference with the common architecture of SEST, this components directly opens webservices to SEST Client without leveraging on the sest-proxy component
public class RequirementImportActivator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(RequirementImportActivator.class.getName());

	private volatile boolean running = true;

	public void start(BundleContext context) throws Exception {

		LOG.info("RequirementImportActivator [ Starting bundle ... ]");

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				while (running) {

					try {
						Thread.sleep(60000L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						LOG.info("InterruptedException");
					}
				}
			}
		} , 0);
		LOG.info("RequirementImportActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("RequirementImportActivator [ Stop ]");
		running = false;
	}

}
