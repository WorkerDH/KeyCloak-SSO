package com.dh.keycloak.fedaration.postgres.model;/**
 * @author EDY
 * @create 2022/7/28 10:51
 */

import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;


/**
 *@author EDY
 *@create 2022/7/28 10:51
 */
public class UserData extends AbstractUserAdapterFederatedStorage implements  UserModel{
    private  static final Logger logger=Logger.getLogger(UserData.class);
    private final ComponentModel model;//???
    public UserData(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel) {
        super(session, realm, storageProviderModel);
        this.model = storageProviderModel;
    }
    String userId;
    String username;
    String password;
    String roleName;
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String s) {
        this.username=s;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public boolean validatePassword(String password){
        return this.password.equals(password);
    }
    public UserModel getUserModel(RealmModel realmModel){
        UserModel userModel=session.userLocalStorage().getUserByUsername(this.getUsername(),realmModel);
        if (userModel==null){
            logger.info("create local usermodel ,name:"+this.getUsername());
            userModel=session.userLocalStorage().addUser(realmModel,this.getUsername());
            userModel.setFederationLink(model.getId());
            userModel.setEnabled(true);
            userModel.setUsername(this.getUsername());
            return userModel;
        }else{
            return null;
        }

    }
}
