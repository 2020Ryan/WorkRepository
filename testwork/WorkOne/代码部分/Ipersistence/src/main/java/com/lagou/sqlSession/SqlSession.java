package com.lagou.sqlSession;

import java.sql.SQLException;
import java.util.List;

public interface SqlSession {
    public <E> List<E> selectList(String statementId, Object... param)throws Exception;
    public <T> T selectOne(String statementId,Object... params) throws Exception;
    public void close() throws SQLException;
    public <T> T getMapper(Class<?> mapperClass);
    public int update(String statementId,Object... params)throws Exception;
}
