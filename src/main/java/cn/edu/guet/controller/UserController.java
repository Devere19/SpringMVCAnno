package cn.edu.guet.controller;

import cn.edu.guet.entity.User;
import cn.edu.guet.ioc.BeanFactory;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.mvc.annotation.Resource;
import cn.edu.guet.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 13:29
 * @Version 1.0
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/userView")
    public String viewUser(HttpServletRequest request){
        // userService = (UserService) BeanFactory.getInstance().getBean("userService");
        User user = userService.queryUser();
        request.setAttribute("user",user);
        System.out.println("访问了UserController的/userView.do");
        return "viewUser.jsp";
    }

    @Override
    public String toString() {
        return "UserController{" +
                "userService=" + userService +
                '}';
    }
}
