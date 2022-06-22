package cn.edu.guet.mvc;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 11:13
 * @Version 1.0
 */
@Data
public class ControllerMapping {
   //存放class类
   private Class<?> controllerClass;

   //存放方法
   private Method handleMethod;

}
