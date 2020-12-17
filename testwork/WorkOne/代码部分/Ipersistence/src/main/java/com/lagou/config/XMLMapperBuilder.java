package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream resourceAsSteam) throws DocumentException, ClassNotFoundException {
        Document document = new SAXReader().read(resourceAsSteam);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        List<Element> select = rootElement.selectNodes("//select");
        if (select.size()>0){
            for (Element element : select) {
                String id = element.attributeValue("id");
                String paramterType = element.attributeValue("paramterType");
                String resultType = element.attributeValue("resultType");
                //参数类型
                Class<?> paramterTypeClass = getClassType(paramterType);
                Class<?> resultTypeClass = getClassType(resultType);

                //statementId 作为key
                String key = namespace+"."+id;
                //sql语句
                String textTrim = element.getTextTrim();
                //封装 mappedStatement
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setId(id);
                mappedStatement.setParamterType(paramterTypeClass);
                mappedStatement.setResultType(resultTypeClass);
                mappedStatement.setSql(textTrim);
                mappedStatement.setFlag("select");
                //填充 configuration
                configuration.getMappedStatementMap().put(key, mappedStatement);
            }
        }
        //update
        List<Element> update = rootElement.selectNodes("//update");
        if (update.size()>0){
            for (Element element : update) {
                String id = element.attributeValue("id");
                String paramterType = element.attributeValue("paramterType");
                //String resultType = element.attributeValue("resultType");
                //参数类型
                Class<?> paramterTypeClass = getClassType(paramterType);
              //  Class<?> resultTypeClass = getClassType(resultType);

                //statementId 作为key
                String key = namespace+"."+id;
                //sql语句
                String textTrim = element.getTextTrim();
                //封装 mappedStatement
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setId(id);
                mappedStatement.setParamterType(paramterTypeClass);
                //mappedStatement.setResultType(resultTypeClass);
                mappedStatement.setSql(textTrim);
                //填充 configuration
                configuration.getMappedStatementMap().put(key, mappedStatement);
            }
        }
        //insert
        List<Element> insert = rootElement.selectNodes("//insert");
        if (insert.size()>0){
            for (Element element : insert) {
                String id = element.attributeValue("id");
                String paramterType = element.attributeValue("paramterType");
               // String resultType = element.attributeValue("resultType");
                //参数类型
                Class<?> paramterTypeClass = getClassType(paramterType);
             //   Class<?> resultTypeClass = getClassType(resultType);

                //statementId 作为key
                String key = namespace+"."+id;
                //sql语句
                String textTrim = element.getTextTrim();
                //封装 mappedStatement
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setId(id);
                mappedStatement.setParamterType(paramterTypeClass);
            //    mappedStatement.setResultType(resultTypeClass);
                mappedStatement.setSql(textTrim);
                //填充 configuration
                configuration.getMappedStatementMap().put(key, mappedStatement);
            }
        }
        //delete
        List<Element> delete = rootElement.selectNodes("//delete");
        if (delete.size()>0){
            for (Element element : delete) {
                String id = element.attributeValue("id");
                String paramterType = element.attributeValue("paramterType");
                String resultType = element.attributeValue("resultType");
                //参数类型
                Class<?> paramterTypeClass = getClassType(paramterType);
            //    Class<?> resultTypeClass = getClassType(resultType);

                //statementId 作为key
                String key = namespace+"."+id;
                //sql语句
                String textTrim = element.getTextTrim();
                //封装 mappedStatement
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setId(id);
                mappedStatement.setParamterType(paramterTypeClass);
              //  mappedStatement.setResultType(resultTypeClass);
                mappedStatement.setSql(textTrim);
                //填充 configuration
                configuration.getMappedStatementMap().put(key, mappedStatement);
            }
        }
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if (paramterType != null){
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
       return  null;
    }
}
