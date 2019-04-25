package cn.ctcc.jdk7.buffer;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @Author: zk
 * @Date: 2019/4/19 9:13
 * @Description:
 * @Modified:
 * @version: V1.0
 * 一 java NIO中Buffer用来存储数据。缓冲区就是数组，用于存储不同数据类型的数据
 * 根据数据类型的不同（boolean除外），有以下相应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 上述缓冲区的管理方式几乎一样，通过allocate()获取缓冲区
 *二 缓冲区存取数据的两个核心的方法
 * 获取：get()
 * 存入：put()
 *
 *三 缓冲区的四个核心属性
 * capacity:容量，缓冲区的最大存储容量，一旦生命不能改变
 * limit:界限，表示缓冲区中可以操作的数据的大小（limit后的数据不能进行读写）
 * position：位置，表示缓冲区中正在操作数据的位置
 * mark:标记，标记当前position的位置，可以通过reset()恢复到mark的位置
 * 0 <= mark <= position <= limit <= capacity
 *
 * 四 直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过 allocate() 方法分配缓冲区，将缓冲区建立在 JVM 的内存中
 * 直接缓冲区：通过 allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率
 */
public class BufferTest {


    @Test
    public void test01(){

        String str="abcde";

        //创建一个制定大小的缓冲区
        ByteBuffer bf = ByteBuffer.allocate(1024);

        //1024
        System.out.println(bf.capacity());
        //1024
        System.out.println(bf.limit());
        //0
        System.out.println(bf.position());

        System.out.println("-----------------------------------------分隔符01-----------------------------");
        //存储数据
        bf.put(str.getBytes());

        //1024
        System.out.println(bf.capacity());
        //1024
        System.out.println(bf.limit());
        //5
        System.out.println(bf.position());

        //切换为读模式
        bf.flip();
        System.out.println("-----------------------------------------分隔符02-----------------------------");
        //1024
        System.out.println(bf.capacity());
        //5
        System.out.println(bf.limit());
        //0
        System.out.println(bf.position());
        System.out.println("-----------------------------------------分隔符03-----------------------------");

        //读取缓冲区的数据
        byte[] bts = new byte[bf.limit()];
        bf.get(bts);
        //abcde
        System.out.println(new String(bts,0,bts.length));

        //1024
        System.out.println(bf.capacity());
        //5
        System.out.println(bf.limit());
        //5
        System.out.println(bf.position());

        System.out.println("-----------------------------------------分隔符04-----------------------------");
        //重复读 limit不变 position归0
        bf.rewind();
        //1024
        System.out.println(bf.capacity());
        //5
        System.out.println(bf.limit());
        //0
        System.out.println(bf.position());

        System.out.println("-----------------------------------------分隔符05-----------------------------");

        //清空缓冲区，但是数据还存在，只是读不到了 position=0,limit=capacity=1024
        bf.clear();
        //1024
        System.out.println(bf.capacity());
        //1024
        System.out.println(bf.limit());
        //0
        System.out.println(bf.position());

    }


    @Test
    public void test(){

        String str="abcde";

        ByteBuffer bf = ByteBuffer.allocate(str.length());

        bf.put(str.getBytes(),0,2);
        //5
        System.out.println(bf.limit());
        //2
        System.out.println(bf.position());

        bf.mark();

        bf.put(str.getBytes(),2,3);

        //5
        System.out.println(bf.limit());
        //5
        System.out.println(bf.position());

        //切换为读模式
//        bf.flip();
//
//        byte[] bts = new byte[str.length()];
//        bf.get(bts);
//        //abcde
//        System.out.println(new String(bts,0,bts.length));

        //reset()和mark()配合使用时,要保证,mark不能被重置为-1
        bf.reset();
        //5
        System.out.println(bf.limit());
        //2
        System.out.println(bf.position());
        byte[] bts02 = new byte[2];
        bf.get(bts02);
        //cd
        System.out.println(new String(bts02,0,bts02.length));
        //5
        System.out.println(bf.limit());
        //4
        System.out.println(bf.position());

    }


    /**
     * 直接缓冲区
     */
    @Test
    public void test03(){

        ByteBuffer bFDirect = ByteBuffer.allocateDirect(5);
        //true
        System.out.println(bFDirect.isDirect());

    }





}
