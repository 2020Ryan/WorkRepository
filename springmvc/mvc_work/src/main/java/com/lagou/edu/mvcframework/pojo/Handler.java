package com.lagou.edu.mvcframework.pojo;

import javax.sound.midi.MetaEventListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 封装需要的参数
 */
public class Handler {

    private Object controller; // method.invoke(obj,)

    //方法
    private Method method;

    // spring中url是支持正则的
    private Pattern pattern;

    // 参数顺序,是为了进行参数绑定，key是参数名，value代表是第几个参数eg:现有的controller中的 name <name,2>
    private Map<String,Integer> paramIndexMapping;

    //存储可以访问该类中method的用户，这里就名字代替了
    private List<String> safeName ;

    public List<String> getSafeName() {
        return safeName;
    }

    public void setSafeName(List<String> safeName) {
        this.safeName = safeName;
    }

    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        this.paramIndexMapping = new HashMap<>();
        this.safeName = new ArrayList<>();
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}
