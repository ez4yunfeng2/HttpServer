package com.demo.Handler;

import com.demo.Connector;
import com.demo.Engine;
import com.demo.HttpHeader;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *Http请求处理
 */
public class HttpHandler implements IHandlerable{
    public static HttpHandler httpHandler;

    /**
     * 初始化HttpHandler
     */
    public static void Initialization(){
        httpHandler = new HttpHandler();
        System.out.println("初始化HttpHandler\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
    private static Connector connector;

    /**
     * 回应http头
     * @param header header字符串
     * @throws IOException IOException
     */
    public static void sendHeader(String header) throws IOException {
        connector.write(header);
    }
    /**
     * 发送http主题
     * @param channel 本地channel
     * @throws IOException IOException
     */
    public static void sendBody(FileChannel channel) throws IOException {
        if(channel == null)
            return;
        connector.write(channel);
    }
    /**
     * 关闭socketchannel
     * @throws IOException IOException
     */
    public static void close() throws IOException {
        connector.close();
    }
    /**
     * 处理Http请求
     * 解析请求头 -- 构建回应头 -- 打开url请求文件 -- 回应
     * @param connector 相应的connector
     */
    @Override
    public void handler(Connector connector,HttpHeader httpHeader){
        if(!connector.isClosed())
            return;
        try {
            FileChannel channel = Engine.PageIO(httpHeader);
            if (channel == null){
                sendHeader(httpHeader.build("404"));
                connector.close();
                return;
            }
            String resp = httpHeader.build("202");
            sendHeader(resp);
            sendBody(channel);
            connector.close();
            System.out.println("[Server] " + httpHeader.getOriginal().split("\\n")[0]);
        } catch (IOException e) {
            System.err.println("[Server] " + e.getMessage());
        }
    }
    public static void setConnector(Connector connector){
        HttpHandler.connector = connector;
    }
}