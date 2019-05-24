package cn.ctcc.jdk7.asyncchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @Author: zk
 * @Date: 2019/5/23 17:19
 * @Description:
 * @Modified:
 * @version: V1.0
 */
public class ClientReq  {

    /**
     * 异步socketchannel
      */
    private AsynchronousSocketChannel asc;

    public ClientReq() throws IOException{
        this.asc = AsynchronousSocketChannel.open();
    }
    public void connect()throws Exception{
        //.get()代表等待到连接完成，而不是直接返回,阻塞到返回结果
        this.asc.connect(new InetSocketAddress("localHost",9898)).get();
    }

    // 客户端写数据
    public void write(String reqStr) throws Exception{
        //异步写出数据
        this.asc.write(ByteBuffer.wrap(reqStr.getBytes()));
        read();
    }

    // 客户端读数据
    private void read() throws Exception {

        ByteBuffer respBuffer = ByteBuffer.allocate(1024);
        //开启异步读取-->读取缓冲区的内容到respBuffer中，.get()会等待到读到数据
        this.asc.read(respBuffer).get();
        respBuffer.flip();
        byte[] respByte = new byte[respBuffer.remaining()];
        respBuffer.get(respByte);

        String respStr = new String(respByte);
        System.err.println("client收到服务端的返回信息===" + respStr);

        //等待服务端返回数据
        System.in.read();
    }

    // 主线程
    public static void main(String[] args) throws Exception {

        ClientReq client = new ClientReq();
        client.connect();
        client.write("Hello Server，你好！");

    }

}