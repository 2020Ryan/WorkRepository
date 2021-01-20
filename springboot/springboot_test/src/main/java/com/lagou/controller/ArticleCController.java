package com.lagou.controller;

import com.github.pagehelper.PageInfo;
import com.lagou.bean.Article;
import com.lagou.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("article")
public class ArticleCController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/list")
    public String queryAll(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize, HttpServletRequest request
    ){
        PageInfo<Article> articlePageInfo =   articleService.findPageBean(pageNum,pageSize);
        System.out.println("pageNUm=="+pageNum+"          pageSize===="+pageSize);
        request.setAttribute("pageInfo",articlePageInfo);
        return "client/index";
    }

}
