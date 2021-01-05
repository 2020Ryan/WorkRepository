package com.lagou.edu.mvcframework.servlet;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.*;
import com.lagou.edu.mvcframework.pojo.Handler;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LagouDispatcherServlet extends HttpServlet {

    //接收mvc的配置文件
    private Properties properties = new Properties();

    //存储扫描到的全限定类名
    private List<String> classNames = new ArrayList<>();

    //ioc容器
    private Map<String,Object> iocMap = new HashMap<>();

    //handlerMapping
    private List<Handler> handlerMapping = new ArrayList<>();

    /**
     * 初始化操作，这里刚好可以运行准备工作
     * 按照源代码直接在这个核心类创建、调用相关方法
     * @param config
     * @throws ServletException
     */

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、加载配置文件，在web.xml配置好相关信息
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");//取出对应文件信息的路径
        doLoadConfig(contextConfigLocation);//具体方法

        //2扫描类上面的注解
        doScan(properties.getProperty("scanPackage"));

        //3、对需要的对象进行初始化，并管理 通过IOC注解实现
        try {
            doInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        //4、实现依赖注入，通过注解
        doAutowired();
        
        //5、构造HandlerMapping处理器映射器，使得路径url和具体方法能一一对应
        initHandlerMapping();
        System.out.println("已经初始化完了");
    }

    //5、构造HandlerMapping处理器映射器，使得路径url和具体方法能一一对应
    private void initHandlerMapping() {
        //iocmap是不是空
        if (iocMap.isEmpty()){return;}

        //扫描所有方法，找requestmapping
        for (Map.Entry<String,Object> entry: iocMap.entrySet()
        ) {
            //获取当前对象class类型
            Class<?> aClass = entry.getValue().getClass();

            if (!aClass.isAnnotationPresent(LagouRequestMapping.class)){
                //没有继续
                continue;
            }

            //类上的url
            String baseUrl = "";
            if (aClass.isAnnotationPresent(LagouRequestMapping.class)){
                LagouRequestMapping annotation = aClass.getAnnotation(LagouRequestMapping.class);
                //requsetMapping("demo")获得demo
                baseUrl = annotation.value();
            }

            String[] namesOnClass = null;
            //设置个标识，就是类上面有安全注解就为true
            Boolean classHas = false;
            if(aClass.isAnnotationPresent(LagouSecurity.class)){
                //就是说接下来的方法知道这个类上有该注解
                classHas = true;
                LagouSecurity annotation = aClass.getAnnotation(LagouSecurity.class);
                namesOnClass = annotation.value();
            }



            //方法上的requestmapping ，先找方法
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                //没有那个注解不管
                if (!method.isAnnotationPresent(LagouRequestMapping.class)){
                    continue;
                }
                //反之处理，同上
                LagouRequestMapping annotation = method.getAnnotation(LagouRequestMapping.class);
                String methodUrl = annotation.value();
                String url = baseUrl +methodUrl;

                //封装handler
                Handler handler = new Handler(entry.getValue(),method,Pattern.compile(url));

                String[] namesOnMethod = null;
                //同时看看有木有安全注解撒
                if (method.isAnnotationPresent(LagouSecurity.class)){
                    //就是说接下来的方法知道这个类上有该注解
                    LagouSecurity annotationSecurity = method.getAnnotation(LagouSecurity.class);
                    namesOnMethod = annotationSecurity.value();
                    List<String> safeName = handler.getSafeName();
                    if (classHas){
                        //先遍历类上的用户
                        for (String name : namesOnClass
                             ) {
                            safeName.add(name);
                        }
                        for (String name : namesOnMethod
                        ) {
                            safeName.add(name);
                        }
                    }else {
                        for (String name : namesOnMethod
                        ) {
                            safeName.add(name);
                        }
                    }
                }
                System.out.println("safeName = = ="+ handler.getSafeName().toString());

                //处理参数位置信息,先获得方法所有参数
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    // 如果是request和response对象,要分开处理，当然这里就按实例方法考虑参数情况
                    if(parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        //参数名称写HttpServletRequest和HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(),j);
                    }else {
                        handler.getParamIndexMapping().put(parameter.getName(),j);
                    }
                }

                //建立url和method之间的映射关系 ，用list 存储
                handlerMapping.add(handler);

            }
        }
    }
    //4、依赖注入
    private void doAutowired() {
        //判断容器有没有对象
        if (iocMap.isEmpty()){
            return;
        }
        //有对象，进行注入，找Autowried
        for (Map.Entry<String,Object> entry: iocMap.entrySet()
             ) {
            //拿value即对象，然后拿字段
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            //在字段中找autowried
            for (int i = 0; i <declaredFields.length ; i++) {
                if (!declaredFields[i].isAnnotationPresent(LagouAutowired.class)){
                    //没有继续
                    continue;
                }

                //有此注解,同理
                LagouAutowired annotation = declaredFields[i].getAnnotation(LagouAutowired.class);
                //类似需要注入的bean id
                String beanName = annotation.value();
                if ("".equals(beanName.trim())){
                    //为空说明没有指定，默认接口注入
                    beanName = declaredFields[i].getType().getName();
                    System.out.println("看看没有指定，默认接口注入获得的Name是不是首字母小写==="+beanName);
                }
                //开启赋值
                declaredFields[i].setAccessible(true);
                try {
                    declaredFields[i].set(entry.getValue(),iocMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //3、创建ioc容器管理对象
    private void doInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //先看看classNames里有没有东西
        if (classNames.size() == 0){
            //没有就不扯了
            return;
        }

        for (int i = 0; i < classNames.size(); i++) {
            //全限定类名
            String classNmae = classNames.get(i);
            //反射获取类
            Class<?> aClass = Class.forName(classNmae);
            //判断具体哪个层,通过注解
            if (aClass.isAnnotationPresent(LagouController.class)){
                //这里就考虑service多值注入情况，controller就小字母小写，在默认注解就是以首字母名字小写
               //是首字母大写 即DemoController
                String simpleName = aClass.getSimpleName();
                //搞个方法，变小写注入时判断
                String lowerFirstName = lowerFirst(simpleName);
                Object o = aClass.newInstance();
                iocMap.put(lowerFirstName,o);
             //service
            }else if (aClass.isAnnotationPresent(LagouService.class)){
                //value配值，直接取注解类再取value
                LagouService annotation = aClass.getAnnotation(LagouService.class);
                String value = annotation.value();
                //判断重命名value是有值
                if (!"".equals(value.trim())){
                    //有了直接放
                    iocMap.put(value,aClass.newInstance());
                }else {
                    //没有跟controller一样处理  首字母小写
                    String newName = lowerFirst(aClass.getSimpleName());
                    iocMap.put(newName,aClass.newInstance());
                }

                //service层往往需要放一份有接口的，因为面向接口开发，会通过接口名注入
                Class<?>[] interfaces = aClass.getInterfaces();
                for (int j = 0; j < interfaces.length; j++) {
                    //接口名为Id key存储
                    iocMap.put(interfaces[j].getName(),aClass.newInstance());
                }
                //其他类注入先不管
            }else{
                continue;
            }
        }
    }
    //整个首字母小写
    public String lowerFirst(String string){
        //算法基础常用东西，string--》char[]
        char[] chars = string.toCharArray();
        //条件为大写字母时
        if ('A'<= chars[0]&&chars[0]<='Z'){
            //相差32位ASCII
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }
    //2、扫描类、注解,考虑递归、考虑自盘路径，所以记得把.替换成/
    private void doScan(String packageUrl) {
        //拿磁盘路径classpath+com...,并替换符号
        String packagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath()+packageUrl.replaceAll("\\.","/");

        File pack = new File(packagePath);
        //获取这条路径的文件
        File[] files = pack.listFiles();
        for (File file: files
             ) {
            //判断是不是目录
            if (file.isDirectory()){
                //是目录就递归,原基础上+文件名
                doScan(packageUrl+"."+file.getName());
            }else if(file.getName().endsWith(".class")){
                //是class文件就可以,全限定类名
                String className = packageUrl+"."+file.getName().replaceAll(".class","");
                classNames.add(className);
            }
        }


    }
    //1、加载配置文件
    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受请求
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }

    /**
     * 处理请求
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取uri、类似/test/test.jsp  url类似http://localhost:8080/test/test.jsp
        String requestURI = req.getRequestURI();
        System.out.println(requestURI);

        //反射调用，需要传入对象和参数，在原来的设计方案中仅仅只存了路径和方法简单的映射关系
        //根据url处理请求handler
        Handler handler = getHandler(requestURI);
        if (handler == null){
            resp.getWriter().write("404，not found");
            return;
        }
        //参数类型绑定，先获取长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        int length = parameterTypes.length;
        Object[] paraValues = new Object[length];

        //接下来从request中取值  req.getParameterMap(), ==> key : name  ,  value: [11,22]
        //?name=sss&&name==ssss222
        Map<String, String[]> parameterMap = req.getParameterMap();
        //遍历map  处理string
        for (Map.Entry<String,String[]> param : parameterMap.entrySet()
             ) {
            //getkey(name)==>名字数组
            String[] paramNames = param.getValue();
            //搞一个标识
            int flag = 0;
            for (String name: paramNames
                 ) {

                for (String safeName : handler.getSafeName()
                     ) {
                    if (safeName.equals(name)){
                        flag += 1;
                    }
                }
            }
            if (flag == 0){
                resp.getWriter().write("Sorry,you have no presssion");
                return;
            }

            //去除value [] eg;[1,2]==>1,2 ;暂时只考虑String
            String value = StringUtils.join(param.getValue(), ",");


            //拿handler中的那个属性进行比较，如果2者匹配上了，就填充
            //先比较没有匹配上
            if (!handler.getParamIndexMapping().containsKey(param.getKey())){
                continue;
            }
            //反之拿出对应索引
            Integer index = handler.getParamIndexMapping().get(param.getKey());

            //这一步就是使Object[] paraValues 这个要填充的数组 按顺序填值
            paraValues[index] = value;
        }
        //开始处理req resp

        Integer reqIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());

        Integer respIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        System.out.println("respIndex=="+respIndex);

        paraValues[reqIndex] = req;

        paraValues[respIndex] = resp;


        //最终是找到匹配上的method  让他执行
        try {//args
            handler.getMethod().invoke(handler.getController(),paraValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    //获取具体指定的handler
    private Handler getHandler(String requestURI) {
        if (handlerMapping.size() == 0){return null;}
        for (Handler handler : handlerMapping
             ) {
            Matcher matcher = handler.getPattern().matcher(requestURI);
            //不匹配继续走
            if (!matcher.matches()){
                continue;
            }
            //匹配返回
            return handler;
        }
        return  null;
    }

}
