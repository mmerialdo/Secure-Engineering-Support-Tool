package org.crmf.proxy.authnauthz;

import java.util.List;

/************************************************************************
 * Created: 08/09/2020                                                  *
 * Author: Gabriela Mihalachi                                        *
 ************************************************************************/
public class PermissionToken {
  String userId;
  String userProfile;
  List<String> read;
  List<String> update;
  List<String> create;
  List<String> view;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<String> getRead() {
    return read;
  }

  public void setRead(List<String> read) {
    this.read = read;
  }

  public List<String> getUpdate() {
    return update;
  }

  public void setUpdate(List<String> update) {
    this.update = update;
  }

  public List<String> getCreate() {
    return create;
  }

  public void setCreate(List<String> create) {
    this.create = create;
  }

  public List<String> getView() {
    return view;
  }

  public void setView(List<String> view) {
    this.view = view;
  }

  public String getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(String userProfile) {
    this.userProfile = userProfile;
  }
}
