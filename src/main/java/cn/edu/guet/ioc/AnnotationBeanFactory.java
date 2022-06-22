package cn.edu.guet.ioc;

import cn.edu.guet.mvc.Configuration;
import cn.edu.guet.mvc.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 郭乐源
 * @Date 2022/5/18 11:10
 * @Version 1.0
 */
public class AnnotationBeanFactory {

    private static AnnotationBeanFactory instance = new AnnotationBeanFactory();
    public static Map<String, Object> map = new HashMap<String, Object>();
    public static String daoPackageName = "cn.edu.guet.dao.Impl";
    public static String servicePackageName = "cn.edu.guet.service.Impl";
    public static String controllerPackageName="cn.edu.guet.controller";


    private AnnotationBeanFactory() {
    }

    public static AnnotationBeanFactory getInstance() {
        return instance;
    }

    public Object getBean(String id) {
        return map.get(id);
    }

    /**
     * 注入dao层的bean对象
     */
    public static void daoBean() {
        try {
            // 把包名转换成路径
            URI uri = Configuration.class.getResource("/" + daoPackageName.replace(".", "/")).toURI();

            //2.通过反射创建对象，放入到map中
            File file = new File(uri);
            String[] daoClassNames = file.list();
            // System.out.println(daoClassNames.length);
            for (String daoClassName : daoClassNames) {
                if (daoClassName.endsWith(".class")) {
                    String fullClassName = daoPackageName + "." + StringUtils.substringBefore(daoClassName, ".class");
                    // System.out.println("dao的全类名:" + fullClassName);
                    Class daoClass = Class.forName(fullClassName);
                    String daoClassId = daoClass.getSimpleName();
                    daoClassId = StringUtils.substringBefore(daoClassId, "Impl");
                    //把第一个字母换成小写
                    daoClassId = StringUtils.replaceChars(daoClassId, daoClassId.substring(0, 1), daoClassId.substring(0, 1).toLowerCase());
                    // System.out.println(daoClassId);
                    //创建对象
                    Object daoClassInstance = daoClass.newInstance();
                    map.put(daoClassId, daoClassInstance);
                }
            }
        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * service注入
     */
    public static void serviceBean() {
        try {
            // 把包名转换成路径
            URI uri = Configuration.class.getResource("/" + servicePackageName.replace(".", "/")).toURI();

            //2.通过反射创建对象，放入到map中
            File file = new File(uri);
            String[] serviceClassNames = file.list();
            // System.out.println(daoClassNames.length);
            for (String serviceClassName : serviceClassNames) {
                if (serviceClassName.endsWith(".class")) {
                    String fullClassName = servicePackageName + "." + StringUtils.substringBefore(serviceClassName, ".class");
                    System.out.println("service的全类名:" + fullClassName);
                    Class serviceClass = Class.forName(fullClassName);
                    Object serviceInstance = serviceClass.newInstance();
                    //获取这个类的所有属性，判断这个属性是否有@Reource注解，如果有的话根据id注入
                    Field[] fields = serviceClass.getDeclaredFields();
                    for (Field field : fields) {
                        // System.out.println(field.getName());
                        if (field.isAnnotationPresent(Resource.class)) {
                            // System.out.println(1111111111);
                            // System.out.println(field.getName() + "使用了@Resource注解");
                            //判断map中是否含有此key的对象
                            String fieldName = field.getName();
                            // System.out.println(field.getGenericType());
                            if (map.containsKey(fieldName)){
                                Method[] methods = serviceClass.getDeclaredMethods();
                                for (Method m : methods) {
                                    //调用set方法赋值，或者使用构造方法，一共两种方式
                                    if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(fieldName.toLowerCase())) {
                                        m.invoke(serviceInstance, map.get(fieldName));
                                    }
                                }
                            }
                        }
                    }
                    //把id改为首字母小写，并且去掉Impl后缀，为了方便controller注入bean
                    
                    String serviceClassId = serviceClass.getSimpleName();
                    serviceClassId = StringUtils.substringBefore(serviceClassId, "Impl");
                    //把第一个字母换成小写
                    serviceClassId = StringUtils.replaceChars(serviceClassId, serviceClassId.substring(0, 1), serviceClassId.substring(0, 1).toLowerCase());
                    // System.out.println(daoClassId);
                    map.put(serviceClassId,serviceInstance);


                }
            }
        } catch (URISyntaxException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * controller注入
     */
    public static void controllerBean(){
        try {
            // 把包名转换成路径
            URI uri = Configuration.class.getResource("/" + controllerPackageName.replace(".", "/")).toURI();

            //2.通过反射创建对象，放入到map中
            File file = new File(uri);
            String[] controllerClassNames = file.list();
            // System.out.println(daoClassNames.length);
            for (String controllerClassName : controllerClassNames) {
                if (controllerClassName.endsWith(".class")) {
                    String fullClassName = controllerPackageName + "." + StringUtils.substringBefore(controllerClassName, ".class");
                    System.out.println("controller的全类名:" + fullClassName);
                    Class controllerClass = Class.forName(fullClassName);
                    Object controllerInstance = controllerClass.newInstance();
                    //获取这个类的所有属性，判断这个属性是否有@Reource注解，如果有的话根据id注入
                    Field[] fields = controllerClass.getDeclaredFields();
                    for (Field field : fields) {
                        // System.out.println(field.getName());
                        if (field.isAnnotationPresent(Resource.class)) {
                            System.out.println(1111111111);
                            System.out.println(field.getName() + "使用了@Resource注解");
                            //判断map中是否含有此key的对象
                            String fieldName = field.getName();
                            // System.out.println(field.getGenericType());
                            if (map.containsKey(fieldName)){
                                Method[] methods = controllerClass.getDeclaredMethods();
                                for (Method m : methods) {
                                    //调用set方法赋值，或者使用构造方法，一共两种方式
                                    if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(fieldName.toLowerCase())) {
                                        m.invoke(controllerInstance, map.get(fieldName));
                                    }
                                }
                            }
                        }
                    }
                    //把id改为首字母小写，并且去掉Impl后缀，为了方便controller注入bean
                    String controllerClassId = controllerClass.getSimpleName();
                    // serviceClassId = StringUtils.substringBefore(serviceClassId, "Impl");
                    //把第一个字母换成小写
                    controllerClassId = StringUtils.replaceChars(controllerClassId, controllerClassId.substring(0, 1), controllerClassId.substring(0, 1).toLowerCase());
                    // System.out.println(daoClassId);
                    map.put(controllerClassId,controllerInstance);


                }
            }
        } catch (URISyntaxException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static {
        //实现dao层bean注入
        daoBean();

        //实现service层bean注入
        serviceBean();

        //实现conreoller层注入
        controllerBean();

        map.forEach((s, o) -> System.out.println("key是" + s + ",value是" + o.toString()));
        System.out.println(map.size());
    }

    public static void main(String[] args) {

    }

}
