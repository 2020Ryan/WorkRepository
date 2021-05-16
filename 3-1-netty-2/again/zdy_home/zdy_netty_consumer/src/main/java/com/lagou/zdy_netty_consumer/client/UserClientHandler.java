package com.lagou.zdy_netty_consumer.client;

import com.lagou.zdy_netty_common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

public class UserClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements Callable {

    private ChannelHandlerContext context;
    //发送的消息
    private String requestMsg;
    //服务端的消息
        private String responseMsg;



    public void channelActive(ChannelHandlerContext ctx){
        context = ctx;
    }

    //收到服务器线程数据，唤醒等待线程
    public synchronized  void channelRead0(ChannelHandlerContext ctx,RpcResponse msg){
        responseMsg = msg.getResult().toString();
        notify();
    }

    //写出数据等待唤醒
    public  synchronized Object call() throws InterruptedException {
        context.writeAndFlush(requestMsg);
        wait();
        return responseMsg;
    }

    //设置参数
    public void setRequestMsg(String requestMsg) {
        this.requestMsg = requestMsg;
    }
}
