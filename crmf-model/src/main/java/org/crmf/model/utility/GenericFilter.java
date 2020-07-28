/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="GenericFilter.java"
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

import java.util.Map;

//This class it is used in order to manage the content of the messages from/to the SEST Client
public class GenericFilter {

	private Map<GenericFilterEnum, String> filterMap;

	public String getFilterValue(GenericFilterEnum type) {
		
		return filterMap.get(type);
	}

	public Map<GenericFilterEnum, String> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Map<GenericFilterEnum, String> filterMap) {
		this.filterMap = filterMap;
	}
	
}
