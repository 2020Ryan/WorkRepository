package com.lagou.zdy_netty_provider.service;

import com.lagou.zdy_netty_common.JSONSerializer;
import com.lagou.zdy_netty_common.RpcDecoder;
import com.lagou.zdy_netty_common.RpcRequest;
import com.lagou.zdy_netty_common.UserService;
import com.lagou.zdy_netty_common.pojo.user;
import com.lagou.zdy_netty_provider.annotation.RpcService;
import com.lagou.zdy_netty_provider.handler.UserServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RpcService
public class UserServiceImpl implements UserService {


    public String getWords(String id) {
        System.out.println(id+" working succsess!");
        return "ok~";
    }
}
