package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {
    private Connection connection = null;


    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object[] param) throws Exception {
       //获取连接
        connection = configuration.getDataSource().getConnection();
        //处理sql
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        String finalSQl = boundSql.getSqlText();
        //入参
        Class<?> paramterType = mappedStatement.getParamterType();
        //预编译JDBC
        PreparedStatement preparedStatement = connection.prepareStatement(finalSQl);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String name = parameterMapping.getContent();
            //反射
            Field declaredField = paramterType.getDeclaredField(name);
            declaredField.setAccessible(true);
            //类似数组，获取第一参数
            Object o = declaredField.get(param[0]);
            //给占位符赋值
            preparedStatement.setObject(1+i,o);
        }
        //结果封装处理
        ResultSet resultSet = preparedStatement.executeQuery();
        //出参类型设置
        Class<?> resultType = mappedStatement.getResultType();
        ArrayList<E> results = new ArrayList<E>();
        while (resultSet.next()){
            //元数据、
            ResultSetMetaData metaData = resultSet.getMetaData();
            //来个出参对象
            Object o = resultType.newInstance();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                //数据库字段从1算起
                String columnName = metaData.getColumnName(i);
                //字段值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultType);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }
            results.add((E) o);
        }
        return results;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws SQLException, NoSuchFieldException, IllegalAccessException {
        //获取连接
        connection = configuration.getDataSource().getConnection();
        //处理sql
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        String finalSQl = boundSql.getSqlText();
        //入参
        Class<?> paramterType = mappedStatement.getParamterType();
        //预编译JDBC
        PreparedStatement preparedStatement = connection.prepareStatement(finalSQl);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String name = parameterMapping.getContent();
            //反射
            Field declaredField = paramterType.getDeclaredField(name);
            declaredField.setAccessible(true);
            //类似数组，获取第一参数
            Object o = declaredField.get(params[0]);
            //给占位符赋值
            preparedStatement.setObject(1+i,o);
        }
        int i = preparedStatement.executeUpdate();
        return i;
    }

    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);
        return boundSql;
    }
}
