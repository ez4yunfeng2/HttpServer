package com.demo;

import com.demo.Bean.Host;
import com.demo.Bean.Proxy;
import com.demo.Handler.BadReqHandler;
import com.demo.Handler.HttpHandler;
import com.demo.Handler.ProxyHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
/**
 * 负责具体事务的处理
 */
public class Engine implements Runnable{
    public static Engine defaultEngine;
    public static HttpHandler httpHandler;
    public static ProxyHandler proxyHandler;
    public static BadReqHandler badreqHandler;
    private Set<Host> hostSet;
    private Set<Proxy> proxySet;
    private final PriorityBlockingQueue<Connector> priorityBlockingQueue = new PriorityBlockingQueue<>();
    private Engine() {

    }
    public static Host getHost(Host host){
        Host host1  = null;
        for(Host h : defaultEngine.hostSet)
            if(h.equals(host)){
                host1 = h;
            }

        return host1;
    }
    public static Proxy getProxy(Host proxy){
        Proxy proxy1 = null;
        for(Proxy p : defaultEngine.proxySet)
            if(p.getTopdomainname().equals(proxy.getTopDomainName()))
                proxy1 = p;
        return proxy1;
    }
    /**
     * 注册Host，并将host加入hostSet
     * @param host 需要注册的
     */
    public void register(Host host){
        System.out.println("\tHost: "+host.getHostName() + "\tDir: " + host.getDirPath());
        hostSet.add(host);
    }

    /**
     * 注册代理
     * @param proxy proxy实体
     */
    public void register(Proxy proxy){
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(proxy.getAddress(),proxy.getPort()),1000);
            if(socket.isConnected()){
                System.out.println("\tProxy: " + proxy.getAddress() + "\tDomainName: " + proxy.getTopdomainname() + "\tstatus: online" );
                proxySet.add(proxy);
            }
        } catch (IOException e) {
            System.err.println("\tProxy: " + proxy.getAddress() + "\tDomainName: " + proxy.getTopdomainname() + "\tstatus: offline" );
        }
    }
    /**
     * 注册Connector，将connector加入事务处理队列
     * @param connector 需要进行注册的connector
     */
    public void register(Connector connector){
        if(!priorityBlockingQueue.contains(connector)){
            priorityBlockingQueue.add(connector);
        }
    }
    /**
     * 初始化引擎，对相关数据结构进行初始化
     * @throws InterruptedException InterruptedException
     */
    public static void Initialization() throws InterruptedException {
        defaultEngine = new Engine();
        defaultEngine.hostSet = new HashSet<>();
        defaultEngine.proxySet = new HashSet<>();
        httpHandler = HttpHandler.httpHandler;
        proxyHandler = ProxyHandler.proxyHandler;
        badreqHandler = BadReqHandler.badreqHandler;
        System.out.println("初始化Engine\t\t" + "\033[32;4m" + "OK" + "\033[0m");
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try {
                Connector connector = priorityBlockingQueue.take();
                parse(connector);
            } catch (InterruptedException | IOException e) {
                System.err.println("[Connector] " + "无效连接");
            }
        }
    }

    /**
     * 解析http请求，依据请求分流至HTTPHANDLER 或 PROXYHANDLER
     * @param connector 链接器
     * @throws IOException IOException
     */
    public static void parse(Connector connector) throws IOException {
        HttpHandler.setConnector(connector);
        HttpHeader httpHeader = new HttpHeader();
        String header = connector.read();
        if(header == null)
            return;
        httpHeader.Request(header);
        if(getHost(httpHeader.getHost())!=null){
            httpHandler.handler(connector,httpHeader);
            return;
        }
        ProxyHandler.proxy = getProxy(httpHeader.getHost());
        if(ProxyHandler.proxy!=null){
            proxyHandler.handler(connector,httpHeader);
            return;
        }
        badreqHandler.handler(connector,httpHeader);
    }
    /**
     * 获取本地文件请求通道,依据url打开相应本地通道
     * @param httpHeader http请求
     * @return 本地Channel
     * @throws IOException IOException
     */
    public static FileChannel PageIO(HttpHeader httpHeader) throws IOException {
        String url = httpHeader.getRequest_url();
        FileChannel channel = null;
        Host host = getHost(httpHeader.getHost());
        if(host==null){
            HttpHeader.sendBadResp("404",httpHeader.getOriginal());
            return null;
        }
        Path path = Paths.get(host.getDirPath() + url);
        if(!path.toFile().exists()) {
            HttpHeader.sendBadResp("404",httpHeader.getOriginal());
            return null;
        }
        channel = new FileInputStream(path.toFile()).getChannel();
        return channel;
    }
}