package cn.edu.guet.service.Impl;

import cn.edu.guet.dao.AccountDao;
import cn.edu.guet.entity.User;
import cn.edu.guet.mvc.annotation.Resource;
import cn.edu.guet.mvc.annotation.Service;
import cn.edu.guet.service.AccountService;

/**
 * @Author 郭乐源
 * @Date 2022/5/17 11:51
 * @Version 1.0
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    AccountDao accountDao;

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public User login() {
        return accountDao.login();
    }

    @Override
    public String toString() {
        return "AccountServiceImpl{" +
                "accountDao=" + accountDao +
                '}';
    }
}
