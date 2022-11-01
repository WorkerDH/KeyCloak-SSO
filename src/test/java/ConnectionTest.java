/**
 * @author EDY
 * @create 2022/9/1 19:03
 */

import com.dh.keycloak.fedaration.postgres.config.PostgresConfig;
import com.dh.keycloak.fedaration.postgres.dao.UserDao;
import com.dh.keycloak.fedaration.postgres.dbutil.DBConnection;
import com.dh.keycloak.fedaration.postgres.model.User;
import javafx.geometry.Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *@author EDY
 *@create 2022/9/1 19:03
 */
public class ConnectionTest {
    public static void main(String[] args)   {
//        PostgresConfig.DATABASE_URL="jdbc:mysql";
//        PostgresConfig.CLASS_NAME="org.postgresql.Driver";
//        PostgresConfig.SERVER_IP="192.168.96.30";
//        PostgresConfig.SERVER_PORT="5432";
//        PostgresConfig.DATABASE_SID="activity";
//        PostgresConfig.USERNAME="postgres";
//        PostgresConfig.PASSWORD="123456";
//        PostgresConfig.TABLE="user_info";
        Real-time overview
        PostgresConfig.DATABASE_URL="jdbc:mysql";
        PostgresConfig.CLASS_NAME="com.mysql.jdbc.Driver";
        PostgresConfig.SERVER_IP="192.168.96.30";
        PostgresConfig.SERVER_PORT="3306";
        PostgresConfig.DATABASE_SID="activity";
        PostgresConfig.USERNAME="root";
        PostgresConfig.PASSWORD="123456";
        PostgresConfig.TABLE="user_info";
        PostgresConfig.LastStuff="?useSSL=false";
       // PostgresConfig.CustomAttributes="phone&name&id&&roleName&password";
        DBConnection conn=new DBConnection();
        UserDao userDao=new UserDao();
        User user=null;
        try {
            Map<String,Object> paramClaim=new HashMap<>();
            paramClaim.put("name","dh");
            user=userDao.getUserOneByClaim(paramClaim);
            for (String key:user.getOtherClaims().keySet()){
                System.out.println(key+":"+user.getOtherClaims().get(key));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(user.getUsername()+":"+user.getPassword());
        conn.getConnection();
        conn.closeConn();
    }
}
