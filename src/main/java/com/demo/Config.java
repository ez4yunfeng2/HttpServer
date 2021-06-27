package com.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.Bean.Host;
import com.demo.Bean.Proxy;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 读取server.json配置文件，进行基础设置
 */
public class Config {
    /**
     * 初始化服务器配置,从server.json读取服务器配置: 端口，主域名，各主机名
     * @throws IOException IOException
     */
    public static void Initialization() throws IOException {
        FileChannel channel = new FileInputStream("server.json").getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int n = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ( (n = channel.read(byteBuffer)) > 0 ){
            byteBuffer.flip();
            stringBuilder.append(new String(byteBuffer.array()).trim());
        }
        JSONObject jsonObject =  JSON.parseObject(stringBuilder.toString());
        Server.siteName = jsonObject.getString("siteName");
        Server.port = jsonObject.getInteger("listen");
        JSONArray array=  jsonObject.getJSONArray("hosts");
        array.stream().map(json->JSON.parseObject(json.toString(), Host.class))
                .forEach(Engine.defaultEngine::register);
        array = jsonObject.getJSONArray("proxys");
        array.stream().map(json->JSON.parseObject(json.toString(), Proxy.class))
                .forEach(Engine.defaultEngine::register);
        System.out.println("初始化Config\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
}