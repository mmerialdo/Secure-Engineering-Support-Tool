/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ISOControlServiceInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.mapper.audit;

import org.crmf.model.audit.ISOControls;

public interface ISOControlServiceInterface {

  void createDefaultQuestionnaireWithISO(ISOControls controls);
}
