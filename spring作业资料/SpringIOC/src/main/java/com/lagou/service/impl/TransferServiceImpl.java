package com.lagou.service.impl;

import com.lagou.dao.AccountDao;
import com.lagou.pojo.Account;
import com.lagou.service.TransferService;
import com.lagou.utils.ConnectionUtils;
import com.lagou.utils.TransactionManager;


public class TransferServiceImpl implements TransferService {


    // 先声明需要调用的对象
    private AccountDao accountDao;

    //使用set方法传值来替代 new 对象
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }



    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        /*try{
            // 开启事务(关闭事务的自动提交)
            TransactionManager.getInstance().beginTransaction();*/

            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(from);
            accountDao.updateAccountByCardNo(to);

        /*    // 提交事务

            TransactionManager.getInstance().commit();
        }catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            TransactionManager.getInstance().rollback();

            // 抛出异常便于上层servlet捕获
            throw e;

        }*/




    }
}
