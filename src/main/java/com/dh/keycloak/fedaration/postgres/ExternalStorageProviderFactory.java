package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/29 10:10
 */

import org.apache.log4j.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.Properties;

/**
 *@author EDY
 *@create 2022/7/29 10:10
 */
public class ExternalStorageProviderFactory  implements UserStorageProviderFactory<ExternalStorageProvider> {
    public static final String PROVIDER_NAME ="external";
    private static final Logger logger = Logger.getLogger(ExternalStorageProviderFactory.class);
    @Override
    public ExternalStorageProvider create(KeycloakSession keycloakSession, ComponentModel model) {
        //logger.info("start establish factory !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return new ExternalStorageProvider(keycloakSession,model);
    }

    @Override
    public String getId() {
        //logger.info("start establish factory get id !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return PROVIDER_NAME;
    }
}
