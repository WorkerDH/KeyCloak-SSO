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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    /**
     * @description:  只允许输入一个查询条件
     * @author DH
     * @date 2022/9/2
     */
    public User getUserOneByClaim(Map<String,Object> paramClaims) throws Exception {
        Set<String> keys=paramClaims.keySet();
        if (keys.size()<=0||keys.size()>1){
            throw new Exception("your claims occur exception,please check");
        }
        String attrName=keys.iterator().next();
        String attrValue=paramClaims.get(attrName).toString();
        String sql = "select * from "+ PostgresConfig.TABLE +" where "+attrName+"='"+attrValue+"'";
        System.out.println("sql:"+sql);
        ResultSet rs=conn.executeQuery(this.connection.prepareStatement(sql));
        User user=null;
//        user.setOtherClaims(null);
        Map<String,Object> dataClaims=getClaimsKey();
        Set<String> claimsKey=dataClaims.keySet();
        while (rs.next()){
            user=new User();
            user.setId(rs.getString("id"));
            for (String key:claimsKey){//获取自定义的属性
                dataClaims.put(key,rs.getObject(key));
            }
            user.setOtherClaims(dataClaims);
        }
        return  user;
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

    public Map<String, Object> getClaimsKey() {
        List<String> attrs=PostgresConfig.CustomAttributes;
        Map<String,Object> map=new HashMap<>();
        for (int i=0;i<attrs.size();i++){
            if (attrs.get(i).toLowerCase().equals("id")|| attrs.get(i).equals("")){
                continue;//id不必加入自定义属性
            }
            map.put(attrs.get(i),null);
        }
        return map;
    }
}
