package cn.ctcc.jdk7.asyncchannel;

/**
 * @Author: zk
 * @Date: 2019/5/23 17:16
 * @Description:
 * @Modified:
 * @version: V1.0
 */

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: zk
 * @Date: 2019/5/23 17:17
 * @Description: AIO服务端实现
 */
public class AioServer {

    // 线程池
    private ExecutorService executorService;
    // 异步线程组
    private AsynchronousChannelGroup threadGroup;
    // 异步服务通道
    public AsynchronousServerSocketChannel assc;

    // 开启aio服务端
    public AioServer(int port){
        try {
            // 创建一个缓存线程池
            executorService = Executors.newCachedThreadPool();
            // 创建异步线程组
            threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            // 创建服务器通道
            assc = AsynchronousServerSocketChannel.open(threadGroup);
            // 服务通道和端口绑定
            assc.bind(new InetSocketAddress(port));
            System.err.println("Aio服务通道开启————————port" + port);

            //异步接受连接，连接完成由ServerCompletionHandler处理
            assc.accept(this, new ServerCompletionHandler());

            //一直启动服务
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 主函数
    public static void main(String[] args) {
        new AioServer(9898);
    }

}
