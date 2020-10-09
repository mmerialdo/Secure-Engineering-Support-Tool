package org.crmf.proxy.authnauthz;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/************************************************************************
 * Created: 08/09/2020                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
@Configuration
public class ShiroConfig {

  @Value("${session.timeout}")
  private long timeout;
  @Value("${session.validation.interval}")
  private long interval;

  /**
   * Create defaultwebseriarymanager
   */
  @Bean(name = "securityManager")
  public DefaultSecurityManager getDefaultWebSecurityManager(@Qualifier("customSecurityRealmBean") CustomSecurityRealm userRealm) {
    DefaultSecurityManager securityManager = new DefaultSecurityManager();
    DefaultSessionManager sessionManager = new DefaultSessionManager();
    sessionManager.setGlobalSessionTimeout(timeout);
    sessionManager.setSessionValidationInterval(interval);

    securityManager.setRealm(userRealm);
    securityManager.setSessionManager(sessionManager);
    return securityManager;
  }

  /**
   * Create Rrealm
   */
  @Bean(name = "customSecurityRealmBean")
  public CustomSecurityRealm getRealm(){
    return new CustomSecurityRealm();
  }
}