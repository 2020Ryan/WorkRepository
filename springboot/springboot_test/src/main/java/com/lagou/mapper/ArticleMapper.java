package com.lagou.mapper;

import com.lagou.bean.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("SELECT * FROM t_article")
    public List<Article> queryAll();
}
