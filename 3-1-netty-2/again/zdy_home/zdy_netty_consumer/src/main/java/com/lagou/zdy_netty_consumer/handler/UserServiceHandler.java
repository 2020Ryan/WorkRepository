package com.lagou.zdy_netty_consumer.handler;

import com.lagou.zdy_netty_common.RpcRequest;
import com.lagou.zdy_netty_common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * netty客户端处理类
 */
public class UserServiceHandler extends SimpleChannelInboundHandler<RpcResponse> implements  Callable  {

    //上下文对象
    ChannelHandlerContext context;

    //发送的消息
    RpcRequest requestMsg;

    //服务端的消息
    String responseMsg;

    public void setRequestMsg(RpcRequest requestMsg) {
        this.requestMsg = requestMsg;
    }

    /**
     * 通道读取，锁住被发送的线线程唤醒，保证数据返回和请求是唯一对应
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse s) throws Exception {
        System.out.println("client  channerlreader--->");
        responseMsg = s.getResult().toString();
        System.out.println("请求id:" + s.getRequestId() + ", 返回结果:" + s.getResult());
        notify();
    }

    /**
     * 给定义的context赋值
     * @param ctx
     * @throws Exception
     */
    @Override
    public  void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    /***
     * 发送消息，等到客户端返回再返回值
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        //消息发送
        context.writeAndFlush(requestMsg);
        //等待被唤醒
        wait();
        //此时返回值有唯一对应返回值
        return responseMsg;
    }
}
