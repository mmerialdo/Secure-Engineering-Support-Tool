/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="ISOControlInputInterface.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.secreqimport.manager.iso;

import org.crmf.model.audit.ISOControls;
import org.crmf.model.utility.audit.ISOControlsSerializerDeserializer;
import org.crmf.persistency.mapper.audit.ISOControlServiceInterface;
import org.crmf.secreqimport.manager.gasf.GASFInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ISOControlInputInterface {

  void importISOFromFile(String filename);
}
