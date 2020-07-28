/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SafeguardScoreEnum.java"
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

public enum SafeguardScoreEnum {
	NONE(0),
	LOW(1),
	MEDIUM(2),
	HIGH(3),
	VERY_HIGH(4);
	
	int score = 1;
	SafeguardScoreEnum(int score){
		this.score = score;
	}
	
	public int getScore(){
		return this.score;
	}
}