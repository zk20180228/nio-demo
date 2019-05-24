package cn.ctcc.jdk7.asyncchannel;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;


/**
 * @Author: zk
 * @Date: 2019/5/23 17:18
 * @version: V1.0
 */

public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

    @Override
    public void completed(AsynchronousSocketChannel asc, AioServer aioServer) {
        //继续异步接受连接，连接完成由ServerCompletionHandler处理
        aioServer.assc.accept(aioServer, this);

        //连接完成后读取数据
        doRead(asc);
    }

    /**
     * 读取客户端的请求信息
     * @param asc
     */
    private void doRead(final AsynchronousSocketChannel asc) {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读取数据数据读取到buffer，再把buffer传递到completed方法
        asc.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            // 重写没有实现的方法
            public void completed(Integer reqByteLength, ByteBuffer byteBuffer) {
                // 读取数据后。整理数据，重新复位
                byteBuffer.flip();
                // 获取读取的字节数
                System.err.println("server---获取客户端的请求的字节长度：" + reqByteLength);
                try {
                    // 获取请求端数据
                    String reqStr = new String(byteBuffer.array()).trim();
                    System.err.println("server---获取客户端的请求的请求数据为：" + reqStr);
                    // 处理请求信息
                    handlerReq(asc, reqStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }

        });
    }

    /**
     * 处理客户请求信息
     * @param asc
     * @param reqStr 客户请求信息
     * @throws UnsupportedEncodingException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void handlerReq(AsynchronousSocketChannel asc, String reqStr) throws Exception {

        // 处理请求，得到相应的返回信息
        String respStr = "服务器返回信息是："+ reqStr;
        byte[] respStrByte = respStr.getBytes();

        ByteBuffer respStrBuffer = ByteBuffer.allocate(1024);
        respStrBuffer.put(respStrByte);
        respStrBuffer.flip();
        // 异步返回响应信息
        asc.write(respStrBuffer);

        System.err.println("服务端响应成功：" +respStr);
    }

    @Override
    public void failed(Throwable exc, AioServer attachment) {
        exc.printStackTrace();
    }

}