/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="SestobjIdentifierGenerator.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency.domain.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SestobjIdentifierGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(SestobjIdentifierGenerator.class.getName());

  public static String newIdentifier() throws Exception {

    return UUID.randomUUID().toString();
  }

}
