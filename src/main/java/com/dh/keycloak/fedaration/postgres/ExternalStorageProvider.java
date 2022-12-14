package com.dh.keycloak.fedaration.postgres;

import com.dh.keycloak.fedaration.postgres.config.PostgresConfig;
import com.dh.keycloak.fedaration.postgres.dao.UserDao;
import com.dh.keycloak.fedaration.postgres.model.User;
import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import java.util.*;
/**
 * @author DH
 * @create 2022/7/29 10:10
 */

public class ExternalStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        UserRegistrationProvider
 {

    protected KeycloakSession session;
    protected ComponentModel model;
    protected Map<String, UserModel> loadUsers = new HashMap<>();
    private UserDao userDao=new UserDao();
    private static final Logger logger = Logger.getLogger(ExternalStorageProvider.class);

    public ExternalStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public UserModel getUserById(String id, RealmModel realmModel) {
        logger.info("getUserById" + id);
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        return getUserByUsername(username, realmModel);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        logger.info("getUserByUsername:1.0");
        UserModel adapter = loadUsers.get(username);
        if (adapter == null) {
            try {
                User user=null;
                if (PostgresConfig.CustomEnable.equals("true")){
                    Map<String,Object> params=new HashMap<>();
                    params.put("name",username);
                    user=userDao.getUserOneByClaim(params);
                }else{
                    user=userDao.getUserOneByName(username);
                }

                if (user != null) {
                    logger.info("getUserByUsername->user!=null");
                    adapter = createAdapter(realmModel, username,user);//?????????????????????usermodel???adapter
                    List<String> list = new ArrayList<>();
                    list.add(user.getPassword());
//                    String id = "f:" + model.getId() + ":" + adapter.getUsername();
//                    logger.info("have a id=>" + id);
                    loadUsers.put(username, adapter);
                }
            } catch (Exception e) {

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

    //??????userModel
    protected UserModel createAdapter(RealmModel realm, String username , User user) {
        logger.info("createAdapter:" + username);
        UserModel local = session.userLocalStorage().getUserByUsername(username, realm);
        if (local == null) {
            local = session.userLocalStorage().addUser(realm, username);
            local.setFederationLink(model.getId());
            local.setEnabled(true);
            local.setSingleAttribute("password",user.getPassword());
            local.setSingleAttribute("phone",user.getPhone());
            local.setSingleAttribute("customattr","cusValue");
            addRole(realm,user.getRoleName());//????????????
            Iterator roles = realm.getRoles().iterator();
            Iterator userRoles = local.getRoleMappings().iterator();
            boolean flag = true;
            while (userRoles.hasNext()) {//??????????????????????????????????????????
                RoleModel roleModel = (RoleModel) userRoles.next();
                if (roleModel.getName().equals(user.getRoleName())) {
                    flag = false;//?????????????????????
                }
            }
            while (roles.hasNext()) {
                RoleModel roleModel = (RoleModel) roles.next();
//                logger.info("get my role info:" + roleModel.getName());
                if (roleModel.getName().equals(user.getRoleName()) && flag) {
//                    logger.info("set my role info:" + roleModel.getName());
                    local.grantRole(roleModel);//????????????
                    local.grantRole(realm.getRole("defaultRole"));
                }
            }
        }else{
            logger.info("local !=null ");
        }
         return local;

    }
    /**
     * @description: ??????role
     * @author DH
     * @date 2022/8/1
     */
     private void addRole(RealmModel realm,String roleName){
        Iterator<RoleModel> iterator=realm.getRoles().iterator();
        if (roleName==null||realm==null){
            return;
        }
        while (iterator.hasNext()){
            RoleModel roleModel=iterator.next();
            if (roleModel.getName().equals(roleName)){
                return;//?????????????????????????????????????????????
            }
        }
        this.session.realms().addRealmRole(realm,roleName);
     }
    //????????????????????????????????????????????????????????????????????????????????????password
    @Override
    public boolean supportsCredentialType(String credentialType) {
        logger.info("supportsCredentialType:" + credentialType);
        return credentialType.equals(PasswordCredentialModel.TYPE);
    }

    //???????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
         logger.info("isConfiguredFor:");
        return supportsCredentialType(credentialType);
    }

    //???isValid()????????????????????????,???
    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        logger.info("isValid=>input:" + input.getChallengeResponse());
        if (userModel.getUsername()==null||input.getChallengeResponse()==null){
            throw new ComponentValidationException("name or password is null");
        }
        UserModel local=this.session.userLocalStorage().getUserByUsername(userModel.getUsername(),realmModel);

        if (local!=null){//?????????????????????
            if (local.getAttribute("password")!=null){
                String password=local.getAttribute("password").get(0);
                return password.equals(input.getChallengeResponse());
            }
        }
        if (!supportsCredentialType(input.getType())) {
            return false;
        }
        try {
            User user = userDao.getUserOneByName(userModel.getUsername());
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
        return true;
    }
}
