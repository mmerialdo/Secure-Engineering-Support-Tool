package org.crmf.model.general;

/************************************************************************
 * Created: 10/09/2020                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class AuthToken {

  private String username;
  private String password;
  private String project;

  public AuthToken(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getProject() {
    return project;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setProject(String project) {
    this.project = project;
  }
}
