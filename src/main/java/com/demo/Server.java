package com.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 接收客户端的请求数据，并进行解析
 */
public class Server implements Runnable{
    public static String siteName;
    public static int port;
    public static Server server;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Engine defaultEngine;
    private Server(){

    }
    /**
     * 初始化server，初始化相应数据结构
     * @throws IOException IOException
     */
    public static void Initialization() throws IOException {
        server = new Server();
        server.defaultEngine = Engine.defaultEngine;
        server.selector = Selector.open();
        server.serverSocketChannel = ServerSocketChannel.open();
        server.serverSocketChannel.configureBlocking(false);
        server.serverSocketChannel.bind(new InetSocketAddress(port));
        server.serverSocketChannel.register(server.selector, SelectionKey.OP_ACCEPT);
        System.out.println("初始化Server\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
    @Override
    public void run() {
        while (true){
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel == null){
                    Thread.yield();
                    continue;
                }
                Connector connector = Connector.build(socketChannel);
                defaultEngine.register(connector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}