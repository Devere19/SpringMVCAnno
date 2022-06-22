package cn.edu.guet.controller;

import cn.edu.guet.entity.User;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.mvc.annotation.Resource;
import cn.edu.guet.service.AccountService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 13:30
 * @Version 1.0
 */
@Controller
public class AccountController {

    @Resource
    private AccountService accountService;

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping("/login")
    public String login(String name, String password, HttpServletRequest request){
        /*
        根据用户名拿到用户
         */
        User user=accountService.login();
        if (user.getName().equals(name)&&password.equals(user.getPassword())){
            request.setAttribute("user",user);
            return "index2.jsp";
        }else {
            System.out.println("用户不存在");
            return null;
        }
    }

    @Override
    public String toString() {
        return "AccountController{" +
                "accountService=" + accountService +
                '}';
    }
}
