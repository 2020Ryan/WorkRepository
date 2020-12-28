package com.lagou.utils;

import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionUtils {

    //老方法，饿汉式
    /*private ConnectionUtils() {

    }
    private static ConnectionUtils connectionUtils = new ConnectionUtils();
    public static ConnectionUtils getInstance() {
        return connectionUtils;
    }*/


    //可以这样想，同是一个service的请求，在一个线程中，创建同一个连接
    //当前的线程，存了数据库连接
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
    /**
     *
     * 创建方法：获取连接
     *判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接
     * 池获取⼀个连接绑定到当前线程
     */
    public Connection getCurrentThreadConn() throws SQLException{

        //尝试获取
        Connection connection = threadLocal.get();
        //进行判断
        if (connection == null ){
            //从当前线程拿链接绑定
             connection = DruidUtils.getInstance().getConnection();
            //绑定到当前线程
            threadLocal.set(connection);
        }
        //结果返回
        return connection;
    }

}
