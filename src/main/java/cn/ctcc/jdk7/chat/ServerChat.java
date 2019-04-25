package cn.ctcc.jdk7.chat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author: zk
 * @Date: 2019/4/19 17:08
 * @Description:
 * @Modified:
 * @version: V1.0
 */
public class ServerChat {


    public static void main(String[] args) throws Exception {

        //获取服务端的侦听通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //设置此serverSocketChannel的服务端套接字
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),9898));
        //创建多路复用器
        Selector selector = Selector.open();
        //将此serverSocketChannel注册到多路复用器，并监听此通道的接收就绪事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            //会阻塞，直到至少有一事件准备就绪
            selector.select();
            //获取准备就绪的选择键集
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                //得到密钥
                SelectionKey k=it.next();
                //判断该密钥是否有效
                if(k.isValid()){
                    //判断是否是接收事件
                    if(k.isAcceptable()){
                        //接收，得到连接的客户端
                        SocketChannel socketChannel = ((ServerSocketChannel)k.channel()).accept();
                        //设置为非阻塞模式
                        socketChannel.configureBlocking(false);
                        //将此socketChannel注册到选择器上，并监听其读事件
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }

                    //判断是否是读事件
                    if(k.isReadable()){
                        //得到当前的客户端
                        SocketChannel channel = (SocketChannel) k.channel();
                        ByteBuffer bf = ByteBuffer.allocate(1024);

                        int len=0;
                        len=channel.read(bf);
                        while(len!=-1&&len!=0){
                            bf.flip();
                            System.out.println("客户端:");
                            System.out.println(new String(bf.array(),0,len));
                            bf.clear();
                            len=channel.read(bf);
                        }

                        //向客户端输出：
                        System.out.println("服务端:");
                        Scanner scanner = new Scanner(System.in);
                        String str = scanner.next();

                        bf.put(str.getBytes());
                        bf.flip();
                        while (bf.hasRemaining()){
                            channel.write(bf);
                        }
                    }
                }
                //从此事件集中移除已经处理过的事件
                it.remove();
            }
        }
    }

}
