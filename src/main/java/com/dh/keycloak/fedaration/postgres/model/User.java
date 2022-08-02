package com.dh.keycloak.fedaration.postgres.model;/**
 * @author EDY
 * @create 2022/7/25 16:47
 */

/**
 *@author EDY
 *@create 2022/7/25 16:47
 */
public class User {
    String id;
    String username;
    String password;
    String roleName;
    String phone;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
