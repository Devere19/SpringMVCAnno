package cn.edu.guet.dao.Impl;

import cn.edu.guet.dao.UserDao;
import cn.edu.guet.entity.User;
import cn.edu.guet.mvc.annotation.Repository;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 16:15
 * @Version 1.0
 */

public class UserDaoImpl implements UserDao {
    @Override
    public User queryUser() {
        User user = new User();
        user.setName("zhangsan");
        user.setId(1001);
        return user;
    }
}
