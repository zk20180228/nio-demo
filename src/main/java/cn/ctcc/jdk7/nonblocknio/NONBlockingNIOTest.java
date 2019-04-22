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
 *      与Selector一起使用时，Channel必须处于非阻塞模式下
 *
 *      通道触发了一个事件意思是该事件已经就绪。所以，某个channel成功连接到另一个服务器称为“连接就绪”。
 *      一个server socket channel准备好接收新进入的连接称为“接收就绪”。
 *      一个有数据可读的通道可以说是“读就绪”。等待写数据的通道可以说是“写就绪”。
 *      一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，访问“已选择键集（selected key set）”中的就绪通道
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


    /**
     * SocketChannel.open(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));
     * 这种方式创建SocketChannel，是阻塞式的，无法用在非阻塞式。可以看源码
     * @throws Exception
     */
    @Test
    public void client01()throws Exception{

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));

        Selector selector = Selector.open();
        socketChannel.register(selector,SelectionKey.OP_CONNECT);

        selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            if (key.isValid()) {
                if (key.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        ByteBuffer bf = ByteBuffer.allocate(1024);
                        bf.put("你好服务端！".getBytes());
                        bf.flip();
                        while (bf.hasRemaining()) {
                            socketChannel.write(bf);
                        }
                    }
                }
            }
        }

        it.remove();
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
