package com.dh.keycloak.fedaration.postgres.dbutil;

import com.dh.keycloak.fedaration.postgres.config.PostgresConfig;

import java.sql.*;

/**
 *@author DH
 *@create 2022/8/2 16:07
 */
public class DBConnection {
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;
    public Connection getConnection(){
        try {
            Class.forName(PostgresConfig.CLASS_NAME);
            String url=PostgresConfig.DATABASE_URL+"://"+PostgresConfig.SERVER_IP+":"+PostgresConfig.SERVER_PORT+"/"+PostgresConfig.DATABASE_SID;
            conn= DriverManager.getConnection(url,PostgresConfig.USERNAME,PostgresConfig.PASSWORD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }
    public void closeConn(){
        if (rs!=null){
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (pstmt!=null){
            try {
                pstmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (conn!=null){
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    /**
     * @description:  增删改
     * @author DH
     * @date 2022/8/2
     */
    public int executeOther(PreparedStatement pstmt){
        try {
            int affectRows=pstmt.executeUpdate();
            return affectRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public  ResultSet executeQuery(PreparedStatement pstmt){
        try {
            rs=pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


}
