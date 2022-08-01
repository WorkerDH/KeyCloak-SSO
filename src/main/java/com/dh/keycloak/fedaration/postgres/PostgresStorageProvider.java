package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/22 15:38
 */

import com.dh.keycloak.fedaration.postgres.model.User;
import com.dh.keycloak.fedaration.postgres.model.UserData;
import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.*;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;


import javax.management.relation.Role;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author EDY
 * @create 2022/7/22 15:38
 */
public class PostgresStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        CredentialInputUpdater, UserRegistrationProvider {
    protected KeycloakSession session;
    protected Properties properties;
    protected ComponentModel model;
    // map of loaded users in this transaction
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public PostgresStorageProvider(KeycloakSession session, ComponentModel model, Properties properties) {
        this.session = session;
        this.model = model;
       // this.properties = properties;
    }

    @Override
    public boolean updateCredential(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        logger.info("updateCredential"+input);

        logger.info("password:->"+userModel.getAttribute("Password"));
        //        ​if (input.getType().equals(PasswordCredentialModel.TYPE))
//            throw new ReadOnlyException("user is read only for this update");
//        if (input.getType().equals(PasswordCredentialModel.TYPE))
//            throw new ReadOnlyException("user is read only for this update");
        //logger.info("updateCredential"+realmModel);
        if (!(input instanceof UserCredentialModel)) return false;
        if (!input.getType().equals(CredentialModel.PASSWORD))return false;
        UserCredentialModel cred= (UserCredentialModel) input;
        save(new Object());
//       ​synchronized (properties) {
//           ​properties.setProperty(user.getUsername(), cred.getValue());
//           ​save();
//       ​}
        return false;
    }

    @Override
    public void disableCredentialType(RealmModel realmModel, UserModel userModel, String s) {

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realmModel, UserModel userModel) {
        return Collections.EMPTY_SET;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {

        logger.info("supportsCredentialType:"+credentialType+";result="+credentialType.equals(PasswordCredentialModel.TYPE));
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        logger.info("isConfiguredFor:"+credentialType);
        String password = properties.getProperty(userModel.getUsername());
        return credentialType.equals(PasswordCredentialModel.TYPE) && password != null;
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        logger.info(" isValid realmodel"+realmModel.getName()+"usemodel:"+userModel.getUsername()+";input:"+input.getChallengeResponse());
        UserServices userServices=new UserServices();

        if (userModel.getUsername()!=null&&input!=null){
            //UserModel localUser=session.userLocalStorage().getUserByUsername(userModel.getUsername(),realmModel);
            logger.info("localuser:"+userModel.getUsername());
            //User user=userServices.getUserByName(userModel.getUsername());
            try {
                User user=userServices.getUserByName(userModel.getUsername());
                if (user==null){
                    return false;
                }
                logger.info("user:"+user);
                return user.getPassword().equals(input.getChallengeResponse());

            } catch (Exception e) {
                // e.printStackTrace();
                logger.info("exception:"+e);
            }


        }
        logger.info("end return true:");
        return false;
//        if (!supportsCredentialType(input.getType())) return false;
//        String password = properties.getProperty(userModel.getUsername());
//        if (password == null) return false;
//        return password.equals(input.getChallengeResponse());
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String userId, RealmModel realmModel) {
        logger.info("getUserById："+userId);
        StorageId storageId = new StorageId(userId);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realmModel);
    }
    UserServices userServices=new UserServices();

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.info("getUserByUsername：4.0:"+username);
        logger.info("getUserByUsername：4.1:");
        User user=null;
        UserData adapter= (UserData) loadedUsers.get(username);
        if (adapter == null) {

            try {
                user=userServices.getUserByName(username);
            }   catch (Exception e) {
               logger.info("userServices.getUserByName error:"+e);
            }
            //String password = properties.getProperty(username);//再走数据库
            logger.info("getUserByUsername：4.2:");
            if (user != null) {
                logger.info("getUserByUsername：4.3:");
                adapter = new UserData(this.session,realmModel,this.model);
                adapter.setUsername(user.getUsername());
                adapter.setUserId(user.getId());
                adapter.setPassword(user.getPassword());
                adapter.setEnabled(true);
                adapter.setFederationLink(model.getId());

               // userData=new UserData(this.session,realmModel,this.model);
//                userData.setUserId(user.getId());
//                userData.setPassword(user.getPassword());
//                userData.setUsername(user.getUse rname());
//                userData.setRoleName(user.getRoleName());
                loadedUsers.put(username, adapter);
            }

        }else{
            logger.info("already has userModel->!!!");
            return adapter;
        }
        Set<RoleModel> roles=new HashSet<>();

        Iterator rolesItr=realmModel.getRoles().iterator();
        boolean flag=true;
        Iterator userRoles=adapter.getRoleMappings().iterator();
        while (userRoles.hasNext()){
            RoleModel roleModel= (RoleModel) userRoles.next();
            if (roleModel.getName().equals(user.getRoleName())){
                flag=false;
            }
        }
        while (rolesItr.hasNext()&&flag){
            RoleModel roleModel=(RoleModel)rolesItr.next();
            logger.info("roles info:"+roleModel.getName()+",user role info:"+user.getRoleName());
            if (roleModel.getName().equals(user.getRoleName())){
                logger.info("set role info:"+roleModel.getName());
                adapter.grantRole(roleModel);
            }
        }



//        Iterator realmRolesItr=realmModel.getRoles().iterator();
//        while (realmRolesItr.hasNext()){
//            logger.info("roles info:"+((RoleModel)realmRolesItr.next()).getName());
//        }
//        RoleModel roleModel=new RoleModel() {
//        }
        //adapter.getRoleMappings();
        //adapter.grantRole();
//        adapter.grantRole();
        logger.info("iterator end");
        return adapter;
    }

    //将用户以userModel形式保存
    private UserData createAdapter(RealmModel realmModel, String username) {
        logger.info("creatAdapter:"+username);

        UserModel local=session.userLocalStorage().getUserByUsername(username,realmModel);

        if (local==null){
            local=session.userLocalStorage().addUser(realmModel,username);
            local.setUsername(username);
            local.setFederationLink(model.getId());
            local.setEnabled(true);
        }
        UserData userData= (UserData) local;
        return userData;
        //properties.remove("");
//        return new UserModelDelegate(local){
//            String password=
//        };
//        return new AbstractUserAdapter(session, realmModel, model) {
//            @Override
//            public String getUsername() {
//                logger.info("getusername");
//                return username;
//            }
//
//            @Override
//            public void setUsername(String username) {
//                logger.info("setusername");
//                super.setUsername(username);
//            }
//        };
    }

    @Override
    public UserModel getUserByEmail(String userEmail, RealmModel realmModel) {
        logger.info("getUserByEmail"+userEmail);
        return null;
    }

    public static final String UNSET_PASSWORD = "#$!-UNSET-PASSWORD";

    @Override
    public UserModel addUser(RealmModel realmModel, String username) {
        logger.info("addUser:"+username);
        Object entity = new Object();
        //entity.setPassword(UNSET_PASSWORD);在这儿，密码需要从全局去进行获取
        save(entity);
        return createAdapter(realmModel, username);
    }
    private static final Logger logger = Logger.getLogger(PostgresStorageProvider.class);
    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        return false;
    }

    public void save(Object object) {
        //将一个用户信息存储到数据库中
        logger.info("save user info");
    }
}
