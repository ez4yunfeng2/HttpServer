package com.demo.Handler;

import com.demo.Connector;
import com.demo.HttpHeader;

import java.io.IOException;

public class BadReqHandler implements IHandlerable{
    public static BadReqHandler badreqHandler;
    private BadReqHandler(){

    }
    public static void Initialization(){
        badreqHandler = new BadReqHandler();
        System.out.println("初始化BadReqHandler\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
    @Override
    public void handler(Connector connector, HttpHeader httpHeader) {
        httpHeader.setEntity_body("<p>404 NOT FOUND</p>");
        String resp = httpHeader.build("404");
        connector.write(resp);
        try {
            connector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("[BadRequest] " + httpHeader.getOriginal().split("\\.")[0]);
    }
}
