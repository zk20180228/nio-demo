package cn.ctcc.jdk7.pipe;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @Author: zk
 * @Date: 2019/4/19 14:43
 * @Description:
 * @Modified:
 * @version: V1.0
 *
 * Pipe:两个通道之间的数据传输
 *
 */
public class PipeTest {


    @Test
    public void test01() throws Exception{

        //获取管道
        Pipe pipe = Pipe.open();

        //获取用于存数据的通道
        Pipe.SinkChannel sinkChannel = pipe.sink();

        ByteBuffer bf = ByteBuffer.allocate(1024);
        bf.put("你好世界！".getBytes());
        bf.flip();

        sinkChannel.write(bf);

        //获取读数据的通道
        Pipe.SourceChannel sourceChannel = pipe.source();

        ByteBuffer bf02 = ByteBuffer.allocate(1024);
        sourceChannel.read(bf02);

        bf02.flip();

        System.out.println(new String(bf02.array(),0,bf02.limit()));



    }



}
