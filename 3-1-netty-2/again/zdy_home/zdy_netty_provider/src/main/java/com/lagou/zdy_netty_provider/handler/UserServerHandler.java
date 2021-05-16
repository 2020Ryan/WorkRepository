package com.lagou.zdy_netty_provider.handler;


import com.alibaba.fastjson.JSON;
import com.lagou.zdy_netty_common.RpcRequest;
import com.lagou.zdy_netty_common.RpcResponse;
import com.lagou.zdy_netty_provider.annotation.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ChannelHandler.Sharable//管道共享，不然其他访问时独占资源出错
public class UserServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {

    private  static ApplicationContext applicationContextOther;

    private static final Map SERVICE_INSTANCE = new ConcurrentHashMap();

    /**
     * 拿容器中的实例，但是只有那个rpc注解的
     * @param applicationContext
     * @throws BeansException
     */

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> providerServiceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (providerServiceMap.size()>0){
            Set<Map.Entry<String, Object>> entries = providerServiceMap.entrySet();
            for (Map.Entry<String,Object> item : entries
                 ) {
                Object value = item.getValue();
                //判断接口是否被实现
                if(value.getClass().getInterfaces().length == 0){
                    throw new RuntimeException("接口未被实现");
                }
                //默认第一个实现
                String name = value.getClass().getInterfaces()[0].getName();
                //存储到服务列表
                SERVICE_INSTANCE.put(name,value);
            }
        }
        UserServerHandler.applicationContextOther = applicationContext;
    }




    /**
     * 都区管道的事件，子类可以定义返回的类型
     * @param channelHandlerContext
     * @param
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        //接收客户端请求- @！！！！！将msg转化RpcRequest对象
        //RpcRequest rpcRequest = JSON.parseObject(s, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        //处理业
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        //响应请求
        channelHandlerContext.writeAndFlush(rpcResponse);
    }

    private Object handler(RpcRequest request) throws ClassNotFoundException, InvocationTargetException {

        //找bean
        Object serviceBean = SERVICE_INSTANCE.get(request.getClassName());
        if (serviceBean == null) {
            throw new RuntimeException("根据beanName找不到服务,beanName:" + request.getClassName());
        }
        //使用Class.forName进行加载Class文件
        Class<?> clazz = Class.forName(request.getClassName());
       // Object serviceBean = applicationContextOther.getBean(clazz);

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();




        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);

        return fastMethod.invoke(serviceBean, parameters);
    }

}
