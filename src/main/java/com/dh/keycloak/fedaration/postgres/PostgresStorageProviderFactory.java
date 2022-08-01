package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/22 16:31
 */

import org.apache.log4j.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.Constants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 *@author EDY
 *@create 2022/7/22 16:31
 */
public class PostgresStorageProviderFactory implements UserStorageProviderFactory<PostgresStorageProvider> {
    public static final String PROVIDER_NAME ="postgres";
    protected Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(PostgresStorageProviderFactory.class);
    @Override
    public PostgresStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new PostgresStorageProvider(keycloakSession,componentModel,properties);
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

//    @Override
//    public List<ProviderConfigProperty> getConfigProperties() {
////        return ProviderConfigurationBuilder.create()
////                .property("base_url","base_url","help_text");
//    }

    @Override
    public void init(Config.Scope config) {

//​       InputStream is = getClass().getClassLoader().getResourceAsStream("/users.properties");
//
//       ​if (is == null) {
//           ​logger.warn("Could not find users.properties in classpath");
//       ​} else {
//           ​try {
//               ​properties.load(is);
//           ​} catch (IOException ex) {
//               ​logger.error("Failed to load users.properties file", ex);
//           ​}
//       ​}
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        //init调用完毕，将调用这个方法
    }
}
