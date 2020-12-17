package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object[] param) throws Exception;

    void close() throws SQLException;

    int update(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws SQLException, NoSuchFieldException, IllegalAccessException;
}
