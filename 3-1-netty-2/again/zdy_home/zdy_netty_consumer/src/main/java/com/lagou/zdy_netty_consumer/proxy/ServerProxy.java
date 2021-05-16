package com.lagou.zdy_netty_consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.lagou.zdy_netty_common.RpcRequest;
import com.lagou.zdy_netty_common.RpcResponse;
import com.lagou.zdy_netty_consumer.client.RpcConsumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 客户端代理类-创建代理对象
 * 1.封装request请求对象
 * 2.创建RpcClient对象
 * 3.发送消息
 * 4.返回结果
 */
public class ServerProxy {

    public static Object createProxy(Class serviceClass){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)throws Throwable  {

                    //要调用远程方法，先建立相关参数
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(args);

                    //要开始发送相关服务，所以需要连接netty，准备相关参数
                    RpcConsumer consumer = new RpcConsumer("127.0.0.1",8899);
                    try{
                        //利用netty发送JSON消息
                        Object responMSG = consumer.send(rpcRequest);
                        //服务端消息
                       // RpcResponse rpcResponse = JSON.parseObject(responMSG.toString(), RpcResponse.class);
                        /*if (rpcResponse.getError()!=null){
                            throw  new RuntimeException(rpcResponse.getError());
                        }*/
                        //返回给调用的

                        //Object result = rpcResponse.getResult();
                        //json转成返回的需要的类型
                        return responMSG.toString();
                    }catch (Exception e){
                        throw e;
                    }finally {
                        consumer.close();
                    }

            }
        });
    }
}
