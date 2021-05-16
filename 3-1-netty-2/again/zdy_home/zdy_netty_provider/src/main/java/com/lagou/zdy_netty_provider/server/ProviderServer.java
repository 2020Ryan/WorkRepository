package com.lagou.zdy_netty_provider.server;

import com.lagou.zdy_netty_common.*;
import com.lagou.zdy_netty_provider.handler.UserServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;

@Service
public class ProviderServer {

    @Autowired
    UserServerHandler userServerHandler;

    /**
     * @param hostName ip地址
     * @param port     端口号
     */
    public static void startServer(String hostName,int port)   {
        NioEventLoopGroup bossGrop = null;
        NioEventLoopGroup workGrop = null;

        try {
            //创建俩个EventLoopGroup

            //处理接收请求
            bossGrop = new NioEventLoopGroup();
            //处理业务逻辑
            workGrop = new NioEventLoopGroup();
            //负责启动前的准备
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //开始初始化
            serverBootstrap.group(bossGrop, workGrop)
                    //nio模型管道模型为nio
                    .channel(NioServerSocketChannel.class)
                    //选择SocketChannel去初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //对最后一个管道的位置添加编码解码
                            // 请求解码
                            pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                            // 响应编码
                            pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                            //业务处理类
                            pipeline.addLast(new UserServerHandler());
                        }
                    });
            //启动同步步执行
            ChannelFuture sync = serverBootstrap.bind(hostName, port).sync();
            System.out.println("provider服务启动");

            sync.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (bossGrop!=null){
                bossGrop.shutdownGracefully();
            }
            if(workGrop!=null){
                workGrop.shutdownGracefully();
            }
        }
    }


}
