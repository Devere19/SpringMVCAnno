package cn.edu.guet.mvc;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 11:46
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) throws URISyntaxException, UnsupportedEncodingException {

        String controllerPackageName ="cn.edu.guet.controller";


        // 把包名转换成路径
        URI uri = Configuration.class.getResource("/" + controllerPackageName.replace(".", "/")).toURI();
        System.out.println(uri.toString());
    }
}
