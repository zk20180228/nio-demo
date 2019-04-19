package cn.ctcc.jdk7.nonblocknio;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @Author: zk
 * @Date: 2019/4/19 13:51
 * @Description:
 * @Modified:
 * @version: V1.0
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
 */
public class NONBlockingNIOTest {



    @Test
    public void server01()throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(9898));

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){

            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){

                SelectionKey k=it.next();
                if(k.isValid()){
                    if(k.isAcceptable()){

                        SocketChannel socketChannel = serverSocketChannel.accept();

                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }

                    if(k.isReadable()){

                        SocketChannel channel = (SocketChannel) k.channel();
                        ByteBuffer bf = ByteBuffer.allocate(1024);

                        int len=0;
                        while((len=channel.read(bf))!=-1){
                            bf.flip();
                            System.out.println(new String(bf.array(),0,len));
                            bf.clear();
                        }
                    }
                }
                //从selectedKeys中移除
                it.remove();
            }
        }
    }



    @Test
    public void client01()throws Exception{

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));

        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();

        //socketChannel.register(selector,SelectionKey.OP_CONNECT);

//        while(true){
//
//
//
//
//
//        }

        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put("你好服务端！".getBytes());
        bf.flip();
        while(bf.hasRemaining()){
            socketChannel.write(bf);
        }

        socketChannel.close();

    }



    @Test
    public void server02()throws Exception{

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(9898));
        Selector selector = Selector.open();
        datagramChannel.register(selector,SelectionKey.OP_READ);

        while(selector.select()>0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                SelectionKey k = it.next();
                if(k.isReadable()){

                    ByteBuffer bf = ByteBuffer.allocate(1024);
                    datagramChannel.receive(bf);
                    bf.flip();
                    System.out.println(new String(bf.array(),0,bf.limit()));
                    bf.clear();
                }
                it.remove();
            }
        }

    }



    @Test
    public void client02()throws Exception{

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put("你好，世界！".getBytes());
        bf.flip();
        while(bf.hasRemaining()) {
            datagramChannel.send(bf, new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));
        }
        datagramChannel.close();

    }























    @Test
    public void testIterator(){

        HashSet<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        set.add("4");

        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){

            String s = iterator.next();
            System.out.println(s);
            iterator.remove();
        }
    }


}
