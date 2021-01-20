package com.lagou.service;

import com.github.pagehelper.PageInfo;
import com.lagou.bean.Article;

public interface ArticleService {

    PageInfo<Article> findPageBean(Integer pageIndex, Integer pageSize);
}
