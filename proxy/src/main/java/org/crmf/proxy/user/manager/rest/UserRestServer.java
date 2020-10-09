/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="UserRestServer.java"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
// 
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package. 
//  No part of the package, including this file, may be copied, modified, propagated, or distributed 
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

package org.crmf.proxy.user.manager.rest;

import com.google.gson.Gson;
import org.crmf.model.exception.RemoteComponentException;
import org.crmf.model.general.AuthToken;
import org.crmf.model.user.User;
import org.crmf.model.utility.GenericFilter;
import org.crmf.model.utility.GenericFilterEnum;
import org.crmf.proxy.authnauthz.Permission;
import org.crmf.proxy.configuration.ApiExceptionEnum;
import org.crmf.proxy.configuration.ResponseMessage;
import org.crmf.user.manager.core.UserManagerInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

//This class manages the business logic behind the webservices related to the User management

@RestController
@RequestMapping(value = "api/user")
public class UserRestServer {

    private static final Logger LOG = LoggerFactory.getLogger(UserRestServer.class.getName());
    @Autowired
    private UserManagerInput userInput;

    @PostMapping("create")
    @Permission(value = "User:Update")
    public ResponseMessage createUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                      @RequestBody User user) {
        try {
            LOG.info("createUser " + user);
            String identifier = userInput.createUser(user);

            if (identifier == null) {
                throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
            } else {
                return new ResponseMessage(identifier);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION, ex);
        }
    }

    @PostMapping("edit")
    @Permission(value = "User:Update")
    public void editUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                         @RequestBody User user) {
        LOG.info("editUser" + user);
        userInput.saveUser(user);
    }

    @PostMapping("editPassword")
    public void editUserPassword(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                 @RequestBody User user) {

        LOG.info("saveUser" + user);
        Gson gson = new Gson();
        String decryptedtoken = new String(
                Base64.getDecoder().decode(token.getBytes()),
                StandardCharsets.UTF_8);
        AuthToken securityToken = gson.fromJson(decryptedtoken, AuthToken.class);
        userInput.saveUserPassword(user, securityToken.getUsername());
    }

    @PostMapping("load")
    @Permission(value = "User:Read")
    public User loadUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                         @RequestBody GenericFilter filter) {
        LOG.info("retrieveUser " + filter);
        User result = userInput.retrieveUser(filter.getFilterValue(GenericFilterEnum.IDENTIFIER));

        if (result == null) {
            throw new RemoteComponentException(ApiExceptionEnum.COMMAND_EXCEPTION);
        } else {
            return result;
        }
    }

    @GetMapping("list")
    @Permission(value = "User:Read")
    public List<User> loadUserList(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token) {
        LOG.info("listUser");
        return userInput.listUser();
    }

    @PostMapping("delete")
    @Permission(value = "User:Update")
    public ResponseMessage deleteUser(@RequestParam(name = "SHIRO_SECURITY_TOKEN") String token,
                                      @RequestBody String identifier) {
        LOG.info("deleteUser" + identifier);
        userInput.deleteUser(identifier);

        return new ResponseMessage(identifier);
    }
}
