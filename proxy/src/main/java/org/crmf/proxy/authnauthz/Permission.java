/*
 * *********************************************************************
 *                                                                      *
 *  RHEA System S.A. CONFIDENTIAL                                       *
 *  ____________________________________________________________________*
 *                                                                      *
 *   Copyright (c) 2017-2018 RHEA System S.A.                           *
 *   All Rights Reserved.                                               *
 *                                                                      *
 *  NOTICE:  All information contained herein is, and remains           *
 *  the property of RHEA System S.A.. The intellectual and technical    *
 *  concepts contained herein are proprietary to RHEA System S.A.       *
 *  and are protected by trade secret or copyright law.                 *
 *  Dissemination of this information or reproduction of this material  *
 *  is strictly forbidden unless prior written permission is obtained   *
 *  from RHEA System S.A..                                              *
 *                                                                      *
 * *********************************************************************
 */
package org.crmf.proxy.authnauthz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
  String value();
}