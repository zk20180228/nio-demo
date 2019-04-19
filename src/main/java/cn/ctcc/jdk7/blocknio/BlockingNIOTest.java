package cn.ctcc.jdk7.blocknio;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Author: zk
 * @Date: 2019/4/19 10:31
 * @Description:
 * @Modified:
 * @version: V1.0
 *
 * 一、使用 NIO 完成网络通信的三个核心：
 *
 * 1. 通道（Channel）：负责连接
 *
 * 	   java.nio.channels.Channel 接口：
 * 			|--SelectableChannel
 * 				|--SocketChannel
 * 				|--ServerSocketChannel
 * 				|--DatagramChannel
 *
 * 				|--Pipe.SinkChannel
 * 				|--Pipe.SourceChannel
 *
 * 2. 缓冲区（Buffer）：负责数据的存取
 *
 * 3. 选择器（Selector）：是 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的 IO 状况
 *
 */
public class BlockingNIOTest {

    @Test
    public void server01() throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(9898));

        FileChannel fileChannel = FileChannel.open(Paths.get("D:/大鱼海棠.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer bf = ByteBuffer.allocate(1024);

        while(socketChannel.read(bf)!=-1){
            //切换为读模式
            bf.flip();
            fileChannel.write(bf);
            //清空缓冲区
            bf.clear();
        }

        socketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();
    }


    /**
     *     C:\mySofts\纸壁中心\\timg.jpg
     */
    @Test
    public void client01() throws Exception{

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));

        FileChannel fileChannel = FileChannel.open(Paths.get("C:/mySofts/纸壁中心/timg.jpg"), StandardOpenOption.READ);

        ByteBuffer bf = ByteBuffer.allocate(1024);

        while((fileChannel.read(bf))!=-1){

            bf.flip();
            socketChannel.write(bf);
            bf.clear();
        }

        fileChannel.close();
        socketChannel.close();
    }


    @Test
    public void server02()throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //bind方法只用于服务端
        serverSocketChannel.bind(new InetSocketAddress(9898));
        FileChannel fileChannel = FileChannel.open(Paths.get("D:/大鱼海棠.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
        SocketChannel socketChannel = serverSocketChannel.accept();
        ByteBuffer bf = ByteBuffer.allocate(1024);
        while(socketChannel.read(bf)!=-1){
            bf.flip();
            fileChannel.write(bf);
            bf.clear();
        }

        bf.put("服务端收到".getBytes());
        bf.flip();
        socketChannel.write(bf);

        socketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();
    }



    @Test
    public void client02()throws Exception{

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));
        FileChannel fileChannel = FileChannel.open(Paths.get("C:/mySofts/纸壁中心/timg.jpg"), StandardOpenOption.READ);
        ByteBuffer bf = ByteBuffer.allocate(1024);
        while(fileChannel.read(bf)!=-1){
            bf.flip();
            socketChannel.write(bf);
            bf.clear();
        }

        //告知服务端，发送完毕
        socketChannel.shutdownOutput();

        int count=0;
        while((count=socketChannel.read(bf))!=-1){
            bf.flip();
            System.out.println(new String(bf.array(),0,count));
            bf.clear();
        }

        socketChannel.close();
        fileChannel.close();

    }




}
