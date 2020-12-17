package com.lagou.mapper;

import com.lagou.pojo.User;

import java.util.List;

public interface UserMapper {
    //查询所有用户
    public List<User> findAll();


    //根据条件进行用户查询
    public User findByCondition(User user);

    public int delete(User user);
    public int update(User user);
    public int insert(User user);


}
