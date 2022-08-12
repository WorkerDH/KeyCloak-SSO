package com.dh.keycloak.fedaration.postgres.dao;/**
 * @author EDY
 * @create 2022/8/2 16:56
 */


import com.dh.keycloak.fedaration.postgres.config.PostgresConfig;
import com.dh.keycloak.fedaration.postgres.dbutil.DBConnection;
import com.dh.keycloak.fedaration.postgres.model.User;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *@author DH
 *@create 2022/8/2 16:56
 */
public class UserDao {
    private static final Logger logger = Logger.getLogger(UserDao.class);
    private Connection connection;
    private DBConnection conn=new DBConnection();
    public UserDao(){
        connection=conn.getConnection();
    }
    public User getUserOneByName(String name) throws SQLException {
        String sql = "select * from "+ PostgresConfig.TABLE +" where name='"+name+"'";
        logger.info("sql:"+sql);
        ResultSet rs=conn.executeQuery(this.connection.prepareStatement(sql));
        User user=null;
        while (rs.next()){
            user=new User();
            user.setPhone(rs.getString("phone"));
            user.setUsername(rs.getString("name"));
            user.setRoleName(rs.getString("roleName"));
            user.setPassword(rs.getString("password"));
            user.setId(rs.getString("id"));
            break;
        }
        rs.close();
        conn.closeConn();
        return user;
    }
}
