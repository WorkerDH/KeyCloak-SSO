package com.dh.keycloak.fedaration.postgres.dao;

import com.dh.keycloak.fedaration.postgres.dbutil.DBConnection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *@author DH
 *@create 2022/8/2 16:21
 */
public class BaseDao<T> {
    DBConnection conn=new DBConnection();
    private Connection connection=null;
    @SuppressWarnings("unchecked")
    private Class<T> persistentClass;

    public BaseDao(){
        initConnection();
        ParameterizedType type= (ParameterizedType) getClass().getGenericSuperclass();

    }

    private void initConnection() {
        connection=conn.getConnection();
    }
    public void save(T entity){
        String sql="insert into"+entity.getClass().getSimpleName().toLowerCase()+"(";
        List<Method> list=this.matchPojoMethods(entity,"get");
        Method tempMethod=null;
        Method idMethod=null;

    }

    private List<Method> matchPojoMethods(T entity,String methodName){
        Method[] methods=entity.getClass().getDeclaredMethods();
        List<Method> list=new ArrayList<>();
        for (int index=0;index<methods.length;index++){
            if (methods[index].getName().indexOf(methodName)!=-1){
                list.add(methods[index]);
            }
        }
        return list;
    }

}
