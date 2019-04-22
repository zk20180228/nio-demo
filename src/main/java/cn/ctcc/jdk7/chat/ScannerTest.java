package cn.ctcc.jdk7.chat;

import java.util.Scanner;

/**
 * @Author: zk
 * @Date: 2019/4/22 9:36
 * @Description:
 * @Modified:
 * @version: V1.0
 */
public class ScannerTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.next();
        System.out.println("你输入的是："+s);
    }

}
