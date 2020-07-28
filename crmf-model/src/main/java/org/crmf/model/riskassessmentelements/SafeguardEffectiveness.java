/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardEffectiveness.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.model.riskassessmentelements;

public enum SafeguardEffectiveness {
	LOW(1),
	MEDIUM(2),
	HIGH(3),
	VERY_HIGH(4);
	
	private final int numberValue;
	SafeguardEffectiveness(int numberValue){
		this.numberValue = numberValue;
	}
	
	public static SafeguardEffectiveness valueOf(int numberValue){
		if(numberValue == 0){
			return LOW;
		}
		
		for(SafeguardEffectiveness effectiveness: SafeguardEffectiveness.values())
			if(effectiveness.numberValue == numberValue)
				return effectiveness;
		
		return LOW;
	}
}
