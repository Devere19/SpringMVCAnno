package cn.edu.guet.ioc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 16:13
 * @Version 1.0
 */
public class BeanFactory {

    /**
     * 单例模式，并且定义一个map存放 id和对象
     */
    private static BeanFactory instance = new BeanFactory();
    public static Map<String, Object> map = new HashMap<String, Object>();

    public static void parseElement(Element ele) {
        try {
            /**
             * 获取applicationContext.xml文件中的id和class，并且创建对象，放入到一个map集合中
             */
            Object beanObj = null;
            Class clazz = null;
            String id = ele.attributeValue("id");
            if (map.get(id) == null) {
                clazz = Class.forName(ele.attributeValue("class"));
                beanObj = clazz.newInstance();
                map.put(id, beanObj);
            }

            /**
             * 通过递归实现引用数据类型的注入
             */
            Object obj = null;
            String ref = "";
            List<Element> childElements = ele.elements();//???ele??????????
            for (Element childEle : childElements) {
                ref = childEle.attributeValue("ref");
                obj = map.get(ref);
                if (obj == null) {
                    for (Element el : list) {
                        String ids = el.attributeValue("id");
                        if (ids.equals(ref)) {
                            parseElement(el);// 递归
                        }
                    }
                }
                obj = map.get(ref);
                if (clazz != null) {
                    Method methods[] = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(ref.toLowerCase())) {
                        /*
                        调用set方法赋值
                         */
                            m.invoke(beanObj, obj);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static List<Element> list;

    //静态代码块 ，加载配置文件取得里面的节点
    static {
        try {
            System.out.println("运行了工厂类");
            SAXReader reader = new SAXReader();
            InputStream in = Class.forName("cn.edu.guet.ioc.BeanFactory")
                    .getResourceAsStream("/applicationContext.xml");
            Document doc = reader.read(in);
            // xPathExpression??xPath????
            list = doc.selectNodes("/beans/bean");
            for (Element ele : list) {
                parseElement(ele);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private BeanFactory() {
    }

    public static BeanFactory getInstance() {
        return instance;
    }

    public Object getBean(String id) {
        return map.get(id);
    }
}
