package com.lagou.pojo;

public class MappedStatement {

    //id
    private String id;
    //sql语句
    private String sql;

    private String flag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    //输入参数
    private Class<?> paramterType;
    //输出参数
    private Class<?> resultType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Class<?> getParamterType() {
        return paramterType;
    }

    public void setParamterType(Class<?> paramterType) {
        this.paramterType = paramterType;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }
}
