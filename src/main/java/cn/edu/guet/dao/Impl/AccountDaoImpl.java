package cn.edu.guet.dao.Impl;

import cn.edu.guet.dao.AccountDao;
import cn.edu.guet.entity.User;
import cn.edu.guet.mvc.annotation.Repository;

/**
 * @Author 郭乐源
 * @Date 2022/5/17 11:48
 * @Version 1.0
 */

public class AccountDaoImpl implements AccountDao {
    @Override
    public User login() {
        User user = new User();
        user.setName("zhangsan");
        user.setPassword("123456");
        return user;
    }
}
