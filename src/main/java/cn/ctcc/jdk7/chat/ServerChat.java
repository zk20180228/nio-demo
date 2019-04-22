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

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"),9898));

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){

            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){

                SelectionKey k=it.next();
                if(k.isValid()){
                    if(k.isAcceptable()){

                        SocketChannel socketChannel = ((ServerSocketChannel)k.channel()).accept();

                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector,SelectionKey.OP_READ);
                    }

                    if(k.isReadable()){

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
                //从selectedKeys中移除
                it.remove();
            }
        }
    }

}
