package cn.ctcc.jdk7.chat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author: zk
 * @Date: 2019/4/19 17:12
 * @Description:
 * @Modified:
 * @version: V1.0
 */
public class ClientChat {


    public static void main(String[] args) throws Exception{
        //创建侦户端套接字的侦听通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置为非阻塞模式
        socketChannel.configureBlocking(false);
        //连接服务端的套接字，非阻塞模式下必须用connect方法
        socketChannel.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));
        //创建多路复用器
        Selector selector = Selector.open();
        //将此socketChannel注册到多多路复用器上，并监听连接就绪事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);


        while(true){
            //会阻塞，直到至少有一IO事件就绪
            selector.select();
            //得到就绪事件集
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                //得到密钥
                SelectionKey k = it.next();
                if(k.isValid()) {
                    //得到此密钥对绑定的通道
                    SocketChannel sc = (SocketChannel) k.channel();
                    //判断是否是连接就绪事件
                    if(k.isConnectable()){
                        //判断是否完成连接
                        if(sc.finishConnect()){
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("客户端:");
                            String str = scanner.next();
                            byte[] bts = str.getBytes();
                            ByteBuffer allocate = ByteBuffer.allocate(bts.length);
                            allocate.put(bts);
                            allocate.flip();
                            while(allocate.hasRemaining()){
                                sc.write(allocate);
                            }

                            sc.register(selector,SelectionKey.OP_READ);
                        }
                    }

                    //判断是否是读事件
                    if (k.isReadable()) {

                        ByteBuffer bf = ByteBuffer.allocate(1024);
                        int len=0;
                        len=sc.read(bf);
                        while(len!=0&&len!=-1){
                            bf.flip();
                            System.out.println("服务端:");
                            System.out.println(new String(bf.array(), 0, len));
                            bf.clear();
                            len=sc.read(bf);
                        }


                        System.out.println("客户端:");
                        Scanner scanner = new Scanner(System.in);
                        String str = scanner.next();
                        bf.put(str.getBytes());
                        bf.flip();
                        while (bf.hasRemaining()) {
                            sc.write(bf);
                        }
                    }
                }
                //从此事件集移除已经处理过的事件
                it.remove();
            }
        }

    }
}
