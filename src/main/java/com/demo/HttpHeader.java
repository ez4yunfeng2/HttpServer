package com.demo;

import com.demo.Bean.Host;
import com.demo.Handler.HttpHandler;

import java.io.IOException;
import java.util.*;

/**
 * 设置HttpHeader
 */
public class HttpHeader {
    private String method;
    private String request_url;
    private String version;
    private String status;
    private String reason;
    private String entity_body = "";
    private Map<String,String> resp_headers;
    private Map<String,String> req_headers;
    private String original;
    private Host host;
    private String url;

    public void debug(){
        System.out.println(original);
    }
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    private static final Map<String,HttpHeader> BadHeader = new HashMap<>();
    private static final Map<String,String> contentType = new HashMap<>();
    /**
     * 初始或HttpHeader
     * 初始化 异常Http请求
     * 初始化content-type
     */
    public static void Initialization(){
        Map<String,String> map = new HashMap<>();
        String text = "<p>404 Not Found</p>";
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        map.put("Content-Length",String.valueOf(text.length()));
        HttpHeader httpHeader = new HttpHeader()
                .setStatus("404")
                .setReason("Not Found")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("404",httpHeader);

        map = new HashMap<>();
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        httpHeader = new HttpHeader()
                .setStatus("200")
                .setReason("OK")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("202",httpHeader);

        map = new HashMap<>();
        text = "<p>400 Bad Request</p>";
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        map.put("Content-Length",String.valueOf(text.length()));
        httpHeader = new HttpHeader()
                .setStatus("400")
                .setReason("Bad Request")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("400",httpHeader);


        map = new HashMap<>();
        text = "<p>405 Method Not Allow</p>";
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        map.put("Content-Length",String.valueOf(text.length()));
        httpHeader = new HttpHeader()
                .setStatus("405")
                .setReason("Bad Request")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("405",httpHeader);

        map = new HashMap<>();
        text = "<p>410 Gone</p>";
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        map.put("Content-Length",String.valueOf(text.length()));
        httpHeader = new HttpHeader()
                .setStatus("410")
                .setReason("Bad Request")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("410",httpHeader);

        map = new HashMap<>();
        text = "<p>501 Not Implemented</p>";
        map.put("Content-Type","text/html");
        map.put("Date",new Date().toString());
        httpHeader = new HttpHeader()
                .setStatus("501")
                .setReason("OK")
                .setVersion("HTTP/1.1")
                .setEntity_body(text)
                .setResp_headers(map);
        BadHeader.put("501",httpHeader);

        contentType.put(".html","text/html");
        contentType.put(".txt","text/plain");
        contentType.put(".xml","text/xml");
        contentType.put(".gif","image/gif");
        contentType.put(".png","image/png");
        contentType.put(".jpeg","image/jpeg");
        contentType.put(".ico","image/x-icon");
        contentType.put(".img","application/x-img");
        contentType.put(".jpg","application/x-jpg");
        contentType.put(".js","application/x-javascript");
        contentType.put(".css","text/css");

        System.out.println("初始化HttpHeader\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }
    public HttpHeader(){
        resp_headers = new HashMap<>();
        req_headers = new HashMap<>();
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    /**
     * 读取Http请求头,并解析
     * @param msg header字符串
     * @throws IOException IOException
     */
    public void Request(String msg) throws IOException {
        original = msg;
        String[] lines = msg.split("\n");
        String[] arg = lines[0].split(" ");
        if(arg.length < 3){
            sendBadResp("400",original);
            return;
        }
        method = arg[0];
        request_url = arg[1];
        version = arg[2];
        for (int i=1;i<lines.length;i++){
            arg = lines[i].split(":");
            req_headers.put(arg[0].trim(),arg[1].trim());
        }
        if(!method.equalsIgnoreCase("GET")){
            sendBadResp("501",original);
            return;
        }
        host = new Host(req_headers.get("Host"));
        request_url = getRequest_url().equals("/")?getRequest_url() + "index.html":getRequest_url();

        Response();
    }
    /**
     * 设置 header
     * @param key key
     * @param value value
     */
    public void setHeader(String key,String value){
        resp_headers.merge(key,value,(o,n)->n);
    }
    /**
     * 设置http回应头
     * @throws IOException IOException
     */
    public void Response() throws IOException {
        resp_headers.put("Date",new Date().toString());
        if(request_url == null){
            sendBadResp("404",original);
            return;
        }
        int index = request_url.lastIndexOf(".");
        if(index<=0){
            sendBadResp("404",original);
            return;
        }
        String suffix = request_url.substring(index);
        if (!contentType.containsKey(suffix)){
            sendBadResp("404",original);
            return;
        }
        resp_headers.put("Content-Type",contentType.get(suffix));
    }
    /**
     * 构建http回应体
     * @param msg status
     * @return 返回http头字符串
     */
    public String build(String msg){
        HttpHeader httpHeader = getBadresp(msg);
        StringBuilder builder = new StringBuilder();
        builder.append(httpHeader.version);
        builder.append(" ");
        builder.append(httpHeader.status);
        builder.append(" ");
        builder.append(httpHeader.reason);
        builder.append("\n");
        resp_headers.forEach((k,v)->{
            builder.append(k).append(": ").append(v).append("\n");
        });
        builder.append("\n");
        builder.append(getEntity_body());
        return builder.toString();
    }
    /**
     * 回应异常http请求,同时打印异常信息
     * @param status http状态码
     * @throws IOException IOException
     */
    public static void sendBadResp(String status,String original) throws IOException {
        HttpHeader bad = getBadresp(status);
        HttpHandler.sendHeader(bad.build(status));
        HttpHandler.close();
        throw new IOException(original.split("\\n")[0]);
    }



    public String getMethod() {
        return method;
    }

    public HttpHeader setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getRequest_url() {
        return request_url;
    }

    public HttpHeader setRequest_url(String request_url) {
        this.request_url = request_url;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeader setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public HttpHeader setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public HttpHeader setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getEntity_body() {
        return entity_body;
    }

    public HttpHeader setEntity_body(String entity_body) {
        this.entity_body = entity_body;
        return this;
    }

    public Map<String, String> getResp_headers() {
        return resp_headers;
    }

    public HttpHeader setResp_headers(Map<String, String> resp_headers) {
        this.resp_headers = resp_headers;
        return this;
    }

    public Map<String, String> getReq_headers() {
        return req_headers;
    }

    public HttpHeader setReq_headers(Map<String, String> req_headers) {
        this.req_headers = req_headers;
        return this;
    }

    public static
    HttpHeader getBadresp(String key){
        return BadHeader.getOrDefault(key,BadHeader.get("404"));
    }
}
