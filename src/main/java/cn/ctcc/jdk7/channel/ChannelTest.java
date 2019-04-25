package cn.ctcc.jdk7.channel;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;

/**
 * @Author: zk
 * @Date: 2019/4/18 16:00
 * @Description:
 * @Modified:
 * @version: V1.0
 *
 *一：通道（Channel）->主要用于源节点与目标节点的连接。在javaNIO中，负责缓冲区中的数据的传输，Channel本身不传输数据，需要配合缓冲区来传输数据
 *二：通道的主要实现：
 * java.nio.channels.Channel接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *三：获取通道
 * 1. Java 针对支持通道的类提供了 getChannel() 方法
 * 		本地 IO：
 * 		FileInputStream/FileOutputStream
 * 		RandomAccessFile
 *
 * 		网络IO：
 * 		Socket
 * 		ServerSocket
 * 		DatagramSocket
 * 2 在JDK1.7中的nio2针对各个通道提供了静态方法open
 * 3 jdk1.7中的nio2的Files工具类的newByteChannel()
 *
 * 四 通道之间的数据传输
 * transferForm()
 * transferTo()
 *
 * 五 分散（Scatter）与聚集(Gather)
 * 分散读取：将通道中的数据分散到多个缓冲区
 * 聚集写入：将多个缓冲区的数据聚集到通道中
 *
 * 六 字符集 Charset
 * 编码：字符串->字节数组
 * 解码：字节数组->字符串
 *
 *
 */
public class ChannelTest {


    /**
     *     利用通道完成文件的复制（非直接缓冲区）
     */
    @Test
    public void test01() throws Exception {


        Instant start = Instant.now();

        try (   //jdk1.7新特性，ARM，自动资源管理
                FileInputStream f1 = new FileInputStream(new File("D:/01.avi"));
                FileOutputStream f2 = new FileOutputStream(new File("D:/02.avi"));

                FileChannel fc1 = f1.getChannel();
                FileChannel fc2 = f2.getChannel()
        ) {

            //fc1.transferTo(0,fc1.size(),fc2);
            //创建一个1024个字节点字节缓冲区
            ByteBuffer bb = ByteBuffer.allocate(1024);

            while (fc1.read(bb) != -1) {

                //切换为读模式
                bb.flip();
                fc2.write(bb);

                //清空缓冲区所有内容
                bb.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();

        long m = Duration.between(start, end).toMillis();
        //3024
        System.out.println(m);
    }


    /**
     * 使用直接缓冲区完成文件的复制(内存映射文件)
     */
    @Test
    public void test02(){

        Instant start = Instant.now();

        try (
                FileChannel f1 = FileChannel.open(Paths.get("D:/01.avi"), StandardOpenOption.READ);
                FileChannel f2 = FileChannel.open(Paths.get("D:/02.avi"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {

            //内存映射文件
            MappedByteBuffer mbf1 = f1.map(FileChannel.MapMode.READ_ONLY, 0, f1.size());
            MappedByteBuffer mbf2 = f2.map(FileChannel.MapMode.READ_WRITE, 0, f1.size());
            byte[] bytes = new byte[mbf1.limit()];

            mbf1.get(bytes);

            mbf2.put(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);

        //378
        System.out.println(duration.toMillis());
    }

    /**
     * 通道之间的数据传递
     */
    @Test
    public void test03(){
        Instant start = Instant.now();

        try (
                FileChannel f1 = FileChannel.open(Paths.get("D:/01.avi"), StandardOpenOption.READ);
                FileChannel f2 = FileChannel.open(Paths.get("D:/02.avi"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ) {
            //f1.transferTo(0, f1.size(), f2);
            f2.transferFrom(f1,0,f1.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Instant end = Instant.now();

        //154
        System.out.println(Duration.between(start,end).toMillis());

    }

    /**
     * 分散读取，聚集写入
     */
    @Test
    public void test04() throws  Exception{

        RandomAccessFile raf = new RandomAccessFile("pom.xml", "rw");
        FileChannel fc = raf.getChannel();

        ByteBuffer bf1 = ByteBuffer.allocate(10);
        ByteBuffer bf2 = ByteBuffer.allocate(2048);

        ByteBuffer[] bfs={bf1,bf2};
        //分散读取
        fc.read(bfs);

        for(ByteBuffer bf:bfs){
            bf.flip();
        }

        System.out.println(new String(bf1.array(),0,bf1.limit()));
        System.out.println(new String(bf2.array(),0,bf2.limit()));

        //聚集写入
        RandomAccessFile rw = new RandomAccessFile("2.xml", "rw");
        FileChannel rwChannel = rw.getChannel();
        rwChannel.write(bfs);

        rwChannel.close();
        fc.close();
    }





}
