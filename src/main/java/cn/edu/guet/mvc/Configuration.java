package cn.edu.guet.mvc;

import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 11:10
 * @Version 1.0
 */
public class Configuration {

    public Map<String,ControllerMapping> config() throws URISyntaxException, UnsupportedEncodingException, ClassNotFoundException {

        Map<String, ControllerMapping> controllerMapping = new HashMap<>();

        String controllerPackageName ="cn.edu.guet.controller";


        // 把包名转换成路径
        URI uri = Configuration.class.getResource("/" + controllerPackageName.replace(".", "/")).toURI();


        //筛选出包中的class字节码文件
        File file = new File(uri);
        String[] controllerClassNames = file.list();
        System.out.println(controllerClassNames.length);
        for (String className : controllerClassNames) {
            if (className.endsWith(".class")) {
                String fullClassName = controllerPackageName + "." + StringUtils.substringBefore(className, ".class");
                System.out.println("全类名:"+fullClassName);
                Class controllerClass = Class.forName(fullClassName);
                if (controllerClass.isAnnotationPresent(Controller.class)){
                    Method[] methods = MethodUtils.getMethodsWithAnnotation(controllerClass, RequestMapping.class);
                    //现在已经拿到了所有的带有@Controller和@RequestMapping注解的类和方法，存放在一个map中，用的时候拿
                    // Map<String, ControllerMapping> controllerMapping
                    //key存放 注解中的value值 代表访问地址
                    //value存放封装类，里面是类的字节码文件和方法
                    for (Method method : methods) {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        String value = annotation.value();
                        System.out.println("注解上的value属性:"+value);
                        ControllerMapping mapping = new ControllerMapping();
                        mapping.setControllerClass(controllerClass);
                        mapping.setHandleMethod(method);
                        controllerMapping.put(value,mapping);
                    }
                }
            }

        }
        return controllerMapping;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException, ClassNotFoundException {
        new Configuration().config();
    }
}
