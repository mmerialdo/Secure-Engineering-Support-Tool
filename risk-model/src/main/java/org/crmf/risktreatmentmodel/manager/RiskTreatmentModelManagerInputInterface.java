/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskTreatmentModelManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.risktreatmentmodel.manager;

import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

public interface RiskTreatmentModelManagerInputInterface {

  String editRiskTreatmentModel(String riskTreatmentModel, String riskTreatmentModelIdentifier);

  String editRiskTreatmentModelDetail(String riskTreatmentModel, String riskTreatmentModelIdentifier);

  ModelObject loadRiskTreatmentModel(GenericFilter filter) throws Exception;

  String loadRiskTreatmentModelDetail(GenericFilter filter) throws Exception;

  String calculateRiskTreatmentModel(String riskTreatmentModel, String riskTreatmentModelIdentifier);

  String calculateRiskTreatmentModelDetail(String riskTreatmentModel, String riskTreatmentModelIdentifier);
}
