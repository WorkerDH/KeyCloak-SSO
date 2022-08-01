package com.dh.keycloak.fedaration.postgres;

import com.dh.keycloak.fedaration.postgres.model.User;
import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import javax.jws.soap.SOAPBinding;
import javax.management.relation.Role;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author EDY
 * @create 2022/7/29 10:10
 */
//实现了UserLookupProvider接口，因为我们希望能够使用此提供程序存储的用户登录。
//它实现了该CredentialInputValidator接口，因为我们希望能够验证使用登录屏幕输入的密码。
//我们的属性文件是只读的。我们实现CredentialInputUpdater因为我们想在用户尝试更新他的密码时发布一个错误条件。
public class ExternalStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        UserRegistrationProvider
 {

    protected KeycloakSession session;
    protected ComponentModel model;
    protected Map<String, UserModel> loadUsers = new HashMap<>();
    protected Map<String, String> loadStoreIds = new HashMap<>();
    private UserServices userServices = new UserServices();
    private static final Logger logger = Logger.getLogger(ExternalStorageProvider.class);

    public ExternalStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public UserModel getUserById(String id, RealmModel realmModel) {
        logger.info("getUserById" + id);
        StorageId storageId = new StorageId(id);
        logger.info("getUserById:storeId=" + storageId.getId());
        String username = storageId.getExternalId();
        return getUserByUsername(username, realmModel);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.info("getUserByUsername:1.0");
        UserModel adapter = loadUsers.get(username);
        //RoleProvider roleProvider=null;
       // roleProvider.addRealmRole(realmModel,"ss");
        //this.session.roleLocalStorage().addRealmRole();
        if (adapter == null) {
            logger.info("getUserByUsername->adapter==null");
            try {
                User user = userServices.getUserByName(username);
                if (user != null) {
                    logger.info("getUserByUsername->user!=null");
                    adapter = createAdapter(realmModel, username,user);//创建了一个新的usermodel给adapter
                    //adapter.setEnabled(true);
                    List<String> list = new ArrayList<>();
                    list.add(user.getPassword());

                    //adapter.setAttribute("password2",list);
                    //adapter.setSingleAttribute("pass",user.getPassword());
                    printAttr(adapter.getAttributes());
                    String id = "f:" + model.getId() + ":" + adapter.getUsername();
                    logger.info("have a id=>" + id);

//                    Iterator roles = realmModel.getRoles().iterator();
//                    Iterator userRoles = adapter.getRoleMappings().iterator();
//                    boolean flag = true;
//                    while (userRoles.hasNext()) {
//                        RoleModel roleModel = (RoleModel) userRoles.next();
//                        if (roleModel.getName().equals(user.getRoleName())) {
//                            flag = false;//已经注册了角色
//                        }
//                    }
//                    while (roles.hasNext()) {
//                        RoleModel roleModel = (RoleModel) roles.next();
//                        logger.info("get my role info:" + roleModel.getName());
//                        if (roleModel.getName().equals(user.getRoleName()) && flag) {
//                            logger.info("set my role info:" + roleModel.getName());
//                            adapter.grantRole(roleModel);
//                        }
//                    }
//                    //adapter.grantRole();
                    loadUsers.put(username, adapter);
                }
            } catch (Exception e) {
                logger.info("exception1:" + e);
                e.printStackTrace();
            }

        }
        logger.info("getUserByUsername->end");
        return adapter;
    }

    @Override
    public UserModel getUserByEmail(String s, RealmModel realmModel) {
        return null;
    }

    //创建userModel
    protected UserModel createAdapter(RealmModel realm, String username , User user) {
        logger.info("createAdapter:" + username);

        UserModel local = session.userLocalStorage().getUserByUsername(username, realm);
        if (local == null) {
            logger.info("createAdapter->local==null:>>>");
            local = session.userLocalStorage().addUser(realm, username);
            local.setFederationLink(model.getId());
            logger.info("createAdapter:->model.getid=" + model.getId());
            local.setEnabled(true);

            Iterator roles = realm.getRoles().iterator();
            Iterator userRoles = local.getRoleMappings().iterator();
            boolean flag = true;
            while (userRoles.hasNext()) {
                RoleModel roleModel = (RoleModel) userRoles.next();
                if (roleModel.getName().equals(user.getRoleName())) {
                    flag = false;//已经注册了角色
                }
            }
            while (roles.hasNext()) {
                RoleModel roleModel = (RoleModel) roles.next();
                logger.info("get my role info:" + roleModel.getName());
                if (roleModel.getName().equals(user.getRoleName()) && flag) {
                    logger.info("set my role info:" + roleModel.getName());
                    local.grantRole(roleModel);
                }
            }
            //ad
        }
         return local;
//        return new AbstractUserAdapterFederatedStorage(session, realm, model) {
//            @Override
//            public String getUsername() {
//                return username;
//            }
//
//            @Override
//            public void setUsername(String s) {
//                logger.info("what should I do???");
//            }
//        };
    }

    //方法返回特定凭证类型是否支持验证。我们检查凭证类型是否为password
    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("supportsCredentialType:" + credentialType);
        //return true;
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    //确定是否为用户配置了特定的凭证类型。此方法检查是否为用户设置了密码
    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        try {
            logger.info("isConfiguredFor:" + credentialType);
            User user = userServices.getUserByName(userModel.getUsername());
            return credentialType.equals(PasswordCredentialModel.TYPE) && user != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //该isValid()方法负责验证密码
    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        logger.info("isValid=>input:" + input.getChallengeResponse() + ",=>userModel:" + userModel.getUsername());
        if (!supportsCredentialType(input.getType())) {
            return false;
        }
        try {
            User user = userServices.getUserByName(userModel.getUsername());

            if (user == null) {
                logger.info("isValid->user=null");
                return false;
            }
            return user.getPassword().equals(input.getChallengeResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void printAttr(Map<String, List<String>> attributes) {
        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            logger.info("getKey=" + key);
            List<String> list = attributes.get(key);
            for (String str : list) {
                logger.info("key=" + key + ",val:" + str);
            }
        }
    }

    @Override
    public void close() {
        logger.info("provider close!!!");
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String username) {
        logger.info("addUser:"+username);
        User user=new User();
        user.setUsername(username);
        return createAdapter(realmModel, username,user);
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        logger.info("remove a user:"+realmModel.getName());
        return false;
    }
}
