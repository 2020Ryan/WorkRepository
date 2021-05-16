package com.lagou.zdy_netty_provider;

import com.lagou.zdy_netty_provider.server.ProviderServer;
import com.lagou.zdy_netty_provider.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZdyNettyProviderApplication implements CommandLineRunner {

    @Autowired
    ProviderServer providerServer;
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(ZdyNettyProviderApplication.class, args);

    }


    public void run(String... args) throws Exception {

        new Thread(new Runnable() {
            public void run() {
                providerServer.startServer("127.0.0.1", 8899);
            }
        }).start();
    }
}
