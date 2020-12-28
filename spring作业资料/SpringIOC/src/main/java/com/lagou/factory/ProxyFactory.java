package com.lagou.factory;

import com.lagou.pojo.Account;
import com.lagou.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {



    //对事物控制进行代理
    private TransactionManager transactionManager;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }



    public Object getJdkProxy(Object obj) {

        return Proxy.newProxyInstance(this.getClass().getClassLoader(), obj.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Object result = null;
                //本质是通过try catch 来控制整个事务管理
                try {
                    //开始事务
                    transactionManager.beginTransaction();
                    //原有的业务逻辑调用
                    result = method.invoke(obj,args);
                    //提交
                    transactionManager.commit();
                }catch (Exception e){
                    e.printStackTrace();
                    //出了异常，就不提交
                    transactionManager.rollback();
                    //将异常抛出
                    throw e.getCause();
                }

                return result;

            }
        });

    }


    /**
     * 使用cglib动态代理生成代理对象
     *
     */
    public Object getCglibProxy(Object obj) {
        return  Enhancer.create(obj.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object result = null;
                try{
                    // 开启事务(关闭事务的自动提交)
                    transactionManager.beginTransaction();

                    result = method.invoke(obj,objects);

                    // 提交事务

                    transactionManager.commit();
                }catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback();

                    // 抛出异常便于上层servlet捕获
                    throw e;

                }
                return result;
            }
        });
    }
}
