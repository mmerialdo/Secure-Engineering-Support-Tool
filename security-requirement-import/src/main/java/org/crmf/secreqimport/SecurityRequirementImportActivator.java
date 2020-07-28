/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SecurityRequirementImportActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.secreqimport;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-security-requirement-import bundle holds the business logic related to the import of security requirements
//At the end of the SEST project this package only implemented the import of security requirements from the GASF tool via a REST API, but it can be extended in order to use also other sources
public class SecurityRequirementImportActivator implements BundleActivator {
	private static final Logger LOG = LoggerFactory.getLogger(SecurityRequirementImportActivator.class.getName());
	private volatile boolean running = true;
	
	public void start(BundleContext context) throws Exception {

		LOG.info("SecurityRequirementImportActivator [ Starting bundle ... ]");

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
		LOG.info("SecurityRequirementImportActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("SecurityRequirementImportActivator [ Stop ]");
		running = false;
	}

}
