package com.demo;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
/**
 * 链接器 负责对Socket进行操作
 */
public class Connector implements Comparable {
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private static final int BSIZE = 1024;
    public SocketChannel getSocketChannel(){
        return socketChannel;
    }
    private Connector() { }
    /**
     * 创建Connector
     * @param socketChannel 打开的socketchannel
     * @return 返回创建的connector
     */
    public static Connector build(SocketChannel socketChannel){
        Connector connector = new Connector();
        connector.socketChannel = socketChannel;
        connector.byteBuffer = ByteBuffer.allocate(1024);
        return connector;
    }
    /**
     * 读取http请求头
     * @return 返回socket读取的内容
     * @throws IOException IOException
     */
    public String read() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int n;
        do {
            byteBuffer.clear();
            n = socketChannel.read(byteBuffer);
            byteBuffer.flip();
            stringBuilder.append(new String(byteBuffer.array()).trim());
        } while (n > 1024);
        try {
            return URLDecoder.decode(stringBuilder.toString(), String.valueOf(Charset.defaultCharset()));
        }catch (IllegalArgumentException e){
            return stringBuilder.toString();
        }
    }
    /**
     * 发送回应
     * @param msg 写入channel的字符串
     */
    public void write(String msg){
        if(socketChannel.socket().isClosed())
            return;
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        byteBuffer.clear();
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送回应
     * @param bytes 写入channel的byte数组
     */
    public void write(byte[] bytes){
        if(socketChannel.socket().isClosed())
            return;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.clear();
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            System.err.println("[Connector] 主机中的软件中止了一个已建立的连接");
        }
    }
    /**
     * 从已打开的本地通道发送数据
     * @param channel 已打开的本地通道
     * @throws IOException IOException
     */
    public void write(FileChannel channel) throws IOException {
        channel.transferTo(0,channel.size(),socketChannel);
    }
    /**
     * 关闭通道
     * @throws IOException IOException
     */
    public void close() throws IOException {
        socketChannel.close();
    }
    /**
     * 返回通道状态
     * @return 通道状态
     */
    public boolean isClosed(){
        return socketChannel.isOpen();
    }
    @Override
    public String toString() {
        return "Connector{" +
                "socketChannel=" + socketChannel +
                ", byteBuffer=" + byteBuffer +
                '}';
    }
    @Override
    public int compareTo(Object o) {
        return 1;
    }
}