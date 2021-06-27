package com.demo.Handler;

import com.demo.Bean.Proxy;
import com.demo.Connector;
import com.demo.HttpHeader;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;

public class ProxyHandler implements IHandlerable{
    public static ProxyHandler proxyHandler;
    public static Proxy proxy;
    private ProxyHandler(){

    }
    public static void Initialization(){
        proxyHandler = new ProxyHandler();
        System.out.println("初始化ProxyHandler\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
    @Override
    public void handler(Connector connector, HttpHeader httpHeader) {
        String header = "";
        try {
            URL url = new URL("http://" + proxy.getAddress() + httpHeader.getRequest_url());
            var conn = url.openConnection();
            conn.connect();
            StringBuilder resp = new StringBuilder();
            resp.append(conn.getHeaderFields().get(null).get(0)).append("\n");
            conn.getHeaderFields().forEach((k,v)->{
                if(k == null)
                    return;
                Iterator<String> iterator = v.listIterator();
                StringBuilder builder = new StringBuilder();
                while (iterator.hasNext())
                    builder.append(iterator.next()).append(",");
                int index = builder.lastIndexOf(",");
                builder.replace(index,index+1,"");
                resp.append(k).append(": ").append(builder.toString()).append("\n");
            });
            resp.append("\n");
            connector.write(resp.toString());
            connector.write(conn.getInputStream().readAllBytes());
            connector.close();
            System.out.println("[Proxy] " + httpHeader.getOriginal().split("\\n")[0]);
        } catch (IOException e) {
            System.err.println("[Proxy] " + httpHeader.getOriginal().split("\\n")[0]);
        }
    }
}
