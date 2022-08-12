package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/29 10:10
 */

import com.dh.keycloak.fedaration.postgres.config.PostgresConfig;
import com.dh.keycloak.fedaration.postgres.dbutil.DBConnection;
import org.apache.log4j.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *@author EDY
 *@create 2022/7/29 10:10
 */
public class ExternalStorageProviderFactory  implements UserStorageProviderFactory<ExternalStorageProvider> {
    public static final String PROVIDER_NAME ="external";
    private static final Logger logger = Logger.getLogger(ExternalStorageProviderFactory.class);
    private ComponentModel comModel ;
    @Override
    public ExternalStorageProvider create(KeycloakSession keycloakSession, ComponentModel model) {
        logger.info("start establish factory !!!");
        comModel=model;
        if (PostgresConfig.SERVER_IP==null||PostgresConfig.SERVER_PORT==null
                ||PostgresConfig.DATABASE_SID==null||PostgresConfig.USERNAME==null
                ||PostgresConfig.PASSWORD==null){
            initConfig(model,false);
        }
        return new ExternalStorageProvider(keycloakSession,model);
    }

    @Override
    public String getId() {
        logger.info("start establish factory get id !!!");
        return PROVIDER_NAME;
    }
    @Override
    public String getHelpText(){
        return "this is help text";
    }
    protected static final List<ProviderConfigProperty> configProperties ;
    static {
        configProperties = getConfigProps();
    }

    private static List<ProviderConfigProperty> getConfigProps() {
        logger.info("getConfig");
        return ProviderConfigurationBuilder.create()
                .property().name("SERVER_IP")
                .label("SERVER_IP")
                .helpText("ip address")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name("SERVER_PORT")
                .label("SERVER_PORT")
                .defaultValue("5432")
                .helpText("database port")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name("DATABASE_SID")
                .label("DATABASE_SID")
                .helpText("database name")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name("USERNAME")
                .label("USERNAME")
                .helpText("username")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name("PASSWORD")
                .label("PASSWORD")
                .helpText("input your password")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name("TABLE")
                .label("TABLE")
                .helpText("input your user table")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .build();
    }

    @Override
    public   List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }


    private void initConfig(ComponentModel config,Boolean flag){
        logger.info("initConfig");
        String SERVER_IP=config.getConfig().getFirst("SERVER_IP");
        String SERVER_PORT=config.getConfig().getFirst("SERVER_PORT");
        String DATABASE_SID=config.getConfig().getFirst("DATABASE_SID");
        String USERNAME=config.getConfig().getFirst("USERNAME");
        String PASSWORD=config.getConfig().getFirst("PASSWORD");
        String TABLE=config.getConfig().getFirst("TABLE");
        if ((SERVER_IP==null||SERVER_PORT==null||DATABASE_SID==null||USERNAME==null||PASSWORD==null||TABLE==null)&&flag){
            throw new ComponentValidationException("all config must input ,can't has null");
        }else {
            PostgresConfig.SERVER_IP=SERVER_IP;
            PostgresConfig.SERVER_PORT=SERVER_PORT;
            PostgresConfig.DATABASE_SID=DATABASE_SID;
            PostgresConfig.USERNAME=USERNAME;
            PostgresConfig.PASSWORD=PASSWORD;
            PostgresConfig.TABLE=TABLE;
        }
    }
    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {

        logger.info("validateConfiguration");
        initConfig(config,true);
        DBConnection conn=new DBConnection();
        Connection connection=conn.getConnection();
        if (connection==null){
            throw new ComponentValidationException("can't connection to database, please check your config");
        }else{
            conn.closeConn();
        }
    }
}
