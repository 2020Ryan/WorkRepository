package com.lagou.zdy_netty_consumer;

import com.lagou.zdy_netty_common.UserService;
import com.lagou.zdy_netty_common.pojo.user;
import com.lagou.zdy_netty_consumer.client.RpcConsumer;
import com.lagou.zdy_netty_consumer.proxy.ServerProxy;

public class ClientBootStrap {

    public static void main(String[] args)throws InterruptedException  {
        UserService userService = (UserService)ServerProxy.createProxy(UserService.class);
        while (true){
            Thread.sleep(2011);
            String netty_client = userService.getWords("netty client");
            System.out.println(userService.getWords("netty client"));

        }

    }
}
