package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.LagouSecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LagouController
@LagouRequestMapping("/demo")
@LagouSecurity(value = {"zhangsan"})
public class DemoController {


    @LagouAutowired
    private IDemoService demoService;



    /**
     * URL: /demo/query?name=lisi
     * @param request
     * @param response
     * @param name
     * @return
     */
    @LagouSecurity(value = {"lisi","wangwu"})
    @LagouRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }

    @LagouSecurity(value = {"zhaoliu"})
    @LagouRequestMapping("/queryAnother")
    public String queryAnother(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }
}
