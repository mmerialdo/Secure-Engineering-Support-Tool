/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="PersistencyMain.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.persistency;

import org.crmf.persistency.mapper.user.UserService;
import org.crmf.persistency.session.PersistencySessionFactory;

//This class represents the entry point of the OSGi bundle
public class PersistencyMain {

  public static void main(String[] args) {

    PersistencySessionFactory sessionFactory = new PersistencySessionFactory();
    sessionFactory.createSessionFactory();

    UserService userService = new UserService();
    userService.setSessionFactory(sessionFactory);

    userService.getAll();

  }
}
