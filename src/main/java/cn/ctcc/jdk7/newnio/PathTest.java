package cn.ctcc.jdk7.newnio;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @Author: zk
 * @Date: 2019/4/16 10:35
 * @Description:  JDK1.7中,Path接口表示的是一个与平台无关的路径（文件和目录都用Path表示）。
 *                JDK1.7以前：File类不能利用特定文件系统的特性，且性能不高，抛出的异常也太抽象
 * @Modified:
 * @version: V1.0
 */
public class PathTest {


    public static void main(String[] args) {

        Path path = Paths.get("E:\\java文档\\HelpDoc\\java基础-特性\\nio-demo\\");
        //E:\java文档\HelpDoc\java基础-特性\nio-demo
        System.out.println(path);
        //nio-demo
        System.out.println(path.getFileName());
        Path absolutePath = path.toAbsolutePath();
        //E:\java文档\HelpDoc\java基础-特性\nio-demo
        System.out.println(absolutePath);

        Iterator<Path> iterator = path.iterator();
        while (iterator.hasNext()){
            //java文档
            //HelpDoc
            //java基础-特性
            //nio-demo
            System.out.println(iterator.next());
        }

        //true
        System.out.println(path.endsWith("nio-demo"));
        //true
        System.out.println(path.startsWith("E:\\java文档"));

        FileSystem fileSystem = path.getFileSystem();

        System.out.println(fileSystem);
        //\
        System.out.println(fileSystem.getSeparator());
        //true
        System.out.println(fileSystem.isOpen());

        Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
        Iterator<Path> pathIterator = rootDirectories.iterator();
        while (pathIterator.hasNext()) {
            //C:\
            //D:\
            //E:\
            //F:\
            System.out.println(pathIterator.next());
        }

        //true
        System.out.println(path.isAbsolute());
        //E:\java文档\HelpDoc\java基础-特性
        System.out.println(path.getParent());
        //E:\
        System.out.println(path.getRoot());


    }



}
