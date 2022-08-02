package com.dh.keycloak.fedaration.postgres;/**
 * @author EDY
 * @create 2022/7/22 15:36
 */

import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author EDY
 * @create 2022/7/22 15:36
 */
public class Test {
    //public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
//        Integer a = 3;
//        Integer b = 4;
//        Integer c = 10;
//        b = a;
//        setToB(b);
//        System.out.println("a:" + a + ",b=:" + b);
//        List<String> list=new ArrayList<>();
//        list.add("a");
//        System.out.println(list.size());
//        setToList(list);
//        System.out.println(list.size());
//        InputStream conf = PostgreSQLJDBC.class.getClassLoader().getResourceAsStream("config/jdbc.properties");
//        Properties pros = new Properties();
//        pros.load(conf);
//        String url = pros.getProperty("url");
//        String username = pros.getProperty("username");
//        String password = pros.getProperty("password");
//        String driverClass = pros.getProperty("driverclass");
//        // 加载驱动
//        connTest(url,username,password,driverClass);
//        for (int i=0;i<10;i++){
//        }
 //   }

    private static void setToList(List<String> list) {
        List<String> list1=new ArrayList<>();
        //list1.addAll(list);
        list.add("2");
        list=list1;

        list1.add("b");
        list1.add("b");
    }
 private string t1(){
        return "1";
    }
private void we(){
        System.out.println("sss");
    }    private static void setToB(Integer b) {
        b = 100;
    }

    private static void connTest(String url, String username, String password, String driverClass) throws ClassNotFoundException, SQLException {
        Class.forName(driverClass);
        // 获取连接
        Connection conn = DriverManager.getConnection(url, username, password);
        String condition = "mose";
        String sql = "select * from user_info where  name='" + condition + "'";
        PreparedStatement ps = conn.prepareStatement(sql);
        // 执行查询并返回结果集
        ResultSet rs = ps.executeQuery();
        // 处理结果集
        while (rs.next()) {
            System.out.println("id: " + rs.getString(1));
//            System.out.print("email " + rs.getString(2));
//            System.out.print("name: " + rs.getString(3));
//            System.out.println("phone: " + rs.getString(4));
        }

        // 关闭资源
        rs.close();
        ps.close();
        conn.close();
    }

}
