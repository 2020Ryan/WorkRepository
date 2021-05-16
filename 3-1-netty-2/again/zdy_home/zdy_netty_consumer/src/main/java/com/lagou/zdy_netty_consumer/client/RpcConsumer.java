package com.lagou.zdy_netty_consumer.client;

import com.alibaba.fastjson.JSON;
import com.lagou.zdy_netty_common.*;
import com.lagou.zdy_netty_consumer.handler.UserServiceHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.awt.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 客户端
 * 1.连接Netty服务端
 * 2.提供给调用者主动关闭资源的方法
 * 3.提供消息发送的方法
 */
public class RpcConsumer {

    //创建线程池对象,根据cpu调度创建
    private   ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private   UserServiceHandler userClientHandler = new UserServiceHandler() ;

    private Channel channel ;
    private EventLoopGroup group ;
    private String ip;

    private int port;

    public RpcConsumer(String ip, int port){
        this.ip = ip;
        this.port = port;
        initClient();
    }





    /**
     * 初始化netty
     */
    public  void initClient()  {
        //userClientHandler = new UserClientHandler();

        //创建线程池
        try {
            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    //设置通道为NIO
                    .channel(NioSocketChannel.class)
                    //设定协议
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    //设置监听
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //创建管道
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //设置编码

                            pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                            pipeline.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));

                            pipeline.addLast(userClientHandler);
                        }
                    });
            //连接Netty服务端
            this.channel = bootstrap.connect(ip, port).sync().channel();

        }catch (Exception e){
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }
            if (group != null) {
                group.shutdownGracefully();
            }
        }

    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    /**
     * 提供消息发送的方法
     */
    public Object send(RpcRequest msg) throws ExecutionException, InterruptedException {
        userClientHandler.setRequestMsg(msg);
        Future submit = executorService.submit(userClientHandler);
        return submit.get();
    }
}
