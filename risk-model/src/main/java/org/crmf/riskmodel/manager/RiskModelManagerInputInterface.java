/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="RiskModelManagerInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.riskmodel.manager;

import java.util.ArrayList;
import java.util.List;

import org.crmf.model.riskassessment.AssessmentProcedure;
import org.crmf.model.riskassessmentelements.RiskScenarioReference;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.ModelObject;

public interface RiskModelManagerInputInterface {

  String editRiskModel(String riskModel, String riskModelIdentifier);

  void editAssetModel(String assetModelIdentifier);

  void editSafeguardModel(String procedureIdentifier);

  ModelObject loadRiskModel(GenericFilter filter) throws Exception;

  String editRiskScenario(String riskModel, String riskModelIdentifier);

  ArrayList<RiskScenarioReference> getRiskScenarioReference();

  boolean updateScenarioRepository(ArrayList<RiskScenarioReference> rsr);

  void editRiskTreatmentModel(AssessmentProcedure procedure, boolean persist);

  void createAssessmentProcedure(AssessmentProcedure procedure);

  void updateScenarioRepository(String catalogue) throws Exception;

  String insertRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;

  void deleteRiskScenarioReference(List<String> identifier) throws Exception;

  void editRiskScenarioReference(RiskScenarioReference riskScenarioReference) throws Exception;
}
