/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="CustomSecurityRealm.java"
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

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.crmf.user.manager.authentication.UserAuthenticationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//Implementation of AuthrizingRealm, used to perform authentication getting credentials from persistency bundle (db).
public class CustomSecurityRealm extends AuthorizingRealm {

	private UserAuthenticationInterface userAuthentication;

	protected boolean permissionsLookupEnabled = false;

	private static final Logger LOG = LoggerFactory.getLogger(CustomSecurityRealm.class);

	public CustomSecurityRealm() {
		super();
		LOG.info("CustomSecurityRealm constructor!!!");
	}

	/*
	 Method used to get credentials (password for username).
	 This method overrides the behavior, using persistency bundle to retrieve password from db not form ini file.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

		LOG.info("---------------- doGetAuthenticationInfo ---------------------");
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();
		LOG.info("Authenticating username : " + username);

		// Null username is invalid
		if (username == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}

		String password = getPassword(username);

		if (password == null) {
			throw new UnknownAccountException("No account found for user [" + username + "]");
		}

		SimplePrincipalCollection principals = new SimplePrincipalCollection();
		principals.add(username, getName());

		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(principals, password.toCharArray(),
				getName());
		return authenticationInfo;
	}

	public String getPassword(String username) {
		String password = userAuthentication.getPasswordForUser(username);
		return password;
	}

	/*
	 Never called because ShiroSecurityProcessor was overrided. 
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		LOG.info("---------------- doGetAuthorizationInfo ---------------------");
		LOG.info("Not needed because ShiroSecurityProcessorCustom manages authorization");
		return null;
	}

	public UserAuthenticationInterface getUserAuthentication() {
		return userAuthentication;
	}

	public void setUserAuthentication(UserAuthenticationInterface userAuthentication) {
		this.userAuthentication = userAuthentication;
	}

}
