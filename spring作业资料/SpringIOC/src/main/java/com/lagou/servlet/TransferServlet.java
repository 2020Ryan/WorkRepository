package com.lagou.servlet;

import com.lagou.factory.BeanFactory;
import com.lagou.factory.ProxyFactory;
import com.lagou.pojo.Result;
import com.lagou.service.TransferService;
import com.lagou.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name="transferServlet",urlPatterns = "/transferServlet")
public class TransferServlet extends HttpServlet {

    // 1. 实例化service层对象
    //private TransferService transferService = new TransferServiceImpl();
    //private TransferService transferService = (TransferService)BeanFactory.getBean("transferService");

    /**
     * 从工厂中获取代理对象，此对象增强了事务
     *
     */

    private ProxyFactory proxyFactory =(ProxyFactory) BeanFactory.getBean("proxyFactory");
    private TransferService transferService = (TransferService)proxyFactory.getJdkProxy(BeanFactory.getBean("transferService"));


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求体的字符编码
        req.setCharacterEncoding("UTF-8");
        //从前端页面获取相关参数
        String fromCardNo = req.getParameter("fromCardNo");
        String toCardNo = req.getParameter("toCardNo");
        String moneyStr = req.getParameter("money");
        int money = Integer.parseInt(moneyStr);
        //创建实体类封装,返回前端需要的参数
        Result result = new Result();

        //运用try-catch来捕获异常
        try {
            //servlet调用业务处理层service
            transferService.transfer(fromCardNo,toCardNo,money);
           //没有出现异常，则说明调用成功
            result.setStatus("200");
            result.setMessage("操作成功！");
        }catch (Exception e){
            //失败信息
            e.printStackTrace();
            result.setStatus("201");
            result.setMessage("操作失败！");
        }
        //响应前端
        resp.setContentType("application/json;charset=utf-8");
        //调用自定义json转换解析工具类
        resp.getWriter().println(JsonUtils.object2Json(result));
    }
}
