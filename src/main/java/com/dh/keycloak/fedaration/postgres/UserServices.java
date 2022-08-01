package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/25 16:47
 */

import com.dh.keycloak.fedaration.postgres.model.User;
import org.apache.log4j.Logger;
import org.postgresql.Driver;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 *@author EDY
 *@create 2022/7/25 16:47
 */
public class UserServices {
    private static final Logger logger = Logger.getLogger(UserServices.class);
    public User getUserByName(String name) throws IOException, SQLException, ClassNotFoundException {
       logger.info("UserServices->getUserByName:"+name+"]");
        InputStream conf = PostgreSQLJDBC.class.getClassLoader().getResourceAsStream("config/jdbc.properties");
        Properties pros = new Properties();
        pros.load(conf);
       // Driver driver=new Driver();
        String url = pros.getProperty("url");
        String username = pros.getProperty("username");
        String password = pros.getProperty("password");
        String driverClass = pros.getProperty("driverclass");
        // 加载驱动
        User  user= connTest(url,username,password,driverClass,name);
        return user;
    }
    public int save(){
        logger.info("UserServices:save info");
        return 1;
    }
    private static User connTest(String url, String username, String password, String driverClass,String name) throws ClassNotFoundException, SQLException {
        logger.info("into conntest");
        Class.forName(driverClass);
        // 获取连接
        Connection conn = DriverManager.getConnection(url, username, password);
        String sql = "select * from user_info where name='"+name+"'";
        logger.info("sql:"+sql);
        PreparedStatement ps = conn.prepareStatement(sql);
        // 执行查询并返回结果集
        ResultSet rs = ps.executeQuery();
        User user=null;
        // 处理结果集
        while (rs.next()) {
            user=new User();
            user.setId(rs.getString(1));
            user.setUsername(rs.getString(3));
            user.setPassword(rs.getString(5));
            user.setRoleName(rs.getString(6));
           // System.out.println("id: " + rs.getString(1));
            break;
//            System.out.print("email " + rs.getString(2));
//            System.out.print("name: " + rs.getString(3));
//            System.out.println("phone: " + rs.getString(4));
        }
        // 关闭资源
        rs.close();
        ps.close();
        conn.close();
        return user;
    }
}
