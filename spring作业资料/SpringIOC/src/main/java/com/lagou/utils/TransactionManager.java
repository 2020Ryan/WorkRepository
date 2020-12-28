package com.lagou.utils;

import java.sql.SQLException;


public class TransactionManager {

    //获得连接对象
    private ConnectionUtils connectionUtils;

    public void setConnectionUtils(ConnectionUtils connectionUtils) {

        this.connectionUtils = connectionUtils;
    }


    // 开启事务，其实就是把自动提交关闭
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }


    //提交事务
    public void commit() throws SQLException{
        connectionUtils.getCurrentThreadConn().commit();;
    }


    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
