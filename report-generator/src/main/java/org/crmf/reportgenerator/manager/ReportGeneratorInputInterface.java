/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ReportGeneratorInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.reportgenerator.manager;

import org.crmf.model.riskassessmentelements.ImpactEnum;

public interface ReportGeneratorInputInterface {

  String editReport(String procedureId) throws Exception;

  String editLightReport(String procedureId, ImpactEnum threshold) throws Exception;

  String editISOReport(String procedureId) throws Exception;
}
