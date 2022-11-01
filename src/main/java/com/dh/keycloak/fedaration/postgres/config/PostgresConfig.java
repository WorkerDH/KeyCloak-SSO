package com.dh.keycloak.fedaration.postgres.config;

import java.util.List;

/**
 *@author DH
 *@create 2022/8/2 16:05
 *@desc 自定义配置的属性
 */
public class PostgresConfig {

    public static String CLASS_NAME ="org.postgresql.Driver";
    public static String DATABASE_URL = "jdbc:postgresql";
    public static String SERVER_IP =null;
    public static String SERVER_PORT =null;
    public static String DATABASE_SID =null;
    public static String USERNAME =null;
    public static String PASSWORD =null;
    public static String TABLE=null;
    public static String LastStuff="";//补充的后缀，例如mysql链接时需要useSSL=false
    //public static String CustomAttributes=null;//自定义的属性以&做分割，
    public static String CustomEnable="";
    public static List<String> CustomAttributes=null;
    public static String DATABASE=null;
}
