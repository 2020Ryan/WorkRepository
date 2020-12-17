package com.lagou.config;

import com.lagou.config.XMLMapperBuilder;
import com.lagou.io.Resource;
import com.lagou.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.lang.model.element.VariableElement;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigerBuilder {
    private Configuration configuration;
    public XMLConfigerBuilder(Configuration configuration) {

        this.configuration = new Configuration();
    }

    /*
        具体的用dom4j解析后，封装Configuration
     */
    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException, ClassNotFoundException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        //"//property"利用xpath，可以使任意路径的proerty被解析到
        List<Element> elements = rootElement.selectNodes("//property");
        //创建能存取key-value的容器
        Properties properties = new Properties();
        for (Element element : elements) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name,value);
        }
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));

        //填充
        configuration.setDataSource(comboPooledDataSource);

       //mapper.xml解析
        List<Element> mapperElements = rootElement.selectNodes("//mapper");
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
        for (Element mapperElement : mapperElements) {
            String mapperPath = mapperElement.attributeValue("resource");
            InputStream resourceAsSteam = Resource.getResourceAsStream(mapperPath);
            xmlMapperBuilder.parse(resourceAsSteam);
        }
        return configuration;
    }
}
