/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="JSONConverter.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.utility;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.ProxyBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JSONConverter {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("spring/config_spring.xml");

		ObjectBuilder proxy = new ProxyBuilder((CamelContext)context.getBean("converter-context")).endpoint("direct:convertGenericFilter").build(ObjectBuilder.class);
		proxy.getObject();

		((ClassPathXmlApplicationContext)context).close();
	}

}
