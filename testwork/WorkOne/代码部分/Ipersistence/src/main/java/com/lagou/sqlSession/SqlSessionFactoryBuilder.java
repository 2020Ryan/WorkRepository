package com.lagou.sqlSession;

import com.lagou.config.XMLConfigerBuilder;
import com.lagou.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    private Configuration configuration;

    public SqlSessionFactoryBuilder() {
        this.configuration = new Configuration();
    }
    public SqlSessionFactory build(InputStream in) throws PropertyVetoException, DocumentException, ClassNotFoundException {
        //1、需要数据源信息，先用dom4j解析配置文件，结果封装在Configuration中
        XMLConfigerBuilder xmlConfigerBuilder = new XMLConfigerBuilder(configuration);
        Configuration configuration = xmlConfigerBuilder.parseConfig(in);

        //2、创建 sqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return sqlSessionFactory;
    }
}
