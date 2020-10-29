/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="GenericFilterBuilder.java"
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

import java.util.HashMap;

//This class creates a mock-up GenericFilter for testing reasons
public class GenericFilterBuilder implements ObjectBuilder {

	@Override
	public Object getObject() {
		
		GenericFilter filter = new GenericFilter();
		HashMap<GenericFilterEnum, String> filterMap = new HashMap<>();
		filterMap.put(GenericFilterEnum.IDENTIFIER, "1");
		filter.setFilterMap(filterMap);
		return filter;
	}

	@Override
	public void printGson(String json) {
		// TODO Auto-generated method stub
	}

}
