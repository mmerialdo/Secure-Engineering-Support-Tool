/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.core.audit;

import org.crmf.model.audit.AuditTypeEnum;
import org.crmf.model.audit.Question;
import org.crmf.model.audit.SestAuditModel;
import org.crmf.model.riskassessment.AssessmentProject;
import org.crmf.model.utility.ModelObject;

import java.util.List;

public interface AuditInputInterface {

	void editAudit(ModelObject auditJson) ;

	SestAuditModel loadAudit(String projectIdentifier, AuditTypeEnum type, boolean includeModels);

	ModelObject loadQuestionnaire(String identifier);

	List<Question> loadQuestionnaireSafeguard();

	void editSafeguardModel(AssessmentProject project);

	void createDefaultQuestionnaire();
}
