package cn.edu.guet.mvc;

import lombok.SneakyThrows;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 16:10
 * @Version 1.0
 */
@WebListener()
public class ContextConfigListener implements ServletContextListener {

    public ContextConfigListener() {
    }

    /**
     * 初始化操作
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("运行了监听器");
        //加载工厂类，注入bean，xml格式
        try {
            // Class.forName("cn.edu.guet.ioc.BeanFactory");
            Class.forName("cn.edu.guet.ioc.AnnotationBeanFactory");
            //扫描控制器
            Map<String, ControllerMapping> controllerMapping = new Configuration().config();

            //把存有@Controller和@RequestMapping的类和方法放入到全局作用域
            sce.getServletContext().setAttribute("cn.guet.web.controller",controllerMapping);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * 销毁操作
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
