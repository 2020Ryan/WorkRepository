package com.lagou.service;

public interface TransferService {
    //接口定义
    void transfer(String fromCardNo, String toCardNo, int money) throws Exception;
}
