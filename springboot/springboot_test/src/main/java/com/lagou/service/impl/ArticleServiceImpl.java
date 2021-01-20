package com.lagou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lagou.bean.Article;
import com.lagou.mapper.ArticleMapper;
import com.lagou.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageInfo<Article> findPageBean(Integer pageIndex, Integer pageSize) {

        PageHelper.startPage(pageIndex,3);
        List<Article> articleList = articleMapper.queryAll();
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);
        return pageInfo;
    }

}
