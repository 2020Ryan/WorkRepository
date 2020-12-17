package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    //真正教Executor去执行
    private Executor simpleExcutor = new SimpleExecutor();

    @Override
    public <E> List<E> selectList(String statementId, Object... param) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        System.out.println(statementId);
        List<E> query = simpleExcutor.query(configuration, mappedStatement, param);
        return query;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("返回结果过多");
        }
    }

    @Override
    public void close() throws SQLException {
        simpleExcutor.close();
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
       //jdk动态代理
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码 //根据不同情况，来调用selctList或者selectOne
                // 准备参数 1：statmentid :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className+"."+methodName;
                // 准备参数2：params
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);

                if (mappedStatement.getFlag()!=null){
                    // 获取被调用方法的返回值类型
                    Type genericReturnType = method.getGenericReturnType();
                    //判断返回值是否为泛型来判断是one还是list
                    if (genericReturnType instanceof ParameterizedType){
                        List<Object> objects = selectList(statementId,args);
                        return objects;
                    }else {
                        return selectOne(statementId,args);
                    }
                }
                    return update(statementId,args);
            }
        });


        return (T) proxyInstance;
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        System.out.println(statementId);
        int result = simpleExcutor.update(configuration, mappedStatement, params);

        return result;
    }
}
