/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CRMFModelActivator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//This class is needed in order to start the OSGi bundle
//The sest-crmf-model bundle holds all the SEST data model as POJO classes. It is mainly code-generated from the Enterprise Architect Class Diagrams
//Several 'utility' packages have been added, encompassing useful classes/methods for transforming/cloning/cloning/serializing SEST Data Model elements
public class CRMFModelActivator implements BundleActivator {

	private static final Logger LOG = LoggerFactory.getLogger(CRMFModelActivator.class.getName());

	private volatile boolean running = true;

	public void start(BundleContext context) throws Exception {

		LOG.info("CrmfModelActivator [ Starting bundle ... ]");
		
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
		LOG.info("CrmfModelActivator [ After timer schedule ... ]");
	}

	public void stop(BundleContext context) throws Exception {

		LOG.info("CrmfModelActivator [ Stop ]");
		running = false;
	}
}
