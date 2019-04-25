package cn.ctcc.jdk7.newnio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;

/**
 * @Author: zk
 * @Date: 2019/4/19 11:26
 * @Description:
 * @Modified:
 * @version: V1.0
 */
public class NIO2Test {


    @Test
    public void test01(){
        Path path = Paths.get("e:/", "nio/hello.txt");

        //true
        System.out.println(path.endsWith("hello.txt"));
        //true
        System.out.println(path.startsWith("e:/"));
        //true
        System.out.println(path.isAbsolute());
        //hello.txt
        System.out.println(path.getFileName());

        for (int i = 0; i < path.getNameCount(); i++) {
            //nio
            //hello.txt
            System.out.println(path.getName(i));
        }

        boolean b = Files.exists(path);
        //false
        System.out.println(b);

    }

    /**
     * Paths 提供的 get() 方法用来获取 Path 对象：
     * Path get(String first, String … more) : 用于将多个字符串串连成路径。
     * Path 常用方法：
     * boolean endsWith(String path) : 判断是否以 path 路径结束
     * boolean startsWith(String path) : 判断是否以 path 路径开始
     * boolean isAbsolute() : 判断是否是绝对路径
     * Path getFileName() : 返回与调用 Path 对象关联的文件名
     * Path getName(int idx) : 返回的指定索引位置 idx 的路径名称
     * int getNameCount() : 返回Path 根目录后面元素的数量
     * Path getParent() ：返回Path对象包含整个路径，不包含 Path 对象指定的文件路径
     * Path getRoot() ：返回调用 Path 对象的根路径
     * Path resolve(Path p) :将相对路径解析为绝对路径
     * Path toAbsolutePath() : 作为绝对路径返回调用 Path 对象
     * String toString() ： 返回调用 Path 对象的字符串表示形式
     */
    @Test
    public void test2(){
        Path path = Paths.get("e:/nio/hello.txt");

        //e:\nio
        System.out.println(path.getParent());
        //e:\
        System.out.println(path.getRoot());

		Path path3 = path.resolve("hello.txt");
		//e:\nio\hello.txt\hello.txt
		System.out.println(path3);

        Path path2 = Paths.get("1.jpg");
        Path newPath = path2.toAbsolutePath();
        //E:\java文档\HelpDoc\java基础-特性\nio-demo\1.jpg
        System.out.println(newPath);
        //false
        System.out.println(Files.exists(path2));
        //e:\nio\hello.txt
        System.out.println(path.toString());
    }


    /**
     *     Files.copy
     */
    @Test
    public void test03() throws Exception{
        Path path1 = Paths.get("e:/nio/hello.txt");
        Path path2 = Paths.get("e:/nio/hello2.txt");

        Files.copy(path1,path2,StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Files.createDirectory
     * Files.createFile
     * Files.delete
     * Files.move
     * Files.size
     * @throws Exception
     */
    @Test
    public void test04()throws Exception{

        Path path1 = Paths.get("e:/nio/nio2");
        Files.createDirectory(path1);

        Path path2 = Paths.get("e:/nio/hello2.txt");
        Files.createFile(path2);

        Files.deleteIfExists(path2);
    }


    /**
     * Files常用方法：
     * Path copy(Path src, Path dest, CopyOption … how) : 文件的复制
     * Path createDirectory(Path path, FileAttribute<?> … attr) : 创建一个目录
     * Path createFile(Path path, FileAttribute<?> … arr) : 创建一个文件
     * void delete(Path path) : 删除一个文件
     * Path move(Path src, Path dest, CopyOption…how) : 将 src 移动到 dest 位置
     * long size(Path path) : 返回 path 指定文件的大小
     */
    @Test
    public void test05() throws IOException {
        Path path1 = Paths.get("e:/nio/hello2.txt");
        Path path2 = Paths.get("e:/nio/hello7.txt");

        System.out.println(Files.size(path2));

//		Files.move(path1, path2, StandardCopyOption.ATOMIC_MOVE);
    }


    /**
     * Files常用方法：用于判断
     * boolean exists(Path path, LinkOption … opts) : 判断文件是否存在
     * boolean isDirectory(Path path, LinkOption … opts) : 判断是否是目录
     * boolean isExecutable(Path path) : 判断是否是可执行文件
     * boolean isHidden(Path path) : 判断是否是隐藏文件
     * boolean isReadable(Path path) : 判断文件是否可读
     * boolean isWritable(Path path) : 判断文件是否可写
     * boolean notExists(Path path, LinkOption … opts) : 判断文件是否不存在
     * public static <A extends BasicFileAttributes> A readAttributes(Path path,Class<A> type,LinkOption... options) : 获取与 path 指定的文件相关联的属性。
     */
    @Test
    public void test06() throws IOException{
        Path path = Paths.get("pom.xml");
//		System.out.println(Files.exists(path, LinkOption.NOFOLLOW_LINKS));

        BasicFileAttributes readAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        System.out.println(readAttributes.creationTime());
        System.out.println(readAttributes.lastModifiedTime());

        DosFileAttributeView fileAttributeView = Files.getFileAttributeView(path, DosFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);

        fileAttributeView.setHidden(false);
    }


    /**
     * Files常用方法：用于操作内容
     * SeekableByteChannel newByteChannel(Path path, OpenOption…how) : 获取与指定文件的连接，how 指定打开方式。
     * DirectoryStream newDirectoryStream(Path path) : 打开 path 指定的目录
     * InputStream newInputStream(Path path, OpenOption…how):获取 InputStream 对象
     * OutputStream newOutputStream(Path path, OpenOption…how) : 获取 OutputStream 对象
     */
    @Test
    public void test7() throws IOException{
       // SeekableByteChannel newByteChannel = Files.newByteChannel(Paths.get("1.jpg"), StandardOpenOption.READ);

        DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get("e:/"));

        for (Path path : newDirectoryStream) {
            System.out.println(path);
        }
    }


    /**
     *     自动资源管理：自动关闭实现 AutoCloseable 接口的资源
     */
    @Test
    public void test8(){
        try(FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
            FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE)){

            ByteBuffer buf = ByteBuffer.allocate(1024);
            inChannel.read(buf);
            buf.flip();
            outChannel.write(buf);

        }catch(IOException e){

        }
    }

}
