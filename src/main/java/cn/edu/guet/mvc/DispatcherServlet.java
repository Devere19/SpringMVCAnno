package cn.edu.guet.mvc;

import cn.edu.guet.ioc.AnnotationBeanFactory;
import cn.edu.guet.ioc.BeanFactory;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Author 郭乐源
 * @Date 2022/5/17 9:28
 * @Version 1.0
 */
@WebServlet(value = "*.do")
public class DispatcherServlet extends HttpServlet {

    Map<String, ControllerMapping> controllerMapping;

    /**
     * 初始化操作，获取全局作用域中的controllerMapping
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("运行了dispatcherServlet");
        controllerMapping = (Map<String, ControllerMapping>) config.getServletContext().getAttribute("cn.guet.web.controller");
        System.out.println("从application作用域获取到的controllerMapping：" + controllerMapping);
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取网址请求url中去掉前缀和后缀的url
        ControllerMapping mapping = null;
        String requestURI = request.getRequestURI();
        System.out.println("请求地址栏中的url:" + requestURI);
        requestURI = StringUtils.substringBetween(requestURI, request.getContextPath(), ".do");
        System.out.println("去掉前缀和后缀：" + requestURI);

        //判断controllerMapping中是否有这个key值，如果有的话把包含这个key值的value取出来
        if (controllerMapping.containsKey(requestURI)) {
            mapping = controllerMapping.get(requestURI);
            System.out.println(mapping);
        }

        //分别拿到mapping封装的controllerClass和handleMethod和方法的参数类型
        Class controllerClass = mapping.getControllerClass();
        Method handleMethod = mapping.getHandleMethod();
        Class[] parameterType = handleMethod.getParameterTypes();


        //反射获取参数名称
        ArrayList<String> parameterList = new ArrayList<>();
        Parameter[] parameters = handleMethod.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println("参数名称：" + parameter.getName());
            parameterList.add(parameter.getName());
        }


        //把网页地址栏中的参数的值赋值给controller中的形参
        Object[] parameterValues = new Object[parameterType.length];
        Object obj = null;
        String controllerClassName = controllerClass.getSimpleName();
        //使用工具类把第一个字母换成小写
        controllerClassName = StringUtils.replaceChars(controllerClassName, controllerClassName.substring(0, 1), controllerClassName.substring(0, 1).toLowerCase());
        obj = AnnotationBeanFactory.getInstance().getBean(controllerClassName);


        for (int i = 0; i < parameterType.length; i++) {

            if (ClassUtils.isAssignable(parameterType[i], HttpServletRequest.class)) {
                parameterValues[i] = request;
            } else if (ClassUtils.isAssignable(parameterType[i], HttpServletResponse.class)) {
                parameterValues[i] = response;
            } else if (ClassUtils.isAssignable(parameterType[i], HttpSession.class)) {
                parameterValues[i] = request.getSession();
            } else if (parameterType[i].isPrimitive()) {
                if (parameterType[i].getTypeName().equals("int")) {
                    parameterValues[i] = Integer.parseInt(request.getParameter(parameterList.get(i)));
                } else if (parameterType[i].getTypeName().equals("float")) {
                    parameterValues[i] = Float.parseFloat(request.getParameter(parameterList.get(i)));
                } else if (parameterType[i].getTypeName().equals("double")) {
                    parameterValues[i] = Double.parseDouble(request.getParameter(parameterList.get(i)));
                } else if (parameterType[i].getTypeName().equals("byte")) {
                    parameterValues[i] = Short.parseShort(request.getParameter(parameterList.get(i)));
                } else if (parameterType[i].getTypeName().equals("long")) {
                    parameterValues[i] = Long.parseLong(request.getParameter(parameterList.get(i)));
                } else if (parameterType[i].getTypeName().equals("boolean")) {
                    parameterValues[i] = Boolean.parseBoolean(request.getParameter(parameterList.get(i)));
                }
            } else if (ClassUtils.isAssignable(parameterType[i], String.class)) {
                System.out.println("参数类型是String.........." + request.getParameter(parameterList.get(i)));
                parameterValues[i] = request.getParameter(parameterList.get(i));
            } else {
                //Bean
                Object pojo = parameterType[i].newInstance();
                //得到请求里所有的参数：Map<参数名, value>
                //获取表单里的数据
                Map<String, String[]> parameterMap = request.getParameterMap();
                //beanutils会自动将map里的key与bean的属性名进行反射赋值
                BeanUtils.populate(pojo, parameterMap);
                parameterValues[i] = pojo;
            }
        }


        //已经拿到了调用方法的对象和参数，现在通过反射调用方法
        Object returnValue = handleMethod.invoke(obj, parameterValues);

        //通过returnValue来判断是重定向还是转发或者是json
        if (null != returnValue && returnValue instanceof String) {
            String path = returnValue.toString();
            if (StringUtils.startsWith(path, "redirect:")) {
                //重定向
                response.sendRedirect(request.getContextPath() + StringUtils.substringAfter(path, "redirect:"));
            } else {
                //转发
                request.getRequestDispatcher(path).forward(request, response);
            }
        } else if (returnValue != null && !(returnValue instanceof String)) {
            response.setContentType("application/json;charset=UTF-8");
            //将bean转换成json
            String json = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create().toJson(returnValue);
            PrintWriter writer = response.getWriter();
            writer.write(json);
            writer.flush();
            writer.close();
        }

    }
}
