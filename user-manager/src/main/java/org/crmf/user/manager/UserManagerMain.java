/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserManagerMain.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.user.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//This class represents the entry point of the OSGi bundle
public class UserManagerMain {

	private static final Logger LOG = LoggerFactory.getLogger(UserManagerMain.class.getName());
	
	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("config_spring.xml");
		
		while(!Thread.currentThread().isInterrupted()){
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOG.info("InterruptedException");
			}
		}

		((ClassPathXmlApplicationContext)context).close();
	}

}
