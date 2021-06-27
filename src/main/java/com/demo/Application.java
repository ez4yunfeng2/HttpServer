package com.demo;

import com.demo.Handler.BadReqHandler;
import com.demo.Handler.HttpHandler;
import com.demo.Handler.ProxyHandler;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 整个程序的入口，在这第对基本组件进行初始化
 */
public class Application {
    /**
     * 主程序入口函数
     * @param args 命令行参数
     * @throws IOException IOException
     * @throws InterruptedException InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpHandler.Initialization();
        ProxyHandler.Initialization();
        HttpHeader.Initialization();
        BadReqHandler.Initialization();
        Engine.Initialization();
        Config.Initialization();
        Server.Initialization();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(Server.server);
        executorService.execute(Engine.defaultEngine);
        System.out.println("服务器初始化完毕\n");
    }
}