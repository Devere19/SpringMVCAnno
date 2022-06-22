package cn.edu.guet.service.Impl;

import cn.edu.guet.dao.UserDao;
import cn.edu.guet.entity.User;
import cn.edu.guet.mvc.annotation.Resource;
import cn.edu.guet.mvc.annotation.Service;
import cn.edu.guet.service.UserService;

/**
 * @Author 郭乐源
 * @Date 2022/5/16 16:22
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User queryUser() {
        User user = userDao.queryUser();
        return user;
    }

    @Override
    public String toString() {
        return "UserServiceImpl{" +
                "userDao=" + userDao +
                '}';
    }
}
