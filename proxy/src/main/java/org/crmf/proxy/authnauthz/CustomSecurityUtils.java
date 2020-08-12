/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CustomSecurityUtils.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.authnauthz;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSecurityUtils {

  private static final Logger LOG = LoggerFactory.getLogger(CustomSecurityUtils.class.getName());

  public static CustomSecurityUtils setSecurityManagerMine(SecurityManager securityManager) {
    LOG.info("------------------- CustomSecurityUtils [ setSecurityManager ... ]");
    SecurityUtils.setSecurityManager(securityManager);
    return new CustomSecurityUtils();
  }
}
