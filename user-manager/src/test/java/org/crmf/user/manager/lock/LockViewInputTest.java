/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="AuditModelSerializerDeserializer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/
package org.crmf.user.manager.lock;

import org.crmf.model.general.SESTObject;
import org.crmf.model.general.SESTObjectTypeEnum;
import org.crmf.model.user.PermissionTypeEnum;
import org.crmf.model.user.User;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.persistency.mapper.general.SestObjService;
import org.crmf.persistency.mapper.user.UserService;
import org.crmf.user.validation.permission.UserPermissionManagerInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class LockViewInputTest {

    @Mock
    private UserPermissionManagerInput permissionManager;
    @Mock
    private UserService userService;
    @Mock
    private SestObjService sestObjService;

    @InjectMocks
    private  LockViewInput manager;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void lockModelAvailable() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        map.put(GenericFilterEnum.USER, "userIdentifier");
        filter.setFilterMap(map);
        User user = new User();
        user.setIdentifier("userIdentifier");
        user.setUsername("username");
        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy(null);
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(userService.getByIdentifier("userIdentifier")).thenReturn(user);
        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssetModel, "projectIdentifier")).thenReturn(true);

        manager.lock(filter);

        verify(sestObjService, times(1)).updateLock("modelIdentifier", "username");
    }

    @Test
    public void lockModelUnavailableByPM() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        map.put(GenericFilterEnum.USER, "userIdentifier");
        filter.setFilterMap(map);
        User user = new User();
        user.setIdentifier("userIdentifier");
        user.setUsername("username");
        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy("actualLockingUser");
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(userService.getByIdentifier("userIdentifier")).thenReturn(user);
        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssetModel, "projectIdentifier")).thenReturn(true);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssessmentProject, "projectIdentifier")).thenReturn(true);

        manager.lock(filter);

        verify(sestObjService, times(1)).updateLock("modelIdentifier", "username");
    }

    @Test
    public void lockModelUnavailableByNotPM() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        map.put(GenericFilterEnum.USER, "userIdentifier");
        filter.setFilterMap(map);
        User user = new User();
        user.setIdentifier("userIdentifier");
        user.setUsername("username");
        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy("actualLockingUser");
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(userService.getByIdentifier("userIdentifier")).thenReturn(user);
        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssetModel, "projectIdentifier")).thenReturn(true);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssessmentProject, "projectIdentifier")).thenReturn(false);

        manager.lock(filter);

        verify(sestObjService, times(0)).updateLock("modelIdentifier", "username");
    }

    @Test
    public void unlockByPM() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        map.put(GenericFilterEnum.USER, "userIdentifier");
        filter.setFilterMap(map);
        User user = new User();
        user.setIdentifier("userIdentifier");
        user.setUsername("username");
        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy("actualLockingUser");
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(userService.getByIdentifier("userIdentifier")).thenReturn(user);
        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssessmentProject, "projectIdentifier")).thenReturn(true);

        manager.unlock(filter);

        verify(sestObjService, times(1)).updateLock("modelIdentifier", null);
    }

    @Test
    public void unlockByOwner() throws Exception {

        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        map.put(GenericFilterEnum.PROJECT, "projectIdentifier");
        map.put(GenericFilterEnum.USER, "userIdentifier");
        filter.setFilterMap(map);
        User user = new User();
        user.setIdentifier("userIdentifier");
        user.setUsername("username");
        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy("username");
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(userService.getByIdentifier("userIdentifier")).thenReturn(user);
        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);
        when(permissionManager.isProjectObjectTypeAllowed(
                "userIdentifier", PermissionTypeEnum.Update,
                SESTObjectTypeEnum.AssessmentProject, "projectIdentifier")).thenReturn(false);

        manager.unlock(filter);

        verify(sestObjService, times(1)).updateLock("modelIdentifier", null);
    }

    @Test
    public void getlock() {
        GenericFilter filter = new GenericFilter();
        Map<GenericFilterEnum, String> map = new HashMap();
        map.put(GenericFilterEnum.IDENTIFIER, "modelIdentifier");
        filter.setFilterMap(map);

        SESTObject sestObj = new SESTObject();
        sestObj.setIdentifier("modelIdentifier");
        sestObj.setLockedBy("userIdentifier");
        sestObj.setObjType(SESTObjectTypeEnum.AssetModel);

        when(sestObjService.getByIdentifier("modelIdentifier")).thenReturn(sestObj);

        String lockedBy = manager.getlock(filter);

        Assertions.assertEquals("userIdentifier", lockedBy);
    }
}
