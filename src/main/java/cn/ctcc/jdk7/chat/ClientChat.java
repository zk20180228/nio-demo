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

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9898));

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);


        while(true){
            selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){

                SelectionKey k = it.next();
                if(k.isValid()) {
                    SocketChannel sc = (SocketChannel) k.channel();

                    if(k.isConnectable()){
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
                it.remove();
            }
        }

    }
}
